package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcValidaCboRespVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7340347665767516663L;

	private String cbo;

	private Long cpf;

	private Integer matriculaProf;

	private Short vinculoProf;

	private Boolean retorno;

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Integer getMatriculaProf() {
		return matriculaProf;
	}

	public void setMatriculaProf(Integer matriculaProf) {
		this.matriculaProf = matriculaProf;
	}

	public Short getVinculoProf() {
		return vinculoProf;
	}

	public void setVinculoProf(Short vinculoProf) {
		this.vinculoProf = vinculoProf;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cbo == null) ? 0 : cbo.hashCode());
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		result = prime * result
				+ ((matriculaProf == null) ? 0 : matriculaProf.hashCode());
		result = prime * result + ((retorno == null) ? 0 : retorno.hashCode());
		result = prime * result
				+ ((vinculoProf == null) ? 0 : vinculoProf.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		RnCthcValidaCboRespVO other = (RnCthcValidaCboRespVO) obj;
		if (cbo == null) {
			if (other.cbo != null) {
				return false;
			}
		} else if (!cbo.equals(other.cbo)) {
			return false;
		}
		if (cpf == null) {
			if (other.cpf != null) {
				return false;
			}
		} else if (!cpf.equals(other.cpf)) {
			return false;
		}
		if (matriculaProf == null) {
			if (other.matriculaProf != null) {
				return false;
			}
		} else if (!matriculaProf.equals(other.matriculaProf)) {
			return false;
		}
		if (retorno == null) {
			if (other.retorno != null) {
				return false;
			}
		} else if (!retorno.equals(other.retorno)) {
			return false;
		}
		if (vinculoProf == null) {
			if (other.vinculoProf != null) {
				return false;
			}
		} else if (!vinculoProf.equals(other.vinculoProf)) {
			return false;
		}
		return true;
	}
}
