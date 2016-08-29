package br.gov.mec.aghu.compras.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.PropostasVencedorasVO;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.action.ActionController;

public class PesquisaPropostasVencedorasController extends ActionController{

	private static final long serialVersionUID = 2426174938717536660L;

	@EJB
	private IComprasFacade comprasFacade;
	
	private ScoLicitacao licitacao;
	private String modalidade;
	private Integer numeroParam;
	private boolean pesquisaConcluida = Boolean.FALSE;
	private static final String PAGE_GER_AUT_FORNECIMENTO_LIST = "gerAutFornecimentoList";

	List<PropostasVencedorasVO> list = new ArrayList<PropostasVencedorasVO>();
	
	public void inicio(){
		if(numeroParam != null){
			licitacao = getComprasFacade().buscarLicitacaoPorNumero(numeroParam);
			atualizarModalidade();
			recuperarLista();
		}
	}
	
	/**
	 * registros do pesquisar
	 * @return
	 */
	public void recuperarLista() {
		list = getComprasFacade().listarItensLicitacao(licitacao.getNumero());
		pesquisaConcluida = Boolean.TRUE;
	}
	
	/**
	 * suggestion
	 * @param param
	 * @return
	 */
	public List<ScoLicitacao> pesquisarLicitacao(String param) {
		return this.returnSGWithCount(getComprasFacade().listarLicitacoesPorNumeroEDescricao(param),pesquisarLicitacaoCount(param));
	}
	
	/**
	 * suggestion count
	 * @param param
	 * @return
	 */
	public Long pesquisarLicitacaoCount(String param) {
		return getComprasFacade().listarLicitacoesCount(param);
	}
	
	public void atualizarModalidade() {
		if(licitacao.getModalidadeLicitacao() != null){
			setaValorModalidade();
		}
	}
	
	private void setaValorModalidade() {
		setModalidade(licitacao.getModalidadeLicitacao().getDescricao());
	}

	public void deletarModalidade() {
		setModalidade(null);
	}
	
	public void limpar() {
		setLicitacao(null);
		setModalidade(null);
		pesquisaConcluida = Boolean.FALSE;
		list = new ArrayList<PropostasVencedorasVO>();
	}
	
	public String voltar() {
		return PAGE_GER_AUT_FORNECIMENTO_LIST;
	}
	
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public void setNumeroParam(Integer numeroParam) {
		this.numeroParam = numeroParam;
	}

	public Integer getNumeroParam() {
		return numeroParam;
	}

	public List<PropostasVencedorasVO> getList() {
		return list;
	}

	public void setList(List<PropostasVencedorasVO> list) {
		this.list = list;
	}

	public void setPesquisaConcluida(boolean pesquisaConcluida) {
		this.pesquisaConcluida = pesquisaConcluida;
	}

	public boolean isPesquisaConcluida() {
		return pesquisaConcluida;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getModalidade() {
		return modalidade;
	}
	/*public List<ScoLicitacao> pesquisarLicitacao(Object param) {
		return getComprasFacade().listarLicitacoesPorNumeroEDescricao(param);
	}
	
	public Long pesquisarItemLicitacaoCount(Object param) {
		return null;
		// Classe excluida. Método inexistente em ComprasFacade
		//return getComprasFacade().listarItensLicitacoesCount(param);
	}
	
	public Long pesquisarLicitacaoCount(Object param) {
		return getComprasFacade().listarLicitacoesCount(param);
	}
	
	@Override
	public Long recuperarCount() {		
		return pesquisarItemLicitacaoCount(getLicitacao().getNumero());
	}

	@Override
	public List<ScoItemLicitacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		// Classe excluida. Método inexistente em ComprasFacade
		//return getComprasFacade().listarItensLicitacao(licitacao.getNumero(), firstResult, maxResult, orderProperty, asc);
		return null;
	}
	
	public void limpar() {
		setLicitacao(null);
		dataModel.setPesquisaAtiva(false);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String cancelar() {
		return voltarParaUrl;
	}
	
	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}
	
	// Get e set
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}
	
	public ScoItemLicitacao getItemLicitacao() {
		return itemLicitacao;
	}

	public void setItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.itemLicitacao = itemLicitacao;
	}

	public DynamicDataModel<ScoItemLicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoItemLicitacao> dataModel) {
		this.dataModel = dataModel;
	}*/

}
