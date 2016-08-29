package br.gov.mec.aghu.cups.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.model.cups.ImpUsuarioVisualiza;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class AtivaVisualizaCupsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
		this.ativarModal = false;
		try {
			ImpUsuarioVisualiza impUsuarioVisualiza = cupsFacade
					.buscarModalAtivoCups(cascaService.recuperarUsuario(this.obterLoginUsuarioLogado()));

			if (impUsuarioVisualiza != null) {
				this.ativarModal = true;
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ERRO_ATIVAR_DESATIVAR_USUARIO_MODAL");
		}
	}

	private static final Log LOG = LogFactory.getLog(AtivaVisualizaCupsController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -970350706922031063L;

	@EJB
	private ICupsFacade cupsFacade;

	@EJB
    private ICascaFacade cascaService;

	private boolean ativarModal;

	public void inicio() {

		// Valores iniciais.

	
	}

	public void salvar() throws ApplicationBusinessException {
		try {
			
			

			ImpUsuarioVisualiza impUsuarioVisualiza = cupsFacade
					.buscarModalAtivoCups(cascaService.recuperarUsuario(this.obterLoginUsuarioLogado()));

			if (this.ativarModal) {

				if (impUsuarioVisualiza ==null) {
					ImpUsuarioVisualiza item = new ImpUsuarioVisualiza();
					
					item.setUsuario(cascaService.recuperarUsuario(this.obterLoginUsuarioLogado()));
					item.setDataInclusao(new Date());
					cupsFacade.incluirUsuarioVisualiza(item);
				}
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ATIVAR_USUARIO_MODAL");
			} else {
				if (impUsuarioVisualiza != null) {
					cupsFacade.excluirUsuarioVisualiza(impUsuarioVisualiza);
				}
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_DESATIVAR_USUARIO_MODAL");
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			//return "erro";
		}

		//return "salvar";
	}

	public String cancelar() {
		return "cancelado";
	}

	// GETTERS e SETTERS

	public boolean isAtivarModal() {
		return ativarModal;
	}

	public void setAtivarModal(boolean ativarModal) {
		this.ativarModal = ativarModal;
	}
}