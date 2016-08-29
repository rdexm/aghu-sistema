package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.CadastroTipoOcorrenciaFornecedorVO;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class CadastroTipoOcorrenciaFornecedorPaginatorController extends ActionController implements ActionPaginator {

	private static final String INCLUIR_TIPO_OCORRENCIA_FORNECEDOR = "incluirTipoOcorrenciaFornecedor";

	@Inject @Paginator
	private DynamicDataModel<ScoTipoOcorrForn> dataModel;

	private static final long serialVersionUID = -7781706374954598794L;

	@EJB
	private IComprasFacade comprasFacade;
	
	private CadastroTipoOcorrenciaFornecedorVO filtro;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		
		
		filtro = new CadastroTipoOcorrenciaFornecedorVO();
		//this.//setIgnoreInitPageConfig(true);
		
		if(this.isAtivo()){
			this.reiniciarPaginator();
		}
	
	}
	
	
	public void limpar(){
		filtro = new CadastroTipoOcorrenciaFornecedorVO();
		setAtivo(Boolean.FALSE);
	}
	
	public String novo(){
		return this.redirecionarIncluirTipoOcorrenciaFornecedor();
	}
	
	public void pesquisar(){
		this.reiniciarPaginator();
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return comprasFacade.buscarTipoOcorrenciaFornecedorCount(filtro);
	}

	@Override
	public List<ScoTipoOcorrForn> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return comprasFacade.buscarTipoOcorrenciaFornecedor(firstResult, maxResults, orderProperty, asc, filtro);
	}
	
	public String redirecionarIncluirTipoOcorrenciaFornecedor(){
		return INCLUIR_TIPO_OCORRENCIA_FORNECEDOR;
	}

	public void setFiltro(CadastroTipoOcorrenciaFornecedorVO filtro) {
		this.filtro = filtro;
	}

	public CadastroTipoOcorrenciaFornecedorVO getFiltro() {
		return filtro;
	}
 
	public DynamicDataModel<ScoTipoOcorrForn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoTipoOcorrForn> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
