package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;

public class ItemAutFornecimentoJnVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7996396869124604079L;

	private ScoItemAutorizacaoFornJn itemAutorizacaoFornJn; 
	private Integer codMatServ;
	private String nomeMatServ;
	private String descricaoMatServ;
	private String descricaoSolicitacao;
	private Integer qtdEf;
	private Integer qtdSaldo;
	private Integer numeroItem;
	
	private String marcaComercial;
	private String modelo;
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

	
	public ScoItemAutorizacaoFornJn getItemAutorizacaoFornJn() {
		return itemAutorizacaoFornJn;
	}
	
	public void setItemAutorizacaoFornJn(
			ScoItemAutorizacaoFornJn itemAutorizacaoFornJn) {
		this.itemAutorizacaoFornJn = itemAutorizacaoFornJn;
	}
	
	public Integer getCodMatServ() {
		return codMatServ;
	}
	
	public void setCodMatServ(Integer codMatServ) {
		this.codMatServ = codMatServ;
	}
	
	public String getNomeMatServ() {
		return nomeMatServ;
	}
	
	public void setNomeMatServ(String nomeMatServ) {
		this.nomeMatServ = nomeMatServ;
	}
	
	public String getDescricaoMatServ() {
		return descricaoMatServ;
	}

	public void setDescricaoMatServ(String descricaoMatServ) {
		this.descricaoMatServ = descricaoMatServ;
	}

	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}

	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}

	public Integer getQtdEf() {
		return qtdEf;
	}
	
	public void setQtdEf(Integer qtdEf) {
		this.qtdEf = qtdEf;
	}

	public Integer getQtdSaldo() {
		return qtdSaldo;
	}

	public void setQtdSaldo(Integer qtdSaldo) {
		this.qtdSaldo = qtdSaldo;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
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
	
}
