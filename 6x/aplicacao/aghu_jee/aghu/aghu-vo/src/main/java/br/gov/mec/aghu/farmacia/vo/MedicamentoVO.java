package br.gov.mec.aghu.farmacia.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que representa dados de AFA_MEDICAMENTOS
 * 
 * @author luismoura
 * 
 */
public class MedicamentoVO implements BaseBean {
	private static final long serialVersionUID = -6080729685118751160L;

	private Integer matCodigo;
	private String descricao;

	public MedicamentoVO() {

	}

	public MedicamentoVO(Integer matCodigo, String descricao) {
		this.matCodigo = matCodigo;
		this.descricao = descricao;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
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
		result = prime * result + ((matCodigo == null) ? 0 : matCodigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MedicamentoVO other = (MedicamentoVO) obj;
		if (matCodigo == null) {
			if (other.matCodigo != null) {
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)) {
			return false;
		}
		return true;
	}

}
