package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.ScoRamoComercialCriteriaVO;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class RamoComercialPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3456087625126644525L;

	private static final String RAMO_COMERCIAL_CRUD = "ramoComercialCRUD";
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private Short codigo;
	private ScoRamoComercialCriteriaVO criteria = new ScoRamoComercialCriteriaVO();

	@Inject @Paginator
	private DynamicDataModel<ScoRamoComercial> dataModel;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limpar() {
		dataModel.limparPesquisa();
		setCriteria(new ScoRamoComercialCriteriaVO());
	}
	
	public String inserir() {
		return RAMO_COMERCIAL_CRUD;
	}
	
	public String visualizar() {
		return RAMO_COMERCIAL_CRUD;
	}
	
	public String editar() {
		return RAMO_COMERCIAL_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.contarScoRamosComerciais(criteria);
	}

	@Override
	public List<ScoRamoComercial> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarScoRamosComerciais(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public ScoRamoComercialCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(ScoRamoComercialCriteriaVO c) {
		criteria = c;
	}

	public DynamicDataModel<ScoRamoComercial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoRamoComercial> dataModel) {
		this.dataModel = dataModel;
	}
}
