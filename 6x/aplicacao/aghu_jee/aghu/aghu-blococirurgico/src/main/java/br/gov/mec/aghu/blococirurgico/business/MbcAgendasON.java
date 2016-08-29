package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaDiagnosticoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaOrtProteseDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaSolicEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.AvalOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MensagemParametro;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.vo.ParametroAgendaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 *  Classe responsável pelas regras de FORMS para #25888
 *  @author rpanassolo
 */
@Stateless
public class MbcAgendasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcAgendaSolicEspecialDAO mbcAgendaSolicEspecialDAO;

	@Inject
	private MbcAgendaHistoricoDAO mbcAgendaHistoricoDAO;

	@Inject
	private MbcAgendaAnestesiaDAO mbcAgendaAnestesiaDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;

	@Inject
	private MbcAgendaDiagnosticoDAO mbcAgendaDiagnosticoDAO;

	@Inject
	private MbcAgendaOrtProteseDAO mbcAgendaOrtProteseDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@EJB
	private MbcAgendaOrtProteseON mbcAgendaOrtProteseON;

	@EJB
	private MbcAgendaAnestesiaRN mbcAgendaAnestesiaRN;

	@EJB
	private IBlocoCirurgicoOpmesFacade iBlocoCirurgicoOpmesFacade;

	@EJB
	private MbcAgendaDiagnosticoRN mbcAgendaDiagnosticoRN;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;

	@EJB
	private MbcAgendaProcedimentoON mbcAgendaProcedimentoON;

	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private MbcAgendasJustificativaRN mbcAgendasJustificativaRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private MbcAgendaHemoterapiaON mbcAgendaHemoterapiaON;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcAgendasHorarioPrevisaoON mbcAgendasHorarioPrevisaoON;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private MbcAgendaSolicEspecialRN mbcAgendaSolicEspecialRN;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final long serialVersionUID = -3929829233425349273L;
	private static final String ATUALIZAR_ESCALA = "atualizarEscalaPortalPlanejamento"; 

	public enum MbcAgendasONExceptionCode implements BusinessExceptionCode {
		MBC_01832, MBC_01833,MBC_01834,MBC_01030,MBC_01040,MBC_00901,MBC_01027,MBC_00940,  MSG_ERRO_REQ_OBRIG_AGENDA, MSG_ERRO_REQ_OBRIG
	}
	
	/**
	 * ON1
	 * verifica se o campo lado cirurgico é obrigatório
	 * se o indicador do lado da cirurgia do procedimento selecionado for true o campo lado cirurgico deve ser obrigatório
	 */
	 public void verificaLadoCirurgiaObrigatorio(MbcAgendas agendas) throws BaseException {
		 if(agendas.getPaciente()!=null && agendas.getPaciente().getProntuario()!=null && agendas.getLadoCirurgia()==null){
			 if(agendas.getProcedimentoCirurgico()!=null && agendas.getProcedimentoCirurgico().getLadoCirurgia()){
				 throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01834);
			 }
		 }
	 }
	
	 /**
	  * ON2
	  * Verifica se existe codigo CID da agenda na aba agendaDiagnostico
	  */
	 public void verificarCidCodigo(MbcAgendas agendas, MbcAgendaDiagnostico agendaDiagnostico )throws BaseException {
		 if(agendas.getPaciente()!=null && agendas.getPaciente().getProntuario()!=null){
			 if(agendaDiagnostico.getAghCid()== null){
				 throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01832);
			 }
		 }
	 }
	 
	 /**
	  * ON3
	  * quando situação for diferente de Escala cirurgiva não pode existir material especial 
	  */
	 public Boolean verificarMaterialEspecial(MbcAgendas agendas) throws BaseException{
		 if(!DominioSituacaoAgendas.ES.equals(agendas.getIndSituacao()) && agendas.getMaterialEspecial()!=null ){
			 //String descricao = "";
			 //if(agendas.getUnidadeFuncional()!=null && agendas.getUnidadeFuncional().getDescricao()!=null){
			 //	descricao = agendas.getUnidadeFuncional().getDescricao();
			 //}
			 //throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01833,descricao);
			 return true;
		 }
		 return false;
	 }
	 
	 
	 /**
	  * ON4
	  * Gravar dados da tabelas MBC_AGENDAS, realizando todas as regras associadas à inserção e alteração
	  * @param agendas
	  * @throws BaseException
	  */
	 private MbcAgendas persistirAgenda(MbcAgendas agenda) throws BaseException {
		 RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		 return getMbcAgendasRN().persistirAgenda(agenda,servidorLogado);
	 }
	 
	private <T> Collection<T> adicionarLista(Collection<T> lista) {
		Collection<T> retorno = new ArrayList<T>();
		if (lista != null) {
			retorno.addAll(lista);
		}
		return retorno;
	}
	
	public MbcAgendas atualizarConvenio(MbcAgendas agenda) throws ApplicationBusinessException {
		if(agenda.getIndGeradoSistema() != null){
			if(agenda.getIndGeradoSistema()){
				return agenda;
			}
		}
		
		
		AghParametros paramSusPlanoInternacao = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		
		AghParametros paramSusPlanoAmbulatorio = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		
		AghParametros paramSusPadrao = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		
		FatConvenioSaude convSus = faturamentoFacade.obterFatConvenioSaudePorId(paramSusPadrao.getVlrNumerico().shortValue());
		
		if(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO.equals(agenda.getRegime()) ){
			FatConvenioSaudePlano plano = faturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(
					new FatConvenioSaudePlanoId(convSus.getCodigo(), paramSusPlanoAmbulatorio.getVlrNumerico().byteValue()));
			agenda.setConvenioSaudePlano(plano);
		}else{
			FatConvenioSaudePlano plano = faturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(
					new FatConvenioSaudePlanoId(convSus.getCodigo(), paramSusPlanoInternacao.getVlrNumerico().byteValue()));
			agenda.setConvenioSaudePlano(plano);
		}
		
		return agenda;
	}
	
	 public MensagemParametro gravarAgenda(MbcAgendas agenda, MbcAgendaDiagnostico diagRemocao, List<MbcAgendaAnestesia> anestesiasRemocao, List<MbcAgendaSolicEspecial> solicEspecRemocao, List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover, List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,List<MbcAgendaOrtProtese> agendaOrtProtesesRemovidas, String obterLoginUsuarioLogado, List<MbcItensRequisicaoOpmes> itensExcluidos, List<MbcOpmesVO> listaClone, List<MbcOpmesVO> listaPesquisada, Boolean zeraFluxo)throws BaseException {
		 MbcAgendas agendaOriginal = null;
		 
		List<MbcAgendaDiagnostico> agendaDiagnosticos = (List<MbcAgendaDiagnostico>) adicionarLista(agenda.getAgendasDiagnosticos());
		List<MbcAgendaAnestesia> agendaAnestesias = (List<MbcAgendaAnestesia>) adicionarLista(agenda.getAgendasAnestesias());
		List<MbcAgendaProcedimento> agendaProcedimentos = (List<MbcAgendaProcedimento>) adicionarLista(agenda.getAgendasprocedimentos());
		Set<MbcAgendaHemoterapia> agendaHemoterapias = new HashSet<MbcAgendaHemoterapia>(adicionarLista(agenda.getAgendasHemoterapias()));
		List<MbcAgendaSolicEspecial> agendaSolicitacoes = (List<MbcAgendaSolicEspecial>) adicionarLista(agenda.getAgendasSolicEspeciais());
		List<MbcAgendaOrtProtese> agendaOrteseProteses = (List<MbcAgendaOrtProtese>) adicionarLista(agenda.getAgendasOrtProteses());
		List<MbcRequisicaoOpmes> requisicoesOpmes = (List<MbcRequisicaoOpmes>) adicionarLista(agenda.getRequisicoesOpmes());
		
		agenda = atualizarConvenio(agenda);
		if(agenda.getSeq() != null) {
			 agendaOriginal = obterAgendaOriginal(agenda);
			 if(agenda.getDtAgenda() != null 
						&& agendaOriginal.getDtAgenda() != null 
						&& CoreUtil.modificados(agenda.getDtAgenda(), agendaOriginal.getDtAgenda())) {
					agenda.setDtAgenda(DateUtil.truncaData(agenda.getDtAgenda()));
			}
		}else{
			 agenda.setDthrInclusao(new Date());
				if(agenda.getDtAgenda() != null) {
					agenda.setDtAgenda(DateUtil.truncaData(agenda.getDtAgenda()));
			}
		}
		
		
		MensagemParametro msgParam = null;
		if (agenda.getRequisicoesOpmes() != null && !agenda.getRequisicoesOpmes().isEmpty()) {
			//#40466
			
		    Boolean todosZerados = getValidarTodasQuantidadesSolicitadas(listaPesquisada);
		    if(todosZerados){
		    	msgParam = getValidarObrigatoriedadeOPME(agenda, listaPesquisada);
		    }	
		}
		if(msgParam!= null){
			return msgParam;
		}
		
		
		 //ON1
		 this.verificaLadoCirurgiaObrigatorio(agenda);
		 //ON2
		 this.verificarCidCodigo(agenda, agenda.getAgendasDiagnosticos().get(0));
		 //ON3
		 this.verificarMaterialEspecial(agenda);
		 //ON4
		 if(agenda.getRequisicoesOpmes().get(0).getSituacao() == null){
			 agenda.setRequisicoesOpmes(null);
			 requisicoesOpmes = null;
		 }
		 agenda = this.persistirAgenda(agenda);
		 
		 agenda = this.gerarHorariosPrevisaoInicioFim(agenda, agendaOriginal);
		 
		 //Double seq = getMbcAgendaHistoricoDAO().buscarProximoSeqp(agenda.getSeq());
		 //super.atribuirContextoConversacao("AGENDA_SEQP_HIST", seq);
		 
		 agenda.setAgendasDiagnosticos(agendaDiagnosticos);
		 agenda.setAgendasAnestesias(agendaAnestesias);
		 agenda.setAgendasHemoterapias(agendaHemoterapias);
		 agenda.setAgendasOrtProteses(agendaOrteseProteses);
		 agenda.setAgendasprocedimentos(agendaProcedimentos);
		 agenda.setAgendasSolicEspeciais(agendaSolicitacoes);
		 agenda.setRequisicoesOpmes(requisicoesOpmes);
		 
		 //ON9
		 this.gravarOutrasAbas(agendaOriginal, agenda, diagRemocao, anestesiasRemocao, solicEspecRemocao, listMbcAgendaHemoterapiaRemover,listMbcAgendaProcedimentoRemover,agendaOrtProtesesRemovidas);
		 
		 /**
		 * Estabilizacao do modulo OPME sera feita posteriormente.
		 * A estoria #31934 não faz parte da versão 5 do AGHU
		 * Codigo inserido indevidamente no branch de estabilizacao do bloco cirurgico 
		 */

		 if(agenda.getRequisicoesOpmes() != null){
			 msgParam = this.gravaRequisicaoOpmes(agenda, itensExcluidos, obterLoginUsuarioLogado, listaClone, listaPesquisada, zeraFluxo);
		 }

		 getMbcAgendaHistoricoDAO().flush();

		 //super.atribuirContextoConversacao("AGENDA_SEQP_HIST", null);
			
		return msgParam;
	 }
	 
	 private MensagemParametro gravaRequisicaoOpmes(MbcAgendas agenda, List<MbcItensRequisicaoOpmes> itensExcluidos, String servidorLogado, List<MbcOpmesVO> listaClone, List<MbcOpmesVO> listaPesquisada, Boolean zeraFluxo)throws BaseException {
		 MensagemParametro msgParam = null;
		 if (agenda.getRequisicoesOpmes() != null && !agenda.getRequisicoesOpmes().isEmpty()) {
			//#40466
			
		    Boolean todosZerados = getValidarTodasQuantidadesSolicitadas(listaPesquisada);
		    if(todosZerados){
		    	msgParam = getValidarObrigatoriedadeOPME(agenda, listaPesquisada);
		    }	
			
//		    if(todosZerados && msgParam != null){
//				return msgParam;
//			}
			
			MbcRequisicaoOpmes requisicaoOpmes = agenda.getRequisicoesOpmes().get(0);
			List<MbcItensRequisicaoOpmes> itens = requisicaoOpmes.getItensRequisicao();
			if(requisicaoOpmes != null && requisicaoOpmes.getSeq() != null){
				requisicaoOpmes = mbcRequisicaoOpmesDAO.merge(requisicaoOpmes);
			}
			
			requisicaoOpmes.setAgendas(agenda);
			requisicaoOpmes.setItensRequisicao(itens);
			getBlocoIBlocoCirurgicoOpmesFacade().gravaRequisicaoFull(requisicaoOpmes, itensExcluidos, listaClone,zeraFluxo);
		}
		
		return msgParam;
	}
	 
	private MensagemParametro getValidarObrigatoriedadeOPME(MbcAgendas agenda, List<MbcOpmesVO> listaPesquisada) throws BaseException{
			
			List<MbcGrupoAlcadaAvalOpms> alcadas = this.blocoCirurgicoFacade.listarGrupoAlcadaFiltro(null,
					agenda.getEspecialidade(), 
					null, 
					null, 
					null,
					DominioSituacao.A);
			 
		    // Não Obriga se não tiver grupoAlcada
		    if(alcadas == null || alcadas.size() == 0){
		    	return null;
		    }
			List<AvalOPMEVO> listaObrigatoriedade = null;
			
			//#49961 - só Obriga se existir OPMES pesquisadas para selecionar
			if(listaPesquisada.size() > 0){
				
				listaObrigatoriedade = this.getBlocoCirurgicoFacade().verificaObrigRegistroOpmes(agenda);
				if(listaObrigatoriedade != null && !listaObrigatoriedade.isEmpty()){
					DominioTipoObrigatoriedadeOpms dominio = listaObrigatoriedade.get(0).getTipoObrigatoriedade();
					
					if(dominio == DominioTipoObrigatoriedadeOpms.AGENDAMENTO){
						MensagemParametro msgParam = new MensagemParametro();
						msgParam.setMensagem("MSG_ERRO_REQ_OBRIG_AGENDA");
						return msgParam;
					} else if(dominio == DominioTipoObrigatoriedadeOpms.PRAZO){
						ParametroAgendaVO parametros = this.getParametroFacade().consultarParametrosParaAgenda();
						
						if(parametros != null){
							Calendar inclusao = Calendar.getInstance();
							inclusao.setTime(agenda.getDthrInclusao());
							inclusao.add(Calendar.DAY_OF_MONTH, parametros.getCodCrgPrzPln().intValue());
							if(agenda.getDtAgenda().getTime() < inclusao.getTimeInMillis()){
								MensagemParametro msgParam = new MensagemParametro();
								msgParam.setMensagem("MSG_ERRO_REQ_OBRIG");
								msgParam.setParametros(new Object[]{parametros.getCodCrgPrzPln().intValue()});
								return msgParam;
							}
						}
					}
				}
			}
			
			return null;
	}
	 
	private Boolean getValidarTodasQuantidadesSolicitadas(List<MbcOpmesVO> listaPesquisada) {
		for(MbcOpmesVO vo : listaPesquisada){
			if(vo.getQtdeSol() != 0){
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	protected MbcAgendaHistoricoDAO getMbcAgendaHistoricoDAO() {
		return mbcAgendaHistoricoDAO;
	}
	 
	 private MbcAgendas obterAgendaOriginal(MbcAgendas agenda) {
		 MbcAgendas agendaOriginal = getMbcAgendasDAO().obterOriginal(agenda);
		 List<MbcAgendaProcedimento> agendaProcedimentos = getMbcAgendaProcedimentoDAO().pesquisarPorAgdSeq(agendaOriginal.getSeq());
		 List<MbcAgendaOrtProtese> agendaOrtProteses = getMbcAgendaOrtProteseDAO().buscarOrteseprotesePorAgenda(agendaOriginal.getSeq());
		 Set<MbcAgendaHemoterapia> agendaHemo = getMbcAgendaHemoterapiaDAO().listarAgendasHemoterapiaPorAgendaSeq(agendaOriginal.getSeq());
		 List<MbcAgendaAnestesia> agendaAnestesia = getMbcAgendaAnestesiaDAO().listarAgendaAnestesiaPorAgdSeq(agendaOriginal.getSeq());
		 List<MbcAgendaSolicEspecial> agendaSolicEsp = getMbcAgendaSolicEspecialDAO().buscarMbcAgendaSolicEspecialPorAgdSeq(agendaOriginal.getSeq());
		 
		 adicionarAgendasOrtProteses(agendaOriginal, agendaOrtProteses);
		 adicionarAgendaProcedimentos(agendaOriginal, agendaProcedimentos);
		 adicionarAgendaHemoterapia(agendaOriginal, agendaHemo);
		 adicionarAgendaAnestesias(agendaOriginal, agendaAnestesia);
		 adicionarAgendaSolicEspecial(agendaOriginal, agendaSolicEsp);
		 
		return agendaOriginal;
	}

	private void adicionarAgendaSolicEspecial(MbcAgendas agendaOriginal, List<MbcAgendaSolicEspecial> agendaSolicEsp) {
		List<MbcAgendaSolicEspecial> agendaSolicEspOriginal = new ArrayList<MbcAgendaSolicEspecial>();
		if (agendaSolicEsp != null && !agendaSolicEsp.isEmpty()) {
			for (MbcAgendaSolicEspecial item : agendaSolicEsp) {
				if (item.getVersion() != null) {
					agendaSolicEspOriginal.add(getMbcAgendaSolicEspecialDAO().obterOriginal(item));
				}
			}
		}
		agendaOriginal.setAgendasSolicEspeciais(agendaSolicEspOriginal);
	}

	private void adicionarAgendaAnestesias(MbcAgendas agendaOriginal, List<MbcAgendaAnestesia> agendaAnestesia) {
		List<MbcAgendaAnestesia> agendaAnestesiaOriginal = new ArrayList<MbcAgendaAnestesia>();
		if (agendaAnestesia != null && !agendaAnestesia.isEmpty()) {
			for (MbcAgendaAnestesia item : agendaAnestesia) {
				if (item.getVersion() != null) {
					agendaAnestesiaOriginal.add(getMbcAgendaAnestesiaDAO().obterOriginal(item));
				}
			}
		}
		agendaOriginal.setAgendasAnestesias(agendaAnestesiaOriginal);
	}

	private void adicionarAgendaHemoterapia(MbcAgendas agendaOriginal, Set<MbcAgendaHemoterapia> agendaHemo) {
		Set<MbcAgendaHemoterapia> agendaHemoOriginal = new HashSet<MbcAgendaHemoterapia>();
		if (agendaHemo != null && !agendaHemo.isEmpty()) {
			for (MbcAgendaHemoterapia mbcAgendaHemoterapia : agendaHemo) {
				if (mbcAgendaHemoterapia.getVersion() != null) {
					agendaHemoOriginal.add(getMbcAgendaHemoterapiaDAO().obterOriginal(mbcAgendaHemoterapia));
				}
			}
		}
		agendaOriginal.setAgendasHemoterapias(agendaHemoOriginal);
	}

	private void adicionarAgendaProcedimentos(MbcAgendas agendaOriginal, List<MbcAgendaProcedimento> agendaProcedimentos) {
		List<MbcAgendaProcedimento> agendaProcedimentosOriginal = new ArrayList<MbcAgendaProcedimento>();
		if (agendaProcedimentos != null && !agendaProcedimentos.isEmpty()) {
			for (MbcAgendaProcedimento mbcAgendaProcedimento : agendaProcedimentos) {
				if (mbcAgendaProcedimento.getVersion() != null) {
					agendaProcedimentosOriginal.add(getMbcAgendaProcedimentoDAO().obterOriginal(mbcAgendaProcedimento));
				}
			}
		}
		agendaOriginal.setAgendasprocedimentos(agendaProcedimentosOriginal);
	}

	private void adicionarAgendasOrtProteses(MbcAgendas agendaOriginal, List<MbcAgendaOrtProtese> agendaOrtProteses) {
		List<MbcAgendaOrtProtese> agendaOrtProtesesOriginal = new ArrayList<MbcAgendaOrtProtese>();
		if (agendaOrtProteses != null && !agendaOrtProteses.isEmpty()) {
			for (MbcAgendaOrtProtese mbcAgendaOrtProtese : agendaOrtProteses) {
				if (mbcAgendaOrtProtese.getVersion() != null) {
					agendaOrtProtesesOriginal.add(getMbcAgendaOrtProteseDAO().obterOriginal(mbcAgendaOrtProtese));
				}
			}
		}
		agendaOriginal.setAgendasOrtProteses(agendaOrtProtesesOriginal);
	}

	 
	 
	/**
	  * ON9
	  * gravar outras abas
	  * @param agendas
	  * @throws BaseException
	  */
	 private void gravarOutrasAbas(MbcAgendas agendaOriginal, MbcAgendas agenda, MbcAgendaDiagnostico diagRemocao,
			 List<MbcAgendaAnestesia> anestesiasRemocao, List<MbcAgendaSolicEspecial> solicEspecRemocao,
			 List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover,List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,
			 List<MbcAgendaOrtProtese> agendaOrtProtesesRemovidas)throws BaseException {
		 this.gravarAbaOrtProtese(agendaOriginal, agenda,agendaOrtProtesesRemovidas);
		 
		 if (DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			 this.gravarAgendaHemoterapia(agendaOriginal, agenda, listMbcAgendaHemoterapiaRemover);
		 }
		 
		 this.gravarAbaOutrosProcedimentos(agendaOriginal, agenda,listMbcAgendaProcedimentoRemover);
		 this.gravarAbaAnestesia(agendaOriginal, agenda, anestesiasRemocao);
		 this.gravarAbaSolicEspecial(agendaOriginal, agenda, solicEspecRemocao);
		 this.gravarAgendaDiagnostico(agenda, diagRemocao);
	 }
	 
	 private void gravarAgendaDiagnostico(final MbcAgendas agenda, MbcAgendaDiagnostico diagRemocao) throws BaseException {
			final MbcAgendaDiagnostico novo = agenda.getAgendasDiagnosticos().get(0);
			if(diagRemocao != null){
				diagRemocao = getMbcAgendaDiagnosticoDAO().obterPorChavePrimaria(diagRemocao.getId());
				if(diagRemocao.getId().getAgdSeq()!=null){
					this.getMbcAgendaDiagnosticoRN().excluirAgendaDiagnostico(diagRemocao);
					getMbcAgendaDiagnosticoDAO().flush();
					getMbcAgendaDiagnosticoDAO().desatachar(novo);
					novo.setId(null);
				}
			}
			if(novo.getId() == null) {
				novo.setMbcAgendas(agenda);
				this.getMbcAgendaDiagnosticoRN().persistirAgendaDiagnostico(novo);
			}
		}

		private void gravarAgendaHemoterapia(MbcAgendas agendaOriginal, MbcAgendas agenda, List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover)
				throws BaseException {
			this.getMbcAgendaHemoterapiaON().gravarAgendaHemoterapia(agendaOriginal, agenda.getAgendasHemoterapias(), listMbcAgendaHemoterapiaRemover, agenda);
		}
	 
	 private void gravarAbaOrtProtese(MbcAgendas agendaOriginal, MbcAgendas agenda,List<MbcAgendaOrtProtese> agendaOrtProtesesRemovidas)throws BaseException {
		 this.getMbcAgendaOrtProteseON().gravarAgendaOrtProtese(agendaOriginal,agenda.getAgendasOrtProteses(), agendaOrtProtesesRemovidas, agenda);
	 }
	 
	 
	 
	 private void gravarAbaAnestesia(MbcAgendas agendaOriginal, MbcAgendas agenda, List<MbcAgendaAnestesia> listaRemocao) throws BaseException {
		  for(MbcAgendaAnestesia item : listaRemocao) {
			  item = getMbcAgendaAnestesiaDAO().obterPorChavePrimaria(item.getId());
			  getMbcAgendaAnestesiaRN().deletar(item);
		  }
		  for(MbcAgendaAnestesia item : agenda.getAgendasAnestesias()) {
			  if (!(agendaOriginal != null && agendaOriginal.getAgendasAnestesias() != null && agendaOriginal.getAgendasAnestesias().contains(item))) {
				  getMbcAgendaAnestesiaDAO().desatachar(item);
				  item.setId(null);
				  getMbcAgendaAnestesiaRN().persistirAgendaAnestesia(item);
			  }
		  }
	 }
	 
	 private void gravarAbaSolicEspecial(MbcAgendas agendaOriginal, MbcAgendas agenda, List<MbcAgendaSolicEspecial> listaRemocao) throws BaseException {
		 for(MbcAgendaSolicEspecial item : listaRemocao) {
			 item = getMbcAgendaSolicEspecialDAO().obterPorChavePrimaria(item.getId());
			 getMbcAgendaSolicEspecialRN().deletar(item);
		 }
		 for(MbcAgendaSolicEspecial item : agenda.getAgendasSolicEspeciais()) {
			 if (!(agendaOriginal != null && agendaOriginal.getAgendasSolicEspeciais() != null && agendaOriginal.getAgendasSolicEspeciais().contains(item))) {
				 getMbcAgendaSolicEspecialDAO().desatachar(item);
				 item.setId(null);
				 getMbcAgendaSolicEspecialRN().persistirAgendaSolicEspecial(item);
			 }
		 }
	 }
	 
	 private void gravarAbaOutrosProcedimentos(MbcAgendas agendaOriginal, MbcAgendas agenda, List<MbcAgendaProcedimento> agendaProcedimentosRemovidas)throws BaseException {
		 getMbcAgendaProcedimentoON().gravarAgendaProcedimento(agendaOriginal, agenda.getAgendasprocedimentos(), agendaProcedimentosRemovidas, agenda);
	 }
	 
	 
	 public void excluirAgenda(MbcAgendaJustificativa agendaJustificativa) throws BaseException {
         MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agendaJustificativa.getMbcAgendas().getSeq());
         RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
         agenda.setIndExclusao(true);
		 getMbcAgendasRN().persistirAgenda(agenda,servidorLogado);
		 //setAgenda em agjustificativa
		 agendaJustificativa.setMbcAgendas(agenda);
		 getMbcAgendasJustificativaRN().persistir(agendaJustificativa);
	 }
	 
	public void excluirPacienteEmAgenda(MbcAgendaJustificativa agendaJustificativa, String cameFrom) throws BaseException {
		MbcAgendas agenda = getMbcAgendasDAO().obterAgendaPorSeq(agendaJustificativa.getMbcAgendas().getSeq());	
		agenda.setIndExclusao(true);
		agenda.setDthrPrevInicio(null);
		agenda.setDthrPrevFim(null);
		agenda.setOrdemOverbooking(null);
		agendaJustificativa.setMbcAgendas(agenda);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
       	 if(ATUALIZAR_ESCALA.equals(cameFrom)) {
       		 if(agenda.getIndGeradoSistema()){
       			 throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_00901);
       		 }
       		 //cancela cirurgias e seta motivo cancelamento do parametro P_MOT_DESMARCAR	
       		 cancelaCirurgiasAgendadas(agenda.getSeq());
       	 } else {
       		 throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_00940);
       	 }
        }
		
		getMbcAgendasRN().persistirAgenda(agenda, servidorLogado);
		
		getMbcAgendasJustificativaRN().persistir(agendaJustificativa);
	}
	 
	public void transferirAgendamento(MbcAgendaJustificativa agendaJustificativa, String comeFrom) throws BaseException {
		MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agendaJustificativa.getMbcAgendas().getSeq());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(ATUALIZAR_ESCALA.equals(comeFrom)){
			if(agenda.getIndGeradoSistema()){
				throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_00901);
			}
		}
		
		if(agendaJustificativa.getTipo().equals(DominioTipoAgendaJustificativa.TAC)) {			
			agenda.setIndSituacao(DominioSituacaoAgendas.CA);
		} else if(agendaJustificativa.getTipo().equals(DominioTipoAgendaJustificativa.TAE)) {
			agenda.setIndSituacao(DominioSituacaoAgendas.LE);
		}
		
		agenda.setDtAgenda(null);
		agenda.setDthrPrevInicio(null);
		agenda.setDthrPrevFim(null);
		agenda.setOrdemOverbooking(null);
		
		getMbcAgendasRN().persistirAgenda(agenda,servidorLogado);
		
		agendaJustificativa.setMbcAgendas(agenda);
		getMbcAgendasJustificativaRN().persistir(agendaJustificativa);
		
		if(ATUALIZAR_ESCALA.equals(comeFrom)){
			//Chama @DB p_limpa_reserva_sangue;
			if(agenda.getAgendasHemoterapias() != null) {
				for (MbcAgendaHemoterapia hemoterapia : agenda.getAgendasHemoterapias()) {
					getMbcAgendaHemoterapiaDAO().remover(hemoterapia);
				}
			}

			//cancela cirurgias	e seta motivo cancelamento do parametro P_MOT_DESMARCAR	
			cancelaCirurgiasAgendadas(agenda.getSeq());
		}
	}
	
	public void gravarTrocaLocalEspecilidadeEquipeSala(MbcAgendas agenda, String comeFrom) throws BaseException {
		List<MbcProfAtuaUnidCirgs> profs = getBlocoCirurgicoPortalPlanejamentoFacade().buscarEquipeMedicaParaMudancaNaAgenda(agenda.getProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome(),agenda.getUnidadeFuncional().getSeq(),null);
		if(! (profs!=null && profs.size()>0)){
			throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01027);
		}
		
		if(ATUALIZAR_ESCALA.equals(comeFrom)){
			if(!DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())){
				throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01040);
			}
		}
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())
				|| DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())) {
			Boolean dataValidada = getBlocoCirurgicoPortalPlanejamentoFacade().validarDataReagendamento(
					agenda.getDtAgenda(), agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade().getSeq(),
					agenda.getUnidadeFuncional().getSeq());
			if(!dataValidada) {
				throw new ApplicationBusinessException(MbcAgendasONExceptionCode.MBC_01030);
			}
		}
		
		getMbcAgendasRN().persistirAgenda(agenda,servidorLogado);
		
		if(agenda.getSalaCirurgica() != null){
			//gera novamente os horarios de inicio e fim da agenda ou overbooking
			getMbcAgendasHorarioPrevisaoON().gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(agenda.getPucServidor(), 
					agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade(), agenda.getUnidadeFuncional(), agenda.getDtAgenda(), agenda,
					null, agenda.getSalaCirurgica().getId().getSeqp(), null, null, true, false);
		}
	}
	
	private MbcAgendas gerarHorariosPrevisaoInicioFim(MbcAgendas agenda, MbcAgendas agendaOriginal) throws BaseException{
		Boolean insereAtualizaAgendaEscala = false;
		if ((!DominioSituacaoAgendas.LE.equals(agenda.getIndSituacao())) && (!DominioSituacaoAgendas.CA.equals(agenda.getIndSituacao()))) {
			if (agendaOriginal == null || (CoreUtil.modificados(agenda.getDtAgenda(), agendaOriginal.getDtAgenda()) && agenda.getDtAgenda() != null) ||
				CoreUtil.modificados(agenda.getTempoSala(), agendaOriginal.getTempoSala()) ||
				CoreUtil.modificados(agenda.getUnidadeFuncional(), agendaOriginal.getUnidadeFuncional()) ||
				CoreUtil.modificados(agenda.getEspecialidade(), agendaOriginal.getEspecialidade()) ||
				CoreUtil.modificados(agenda.getProfAtuaUnidCirgs(), agendaOriginal.getProfAtuaUnidCirgs()) ||
				CoreUtil.modificados(agenda.getSalaCirurgica(), agendaOriginal.getSalaCirurgica())) {
			
				agenda.setDthrPrevInicio(null);
				agenda.setDthrPrevFim(null);
				agenda.setOrdemOverbooking(null);
				
				if(agenda.getIndSituacao().equals(DominioSituacaoAgendas.ES)){
					insereAtualizaAgendaEscala = true;
				}
				
				Date dataAgenda = (agenda.getDtAgenda() != null ? agenda.getDtAgenda() : null);
				Short salaSeqp = (agenda.getSalaCirurgica() != null ? agenda.getSalaCirurgica().getId().getSeqp() : null);
				
				agenda = getMbcAgendasHorarioPrevisaoON().gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(agenda.getPucServidor(), 
						agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade(), agenda.getUnidadeFuncional(), dataAgenda, agenda,
						null, salaSeqp, null, null, true, insereAtualizaAgendaEscala);
				
				//quando se insere agenda em situacao ES ajusta-se os horarios das outras agendas do dia de maneira que estas fiquem 
				//após a agenda inserida
				if(insereAtualizaAgendaEscala){
					getMbcAgendasDAO().flush();
					getMbcAgendasHorarioPrevisaoON().ajustarHorariosAgendamentoEmEscala(agenda.getDtAgenda(), agenda.getSalaCirurgica().getId().getSeqp(), agenda.getProfAtuaUnidCirgs(), 
							agenda.getEspecialidade().getSeq(), agenda.getUnidadeFuncional().getSeq(), true);
				}
			}
		}
		return agenda;
	}
	
	public void cancelaCirurgiasAgendadas(Integer agdSeq) throws BaseException {
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		List<MbcCirurgias> eletivas = getMbcCirurgiasDAO().pesquisarCirurgiasEletivasParaAgenda(agdSeq);
		MbcMotivoCancelamento motivo = getBlocoCirurgicoFacade().obterMotivoCancelamentoPorChavePrimaria(Short.valueOf(parametro.getVlrNumerico().toString()));
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!eletivas.isEmpty() && motivo != null){
			for (MbcCirurgias cirurgia : eletivas) {
				cirurgia.setMotivoCancelamento(motivo);			
				cirurgia.setSituacao(DominioSituacaoCirurgia.CANC);
				getBlocoCirurgicoFacade().persistirCirurgia(cirurgia,servidorLogado);
			}	
		}
	}
	 
	protected MbcAgendaDiagnosticoRN getMbcAgendaDiagnosticoRN() {
		return mbcAgendaDiagnosticoRN;
	}
	protected MbcAgendaDiagnosticoDAO getMbcAgendaDiagnosticoDAO() {
		return mbcAgendaDiagnosticoDAO;
	}
	protected MbcAgendasRN getMbcAgendasRN(){
		return mbcAgendasRN;
	}
	protected MbcAgendaProcedimentoON getMbcAgendaProcedimentoON(){
		return mbcAgendaProcedimentoON;
	}
	protected MbcAgendaHemoterapiaON getMbcAgendaHemoterapiaON(){
		return mbcAgendaHemoterapiaON;
	}
	protected MbcAgendaAnestesiaRN getMbcAgendaAnestesiaRN(){
		return mbcAgendaAnestesiaRN;
	}
	protected MbcAgendaSolicEspecialRN getMbcAgendaSolicEspecialRN(){
		return mbcAgendaSolicEspecialRN;
	}
	protected MbcAgendaOrtProteseON getMbcAgendaOrtProteseON(){
		return mbcAgendaOrtProteseON;
	}
	protected MbcAgendasJustificativaRN getMbcAgendasJustificativaRN(){
		return mbcAgendasJustificativaRN;
	}
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return iBlocoCirurgicoPortalPlanejamentoFacade;
	} 
	protected IBlocoCirurgicoOpmesFacade getBlocoIBlocoCirurgicoOpmesFacade() {
		return iBlocoCirurgicoOpmesFacade;
	}
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	protected MbcAgendaAnestesiaDAO getMbcAgendaAnestesiaDAO() {
		return mbcAgendaAnestesiaDAO;
	}
	protected MbcAgendaSolicEspecialDAO getMbcAgendaSolicEspecialDAO(){
		return mbcAgendaSolicEspecialDAO;
	}
	protected MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO(){
		return mbcAgendaProcedimentoDAO;
	}
	protected MbcAgendaOrtProteseDAO getMbcAgendaOrtProteseDAO(){
		return mbcAgendaOrtProteseDAO;
	}
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO(){
		return mbcAgendaHemoterapiaDAO;
	}
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	protected MbcAgendasHorarioPrevisaoON getMbcAgendasHorarioPrevisaoON(){
		return mbcAgendasHorarioPrevisaoON;
	}
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
}
