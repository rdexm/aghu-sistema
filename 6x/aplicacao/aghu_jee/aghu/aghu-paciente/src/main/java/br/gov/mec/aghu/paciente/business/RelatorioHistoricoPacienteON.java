package br.gov.mec.aghu.paciente.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiaPacienteVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistoricoPacientePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SubHistorioPacientePolVO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório do histórico do paciente no prontuário online
 * 
 * @author tfelini
 */
@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.AtributoEmSeamContextManager"})
@Stateless
public class RelatorioHistoricoPacienteON extends BaseBusiness {

private static final String _HIFEN_ = " - ";

private static final Log LOG = LogFactory.getLog(RelatorioHistoricoPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IExamesLaudosFacade examesLaudosFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115363889671220019L;

	private List<HistoricoPacientePolVO> historicoPacientePol = new ArrayList<HistoricoPacientePolVO>();
	
	private List<AacConsultas> consultas = new ArrayList<AacConsultas>();

	private List<MbcCirurgias> cirurgias = new ArrayList<MbcCirurgias>();
	
	private List<CirurgiaPacienteVO> cirurgiasPaciente = new ArrayList<CirurgiaPacienteVO>(); 

	private List<MamDiagnostico> diagnosticos = new ArrayList<MamDiagnostico>();	

	private List<AinInternacao> internacoes = new ArrayList<AinInternacao>();

	private List<MptTratamentoTerapeutico> tratamentosTerapeuticos = new ArrayList<MptTratamentoTerapeutico>();
	
	private List<AelProjetoPacientes> projetosPesquisa = new ArrayList<AelProjetoPacientes>();
	
	private List<SubHistorioPacientePolVO> listSubHistorico = new ArrayList<SubHistorioPacientePolVO>(); 

	private static final Comparator<MamDiagnostico> COMPARATOR_DIAGNOSTICOS = new Comparator<MamDiagnostico>() {
		@Override
		public int compare(MamDiagnostico o1, MamDiagnostico o2) {
			return (o1.getDataFim() == null && o2.getDataFim() == null) ? 0 : ((o1.getDataFim() == null)?-1: ((o2.getDataFim() == null)?1:o1.getData().compareTo(o2.getData())));
		}
	};

	private static final Comparator<CirurgiaPacienteVO> CIRURGIAS_PACIENTE_COMPARATOR = new Comparator<CirurgiaPacienteVO>() {
		@Override
		public int compare(CirurgiaPacienteVO o1, CirurgiaPacienteVO o2) {
			return (o1.getData() == null && o2.getData() == null) ? 0
					: ((o1.getData() == null) ? 1
							: ((o2.getData() == null) ? -1 : o2.getData()
									.compareTo(o1.getData())));
		}
	};
	
	/**
	 * Método que limpa as listagens envolvidas no report.
	 * Isto foi necessário devido a um problema que ocorria ao usuário sair
	 * da aba do report e retornar novamente (as informações estavam replicando)
	 */
	private void limparListagens(){
		historicoPacientePol = new ArrayList<HistoricoPacientePolVO>();
		consultas = new ArrayList<AacConsultas>();
		cirurgias = new ArrayList<MbcCirurgias>();
		cirurgiasPaciente = new ArrayList<CirurgiaPacienteVO>(); 
		diagnosticos = new ArrayList<MamDiagnostico>();	
		internacoes = new ArrayList<AinInternacao>();
		tratamentosTerapeuticos = new ArrayList<MptTratamentoTerapeutico>();
		projetosPesquisa = new ArrayList<AelProjetoPacientes>();
		listSubHistorico = new ArrayList<SubHistorioPacientePolVO>(); 
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<HistoricoPacientePolVO> pesquisa(Integer prontuario 
				) throws ApplicationBusinessException{
		
		//Limpa as listagens pelo caso de o usuário mudar de aba
		this.limparListagens();
		
		AipPacientes paciente = this.getPacienteFacade().pesquisarPacientePorProntuario(prontuario);
		
		Integer ordem = 0;
	
		if (paciente != null) {
			
			Integer matricula;
			short vinculo;
			
			//INICIO DIAGNOSTICOS
			this.diagnosticos = this.getAmbulatorioFacade().pesquisarDiagnosticosPorPaciente(paciente);
			
			if(diagnosticos.size() > 0){
				ordem++;
				//Trata ordenacao dos registros pois nao foi possivel implementar o seguinte order by em criteria: DECODE(data_fim,NULL,'31/12/2060') ASC
				Collections.sort(this.diagnosticos, COMPARATOR_DIAGNOSTICOS);

				this.trataDadosDescricaoDiagnosticoParaExibicao(diagnosticos);
				for (MamDiagnostico diagnostico : diagnosticos) {
					SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
					subHistorico.setData(diagnostico.getData());
					subHistorico.setDataFim(diagnostico.getDataFim());
					subHistorico.setDescricao(diagnostico.getDescricao());
					listSubHistorico.add(subHistorico);
				}
				HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
				hpp.setGrupo(ordem.toString() + ". DIAGNÓSTICOS");
				hpp.setHasDataFim(true);
				hpp.setSubHistorico(listSubHistorico);
				
				historicoPacientePol.add(hpp);	
				
			}
			//FIM DIAGNOSTICOS
			
			//INICIO CONSULTAS
			this.consultas = this.getAmbulatorioFacade().pesquisarConsultasPorPaciente(paciente);
			
			if (consultas.size() > 0){
				ordem++;
				listSubHistorico = new ArrayList<SubHistorioPacientePolVO>();
				
				for (AacConsultas consulta : consultas) {
					if (consulta.getGradeAgendamenConsulta().getProfEspecialidade()!=null){
						matricula = consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getId().getMatricula();
					}else{
						matricula = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel().getId().getMatricula();
					}
					
					if (consulta.getGradeAgendamenConsulta().getProfEspecialidade()!=null){
						vinculo = consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getId().getVinCodigo();
					}else{
						vinculo = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel().getId().getVinCodigo();
					}
					
					StringBuffer descricao = new StringBuffer(StringUtils.capitalize(consulta.getGradeAgendamenConsulta().getEspecialidade().getCentroCusto().getDescricao().toLowerCase()));
					if (!consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade().isEmpty() 
						 && !consulta.getGradeAgendamenConsulta().getEspecialidade().getIndImpSoServico().isSim()){
						descricao.append(_HIFEN_).append(StringUtils.capitalize(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade().toLowerCase()));
					}
					SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
					subHistorico.setData(consulta.getDtConsulta());
					subHistorico.setDataFim(null);
					subHistorico.setDescricao(descricao.append(", ").append(this.getPacienteFacade().pesquisarNomeProfissional(matricula,vinculo)).toString());
					listSubHistorico.add(subHistorico);
				}
				HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
				hpp.setGrupo(ordem.toString() + ". CONSULTAS");
				hpp.setHasDataFim(false);
				hpp.setSubHistorico(listSubHistorico);
				
				historicoPacientePol.add(hpp);
			}
			//FIM CONSULTAS
			
			//INICIO INTERNACOES
			this.internacoes = this.getInternacaoFacade().pesquisarInternacoesPorPaciente(paciente);
			if (this.internacoes.size() > 0){
				ordem++;		
				listSubHistorico = new ArrayList<SubHistorioPacientePolVO>();
				for(AinInternacao internacao: this.internacoes){
					StringBuffer descricao = new StringBuffer();
					if(internacao.getEspecialidade() != null){
						if(internacao.getEspecialidade().getCentroCusto() != null
								&& !StringUtils.isBlank(internacao.getEspecialidade().getCentroCusto().getDescricao())) {
							descricao.append(StringUtils.capitalize(internacao.getEspecialidade().getCentroCusto().getDescricao().toLowerCase()));
						}
						if(internacao.getEspecialidade().getIndImpSoServico() != DominioSimNao.S){
							if(!StringUtils.isBlank(internacao.getEspecialidade().getNomeEspecialidade())){
								if (StringUtils.isNotBlank(descricao.toString())) {
									descricao.append(_HIFEN_);
								}
								descricao.append(StringUtils.capitalize(internacao.getEspecialidade().getNomeEspecialidade().toLowerCase()));
							}
						}
						if(internacao.getServidorProfessor() != null){
							if (StringUtils.isNotBlank(descricao.toString())) {
								descricao.append(", ");
							}
							descricao.append(this.getPacienteFacade().pesquisarNomeProfissional(internacao.getServidorProfessor().getId().getMatricula(),internacao.getServidorProfessor().getId().getVinCodigo()));
						}
					}
					SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
					subHistorico.setData(internacao.getAtendimento().getDthrInicio());
					subHistorico.setDataFim(internacao.getDthrAltaMedica());
					subHistorico.setDescricao(descricao.toString());
					listSubHistorico.add(subHistorico);
					
				}
				
				HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
				hpp.setGrupo(ordem.toString() + ". INTERNAÇÕES");
				hpp.setHasDataFim(true);
				hpp.setSubHistorico(listSubHistorico);
				
				historicoPacientePol.add(hpp);
			}
			//FIM INTERNACOES
			
			//INICIO CIRURGIAS E PROCEDIMENTOS DIAGNÓSTICOS
			this.cirurgias = this.getBlocoCirurgicoFacade().pesquisarCirurgiasPorPaciente(paciente);
			
			Set <CirurgiaPacienteVO> cirurgiasPacienteSet = new HashSet<CirurgiaPacienteVO>();  
			
			if (this.cirurgias.size() > 0){
				ordem++;		
				listSubHistorico = new ArrayList<SubHistorioPacientePolVO>();
				for (MbcCirurgias cirurgia : cirurgias){
					
					MbcProfCirurgias profCirurgia = this.getBlocoCirurgicoFacade().retornaResponsavelCirurgia(cirurgia);
					matricula = profCirurgia.getId().getPucSerMatricula();
					vinculo = profCirurgia.getId().getPucSerVinCodigo();
					
					List<MbcProcEspPorCirurgias> procEspCrgs = this.getBlocoCirurgicoFacade().retornaProcEspCirurgico(cirurgia);
					
					for (MbcProcEspPorCirurgias procEspCrg : procEspCrgs){
					
						CirurgiaPacienteVO cirurgiaPaciente = new CirurgiaPacienteVO();
						
						cirurgiaPaciente.setData(cirurgia.getData());
						cirurgiaPaciente.setNomeResponsavel(this.getPacienteFacade().pesquisarNomeProfissional(matricula,vinculo));
						cirurgiaPaciente.setDescProcCirurgico(StringUtils.capitalize(
							procEspCrg.getProcedimentoCirurgico().getDescricao().toLowerCase()));
						
						cirurgiasPacienteSet.add(cirurgiaPaciente);
					}
				}
				this.cirurgiasPaciente.addAll(cirurgiasPacienteSet);
				Collections.sort(this.cirurgiasPaciente, CIRURGIAS_PACIENTE_COMPARATOR);
				if (this.cirurgiasPaciente.size()>0){
					for (CirurgiaPacienteVO cirurgia : this.cirurgiasPaciente){
						SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
						subHistorico.setData(cirurgia.getData());
						subHistorico.setDataFim(null);
						subHistorico.setDescricao(cirurgia.getDescProcCirurgico() + ", " + cirurgia.getNomeResponsavel());
						listSubHistorico.add(subHistorico);
									
					}
					HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
					hpp.setGrupo(ordem.toString() + ". CIRURGIAS E PROCEDIMENTOS DIAGNÓSTICOS");
					hpp.setHasDataFim(false);
					hpp.setSubHistorico(listSubHistorico);
					
					historicoPacientePol.add(hpp);		
				}
			}
			//FIM CIRURGIAS
			
			//INICIO TRATAMENTOS TERAPEUTICOS
			this.tratamentosTerapeuticos = this.getProcedimentoTerapeuticoFacade().pesquisarTratamentosTerapeuticosPorPaciente(paciente);
			
			if (tratamentosTerapeuticos.size() >0){
				ordem++;
				listSubHistorico = new ArrayList<SubHistorioPacientePolVO>();
				for (MptTratamentoTerapeutico tratamentoTerapeutico : tratamentosTerapeuticos){
					
					matricula = tratamentoTerapeutico.getServidorResponsavel().getId().getMatricula();
					vinculo = tratamentoTerapeutico.getServidorResponsavel().getId().getVinCodigo();
					
								
					StringBuffer especialidade = new StringBuffer(tratamentoTerapeutico.getEspecialidade().getCentroCusto().getDescricao());
					if (tratamentoTerapeutico.getEspecialidade().getIndImpSoServico().equals(DominioSimNao.N)){
						especialidade.append(_HIFEN_).append(tratamentoTerapeutico.getEspecialidade().getNomeEspecialidade());
					}
					
					StringBuffer descricao = new StringBuffer(StringUtils.capitalize(tratamentoTerapeutico.getTipoTratamento().getDescricao().toLowerCase()));
					descricao.append(", ").append(StringUtils.capitalize(especialidade.toString().toLowerCase()))
					.append(", ").append(this.getPacienteFacade().pesquisarNomeProfissional(matricula,vinculo));
					
					SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
					subHistorico.setData(tratamentoTerapeutico.getDthrInicio());
					subHistorico.setDataFim(tratamentoTerapeutico.getDthrFim());
					subHistorico.setDescricao(descricao.toString());
					listSubHistorico.add(subHistorico);
				}
				
				HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
				hpp.setGrupo(ordem.toString() + ". SESSÕES TERAPÊUTICAS");
				hpp.setHasDataFim(true);
				hpp.setSubHistorico(listSubHistorico);
				
				historicoPacientePol.add(hpp);	
			}
			//FIM TRATAMENTOS TERAPEUTICOS

			//INICIO PROJETOS DE PESQUISA
			this.projetosPesquisa = this.getExamesLaudosFacade().pesquisarProjetosDePesquisaPorPaciente(paciente);
			
			if (this.projetosPesquisa.size()>0){
				ordem++;
				listSubHistorico = new ArrayList<SubHistorioPacientePolVO>();
				for (AelProjetoPacientes projetoPesquisa : projetosPesquisa){
					SubHistorioPacientePolVO subHistorico = new SubHistorioPacientePolVO();
					subHistorico.setData(projetoPesquisa.getDtInicio());
					subHistorico.setDataFim(projetoPesquisa.getDtFim());
					subHistorico.setDescricao(projetoPesquisa.getProjetoPesquisa().getNome() + _HIFEN_ + projetoPesquisa.getProjetoPesquisa().getNomeResponsavel());
					listSubHistorico.add(subHistorico);
				}
				
				HistoricoPacientePolVO hpp = new HistoricoPacientePolVO();
				hpp.setGrupo(ordem.toString() + ". PROJETOS DE PESQUISA");
				hpp.setHasDataFim(true);
				hpp.setSubHistorico(listSubHistorico);
				
				historicoPacientePol.add(hpp);	
			}
			//FIM PROJETOS DE PESQUISA
		}
		
		return this.historicoPacientePol;
	}
	
	private void trataDadosDescricaoDiagnosticoParaExibicao(List<MamDiagnostico> listaDiagnosticos){
		StringBuffer descricaoTotal = new StringBuffer();
		for(MamDiagnostico diagnostico: listaDiagnosticos){
			if(diagnostico.getCid() != null &&  !StringUtils.isBlank(diagnostico.getCid().getDescricao())){
				descricaoTotal.append(StringUtils.capitalize(diagnostico.getCid().getDescricao().toLowerCase()));
				descricaoTotal.append(' ');
			}
			if(!StringUtils.isBlank(diagnostico.getComplemento())){
				descricaoTotal.append(diagnostico.getComplemento());
				descricaoTotal.append(' ');
			}
			if(diagnostico.getCid() != null && diagnostico.getCid().getCodigo() != null ){
				descricaoTotal.append("(CID ");
				descricaoTotal.append(diagnostico.getCid().getCodigo());
				descricaoTotal.append(')');
			}
			diagnostico.setDescricao(descricaoTotal.toString());
			descricaoTotal.delete(0, descricaoTotal.length());
		}
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return this.procedimentoTerapeuticoFacade;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
}
