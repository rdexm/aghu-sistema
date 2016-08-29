package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CidVO implements BaseBean  {

	private static final long serialVersionUID = -8340444079426494575L;
	
	private Integer seq;
	private String codHcpa;
	private String codSus;
	private String descricao;	
	private String codigo;
	
	public String getCodHcpa() {
		return codHcpa;
	}
	public void setCodHcpa(String codHcpa) {
		this.codHcpa = codHcpa;
	}
	public String getCodSus() {
		return codSus;
	}
	public void setCodSus(String codSus) {
		this.codSus = codSus;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public enum Fields {
		SEQ("seq"),
		CODIGO("codigo"),
		DESCRICAO("descricao");
		
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
