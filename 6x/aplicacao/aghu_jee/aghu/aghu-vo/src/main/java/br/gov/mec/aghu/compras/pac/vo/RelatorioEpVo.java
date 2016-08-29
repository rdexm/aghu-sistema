package br.gov.mec.aghu.compras.pac.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoLicitacao;

public class RelatorioEpVo {

	private Integer pac;
	private String lctDescricao;
	private String descricao;
	private DominioTipoLicitacao tipo;
	private String inciso;
	private Integer artigo;
	private Date dtGeracao;
	private Date dtEncerramento;
	
	public Integer getPac() {
		return pac;
	}
	public void setPac(Integer pac) {
		this.pac = pac;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public DominioTipoLicitacao getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoLicitacao tipo) {
		this.tipo = tipo;
	}
	public String getInciso() {
		return inciso;
	}
	public void setInciso(String inciso) {
		this.inciso = inciso;
	}
	public Integer getArtigo() {
		return artigo;
	}
	public void setArtigo(Integer artigo) {
		this.artigo = artigo;
	}
	public Date getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	
	public void setLctDescricao(String lctDescricao) {
		this.lctDescricao = lctDescricao;
	}
	public String getLctDescricao() {
		return lctDescricao;
	}

	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}
	public Date getDtEncerramento() {
		return dtEncerramento;
	}

	public enum Fields {
		
		PAC("pac"), 
		LCT_DESCRICAO("lctDescricao"),
		DESCRICAO("descricao"),
		TIPO("tipo"),
		INCISO("inciso"),
		ARTIGO("artigo"),
		DT_GERACAO("dtGeracao"),
		DT_ENCERRAMENTO("dtEncerramento");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
}
