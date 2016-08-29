package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class FatProcedHospInternosVO implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3454384469249416072L;
	
	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
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
		if (getClass() != obj.getClass()){
			return false;
		}
		FatProcedHospInternosVO other = (FatProcedHospInternosVO) obj;
		if (descricao == null) {
			if (other.descricao != null){
				return false;
			}
		} else if (!descricao.equals(other.descricao)){
			return false;
		}
		return true;
	}
	
}
