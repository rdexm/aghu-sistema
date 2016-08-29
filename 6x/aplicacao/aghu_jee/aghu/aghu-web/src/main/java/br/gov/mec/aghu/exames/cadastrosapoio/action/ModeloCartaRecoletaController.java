package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ModeloCartaRecoletaController extends ActionController {

	private static final long serialVersionUID = 5014686465596284374L;

	private static final String MODELO_CARTA_RECOLETA_LIST = "modeloCartaRecoletaList";
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;	
	
	private AelModeloCartas modeloCarta;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (modeloCarta != null && modeloCarta.getSeq() != null) {
			modeloCarta = cadastrosApoioExamesFacade.obterModeloCartaPorId(modeloCarta.getSeq());

			if(modeloCarta == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			modeloCarta = new AelModeloCartas();
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			
			// Persiste o modelo carta
			cadastrosApoioExamesFacade.persistirModeloCarta(modeloCarta);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_MODELO_CARTA_RECOLETA", modeloCarta.getNome());
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		modeloCarta = null;
		return MODELO_CARTA_RECOLETA_LIST;
	}

	public AelModeloCartas getModeloCarta() {
		return modeloCarta;
	}

	public void setModeloCarta(AelModeloCartas modeloCarta) {
		this.modeloCarta = modeloCarta;
	}
}