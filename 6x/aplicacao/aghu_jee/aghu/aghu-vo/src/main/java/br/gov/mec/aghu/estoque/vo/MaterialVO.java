package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * @author pedro.santiago
 *
 */
public class MaterialVO implements Serializable {

	private static final long serialVersionUID = 9016707893170339187L;
	
	private Integer codigo;
	private String nome;

	public enum Fields {
		CODIGO("codigo"), 
		NOME("nome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	
}
