package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business.IParecerCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class AgrupamentoMaterialPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4386431856770866957L;

	private static final String AGRUPAMENTO_MATERIAL_CRUD = "agrupamentoMaterialCRUD";

	@EJB
	private IParecerCadastrosBasicosFacade parecerCadastrosBasicosFacade;
	
	private ScoAgrupamentoMaterial agrupMaterial = new ScoAgrupamentoMaterial();

	@Inject @Paginator
	private DynamicDataModel<ScoAgrupamentoMaterial> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return parecerCadastrosBasicosFacade.listarAgrupamentoMaterialCount(agrupMaterial);
	}

	@Override
	public List<ScoAgrupamentoMaterial> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return parecerCadastrosBasicosFacade.listarAgrupamentoMaterial(firstResult, maxResult, orderProperty, asc, agrupMaterial);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setAgrupMaterial(new ScoAgrupamentoMaterial());
		dataModel.limparPesquisa();
	}
	
	public String inserir() {
		return AGRUPAMENTO_MATERIAL_CRUD;
	}
	
	public String editar() {
		return AGRUPAMENTO_MATERIAL_CRUD;
	}
	
	public String visualizar() {
		return AGRUPAMENTO_MATERIAL_CRUD;
	}
	
	public ScoAgrupamentoMaterial getAgrupMaterial() {
		return agrupMaterial;
	}

	public void setAgrupMaterial(ScoAgrupamentoMaterial agrupMaterial) {
		this.agrupMaterial = agrupMaterial;
	}

	public DynamicDataModel<ScoAgrupamentoMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAgrupamentoMaterial> dataModel) {
		this.dataModel = dataModel;
	}
}