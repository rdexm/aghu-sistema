package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EtapaPACVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7444940490464557188L;
	private Integer codigo;
	private String descricao;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
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
		if (!(obj instanceof EtapaPACVO)){
			return false;
		}
		EtapaPACVO other = (EtapaPACVO) obj;
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
