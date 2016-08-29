package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItemAutFornecimentoJnVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;


public class PesquisaItensDeVersoesAutFornecimentoController extends ActionController{

	private static final long serialVersionUID = -4229556845363748165L;
	

	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	private Integer numeroAF;
	private Short numeroComplemento;
	private Integer sequenciaAlteracao;
	private String origem;
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoFornecedor fornecedor;
	private ScoModalidadeLicitacao modalidadeCompra;
	private Date dataGeracao;
	
	private String marcaComercial;
	private String modelo;
	private Integer qtdSaldo;
	private BigDecimal valorUnitario;
	private BigDecimal valorBruto;
	private BigDecimal valorSaldo;
	private BigDecimal descItem;
	private BigDecimal descCondPag;
	private BigDecimal valorDesconto;
	private BigDecimal valorEfetivado;
	private BigDecimal acrescItem;
	private BigDecimal acresCondPag;
	private BigDecimal valorAcrescimo;
	private BigDecimal valorTotal;
	private BigDecimal ipi;
	private BigDecimal valorIPI;
	
	private List<ItemAutFornecimentoJnVO> listaItemAutFornecimentoJnVO;
	private ItemAutFornecimentoJnVO itemSelecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

	 

		autorizacaoForn = autFornecimentoFacade.buscarAutFornPorNumPac(numeroAF, numeroComplemento);	
		if (autorizacaoForn != null){
			modalidadeCompra = buscarModalidadeCompra(autorizacaoForn);
			fornecedor = buscarFornecedor(autorizacaoForn);
			dataGeracao = autorizacaoForn.getDtGeracao();
		}
		
		listaItemAutFornecimentoJnVO = autFornecimentoFacade.obterListaItemAutFornecimentoJnVO(autorizacaoForn.getNumero(), numeroAF, sequenciaAlteracao);
	
	}
	
	
	private ScoModalidadeLicitacao buscarModalidadeCompra(ScoAutorizacaoForn autorizacaoForn){
		ScoModalidadeLicitacao modalidadeLicitacao = null;
		
		if (autorizacaoForn.getPropostaFornecedor() != null && autorizacaoForn.getPropostaFornecedor().getLicitacao() != null){
			modalidadeLicitacao = autorizacaoForn.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao();
		}
		
		return modalidadeLicitacao;
	}
	
	private ScoFornecedor buscarFornecedor(ScoAutorizacaoForn autorizacaoForn){
		ScoFornecedor fornecedor = null;

		if (autorizacaoForn.getPropostaFornecedor() != null){
			fornecedor = autorizacaoForn.getPropostaFornecedor().getFornecedor();
		}

		return fornecedor;
	}
	
	public void visualizarDetalhesItem(){	
		
		marcaComercial = this.itemSelecionado.getMarcaComercial();
		modelo = this.itemSelecionado.getModelo();
		qtdSaldo = this.itemSelecionado.getQtdSaldo();
		valorUnitario = this.itemSelecionado.getValorUnitario();
		valorBruto = this.itemSelecionado.getValorBruto();
		valorSaldo = this.itemSelecionado.getValorSaldo();     
		descItem = this.itemSelecionado.getDescItem();       
		descCondPag = this.itemSelecionado.getDescCondPag();    
		valorDesconto = this.itemSelecionado.getValorDesconto();  
		valorEfetivado = this.itemSelecionado.getValorEfetivado(); 
		acrescItem = this.itemSelecionado.getAcrescItem();     
		acresCondPag = this.itemSelecionado.getAcresCondPag();   
		valorAcrescimo = this.itemSelecionado.getValorAcrescimo(); 
		valorTotal = this.itemSelecionado.getValorTotal();     
		ipi = this.itemSelecionado.getIpi();            
		valorIPI = this.itemSelecionado.getValorIPI();    		
	}
	
	
	public void limpar(){
		marcaComercial = null;
		modelo = null;
		qtdSaldo = null;
		valorUnitario = null;
		valorBruto = null;
		valorSaldo = null;     
		descItem = null;       
		descCondPag = null;    
		valorDesconto = null;  
		valorEfetivado = null; 
		acrescItem = null;     
		acresCondPag = null;   
		valorAcrescimo = null; 
		valorTotal = null;     
		ipi = null;            
		valorIPI = null; 
		itemSelecionado = null;
	}

	public String voltar() {
		return origem;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public Integer getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public String getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(String marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Integer getQtdSaldo() {
		return qtdSaldo;
	}

	public void setQtdSaldo(Integer qtdSaldo) {
		this.qtdSaldo = qtdSaldo;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}

	public BigDecimal getValorSaldo() {
		return valorSaldo;
	}

	public void setValorSaldo(BigDecimal valorSaldo) {
		this.valorSaldo = valorSaldo;
	}

	public BigDecimal getDescItem() {
		return descItem;
	}

	public void setDescItem(BigDecimal descItem) {
		this.descItem = descItem;
	}

	public BigDecimal getDescCondPag() {
		return descCondPag;
	}

	public void setDescCondPag(BigDecimal descCondPag) {
		this.descCondPag = descCondPag;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(BigDecimal valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public BigDecimal getAcrescItem() {
		return acrescItem;
	}

	public void setAcrescItem(BigDecimal acrescItem) {
		this.acrescItem = acrescItem;
	}

	public BigDecimal getAcresCondPag() {
		return acresCondPag;
	}

	public void setAcresCondPag(BigDecimal acresCondPag) {
		this.acresCondPag = acresCondPag;
	}

	public BigDecimal getValorAcrescimo() {
		return valorAcrescimo;
	}

	public void setValorAcrescimo(BigDecimal valorAcrescimo) {
		this.valorAcrescimo = valorAcrescimo;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getIpi() {
		return ipi;
	}

	public void setIpi(BigDecimal ipi) {
		this.ipi = ipi;
	}

	public BigDecimal getValorIPI() {
		return valorIPI;
	}

	public void setValorIPI(BigDecimal valorIPI) {
		this.valorIPI = valorIPI;
	}

	public List<ItemAutFornecimentoJnVO> getListaItemAutFornecimentoJnVO() {
		return listaItemAutFornecimentoJnVO;
	}

	public void setListaItemAutFornecimentoJnVO(
			List<ItemAutFornecimentoJnVO> listaItemAutFornecimentoJnVO) {
		this.listaItemAutFornecimentoJnVO = listaItemAutFornecimentoJnVO;
	}

	public ItemAutFornecimentoJnVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ItemAutFornecimentoJnVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

}
