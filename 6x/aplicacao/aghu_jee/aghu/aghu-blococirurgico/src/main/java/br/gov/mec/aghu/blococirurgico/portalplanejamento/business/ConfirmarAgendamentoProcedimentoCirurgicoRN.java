package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.AghWFTemplateEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.InformacaoAgendaVO;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * 
 * Classe que implementa as regras de negócio da estória #31975.
 * @author Paulo Silveira
 *
 */
@Stateless
public class ConfirmarAgendamentoProcedimentoCirurgicoRN extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(ConfirmarAgendamentoProcedimentoCirurgicoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AghWFTemplateEtapaDAO aghWFTemplateEtapaDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoOpmesFacade iBlocoCirurgicoOpmesFacade;

	private static final long serialVersionUID = 5207249640534235620L;	
	
	
	public Boolean isConvenioPacienteSUS(MbcAgendas agenda) {
		agenda = getMbcAgendasDAO().obterPorChavePrimaria(agenda.getSeq());
		AghParametros convenioSusPadrao = getParametroFacade().getAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		AghParametros susPlanoInternacao = getParametroFacade().getAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		InformacaoAgendaVO vo = obterInformacaoAgendaVO(agenda, agenda.getPaciente(), agenda.getUnidadeFuncional(), convenioSusPadrao, susPlanoInternacao);
		return (vo.getCspCnvCodigo() == vo.getCnvSus() && vo.getCspSeq() == vo.getCspIntSeq());
	}

	private InformacaoAgendaVO obterInformacaoAgendaVO(MbcAgendas agenda, AipPacientes paciente, AghUnidadesFuncionais unidadeFuncional, AghParametros convenioSusPadrao, AghParametros susPlanoInternacao) {
		InformacaoAgendaVO vo = new InformacaoAgendaVO();
		vo.setSeq(agenda.getSeq());
		vo.setDtAgenda(agenda.getDtAgenda());
		vo.setIndSituacao(agenda.getIndSituacao());
		vo.setPacCodigo(paciente.getCodigo());
		vo.setEprPciSeq(agenda.getProcedimentoCirurgico().getSeq());
		vo.setEprEspSeq(agenda.getEspProcCirgs().getId().getEspSeq());
		vo.setCspCnvCodigo(agenda.getFatConvenioSaude() == null ? convenioSusPadrao.getVlrNumerico().shortValue() : agenda.getFatConvenioSaude().getCodigo());
		vo.setCspSeq(agenda.getConvenioSaudePlano() == null ? susPlanoInternacao.getVlrNumerico().byteValue() : agenda.getConvenioSaudePlano().getId().getSeq());
		vo.setCnvSus(convenioSusPadrao.getVlrNumerico().shortValue());
		vo.setCspIntSeq(susPlanoInternacao.getVlrNumerico().byteValue());
		Date dtBase = DateUtil.adicionaDias(agenda.getDthrInclusao(), unidadeFuncional.getQtdDiasLimiteCirg().intValue());
		vo.setDtBase(dtBase);
		vo.setQtdDiasLimiteCirg(unidadeFuncional.getQtdDiasLimiteCirg().intValue());
		
		return vo;
	}

	public AgendamentoProcedimentoCirurgicoVO verificarRequisicaoOPMEs(MbcAgendas agenda) throws BaseException {
		List<MbcRequisicaoOpmes> requisicoes = getMbcRequisicaoOpmesDAO().consultarListaRequisicoesPorAgenda(agenda.getSeq());
		agenda = mbcAgendasDAO.obterPorChavePrimaria(agenda.getSeq());
		if (requisicoes == null	
				|| requisicoes.isEmpty() 
				|| !verificarSeTemOpmeSelecionada(requisicoes.get(0)
						.getItensRequisicao())) {
			AghParametros dias = getParametroFacade().getAghParametro(AghuParametrosEnum.P_AGHU_PRAZO_PLAN_CIRURGIA);
			if(dias != null){
				return new AgendamentoProcedimentoCirurgicoVO("MSG_REQ_DEVE_SER_REALIZADA", false, false, dias.getVlrNumerico().toString());
			}else{
				return new AgendamentoProcedimentoCirurgicoVO("MSG_REQ_DEVE_SER_REALIZADA", false, false, null);
			}
			
//			return new AgendamentoProcedimentoCirurgicoVO();
		} else {
			MbcRequisicaoOpmes requisicao = requisicoes.get(0);
			
			if (DominioSituacaoRequisicao.COMPATIVEL.equals(requisicao.getSituacao())) {
				return new AgendamentoProcedimentoCirurgicoVO("MSG_REQ_COMPATIVEL", false, true, null);
			} else {
				//40464
				if (requisicao.getFluxo() != null)  {
					if(isRequisicaoEmAndamento(requisicao)) {
						AghWFTemplateEtapa templateEtapa = getAghWFTemplateEtapaDAO().consultarDescricaoTemplateEtapa(requisicao.getSeq());
						String descricaoEtapa = "";
						if(templateEtapa != null) {
							descricaoEtapa = templateEtapa.getDescricao(); 
						}
						return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_EM_ANDAMENTO", true, false, descricaoEtapa);
					} else if (DominioSituacaoRequisicao.AUTORIZADA.equals(requisicao.getSituacao())) {
						return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_AUTORIZADO", false, true, null);
						
					} else if (DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicao.getSituacao())) { 
						return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_NAO_AUTORIZADO", true, false, null);
					}
				} else {
					return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_SERA_ACIONADO", false, false, null);
				}
				
	//				if (requisicao.getFluxo() == null) {
	//				getBlocoCirurgicoOpmesFacade().iniciarFluxoAutorizacaoOPMEs(servidorLogadoFacade.obterServidorLogado(), requisicao);
	//				return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_SERA_ACIONADO", false, false, null);
	//			} else {
	//				if(isRequisicaoEmAndamento(requisicao)) {
	//					AghWFTemplateEtapa templateEtapa = getAghWFTemplateEtapaDAO().consultarDescricaoTemplateEtapa(requisicao.getSeq());
	//					String descricaoEtapa = "";
	//					if(templateEtapa != null) {
	//						descricaoEtapa = templateEtapa.getDescricao(); 
	//					}
	//					return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_EM_ANDAMENTO", true, false, descricaoEtapa);
	//				} else if (DominioSituacaoRequisicao.AUTORIZADA.equals(requisicao.getSituacao())) {
	//					return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_AUTORIZADO", false, true, null);
	//					
	//				} else if (DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicao.getSituacao())) { 
	//					return new AgendamentoProcedimentoCirurgicoVO("MSG_PROC_AUTO_REQ_NAO_AUTORIZADO", true, false, null);
	//				}
	//			}
			}
		}
		

		return new AgendamentoProcedimentoCirurgicoVO();
	}	
	
	private boolean isRequisicaoEmAndamento(MbcRequisicaoOpmes requisicao) {
		return (!DominioSituacaoRequisicao.INCOMPATIVEL.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.COMPATIVEL.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.AUTORIZADA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.CONCLUIDA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.CANCELADA.equals(requisicao.getSituacao()));

	}
	
	// Referente ao TOTAL_ITENS e CONT_ITENS_SEL da C02_CONS_REQ
	private boolean verificarSeTemOpmeSelecionada(List<MbcItensRequisicaoOpmes> itens) {
		int totalItens;
		int totalItensSelecionados = 0;
		if (itens == null || itens.isEmpty()) {
			return true;
		}

		totalItens = itens.size();
		for (MbcItensRequisicaoOpmes item : itens) {
			if (item.getRequerido() != DominioRequeridoItemRequisicao.NRQ) {
				totalItensSelecionados++;
			}
		}
		
		// Valida se tem OPME selecionada
		if (totalItens > 0 && totalItensSelecionados == 0) {
			return false;
		}	
		return true;
	}

	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected IBlocoCirurgicoOpmesFacade getBlocoCirurgicoOpmesFacade() {
		return this.iBlocoCirurgicoOpmesFacade;	
	}	
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO(){
		return mbcRequisicaoOpmesDAO;
	}
	
	protected AghWFTemplateEtapaDAO getAghWFTemplateEtapaDAO() {
		return aghWFTemplateEtapaDAO;
	}

}
