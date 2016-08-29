package br.gov.mec.aghu.paciente.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


public class ReindexarPacientesController extends ActionController {

	private static final long serialVersionUID = -5294164105303203012L;

	private static final String REINDEXANDO_PACIENTES = "reindexandoPacientes";
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void reindexarPacientes() {
		try {
			
			if(!reindexando()){
				setaReindexacao(Boolean.TRUE);
				pacienteFacade.reindexarPacientes();
				setaReindexacao(Boolean.FALSE);
			}
			
		} catch (BaseException e) {
			setaReindexacao(Boolean.FALSE);
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void setaReindexacao(Boolean reindexando){
		FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(REINDEXANDO_PACIENTES, reindexando);
	}
	
	private boolean reindexando() {
		Boolean result = Boolean.class.cast(FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(REINDEXANDO_PACIENTES));
		
		if(result == null){
			return false;
		} else {
			return result;
		}
	}
	
}
