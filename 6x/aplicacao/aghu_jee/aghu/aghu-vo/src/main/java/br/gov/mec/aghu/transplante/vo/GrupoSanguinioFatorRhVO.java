package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;

public class GrupoSanguinioFatorRhVO implements Serializable {

	private static final long serialVersionUID = 9112286446286421182L;
	
	private String descricao;
	private Integer seq;
	
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

	public enum Fields {
		DESCRICAO("descricao"),
		SEQ("seq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}	
}
