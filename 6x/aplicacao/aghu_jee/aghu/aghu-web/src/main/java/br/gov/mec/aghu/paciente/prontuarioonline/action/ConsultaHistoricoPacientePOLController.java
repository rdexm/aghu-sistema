package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiaPacienteVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ConsultaPacienteVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.DiagnosticosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoPacienteVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProjetoPacientesVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.TratamentoTerapeuticoVO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável por exibir o histórico do paciente na consulta de
 * pacientes do POL.
 * 
 * @author tiago.felini
 */


public class ConsultaHistoricoPacientePOLController extends ActionController {

	private static final long serialVersionUID = -8046443188033341103L;

	@EJB
	private IPacienteFacade pacienteFacade;

	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private RelatorioHistoricoPacienteController relatorioHistoricoPacienteController;
	
	@Inject
	private SecurityController securityController;	
	
	@Inject @SelectionQualifier
	private NodoPOLVO itemPOL;	
	
	private List<ConsultaPacienteVO> consultasPaciente = new ArrayList<ConsultaPacienteVO>(); 
	private List<CirurgiaPacienteVO> cirurgiasPaciente = new ArrayList<CirurgiaPacienteVO>();
	private List<DiagnosticosVO> diagnosticos = new ArrayList<DiagnosticosVO>();	
	private List<InternacaoPacienteVO> internacoesVO = new ArrayList<InternacaoPacienteVO>();
	private List<TratamentoTerapeuticoVO> tratamentosTerapeuticosPaciente = new ArrayList<TratamentoTerapeuticoVO>();
	private List<ProjetoPacientesVO> projetosPesquisa = new ArrayList<ProjetoPacientesVO>();	
	
	/**
	 * Número do prontuário do paciente, obtido via page parameter.
	 */
	private Boolean relatorioHistoricoVazio;

	/**
	 * Paciente consultado.
	 */
	private AipPacientes paciente;

	private static final Comparator<DiagnosticosVO> COMPARATOR_DIAGNOSTICOS = new Comparator<DiagnosticosVO>() {
		@Override
		public int compare(DiagnosticosVO o1, DiagnosticosVO o2) {
			return (o1.getDataFim() == null && o2.getDataFim() == null) ? 0
					: ((o1.getDataFim() == null) ? -1 : ((o2.getDataFim() == null) ? 1 : o1
							.getData().compareTo(o2.getData())));
		}
	};

