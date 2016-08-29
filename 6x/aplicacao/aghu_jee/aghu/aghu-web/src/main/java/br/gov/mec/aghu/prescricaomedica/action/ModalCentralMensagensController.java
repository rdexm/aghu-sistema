package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.action.EnviarInformacaoPrescribenteController;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.CentralMensagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas operações na modal Central de Mensagens.
 *
 */
public class ModalCentralMensagensController extends ActionController {

	private static final long serialVersionUID = 6372973396272585240L;

	 private static final String NAV_CONSULTORIA = "prescricaomedica-consultarRetornoConsultoria";
	
	 private static final String NAV_PRESCRIBENTE = "farmacia-visualizarInformacaoPrescribente";
	 
	 private static final String PAGE_DETALHA_PARECER_MEDICAMENTOS = "prescricaomedica-detalhaParecerMedicamentos";
	 							 

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade; 
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private boolean showModal;

	private Integer atdSeq;

	private List<CentralMensagemVO> listaMensagens;
	
	@Inject
	private ConsultarRetornoConsultoriaController consultarRetornoConsultoriaController;
	
	@Inject
	private EnviarInformacaoPrescribenteController enviarInformacaoPrescribenteController;
	
	@Inject
	private DetalhaParecerMedicamentosController detalhaParecerMedicamentosController;

	/**
	 * Método responsável por iniciar o modal de Central de Mensagens.
	 */
	public void iniciarModal() {

		listaMensagens = prescricaoMedicaFacade.listarMensagensPendentes(atdSeq);

		if (!listaMensagens.isEmpty()) {
			showModal = true;
		}else{
			showModal = false;
		}
			
	}

	/**
	 * Redireciona para tela de detalhamento, conforme tipo da mensagem.
	 * 
	 * @param selecionado - Mensagem selecionada
	 * 
	 * @return Navegação para tela de detalhamento
	 */
	public String redirecionarMensagem(CentralMensagemVO selecionado) throws BaseException {

		if (selecionado != null && selecionado.getEntidade() != null) {
			if (selecionado.getEntidade() instanceof MpmSolicitacaoConsultoria) {
				MpmSolicitacaoConsultoria consultoria = (MpmSolicitacaoConsultoria) selecionado.getEntidade();
				prescricaoMedicaFacade.atualizarVisualizacaoConsultoria(consultoria);

				consultarRetornoConsultoriaController.setParamConsultoria((MpmSolicitacaoConsultoria)selecionado.getEntidade());
				return NAV_CONSULTORIA;
			}
			if (selecionado.getEntidade() instanceof ParecerPendenteVO) {
				ParecerPendenteVO parecerPendenteVO = (ParecerPendenteVO)selecionado.getEntidade();
				prescricaoMedicaFacade.atualizarVisualizacaoParecer(atdSeq, parecerPendenteVO);
				
				detalhaParecerMedicamentosController.setParecerSeq(parecerPendenteVO.getSeq());
				detalhaParecerMedicamentosController.setCameFrom("PAGE_MANTER_PRESCRICAO_MEDICA");
				
//				apresentarMsgNegocio(Severity.INFO, "MSG_PESQ_PARECER_AGHWEB");
				detalhaParecerMedicamentosController.obterParecerMedicamento();
				
				return PAGE_DETALHA_PARECER_MEDICAMENTOS;
			}
			if (selecionado.getEntidade() instanceof MpmInformacaoPrescribente) {
				MpmInformacaoPrescribente informacaoPrescribente = (MpmInformacaoPrescribente) selecionado.getEntidade();
				prescricaoMedicaFacade.atualizarVisualizacaoInformacaoPrescribente(informacaoPrescribente, servidorLogadoFacade.obterServidorLogado());
				enviarInformacaoPrescribenteController.setCodigoRequest(informacaoPrescribente.getSeq());
				enviarInformacaoPrescribenteController.setModoVisualizacao(true);
				enviarInformacaoPrescribenteController.setModoEdicao(false);
				enviarInformacaoPrescribenteController.setTransicaoTela(true);
				enviarInformacaoPrescribenteController.setTelaOrigem("/pages/prescricaomedica/manterPrescricaoMedica.xhtml");
				
				return NAV_PRESCRIBENTE;
			}
		}

		return null;
	}

    /**
     * #47639
     *
     * Usei as regras que ja estavam implementadas e nao removi o
     * metodo 'redirecionarMensagem', porque ele provavelmente será reativado no futuro #oremos
     *
     *
     */
//    public String buscaFormAghWeb(CentralMensagemVO selecionado) {
//
//    	try {
//	        if (selecionado != null && selecionado.getEntidade() != null) {
//	            if (selecionado.getEntidade() instanceof MpmSolicitacaoConsultoria) {
//					MpmSolicitacaoConsultoria consultoria = (MpmSolicitacaoConsultoria) selecionado.getEntidade();
//					prescricaoMedicaFacade.atualizarVisualizacaoConsultoria(consultoria);
//	            	
//	                consultarRetornoConsultoriaController.setParamConsultoria((MpmSolicitacaoConsultoria)selecionado.getEntidade());
//	                return "mpmf_atcon_ret_cnst.fmx";
//	            }
//	            if (selecionado.getEntidade() instanceof ParecerPendenteVO) {
//					prescricaoMedicaFacade.atualizarVisualizacaoParecer(atdSeq);
//	                return "mpmf_pcer_autom_elab.fmx";
//	            }
//	            if (selecionado.getEntidade() instanceof MpmInformacaoPrescribente) {
//	            	MpmInformacaoPrescribente informacaoPrescribente = (MpmInformacaoPrescribente) selecionado.getEntidade();
//					prescricaoMedicaFacade.atualizarVisualizacaoInformacaoPrescribente(informacaoPrescribente, servidorLogadoFacade.obterServidorLogado());
//	                return "mpmf_inf_autom_elab.fmx";
//	            }
//	        }
//        } catch (BaseException e) {
//        	apresentarExcecaoNegocio(e);
//        }
//    
//        return null;
//    }

	/**
	 * Fecha o modal.
	 */
	public void hideModal() {

		showModal = false;
	}

	public boolean isShowModal() {
		return showModal;
	}

	public void setShowModal(boolean showModal) {
		this.showModal = showModal;
	}

	public List<CentralMensagemVO> getListaMensagens() {
		return listaMensagens;
	}

	public void setListaMensagens(List<CentralMensagemVO> listaMensagens) {
		this.listaMensagens = listaMensagens;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

}
