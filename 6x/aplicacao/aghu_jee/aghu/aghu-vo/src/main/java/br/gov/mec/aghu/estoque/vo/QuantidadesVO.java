package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * Utilizado como resultado de algumas queries que retornam par de inteiros
 * 
 * @author luismoura
 * 
 */
public class QuantidadesVO implements Serializable {
	private static final long serialVersionUID = 3758522936860979868L;

	private Long quantidade1;
	private Long quantidade2;

	public QuantidadesVO() {

	}

	public QuantidadesVO(Long quantidade1, Long quantidade2) {
		this.quantidade1 = quantidade1;
		this.quantidade2 = quantidade2;
	}

	public Long getQuantidade1() {
		return quantidade1;
	}

	public void setQuantidade1(Long quantidade1) {
		this.quantidade1 = quantidade1;
	}

	public Long getQuantidade2() {
		return quantidade2;
	}

	public void setQuantidade2(Long quantidade2) {
		this.quantidade2 = quantidade2;
	}

	public enum Fields {
		QUANTIDADE_1("quantidade1"), //
		QUANTIDADE_2("quantidade2"), //
		;
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
