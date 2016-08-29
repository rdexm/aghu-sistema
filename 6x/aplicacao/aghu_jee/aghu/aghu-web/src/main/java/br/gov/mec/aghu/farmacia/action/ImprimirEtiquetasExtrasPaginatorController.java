package br.gov.mec.aghu.farmacia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class ImprimirEtiquetasExtrasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 810919466393702727L;

	@Inject
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private IComprasFacade comprasFacade;
	
	private SceLoteDocImpressao entidadePesquisa;
	
	private Boolean refreshPesquisa;
	
	@Inject @Paginator
	private DynamicDataModel<SceLoteDocImpressao> dataModel;
	
	private Integer pGrFarmIndustrial;
	private Integer pGrMatMedic;

	@EJB
	private IParametroFacade parametroFacade;
	
	@PostConstruct
	public void iniciarPagina(){
		begin(conversation);
		if(entidadePesquisa==null){
			entidadePesquisa = new SceLoteDocImpressao();
		}
		//getDataModel().reiniciarPaginator();
		if(pGrFarmIndustrial == null || pGrMatMedic == null){
			pGrFarmIndustrial = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_FARM_INDUSTRIAL.toString()).intValue();
			pGrMatMedic = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_MAT_MEDIC.toString()).intValue();
		}
	}
	
	public List<ScoMarcaComercial> pesquisarMarcas(String parametro){
		try {
			return this.comprasFacade.getListaMarcasByNomeOrCodigo(parametro, 0, 100, null, true);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public List<ScoMaterial> pesquisarMateriais(String objPesquisa){
		List<ScoMaterial> lista = this.comprasFacade.listarScoMateriaisPorGrupoEspecifico(objPesquisa, 0, 100, null, true, pGrFarmIndustrial, pGrMatMedic);    
		return lista;
	}
	
	@Override
	public List<SceLoteDocImpressao> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.estoqueFacade.efetuarPesquisaUnitarizacaoDeMedicamentosComEtiqueta(entidadePesquisa, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.efetuarPesquisaUnitarizacaoDeMedicamentosComEtiquetaCount(entidadePesquisa);
	}
	
	public void efetuarPesquisa(){
		getDataModel().reiniciarPaginator();
	}
	
	public void limpar(){
		entidadePesquisa = new SceLoteDocImpressao();
		getDataModel().setPesquisaAtiva(Boolean.FALSE);
	}
	
	public String novo(){
		return "imprimirEtiquetasExtras";
	}
	
	public Long pesquisarMateriaisCount(Object objPesquisa){
		return this.comprasFacade.listarScoMateriaisPorGrupoEspecificoCount(objPesquisa, pGrFarmIndustrial, pGrMatMedic);
	}
	
	public Long pesquisarMarcasCount(Object objPesquisa){
		return this.comprasFacade.getListaMarcasByNomeOrCodigoCount(objPesquisa);
	}

	public SceLoteDocImpressao getEntidadePesquisa() {
		return entidadePesquisa;
	}

	public void setEntidadePesquisa(SceLoteDocImpressao entidadePesquisa) {
		this.entidadePesquisa = entidadePesquisa;
	}

	public Boolean getRefreshPesquisa() {
		return refreshPesquisa;
	}

	public void setRefreshPesquisa(Boolean refreshPesquisa) {
		this.refreshPesquisa = refreshPesquisa;
	}

	public DynamicDataModel<SceLoteDocImpressao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceLoteDocImpressao> dataModel) {
		this.dataModel = dataModel;
	}

}
