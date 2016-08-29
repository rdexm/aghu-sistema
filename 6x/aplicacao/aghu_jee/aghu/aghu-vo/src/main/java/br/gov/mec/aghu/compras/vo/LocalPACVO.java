package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class LocalPACVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2652801978496022088L;
	
	private Short codigo;
	private String descricao;
	
	public enum Fields {

		CODIGO("codigo"),
		DECRICAO("descricao");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (!(obj instanceof LocalPACVO)){
			return false;
		}
		LocalPACVO other = (LocalPACVO) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		return true;
	}

}