	private static final Comparator<CirurgiaPacienteVO> CIRURGIAS_PACIENTE_COMPARATOR = new Comparator<CirurgiaPacienteVO>() {
		@Override
		public int compare(CirurgiaPacienteVO o1, CirurgiaPacienteVO o2) {
			return (o1.getData() == null && o2.getData() == null) ? 0 : ((o1.getData() == null) ? 1
					: ((o2.getData() == null) ? -1 : o2.getData().compareTo(o1.getData())));
		}
	};

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation, true);
		inicio();
	}	
	
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void inicio() {
		this.diagnosticos = new ArrayList<DiagnosticosVO>();
		this.projetosPesquisa = new ArrayList<ProjetoPacientesVO>();	
		
		this.consultasPaciente  = new ArrayList<ConsultaPacienteVO>();
		this.cirurgiasPaciente = new ArrayList<CirurgiaPacienteVO>();
		this.internacoesVO = new ArrayList<InternacaoPacienteVO>();
		this.tratamentosTerapeuticosPaciente = new ArrayList<TratamentoTerapeuticoVO>();
		
		this.paciente = this.pacienteFacade.pesquisarPacientePorProntuario(itemPOL.getProntuario());
		if (this.paciente != null) {
			
			Integer matricula;
			Short vinculo;
			
			//INICIO CONSULTAS
			List<AacConsultas> consultas = this.ambulatorioFacade.pesquisarConsultasPorPacientePOL(this.paciente);
			List<RapServidoresId> servidores = new ArrayList<RapServidoresId>();
			
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
					descricao.append(" - ").append(StringUtils.capitalize(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade().toLowerCase()));
				}

				ConsultaPacienteVO consultaPaciente = new ConsultaPacienteVO();
				consultaPaciente.setDtConsulta(consulta.getDtConsulta());
				consultaPaciente.setMatricula(matricula);
				consultaPaciente.setVinCodigo(vinculo);
				servidores.add(new RapServidoresId(matricula, vinculo));
				consultaPaciente.setSeqEspecialidade(consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq());
				consultaPaciente.setDescricao(descricao.toString());

				this.consultasPaciente.add(consultaPaciente);
			}
			this.carregaInformacaoPacienteVO(servidores);
					
			//FIM CONSULTAS
			
			//INICIO CIRURGIAS
			List<MbcProcEspPorCirurgias> procEspCrgs = this.blocoCirurgicoFacade.obterProcedimentosEspeciaisPorCirurgia(this.paciente.getCodigo());
			
			Set <CirurgiaPacienteVO> cirurgiasPacienteSet = new HashSet<CirurgiaPacienteVO>();  
			servidores.clear();
			for (MbcProcEspPorCirurgias procEspCrg : procEspCrgs) {
				
				MbcCirurgias cirurgia = procEspCrg.getCirurgia();
				MbcProfCirurgias profCirurgia = this.blocoCirurgicoFacade
						.retornaResponsavelCirurgia(cirurgia);
				
				CirurgiaPacienteVO cirurgiaPaciente = new CirurgiaPacienteVO();
				if (profCirurgia != null && profCirurgia.getId() != null) {
					matricula = profCirurgia.getId().getPucSerMatricula();
					vinculo = profCirurgia.getId().getPucSerVinCodigo();
					servidores.add(new RapServidoresId(matricula, vinculo));
					cirurgiaPaciente.setMatricula(matricula);
					cirurgiaPaciente.setVinculo(vinculo);
					//nome = this.pacienteFacade.pesquisarNomeProfissional(
						//	matricula, vinculo);
				}

				cirurgiaPaciente.setData(cirurgia.getData());
				//cirurgiaPaciente.setNomeResponsavel(nome);
				cirurgiaPaciente.setDescProcCirurgico(StringUtils
						.capitalize(procEspCrg.getProcedimentoCirurgico()
								.getDescricao().toLowerCase()));

				cirurgiasPacienteSet.add(cirurgiaPaciente);

			}
			
			this.cirurgiasPaciente.addAll(cirurgiasPacienteSet);
			Collections.sort(this.cirurgiasPaciente, CIRURGIAS_PACIENTE_COMPARATOR);
			this.carregaInformacaoCirurgiasVO(servidores);
			
			//FIM CIRURGIAS
			
			if(getUsuarioAdministrativo() == false){
			
				//INICIO DIAGNOSTICOS
				List<MamDiagnostico> diagnosticos = this.ambulatorioFacade.pesquisarDiagnosticosPorPaciente(this.paciente);
				
				if(diagnosticos.size() > 0){
					for (int i = 0; i < diagnosticos.size(); i++) {
						MamDiagnostico diagnostico = diagnosticos.get(i);
						DiagnosticosVO diagnosticosVO = new DiagnosticosVO();
						diagnosticosVO.setData(diagnostico.getData());
						diagnosticosVO.setDataFim(diagnostico.getDataFim());
						diagnosticosVO.setDescricao(trataDadosDescricaoDiagnosticoParaExibicao(diagnostico));
						
						this.diagnosticos.add(diagnosticosVO);
					}
					
					// Trata ordenacao dos registros pois nao foi possivel
					// implementar o seguinte order by em criteria:
					// DECODE(data_fim,NULL,'31/12/2060') ASC
					Collections.sort(this.diagnosticos, COMPARATOR_DIAGNOSTICOS);
				}
				//FIM DIAGNOSTICOS
			}
			
			//INICIO TRATAMENTOS TERAPEUTICOS
			servidores.clear();
			List<MptTratamentoTerapeutico> tratamentosTerapeuticos = this.procedimentoTerapeuticoFacade
					.pesquisarTratamentosTerapeuticosPorPaciente(this.paciente);
			
			for (MptTratamentoTerapeutico tratamentoTerapeutico : tratamentosTerapeuticos){
				
				matricula = tratamentoTerapeutico.getServidorResponsavel().getId().getMatricula();
				vinculo = tratamentoTerapeutico.getServidorResponsavel().getId().getVinCodigo();
				
				TratamentoTerapeuticoVO tratamentoTerapeuticoPaciente = new TratamentoTerapeuticoVO();
				
				servidores.add(new RapServidoresId(matricula, vinculo));
				tratamentoTerapeuticoPaciente.setMatricula(matricula);
				tratamentoTerapeuticoPaciente.setVinculo(vinculo);
				
				StringBuffer especialidade = new StringBuffer(tratamentoTerapeutico.getEspecialidade().getCentroCusto()
						.getDescricao());
				if (tratamentoTerapeutico.getEspecialidade().getIndImpSoServico()
						.equals(DominioSimNao.N)) {
					especialidade.append(" - ").append(tratamentoTerapeutico.getEspecialidade().getNomeEspecialidade());
				}
				
				tratamentoTerapeuticoPaciente.setDthrInicio(tratamentoTerapeutico.getDthrInicio());
				tratamentoTerapeuticoPaciente.setDthrFim(tratamentoTerapeutico.getDthrFim());
				//tratamentoTerapeuticoPaciente.setNome(this.pacienteFacade.pesquisarNomeProfissional(
					//	matricula, vinculo));
				tratamentoTerapeuticoPaciente.setEspecialidade(StringUtils.capitalize(especialidade.toString()
						.toLowerCase()));
				tratamentoTerapeuticoPaciente.setTipoSessao(StringUtils
						.capitalize(tratamentoTerapeutico.getTipoTratamento().getDescricao()
								.toLowerCase()));
				this.tratamentosTerapeuticosPaciente.add(tratamentoTerapeuticoPaciente);
		
			}	

			this.carregaInformacaoTratamentoVO(servidores);
			
			
			//FIM TRATAMENTOS TERAPEUTICOS

			//INICIO PROJETOS DE PESQUISA
			List<AelProjetoPacientes> projetosPesquisa = this.examesLaudosFacade
					.pesquisarProjetosDePesquisaPorPaciente(this.paciente);
			for (AelProjetoPacientes aelProjetoPacientes : projetosPesquisa) {
				ProjetoPacientesVO projeto = new ProjetoPacientesVO();
				projeto.setDtInicio(aelProjetoPacientes.getDtInicio());
				projeto.setDtFim(aelProjetoPacientes.getDtFim());
				projeto.setNome(aelProjetoPacientes.getProjetoPesquisa().getNome());
				projeto.setNomeResponsavel(aelProjetoPacientes.getProjetoPesquisa().getNomeResponsavel());
				this.projetosPesquisa.add(projeto);
			}
			
			
			//FIM PROJETOS DE PESQUISA
			
			//INICIO INTERNACOES
			List<AinInternacao> internacoes = this.internacaoFacade.pesquisarInternacoesPorPacientePOL(this.paciente);
			servidores.clear();
			for(AinInternacao internacao: internacoes){
				StringBuffer descricao = new StringBuffer();
				InternacaoPacienteVO internacaoVO = new InternacaoPacienteVO(); 
				if(internacao.getEspecialidade() != null){
					if(internacao.getEspecialidade().getCentroCusto() != null
							&& !StringUtils.isBlank(internacao.getEspecialidade().getCentroCusto()
									.getDescricao())) {
						descricao.append(StringUtils.capitalize(internacao.getEspecialidade()
								.getCentroCusto().getDescricao().toLowerCase()));
					}
					if(internacao.getEspecialidade().getIndImpSoServico() != DominioSimNao.S){
						if (!StringUtils.isBlank(internacao.getEspecialidade()
								.getNomeEspecialidade())) {
							if (StringUtils.isNotBlank(descricao.toString())) {
								descricao.append(" - ");
							}
							descricao.append(StringUtils.capitalize(internacao.getEspecialidade()
									.getNomeEspecialidade().toLowerCase()));
						}
					}
					if(internacao.getServidorProfessor() != null){
						if (StringUtils.isNotBlank(descricao.toString())) {
							descricao.append(", ");
						}
						
						matricula = internacao.getServidorProfessor().getId().getMatricula();
						vinculo = internacao.getServidorProfessor().getId().getVinCodigo();
						servidores.add(new RapServidoresId(matricula, vinculo));
						internacaoVO.setMatricula(matricula);
						internacaoVO.setVinculo(vinculo);
						//descricao.append(this.pacienteFacade.pesquisarNomeProfissional));
					}
				}
				internacaoVO.setDescricao(descricao.toString());
				
				internacaoVO.setDthrAltaMedica(internacao.getDthrAltaMedica());
				
				internacaoVO.setDthrInicio(internacao.getDthrInternacao());
				
				this.internacoesVO.add(internacaoVO);
			}

			this.carregaInformacaoIntenacaoVO(servidores);			
			
			//FIM INTERNACOES
			
		}
		relatorioHistoricoVazio = processarRelatorioHistoricoVazio();
	}
	
	
	private Boolean processarRelatorioHistoricoVazio() {
		if(ambulatorioFacade.pesquisarDiagnosticosPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		if(ambulatorioFacade.pesquisarConsultasPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		if(internacaoFacade.pesquisarInternacoesPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		if(blocoCirurgicoFacade.pesquisarCirurgiasPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		if(procedimentoTerapeuticoFacade.pesquisarTratamentosTerapeuticosPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		if(examesLaudosFacade.pesquisarProjetosDePesquisaPorPacienteCount(paciente) > 0){
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}

	private void carregaInformacaoIntenacaoVO(List<RapServidoresId> servidores) {
		if(servidores.isEmpty()){
			return;
		}
		
		List<RapServidoresVO> servidoresVO = pacienteFacade.obterNomeProfissionalServidores(new ArrayList<RapServidoresId>(servidores));
		for (InternacaoPacienteVO internacaoVO : internacoesVO) {
			for (RapServidoresVO rapServidoresVO : servidoresVO) {
				if(internacaoVO.getVinculo().shortValue() == rapServidoresVO.getVinculo().shortValue()
					&& internacaoVO.getMatricula().longValue() == rapServidoresVO.getMatricula().longValue()){
					internacaoVO.setDescricao(internacaoVO.getDescricao() + rapServidoresVO.getNomeProfissional());
					break;
				}
			}
		}
		
	}

	private void carregaInformacaoTratamentoVO(List<RapServidoresId> servidores) {
		if(servidores.isEmpty()){
			return;
		}
		
		List<RapServidoresVO> servidoresVO = pacienteFacade.obterNomeProfissionalServidores(servidores);
		for (TratamentoTerapeuticoVO tratamentoTerapeutico : tratamentosTerapeuticosPaciente) {
			for (RapServidoresVO rapServidoresVO : servidoresVO) {
				if(tratamentoTerapeutico.getVinculo().shortValue() == rapServidoresVO.getVinculo().shortValue()
					&& tratamentoTerapeutico.getMatricula().longValue() == rapServidoresVO.getMatricula().longValue()){
					tratamentoTerapeutico.setNome(rapServidoresVO.getNomeProfissional());
					break;
				}
			}
		}
	}

	private void carregaInformacaoCirurgiasVO(List<RapServidoresId> servidores) {
		if(servidores.isEmpty()){
			return;
		}
		
		List<RapServidoresVO> servidoresVO = pacienteFacade.obterNomeProfissionalServidores(servidores);
		
		for (CirurgiaPacienteVO cirurgiaPaciente : cirurgiasPaciente) {
			
			for (RapServidoresVO rapServidoresVO : servidoresVO) {
				
				// #37343
				if ( cirurgiaPaciente.getVinculo() != null && cirurgiaPaciente.getMatricula() != null) {
					
					if(cirurgiaPaciente.getVinculo().shortValue() == rapServidoresVO.getVinculo().shortValue()
						&& cirurgiaPaciente.getMatricula().longValue() == rapServidoresVO.getMatricula().longValue()){
						cirurgiaPaciente.setNomeResponsavel(rapServidoresVO.getNomeProfissional());
						break;
					}
				}
			}
		}
	}

	private void carregaInformacaoPacienteVO(List<RapServidoresId> servidores) {
		List<RapServidoresVO> servidoresVO = new ArrayList<RapServidoresVO>();
		if(servidores.isEmpty() == false){
			//carrega nome do profissional
			servidoresVO = pacienteFacade.obterNomeProfissionalServidores(servidores);
		}
		
		List<MamEmgEspecialidades> especialidades = this.ambulatorioFacade.listarTodasMamEmgEspecialidade();
		for (ConsultaPacienteVO consultaPaciente : consultasPaciente) {
			for (RapServidoresVO rapServidoresVO : servidoresVO) {
				if(consultaPaciente.getVinCodigo().shortValue() == rapServidoresVO.getVinculo().shortValue()
					&& consultaPaciente.getMatricula().longValue() == rapServidoresVO.getMatricula().longValue()){
					consultaPaciente.setNome(rapServidoresVO.getNomeProfissional());
					break;
				}
			}
			
			//mamemgespecialidades
			for(MamEmgEspecialidades especialidade : especialidades){
				if(consultaPaciente.getSeqEspecialidade().shortValue() == especialidade.getEspSeq().shortValue()){
					consultaPaciente.setEmergencia(true);
					consultaPaciente.setEstilo("background-color:red;color:white;");
					break;
				}
			}
		}
	}
	
	private String trataDadosDescricaoDiagnosticoParaExibicao(
			MamDiagnostico diagnostico) {
		StringBuffer descricaoTotal = new StringBuffer();

		if (diagnostico.getCid() != null
				&& !StringUtils.isBlank(diagnostico.getCid().getDescricao())) {
			descricaoTotal.append(StringUtils.capitalize(diagnostico.getCid()
					.getDescricao().toLowerCase()));
			descricaoTotal.append(' ');
		}
		if (!StringUtils.isBlank(diagnostico.getComplemento())) {
			descricaoTotal.append(diagnostico.getComplemento());
			descricaoTotal.append(' ');
		}
		if (diagnostico.getCid() != null
				&& diagnostico.getCid().getCodigo() != null) {
			descricaoTotal.append("(CID ");
			descricaoTotal.append(diagnostico.getCid().getCodigo());
			descricaoTotal.append(')');
		}
		
		return descricaoTotal.toString();

	}

	public String redirecionarRelatorioHistoricoPaciente() {
		relatorioHistoricoPacienteController.setProntuario(itemPOL.getProntuario());
		return "relatorioHistoricoPacientePdf";
	}
	
	public void imprimirRelatoriohistoricoPaciente() {
		try {			
			relatorioHistoricoPacienteController.observarGeracaoRelatorioHistoricoPaciente(itemPOL.getProntuario());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean getUsuarioAdministrativo(){
		return securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	/*public void setConsultas(List<AacConsultas> consultas) {
		this.consultas = consultas;
	}

	public List<AacConsultas> getConsultas() {
		return consultas;
	}*/

	public List<ConsultaPacienteVO> getConsultasPaciente() {
		return consultasPaciente;
	}

	public void setConsultasPaciente(List<ConsultaPacienteVO> consultasPaciente) {
		this.consultasPaciente = consultasPaciente;
	}

	/*public List<MbcCirurgias> getCirurgias() {
		return cirurgias;
	}

	public void setCirurgias(List<MbcCirurgias> cirurgias) {
		this.cirurgias = cirurgias;
	}*/

	public List<CirurgiaPacienteVO> getCirurgiasPaciente() {
		return cirurgiasPaciente;
	}

	public void setCirurgiasPaciente(List<CirurgiaPacienteVO> cirurgiasPaciente) {
		this.cirurgiasPaciente = cirurgiasPaciente;
	}
	
	public List<DiagnosticosVO> getDiagnosticos() {
		return diagnosticos;
	}

	public void setDiagnosticos(List<DiagnosticosVO> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}

	/*public List<AinInternacao> getInternacoes() {
		return internacoes;
	}

	public void setInternacoes(List<AinInternacao> internacoes) {
		this.internacoes = internacoes;
	}*/
	
	public List<InternacaoPacienteVO> getInternacoesVO() {
		return internacoesVO;
	}

	public void setInternacoesVO(List<InternacaoPacienteVO> internacoesVO) {
		this.internacoesVO = internacoesVO;
	}

	/*public List<AelProjetoPacientes> getProjetosPesquisa() {
		return projetosPesquisa;
	}

	public void setProjetosPesquisa(List<AelProjetoPacientes> projetosPesquisa) {
		this.projetosPesquisa = projetosPesquisa;
	}*/

	/*public List<MptTratamentoTerapeutico> getTratamentosTerapeuticos() {
		return tratamentosTerapeuticos;
	}

	public void setTratamentosTerapeuticos(List<MptTratamentoTerapeutico> tratamentosTerapeuticos) {
		this.tratamentosTerapeuticos = tratamentosTerapeuticos;
	}*/

	public List<TratamentoTerapeuticoVO> getTratamentosTerapeuticosPaciente() {
		return tratamentosTerapeuticosPaciente;
	}

	public void setTratamentosTerapeuticosPaciente(
			List<TratamentoTerapeuticoVO> tratamentosTerapeuticosPaciente) {
		this.tratamentosTerapeuticosPaciente = tratamentosTerapeuticosPaciente;
	}

	public List<ProjetoPacientesVO> getProjetosPesquisa() {
		return projetosPesquisa;
	}

	public void setProjetosPesquisa(List<ProjetoPacientesVO> projetosPesquisa) {
		this.projetosPesquisa = projetosPesquisa;
	}

	public Boolean getRelatorioHistoricoVazio() {
		return relatorioHistoricoVazio;
	}

	public void setRelatorioHistoricoVazio(Boolean relatorioHistoricoVazio) {
		this.relatorioHistoricoVazio = relatorioHistoricoVazio;
	}
}