package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOutraEquipeSumrDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPedidoExameDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPlanoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaRecomendacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaReinternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioConclusaoSumarioAltaON extends BaseBusiness {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7706363886652454977L;

	private static final Log LOG = LogFactory.getLog(RelatorioConclusaoSumarioAltaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;
	
	@Inject
	private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;
	
	@Inject
	private MpmAltaPlanoDAO mpmAltaPlanoDAO;
	
	@Inject
	private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;
	
	@Inject
	private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;
	
	@Inject
	private MpmAltaItemPedidoExameDAO mpmAltaItemPedidoExameDAO;
	
	@Inject
	private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;
	
	@Inject
	private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;
	
	@Inject
	private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;
	
	@Inject
	private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;
	
	@Inject
	private MpmAltaPedidoExameDAO mpmAltaPedidoExameDAO;
	
	@Inject
	private MpmAltaMotivoDAO mpmAltaMotivoDAO;
	
	@Inject
	private MpmAltaReinternacaoDAO mpmAltaReinternacaoDAO;
	
	@Inject
	private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;
	
	@Inject
	private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;
	
	@Inject
	private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	@Inject
	private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO; 

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	public RelSumarioAltaVO criaRelatorioSumarioAltaPorId(MpmAltaSumarioId id) {
		if (id == null) {
			LOG.warn("o id esta nulo...");
			return null;
		}
		
		LOG.warn("entrei em criaRelatorioSumarioAlta...");

		MpmAltaSumario altaSumario = this.mpmAltaSumarioDAO.obterAltaSumarioPeloId(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		LOG.warn("1");
		RelSumarioAltaVO relSumario = new RelSumarioAltaVO();
		relSumario.setNomePaciente(altaSumario.getNome());
		relSumario.setIdade(criaIdadeExtenso(altaSumario.getIdadeAnos(),altaSumario.getIdadeMeses(),altaSumario.getIdadeDias()));
		relSumario.setSexo(altaSumario.getSexo().getDescricao());
		relSumario.setDataInternacao(altaSumario.getDthrAtendimento());
		if(altaSumario.getDthrAlta() != null){
			relSumario.setDataAlta(altaSumario.getDthrAlta());
		}
		else{
			relSumario.setDataAlta(new Date());
		}
		relSumario.setProntuario(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		if(altaSumario.getLeito()!=null){
			relSumario.setLeito(altaSumario.getLeito().getLeitoID());
		}
		relSumario.setPermanencia(altaSumario.getDiasPermanencia() + " dias");
		relSumario.setConvenio(altaSumario.getDescConvenio());
		relSumario.setEquipeResponsavel(altaSumario.getDescEquipeResponsavel());
		relSumario.setServicoResponsavel(altaSumario.getDescServicoResponsavel());
		relSumario.setIndetificador(id.toString());

		Integer matriculaValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getMatricula() : null;
		Short 	vinCodigoValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getVinCodigo() : null;
		try {
			relSumario.setNomeSiglaRegistro(buscaNomeSiglaRegistro(matriculaValida, vinCodigoValida));
		} catch (ApplicationBusinessException e) {
			LOG.error("Nao foi possivel pegar o nome e a sigla de registro...");
		}

		relSumario.setOutrasEquipes(buscaOutrasEquipes(id));
		relSumario.setMotivosInternacao(buscaAltaDiagMtvoInternacoes(id));  
		relSumario.setDignosticoPrincipalAlta(buscaAltaDiagPrincipal(id));  
		relSumario.setDignosticosSecundarios(buscaAltaDiagSecundarios(id));
		relSumario.setCirurgiasRealizadas(buscaAltaCirgRealizadas(id));
		relSumario.setOutrosProcedimentos(buscaOutrosProcedimentos(id));
		relSumario.setConsultorias(buscaAltaConsultorias(id));
		relSumario.setPrincipaisFarmacos(buscaPrincFarmacos(id));   
		relSumario.setComplementoFarmacos(buscaComplFarmacos(id));
		relSumario.setEvolucao(buscaEvolucoes(id));   		
		relSumario.setMotivoAlta(buscaAltaMotivos(id)); 
		relSumario.setPlanoPosAlta(buscaAltaPlano(id));
		relSumario.setRecomendacoesAlta(buscaAltaRecomendacoes(id));
		relSumario.setMedicamentosAlta(buscaMedicamentosAlta(id));
		relSumario.setConsultas(buscaAltaPedidoExame(id));
		relSumario.setReInternacao(buscaAltaReinternacao(id));
		relSumario.setExamesSolicitados(buscaAltaItemPedidoExame(id));
		relSumario.setEstadoPaciente(buscaAltaEstadoPaciente(id));
		relSumario.setConsultasFuturas(buscaConsultasFuturas(altaSumario.getPaciente().getCodigo()));
		
		popularIndices(relSumario);
		popularLeitoQuarto(relSumario, altaSumario);
		
		return relSumario;
	}
	
	private void popularIndices(RelSumarioAltaVO relSumario) {
		Integer indice = 1;
		
		if ((relSumario.getMotivosInternacao() != null && !relSumario.getMotivosInternacao().isEmpty())
				|| relSumario.getDignosticoPrincipalAlta() != null) {
			
			relSumario.setIndiceDiagnostico(++indice + ".");
		}
		
		indice = popularIndiceProcedTerapeutico(relSumario, indice);
		
		if (relSumario.getEvolucao() != null){
			relSumario.setIndiceEvolucao(++indice + ".");
		}
		
		if (relSumario.getMotivoAlta()!=null || relSumario.getPlanoPosAlta()!=null 
				|| (relSumario.getRecomendacoesAlta()!= null && !relSumario.getRecomendacoesAlta().isEmpty())
				|| (relSumario.getMedicamentosAlta()!= null && !relSumario.getMedicamentosAlta().isEmpty())){
			
			relSumario.setIndicePlanoPosAlta(++indice + ".");
		}
		
		if (relSumario.getConsultas()!=null || relSumario.getReInternacao()!=null 
				|| (relSumario.getExamesSolicitados() != null && !relSumario.getExamesSolicitados().isEmpty())
				|| (relSumario.getConsultasFuturas() != null && !relSumario.getConsultasFuturas().isEmpty())){
			
			relSumario.setIndiceSeguimentoAtendimento(++indice + ".");
		}
		
		if (relSumario.getEstadoPaciente() != null){
			relSumario.setIndiceEstadoAlta(++indice + ".");
		}
	}
	
	private Integer popularIndiceProcedTerapeutico(RelSumarioAltaVO relSumario, Integer indice) {
		if ((relSumario.getCirurgiasRealizadas()!=null && !relSumario.getCirurgiasRealizadas().isEmpty())
				||(relSumario.getOutrosProcedimentos()!=null && !relSumario.getOutrosProcedimentos().isEmpty())
				||(relSumario.getConsultorias()!=null && !relSumario.getConsultorias().isEmpty())
				||(relSumario.getPrincipaisFarmacos()!=null && !relSumario.getPrincipaisFarmacos().isEmpty())
				||(relSumario.getComplementoFarmacos()!=null && !relSumario.getComplementoFarmacos().isEmpty())){
				
				relSumario.setIndiceProcedTerapeutico(++indice + ".");
			}
		return indice;
	}
	
	private void popularLeitoQuarto(RelSumarioAltaVO relSumario, MpmAltaSumario altaSumario) {

		if (altaSumario.getLeito() != null) {
			relSumario.setLocalRodape("Leito: " + altaSumario.getLeito().getLeitoID());
		} else if (altaSumario.getQuarto() != null) {
			relSumario.setLocalRodape("Quarto: " + altaSumario.getQuarto().getDescricao());
		} else if (altaSumario.getUnidadeFuncional() != null) {
			StringBuffer unidade = null;
			if (altaSumario.getUnidadeFuncional().getSigla() != null) {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getSigla());
			} else {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getAndar().toString());
				if (altaSumario.getUnidadeFuncional().getIndAla() != null) {
					unidade.append(altaSumario.getUnidadeFuncional().getIndAla());
				}
			}
			relSumario.setLocalRodape(unidade.insert(0, "Unidade: ").toString());
		}
	}
	
	private String buscaNomeSiglaRegistro(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		BuscaConselhoProfissionalServidorVO vo = laudoProcedimentoSusRN.buscaConselhoProfissionalServidorVO(matricula, vinculo, Boolean.FALSE);
		if(StringUtils.isNotBlank(vo.getSiglaConselho()) && StringUtils.isNotBlank(vo.getNome())){
			return substituiNullPorEspaco(vo.getNome(),true) + 
				   substituiNullPorEspaco(vo.getSiglaConselho(),true)+ 
				   substituiNullPorEspaco(vo.getNumeroRegistroConselho(),false);
		}else{
			if(matricula != null && vinculo != null){
				return registroColaboradorFacade.buscarServidor(vinculo, matricula).getPessoaFisica().getNome();
			}else{
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogadoSemCache();
				
				return servidorLogado.getPessoaFisica().getNome();
			}
		}
	}	

	private List<LinhaReportVO> buscaOutrasEquipes(final MpmAltaSumarioId id){
		List<LinhaReportVO> lista = this.mpmAltaOutraEquipeSumrDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		List<LinhaReportVO> novaLista = new ArrayList<LinhaReportVO>();
		for (LinhaReportVO linha : lista){
			linha.setTexto1(WordUtils.capitalizeFully(linha.getTexto1()).replaceAll(" Da ", " da ").replaceAll(" De ", " de ").replaceAll(" Do ", " do "));
			novaLista.add(linha);
		}
		return novaLista;
	}
	
	private List<LinhaReportVO> buscaAltaDiagMtvoInternacoes(final MpmAltaSumarioId id){
		return this.mpmAltaDiagMtvoInternacaoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}
	
	private String buscaAltaDiagPrincipal(final MpmAltaSumarioId id){
		return this.mpmAltaDiagPrincipalDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}	

	private List<LinhaReportVO> buscaAltaDiagSecundarios(final MpmAltaSumarioId id){
		return this.mpmAltaDiagSecundarioDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}	
	
	private List<LinhaReportVO> buscaAltaCirgRealizadas(final MpmAltaSumarioId id){
		return this.mpmAltaCirgRealizadaDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		

	private List<LinhaReportVO> buscaOutrosProcedimentos(final MpmAltaSumarioId id){
		return this.mpmAltaOtrProcedimentoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}
	
	private List<LinhaReportVO> buscaAltaConsultorias(final MpmAltaSumarioId id){
		return this.mpmAltaConsultoriaDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		
	
	private List<LinhaReportVO> buscaPrincFarmacos(final MpmAltaSumarioId id){
		return this.mpmAltaPrincFarmacoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		
	
	private List<LinhaReportVO> buscaComplFarmacos(final MpmAltaSumarioId id){
		return this.mpmAltaComplFarmacoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		

	private String buscaEvolucoes(final MpmAltaSumarioId id){
		return this.mpmAltaEvolucaoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		
	
	private String buscaAltaMotivos(final MpmAltaSumarioId id){
		return this.mpmAltaMotivoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		

	private String buscaAltaPlano(final MpmAltaSumarioId id){
		return this.mpmAltaPlanoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		

	private List<LinhaReportVO> buscaAltaRecomendacoes(final MpmAltaSumarioId id){
		return this.mpmAltaRecomendacaoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	private List<LinhaReportVO> buscaMedicamentosAlta(final MpmAltaSumarioId id){
		return this.mpmAltaRecomendacaoDAO.buscaMedicamentosAlta(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}	

	private List<LinhaReportVO> buscaConsultasFuturas(final Integer pacCodigo){
		List<AacConsultas> listaConsultas = aacConsultasDAO.listarConsultasPaciente(0, 99999, null, true, pacCodigo, null, 
				null, null, null, null, null, new Date(), null, false);
		List<LinhaReportVO> resultList = new ArrayList<LinhaReportVO>();
		for(AacConsultas consulta : listaConsultas){
			LinhaReportVO linha = new LinhaReportVO();
			linha.setData(consulta.getDtConsulta());
			linha.setTexto1(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade());
			linha.setTexto2(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getSigla() + " - " + consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getId().getSala());
			resultList.add(linha);
		}
		return resultList;
	}

	private String buscaAltaPedidoExame(final MpmAltaSumarioId id){
		return this.mpmAltaPedidoExameDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		
	
	private String buscaAltaReinternacao(final MpmAltaSumarioId id){ 
		return this.mpmAltaReinternacaoDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		
	
	private List<LinhaReportVO> buscaAltaItemPedidoExame(final MpmAltaSumarioId id){ 
		return this.mpmAltaItemPedidoExameDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteVO();
	}
	
	private String buscaAltaEstadoPaciente(final MpmAltaSumarioId id){
		return this.mpmAltaEstadoPacienteDAO.obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		

	private String criaIdadeExtenso(Short anos, Integer meses, Integer dias){		
		StringBuffer idadeExt = new StringBuffer();
		if (anos!=null && anos > 1) {
			idadeExt.append(anos).append(" anos ");
		}else if (anos!=null && anos == 1) {
			idadeExt.append(anos).append(" ano ");
		}
		if (meses!=null && meses > 1) {
			idadeExt.append(meses).append(" meses ");
		}else if (meses!=null && meses == 1) {
			idadeExt.append(meses).append(" mês ");
		}
		if (anos==null || anos == 0){ 
			if (dias!=null && dias > 1) {
				idadeExt.append(dias).append(" dias ");
			}else if (dias!=null && dias == 1) {
				idadeExt.append(dias).append(" dia ");
			}
		}		
		return idadeExt.toString();
	}

	private String substituiNullPorEspaco(String str, boolean addEspaco){
		if (str==null){
			return "";
		}else{
			if (addEspaco){
				return str+" ";
			}else{
				return str;
			}	
		}
	}
}