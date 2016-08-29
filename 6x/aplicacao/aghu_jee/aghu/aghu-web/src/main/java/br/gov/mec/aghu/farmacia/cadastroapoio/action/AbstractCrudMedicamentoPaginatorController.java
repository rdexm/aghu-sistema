package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public abstract class AbstractCrudMedicamentoPaginatorController<E extends BaseBean> extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = 3038351065821700543L;
	private AfaMedicamento medicamento;
	private Boolean exibirIncluir;
	private Boolean fromList;
	
	private E entidadeSelecionada;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<E> dataModel;
	
	public List<AfaMedicamento> pesquisarMedicamentos(Object strObject) {	
		return farmaciaFacade.obterMedicamentos(strObject, Boolean.TRUE);
	}

	public Integer pesquisarMedicamentosCount(Object strObject) {
		return farmaciaFacade.obterMedicamentosCount(strObject, Boolean.TRUE);
	}
	
	public String editar(){
		getCrudController().setMedicamento(getMedicamento());
		getCrudController().setEntidade(getEntidadeSelecionada());
		getCrudController().setEdicao(Boolean.TRUE);
		return paginaCrud();
	}
	
	public String novo(){
		getCrudController().setMedicamento(getMedicamento());
		getCrudController().instanciarEntidade();
		getCrudController().setEdicao(Boolean.FALSE);
		return paginaCrud();
	}
	
	protected abstract String paginaHist();

	@PostConstruct
	public void iniciarPagina() {
		begin(conversation);
		dataModel.reiniciarPaginator();
	}
		
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		this.setExibirIncluir(Boolean.TRUE);
	}

	public void limparPesquisa() {
		
		this.setMedicamento(null);
		this.setExibirIncluir(Boolean.FALSE);
	}

	public AfaMedicamento getMedicamento() {
		return this.medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		
		this.medicamento = medicamento;
	}
	
	public Boolean getExibirIncluir() {
		
		return this.exibirIncluir;
	}
	
	public void setExibirIncluir(Boolean exibirIncluir) {
		
		this.exibirIncluir = exibirIncluir;
	}

	public Boolean getFromList() {
		
		return this.fromList;
	}
	
	public void setFromList(Boolean fromList) {
		
		this.fromList = fromList;
	}

	public DynamicDataModel<E> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<E> dataModel) {
		this.dataModel = dataModel;
	}

	public E getEntidadeSelecionada() {
		return entidadeSelecionada;
	}

	public void setEntidadeSelecionada(E entidadeSelecionada) {
		this.entidadeSelecionada = entidadeSelecionada;
	}
	
	protected abstract AbstractCrudMedicamentoController getCrudController();
	
	protected abstract String paginaCrud();

	
}
