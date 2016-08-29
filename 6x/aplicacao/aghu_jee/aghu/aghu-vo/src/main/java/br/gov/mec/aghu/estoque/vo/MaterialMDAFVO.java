package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MaterialMDAFVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7840745313103941568L;
	private Integer codigo;
	private String nome;
	private String descricao;
	
	
	public enum Fields {
		
		CODIGO("codigo"),
		NOME("nome"),
		DESCRICAO("descricao");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	/**
	 * @return the codigo
	 */
	public Integer getCodigo() {
		return codigo;
	}


	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}


	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}


	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}


	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	
}
