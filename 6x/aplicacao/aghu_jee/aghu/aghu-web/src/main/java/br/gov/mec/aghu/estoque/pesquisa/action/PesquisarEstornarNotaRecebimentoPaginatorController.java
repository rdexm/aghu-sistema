package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PesquisaEstornarNotaRecebimentoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisarEstornarNotaRecebimentoPaginatorController extends ActionController implements ActionPaginator {


	@Inject @Paginator
	private DynamicDataModel<PesquisaEstornarNotaRecebimentoVO> dataModel;

	private static final long serialVersionUID = 8978167598244097860L;

	private static final String VISUALIZAR_ESTORNO_NOTA_RECEBIMENTO = "estoque-visualizarEstornoNotaRecebimento";

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// Campos de filtro para pesquisa
	private Integer numeroNotaRecebimento;
	private Integer numeroProcessoCompra;
	private Long numeroDocumentoFiscalEntrada;
	private Date dataGeracao;
	private ScoFornecedor fornecedor;
	private DominioSimNao situacaoDebitoNotaRecebimento;
	private DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoFornecimento;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	} 

	private SceNotaRecebimento getElementoFiltroPesquisa(){
		
		final SceNotaRecebimento elementoFiltroPesquisa = new SceNotaRecebimento();
		
		// Popula filtro de pesquisa
		elementoFiltroPesquisa.setSeq(this.numeroNotaRecebimento);
		
		ScoAutorizacaoForn autorizacaoFornecedor = new ScoAutorizacaoForn();
		ScoPropostaFornecedor propostaFornecedor = new ScoPropostaFornecedor();
		ScoPropostaFornecedorId id = new ScoPropostaFornecedorId();
		id.setLctNumero(this.numeroProcessoCompra);
		propostaFornecedor.setId(id);
		propostaFornecedor.setFornecedor(this.fornecedor);
		autorizacaoFornecedor.setPropostaFornecedor(propostaFornecedor);	
		autorizacaoFornecedor.setSituacao(this.situacaoAutorizacaoFornecimento);
		elementoFiltroPesquisa.setAutorizacaoFornecimento(autorizacaoFornecedor);
		
		SceDocumentoFiscalEntrada documentoFiscalEntrada = new SceDocumentoFiscalEntrada();
		documentoFiscalEntrada.setNumero(this.numeroDocumentoFiscalEntrada);
		elementoFiltroPesquisa.setDocumentoFiscalEntrada(documentoFiscalEntrada);

		elementoFiltroPesquisa.setDtGeracao(this.dataGeracao);
		
		elementoFiltroPesquisa.setDebitoNotaRecebimento(this.situacaoDebitoNotaRecebimento != null ? this.situacaoDebitoNotaRecebimento.isSim() : null);

		return elementoFiltroPesquisa;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisarEstornarNotaRecebimentoCount(this.getElementoFiltroPesquisa());
	}
	
	@Override
	public List<PesquisaEstornarNotaRecebimentoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<SceNotaRecebimento> listaNotasRecebimento = this.estoqueFacade.pesquisarEstornarNotaRecebimento(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa());
		List<PesquisaEstornarNotaRecebimentoVO> resultado = new LinkedList<PesquisaEstornarNotaRecebimentoVO>();
		
		if(listaNotasRecebimento != null && !listaNotasRecebimento.isEmpty()){
			
			for (SceNotaRecebimento notaRecebimento : listaNotasRecebimento) {
				
				PesquisaEstornarNotaRecebimentoVO vo = new PesquisaEstornarNotaRecebimentoVO();
				
				vo.setSeq(notaRecebimento.getSeq());
				vo.setDtGeracao(notaRecebimento.getDtGeracao());
				vo.setDebitoNotaRecebimento(notaRecebimento.getDebitoNotaRecebimento());
				vo.setAutorizacaoFornecimento(notaRecebimento.getAutorizacaoFornecimento());
				vo.setDocumentoFiscalEntrada(notaRecebimento.getDocumentoFiscalEntrada());
				vo.setEstorno(notaRecebimento.getEstorno());
				
		
				resultado.add(vo);
				
			}
		}
		return resultado;
	}
	
	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.numeroNotaRecebimento = null;
		this.numeroProcessoCompra = null;
		this.numeroDocumentoFiscalEntrada = null;	
		this.dataGeracao = null;
		this.fornecedor = null;
		this.situacaoDebitoNotaRecebimento = null;
		this.situacaoAutorizacaoFornecimento = null;
	}
	
	public String visualizarEstornoNotaRecebimento(){
		return VISUALIZAR_ESTORNO_NOTA_RECEBIMENTO;
	}
	
	public List<ScoFornecedor> obterFornecedores(String param) {
		return comprasFacade.obterFornecedor(param);
	}

	public Integer getNumeroNotaRecebimento() {
		return numeroNotaRecebimento;
	}

	public void setNumeroNotaRecebimento(Integer numeroNotaRecebimento) {
		this.numeroNotaRecebimento = numeroNotaRecebimento;
	}

	public Integer getNumeroProcessoCompra() {
		return numeroProcessoCompra;
	}

	public void setNumeroProcessoCompra(Integer numeroProcessoCompra) {
		this.numeroProcessoCompra = numeroProcessoCompra;
	}

	public Long getNumeroDocumentoFiscalEntrada() {
		return numeroDocumentoFiscalEntrada;
	}

	public void setNumeroDocumentoFiscalEntrada(Long numeroDocumentoFiscalEntrada) {
		this.numeroDocumentoFiscalEntrada = numeroDocumentoFiscalEntrada;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAutorizacaoFornecimento() {
		return situacaoAutorizacaoFornecimento;
	}

	public void setSituacaoAutorizacaoFornecimento(
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoFornecimento) {
		this.situacaoAutorizacaoFornecimento = situacaoAutorizacaoFornecimento;
	}

	public DominioSimNao getSituacaoDebitoNotaRecebimento() {
		return situacaoDebitoNotaRecebimento;
	}

	public void setSituacaoDebitoNotaRecebimento(DominioSimNao situacaoDebitoNotaRecebimento) {
		this.situacaoDebitoNotaRecebimento = situacaoDebitoNotaRecebimento;
	}

	public DynamicDataModel<PesquisaEstornarNotaRecebimentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaEstornarNotaRecebimentoVO> dataModel) {
		this.dataModel = dataModel;
	}
}