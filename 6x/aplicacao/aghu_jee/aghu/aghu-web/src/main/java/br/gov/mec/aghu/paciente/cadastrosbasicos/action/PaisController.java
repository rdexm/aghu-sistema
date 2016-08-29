package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PaisController extends ActionController {

	private static final long serialVersionUID = 7225815736873434484L;
	
	private static final String REDIRECIONA_LISTAR_PAIS = "paisList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipPaises pais;

	private boolean desabilitarPaisSigla;

	public String inicio() {
	 

		desabilitarPaisSigla = true;
		
		if(pais != null && pais.getSigla() != null){
			pais = cadastrosBasicosPacienteFacade.obterPaisPorSigla(pais.getSigla());
			
			if(pais == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
					
		} else {
			pais = new AipPaises();
			desabilitarPaisSigla = false;
		}
		return null;
	
	}

	public String salvar() {
		
		try{
			cadastrosBasicosPacienteFacade.persistirPais(pais, desabilitarPaisSigla);			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_PAIS", this.pais.getNome());			
			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	public String cancelar() {
		pais = null;
		return REDIRECIONA_LISTAR_PAIS;
	}


	public AipPaises getPais() {
		return pais;
	}

	public void setPais(AipPaises paises) {
		this.pais = paises;
	} 

	public boolean isDesabilitarPaisSigla() {
		return desabilitarPaisSigla;
	}

	public void setDesabilitarPaisSigla(boolean desabilitarPaisSigla) {
		this.desabilitarPaisSigla = desabilitarPaisSigla;
	}
}