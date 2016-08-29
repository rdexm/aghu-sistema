package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RamalTelefonicoController extends ActionController {

	private static final long serialVersionUID = 1199111795950135592L;

	private static final String PESQUISAR_RAMAL_TELEFONICO = "pesquisarRamalTelefonico";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapRamalTelefonico rapRamalTelefonico;

	private boolean altera;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public String iniciar() {
	 

		
		if (rapRamalTelefonico != null && rapRamalTelefonico.getNumero() != null) {
			try {
				altera = true;
				rapRamalTelefonico = cadastrosBasicosFacade.obterRamalTelefonico(rapRamalTelefonico.getNumero());
				if(rapRamalTelefonico == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else {
			rapRamalTelefonico = new RapRamalTelefonico();
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			cadastrosBasicosFacade.salvar(rapRamalTelefonico, altera);
			
			if (altera) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_RAMAL");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_RAMAL");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return cancelar();
	}

	public String cancelar() {
		rapRamalTelefonico = new RapRamalTelefonico();
		altera = false;
		return PESQUISAR_RAMAL_TELEFONICO;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setRapRamalTelefonico(RapRamalTelefonico rapRamalTelefonico) {
		this.rapRamalTelefonico = rapRamalTelefonico;
	}

	public RapRamalTelefonico getRapRamalTelefonico() {
		return rapRamalTelefonico;
	}
}