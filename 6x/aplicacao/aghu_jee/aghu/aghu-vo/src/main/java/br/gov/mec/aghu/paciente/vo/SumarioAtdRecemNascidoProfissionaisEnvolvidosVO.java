package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class SumarioAtdRecemNascidoProfissionaisEnvolvidosVO implements Serializable {

	private static final long serialVersionUID = 7481422029281439118L;
	private String nomeProfissional;
	private String conselhoProfissional;
	
	/**
	 * 
	 */
	public SumarioAtdRecemNascidoProfissionaisEnvolvidosVO() {
	}
	
	public String getNomeProfissional() {
		return nomeProfissional;
	}
	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}
	public String getConselhoProfissional() {
		return conselhoProfissional;
	}
	public void setConselhoProfissional(String conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((conselhoProfissional == null) ? 0 : conselhoProfissional
						.hashCode());
		result = prime
				* result
				+ ((nomeProfissional == null) ? 0 : nomeProfissional.hashCode());
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
		SumarioAtdRecemNascidoProfissionaisEnvolvidosVO other = (SumarioAtdRecemNascidoProfissionaisEnvolvidosVO) obj;
		if (conselhoProfissional == null) {
			if (other.conselhoProfissional != null) {
				return false;
			}
		} else if (!conselhoProfissional.equals(other.conselhoProfissional)) {
			return false;
		}
		if (nomeProfissional == null) {
			if (other.nomeProfissional != null) {
				return false;
			}
		} else if (!nomeProfissional.equals(other.nomeProfissional)) {
			return false;
		}
		return true;
	}

	
}
