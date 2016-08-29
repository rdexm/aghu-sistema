package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaDocumentoFiscalEntradaVO implements BaseBean {

	private static final long serialVersionUID = -9129864594041567204L;

	private Integer seq;
	private Long numero;
	private String serie;
	private DominioTipoDocumentoEntrada tipo;
	private DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada;
	private Date dtAutorizada;
	private Date dtEmissao;
	private Date dtEntrada;
	private Date dtGeracao;
	private ScoFornecedor fornecedor;
	private SceFornecedorEventual fornecedorEventual;
	private Double valorTotalNf;
	private String cpfCnpfFornecedor;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public DominioTipoDocumentoEntrada getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoDocumentoEntrada tipo) {
		this.tipo = tipo;
	}

	public DominioTipoDocumentoFiscalEntrada getTipoDocumentoFiscalEntrada() {
		return tipoDocumentoFiscalEntrada;
	}

	public void setTipoDocumentoFiscalEntrada(DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada) {
		this.tipoDocumentoFiscalEntrada = tipoDocumentoFiscalEntrada;
	}

	public Date getDtAutorizada() {
		return dtAutorizada;
	}

	public void setDtAutorizada(Date dtAutorizada) {
		this.dtAutorizada = dtAutorizada;
	}

	public Date getDtEmissao() {
		return dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public SceFornecedorEventual getFornecedorEventual() {
		return fornecedorEventual;
	}

	public void setFornecedorEventual(SceFornecedorEventual fornecedorEventual) {
		this.fornecedorEventual = fornecedorEventual;
	}

	public Double getValorTotalNf() {
		return valorTotalNf;
	}

	public void setValorTotalNf(Double valorTotalNf) {
		this.valorTotalNf = valorTotalNf;
	}

	public String getCpfCnpfFornecedor() {
		return cpfCnpfFornecedor;
	}

	public void setCpfCnpfFornecedor(String cpfCnpfFornecedor) {
		this.cpfCnpfFornecedor = cpfCnpfFornecedor;
	}
}
