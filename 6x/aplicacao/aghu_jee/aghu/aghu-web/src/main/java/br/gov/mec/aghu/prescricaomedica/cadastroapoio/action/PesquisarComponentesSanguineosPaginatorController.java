package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarComponentesSanguineosPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final long serialVersionUID = 6011824563325989071L;

	private static final String PAGE_CRUD = "manterComponentesSanguineos";
	
	@Inject @Paginator
	private DynamicDataModel<AbsComponenteSanguineo> dataModel;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	private AbsComponenteSanguineo componenteSanguineo = new AbsComponenteSanguineo();
	
	private AbsComponenteSanguineo componenteSanguineoSelecionado;
	
	public String editar(){
		return PAGE_CRUD;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		componenteSanguineo = new AbsComponenteSanguineo();
		componenteSanguineoSelecionado = null;
		dataModel.limparPesquisa();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return bancoDeSangueFacade.listaComponentesSanguineos(firstResult, maxResult, orderProperty, asc, this.componenteSanguineo);
	}
	
	public Long recuperarCount() {
		return bancoDeSangueFacade.pesquisarComponentesSanguineosCount(this.componenteSanguineo);
	}
	
	public String inserirNovo(){
			return PAGE_CRUD;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	} 

	public DynamicDataModel<AbsComponenteSanguineo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AbsComponenteSanguineo> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsComponenteSanguineo getComponenteSanguineoSelecionado() {
		return componenteSanguineoSelecionado;
	}

	public void setComponenteSanguineoSelecionado(
			AbsComponenteSanguineo componenteSanguineoSelecionado) {
		this.componenteSanguineoSelecionado = componenteSanguineoSelecionado;
	}
}