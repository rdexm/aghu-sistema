package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.AFPFornecedoresVO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class PesquisaProgEntregaFornecedorPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AFPFornecedoresVO> dataModel;
	
	

	private static final Log LOG = LogFactory.getLog(PesquisaProgEntregaFornecedorPaginatorController.class);
	
	private static final long serialVersionUID = -3735240336688571771L;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	private AcessoFornProgEntregaFiltrosVO filtro = new AcessoFornProgEntregaFiltrosVO();
	
	private static final String CONSULTAR_PARCELAS_AF_PEND_ENTREGA = "consultarParcelasAFPendEntrega";
	
	
	private String fromCompradorAF = "";
	private boolean primeiraConsulta = true;
	
	private AFPFornecedoresVO selecionado = new AFPFornecedoresVO();
	
	private	VScoFornecedor sugFornecedorAF; 
	
	public void iniciar() {
	 

	 

		if ("Sim".equals(fromCompradorAF) && primeiraConsulta) {
			pesquisar();
			this.primeiraConsulta = false;
		}
	
	}
	

	@Override
	public Long recuperarCount() {
		return autFornecimentoFacade.listProgEntregaFornecedorCount(this.filtro);
	}

	@Override
	public List<AFPFornecedoresVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return autFornecimentoFacade.listProgEntregaFornecedor(this.filtro, false, firstResult, maxResult);
	}

	public void pesquisar(){
		try {
			autFornecimentoFacade.validaFiltrosProgEntregaFornecedor(this.filtro);
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void limpar() {
		this.filtro.setComplemento(null);
		this.filtro.setDataAcessoFinal(null);
		this.filtro.setDataAcessoInicial(null);
		this.filtro.setDataPublicacaoInicial(null);
		this.filtro.setDataPublicacaoFinal(null);
		this.filtro.setFornecedor(null);
		this.filtro.setNumeroAF(null);
		this.filtro.setNumeroAFP(null);
		this.filtro.setPublicacao(null);
		this.dataModel.limparPesquisa();
	}
	
	public void atualizarFiltros(){
		if(this.filtro.getNumeroAF() == null){
			this.filtro.setNumeroAFP(null);
			this.filtro.setComplemento(null);
		}
	}
	
	public String obterFornecedor(String fornecedor, boolean isTruncate){
		String retorno = "";
		if(fornecedor != null){
			retorno = avaliarFornecedor(fornecedor, isTruncate);
		}
		return retorno;
	}
	
	public String avaliarFornecedor(String fornecedor, boolean isTruncate){
		if(isTruncate && fornecedor.length() > 50){
			return obterFornecedorTruncado(fornecedor);
		}
		return fornecedor;
	}
	
	public String obterFornecedorTruncado(String fornecedor){
		return fornecedor.substring(0, 50) + "...";
	}
	
	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro, 100),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}
	
	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.contarVFornecedorPorCgcCpfRazaoSocial(parametro);
	}
	
	public String voltar() {
		primeiraConsulta = true;
		return CONSULTAR_PARCELAS_AF_PEND_ENTREGA;
	}

	public void setFiltro(AcessoFornProgEntregaFiltrosVO filtro) {
		this.filtro = filtro;
	}

	public AcessoFornProgEntregaFiltrosVO getFiltro() {
		return filtro;
	}

	public void setFromCompradorAF(String fromCompradorAF) {
		this.fromCompradorAF = fromCompradorAF;
	}

	public String getFromCompradorAF() {
		return fromCompradorAF;
	}

	public boolean isPrimeiraConsulta() {
		return primeiraConsulta;
	}

	public void setPrimeiraConsulta(boolean primeiraConsulta) {
		this.primeiraConsulta = primeiraConsulta;
	}

	public DynamicDataModel<AFPFornecedoresVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AFPFornecedoresVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public AFPFornecedoresVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AFPFornecedoresVO selecionado) {
		this.selecionado = selecionado;
	}
	
	
	public VScoFornecedor getSugFornecedorAF() {
		return sugFornecedorAF;
	}


	public void setSugFornecedorAF(VScoFornecedor sugFornecedorAF) {
		this.sugFornecedorAF = sugFornecedorAF;
	}
	
	
}