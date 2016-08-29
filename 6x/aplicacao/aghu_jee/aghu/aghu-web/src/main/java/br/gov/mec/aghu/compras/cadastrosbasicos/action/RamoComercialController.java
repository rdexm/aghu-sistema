package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RamoComercialController extends ActionController {

	private static final long serialVersionUID = -4087250476079119943L;

	private static final String RAMO_COMERCIAL_LIST = "ramoComercialList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private Short codigo;
	
	private ScoRamoComercial ramoComercial;
	
	private boolean readonly;
	
	private boolean update;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Inicia o controller, carregando um ramo comercial já cadastrado, se for alteração;
	 * ou instanciando um novo ramo comercial, caso contrário.
	 */
	public String iniciar() {
	 

	 

		setUpdate(getCodigo() != null);
		
		if (isUpdate()) {
			ramoComercial = comprasCadastrosBasicosFacade.obterScoRamoComercial(getCodigo());
			
			if(ramoComercial == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			ramoComercial = new ScoRamoComercial();
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			
			comprasCadastrosBasicosFacade.persistir(getRamoComercial());
			
			if (isUpdate()) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RAMO_COMERCIAL_UPDATE_SUCESSO", getRamoComercial().getDescricao());
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RAMO_COMERCIAL_INSERT_SUCESSO", getRamoComercial().getDescricao());
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		codigo = null;
		ramoComercial = null;
		return RAMO_COMERCIAL_LIST;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public ScoRamoComercial getRamoComercial() {
		return ramoComercial;
	}

	public void setRamoComercial(ScoRamoComercial ramoComercial) {
		this.ramoComercial = ramoComercial;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	private boolean isUpdate() {
		return update;
	}

	private void setUpdate(boolean update) {
		this.update = update;
	}
}
