package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Utilizado para validações de um item de af é válido para programação de entrega
 * 
 * @author luismoura
 * 
 */
public class DadosItemAptoProgramacaoEntregaVO implements Serializable {
	private static final long serialVersionUID = 3758522936860979868L;

	private Integer afnNumero;
	private Integer numero;
	private Integer fornecedorPadrao;
	private Integer codigoMaterial;
	private Integer slcNumero;
	private Integer estoqueAlmoxSeq;
	private Integer numeroFornecedor;
	private boolean indUtilizaEspaco;
	private boolean indControleValidade;
	private Integer tempoReposicao;
	private BigDecimal consumoDiario;
	private Integer qtdePontoPedido;
	private Integer qtdeEstqMax;
	private Integer qtdeEspacoArmazena;

	public DadosItemAptoProgramacaoEntregaVO() {

	}

	public DadosItemAptoProgramacaoEntregaVO(Integer afnNumero, Integer numero) {
		this.afnNumero = afnNumero;
		this.numero = numero;
	}

	public Integer getFornecedorPadrao() {
		return fornecedorPadrao;
	}

	public void setFornecedorPadrao(Integer fornecedorPadrao) {
		this.fornecedorPadrao = fornecedorPadrao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	public Integer getEstoqueAlmoxSeq() {
		return estoqueAlmoxSeq;
	}

	public void setEstoqueAlmoxSeq(Integer estoqueAlmoxSeq) {
		this.estoqueAlmoxSeq = estoqueAlmoxSeq;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public boolean isIndUtilizaEspaco() {
		return indUtilizaEspaco;
	}

	public void setIndUtilizaEspaco(boolean indUtilizaEspaco) {
		this.indUtilizaEspaco = indUtilizaEspaco;
	}

	public boolean isIndControleValidade() {
		return indControleValidade;
	}

	public void setIndControleValidade(boolean indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	public Integer getTempoReposicao() {
		return tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	public BigDecimal getConsumoDiario() {
		return consumoDiario;
	}

	public void setConsumoDiario(BigDecimal consumoDiario) {
		this.consumoDiario = consumoDiario;
	}

	public Integer getQtdePontoPedido() {
		return qtdePontoPedido;
	}

	public void setQtdePontoPedido(Integer qtdePontoPedido) {
		this.qtdePontoPedido = qtdePontoPedido;
	}

	public Integer getQtdeEstqMax() {
		return qtdeEstqMax;
	}

	public void setQtdeEstqMax(Integer qtdeEstqMax) {
		this.qtdeEstqMax = qtdeEstqMax;
	}

	public Integer getQtdeEspacoArmazena() {
		return qtdeEspacoArmazena;
	}

	public void setQtdeEspacoArmazena(Integer qtdeEspacoArmazena) {
		this.qtdeEspacoArmazena = qtdeEspacoArmazena;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}
}
