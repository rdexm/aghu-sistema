package br.gov.mec.aghu.suprimentos.vo;

import java.util.Date;

public class RelatorioESVO {
	
	private String situacao;
	private Integer afn_numero;
	private String fornecedor;
	private String campo021;
	private String af;
	private String sit_af;
	private Integer nr;
	private Date dtentrada;
	private Date dtemisnf;
	private String modllict;
	private String artigo;
	private String inciso;
	private Double totalEfetivado;
	private Double totalEmpenhado;
	
	public RelatorioESVO(String situacao, Integer afnNumero, String fornecedor, String numeroLista, String af, String indSituacao,
			Integer nrSeq, Date dtGeracao, Date dtEmissao, String modLict, String artigo, String inciso) {
		
		this.situacao = situacao;
		this.afn_numero = afnNumero;
		this.fornecedor = fornecedor;
		this.campo021 = numeroLista;
		this.af = af;
		this.sit_af = indSituacao;
		this.nr = nrSeq;
		this.dtentrada = dtGeracao;
		this.dtemisnf = dtEmissao;
		this.modllict = modLict;
		this.artigo = artigo;
		this.inciso = inciso;
	}
	
	public enum Fields {

		SITUACAO("situacao"),
		AFN_NUMERO("afn_numero"),
		FORNECEDOR("fornecedor"),
		CAMPO021("campo021"),
		AF("af"),
		SIT_AF("sit_af"),
		NR("nr"),
		DTENTRADA("dtentrada"),
		DTEMISNF("dtemisnf"),
		MODLLICT("modllict"),
		ARTIGO("artigo"),
		INCISO("inciso"),
		VALOR_EFET_VIG("totalEfetivado"),
		VALOR_EMP_VIG("totalEmpenhado");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Integer getAfnNumero() {
		return afn_numero;
	}

	public void setAfnNumero(Integer afn_numero) {
		this.afn_numero = afn_numero;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getCampo021() {
		return campo021;
	}

	public void setCampo021(String campo021) {
		this.campo021 = campo021;
	}

	public String getAf() {
		return af;
	}

	public void setAf(String af) {
		this.af = af;
	}

	public String getSitAf() {
		return sit_af;
	}

	public void setSitAf(String sit_af) {
		this.sit_af = sit_af;
	}

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Date getDtentrada() {
		return dtentrada;
	}

	public void setDtentrada(Date dtentrada) {
		this.dtentrada = dtentrada;
	}

	public Date getDtemisnf() {
		return dtemisnf;
	}

	public void setDtemisnf(Date dtemisnf) {
		this.dtemisnf = dtemisnf;
	}

	public String getModllict() {
		return modllict;
	}

	public void setModllict(String modllict) {
		this.modllict = modllict;
	}

	public String getArtigo() {
		return artigo;
	}

	public void setArtigo(String artigo) {
		this.artigo = artigo;
	}

	public String getInciso() {
		return inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public Double getTotalEfetivado() {
		return totalEfetivado;
	}

	public void setTotalEfetivado(Double totalEfetivado) {
		this.totalEfetivado = totalEfetivado;
	}

	public Double getTotalEmpenhado() {
		return totalEmpenhado;
	}

	public void setTotalEmpenhado(Double totalEmpenhado) {
		this.totalEmpenhado = totalEmpenhado;
	}
	
}
