package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoFornecedor;

public class SceDocumentoFiscalEntradaVO {
	
	private Integer seq;
	private Long numero;
	private String serie;	
	private DominioTipoDocumentoEntrada tipo;
	private DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada;
	private Date dtGeracao;
	private Date dtEmissao;
	private Date dtEntrada;
	private Date dtAutorizada;
	private ScoFornecedor fornecedor;
	private Double valorTotalNf;
	
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
	public void setTipoDocumentoFiscalEntrada(
			DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada) {
		this.tipoDocumentoFiscalEntrada = tipoDocumentoFiscalEntrada;
	}
	public Date getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
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
	public Date getDtAutorizada() {
		return dtAutorizada;
	}
	public void setDtAutorizada(Date dtAutorizada) {
		this.dtAutorizada = dtAutorizada;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public Double getValorTotalNf() {
		return valorTotalNf;
	}
	public void setValorTotalNf(Double valorTotalNf) {
		this.valorTotalNf = valorTotalNf;
	}
	

	
}
