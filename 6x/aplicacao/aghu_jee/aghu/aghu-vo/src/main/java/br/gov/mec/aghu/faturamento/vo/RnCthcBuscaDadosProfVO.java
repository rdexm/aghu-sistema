package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcBuscaDadosProfVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4951200542332206761L;

	private Short equipe;

	private Long cpfCns;

	private String cbo;

	private Integer matriculaProf;

	private Short vinculoProf;

	private Boolean retorno;

	public Short getEquipe() {
		return equipe;
	}

	public void setEquipe(Short equipe) {
		this.equipe = equipe;
	}

	public Long getCpfCns() {
		return cpfCns;
	}

	public void setCpfCns(Long cpfCns) {
		this.cpfCns = cpfCns;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
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
		result = prime * result + ((cpfCns == null) ? 0 : cpfCns.hashCode());
		result = prime * result + ((equipe == null) ? 0 : equipe.hashCode());
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
		RnCthcBuscaDadosProfVO other = (RnCthcBuscaDadosProfVO) obj;
		if (cbo == null) {
			if (other.cbo != null) {
				return false;
			}
		} else if (!cbo.equals(other.cbo)) {
			return false;
		}
		if (cpfCns == null) {
			if (other.cpfCns != null) {
				return false;
			}
		} else if (!cpfCns.equals(other.cpfCns)) {
			return false;
		}
		if (equipe == null) {
			if (other.equipe != null) {
				return false;
			}
		} else if (!equipe.equals(other.equipe)) {
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
