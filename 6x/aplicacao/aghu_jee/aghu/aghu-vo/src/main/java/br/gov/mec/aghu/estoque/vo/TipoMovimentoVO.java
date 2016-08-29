package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * @author pedro.santiago
 *
 */
public class TipoMovimentoVO implements Serializable{

	private static final long serialVersionUID = -8476583523482210104L;

	private Short seq;
	private String sigla;
	private String descricao;

	public enum Fields {
		SEQ("seq"), 
		SIGLA("sigla"), 
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
