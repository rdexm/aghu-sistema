package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class SumarioAtdRecemNascidoMedicamentosVO implements Serializable {

	private static final long serialVersionUID = -3873862900645597235L;
	private String descricaoPni;
	private Integer doseRnr;
	private String unidadeRnr;
	private String vadSiglaRnr;
	
	/**
	 * 
	 */
	public SumarioAtdRecemNascidoMedicamentosVO() {
	}
	
	public Integer getDoseRnr() {
		return doseRnr;
	}

	public void setDoseRnr(Integer doseRnr) {
		this.doseRnr = doseRnr;
	}

	public String getUnidadeRnr() {
		return unidadeRnr;
	}

	public void setUnidadeRnr(String unidadeRnr) {
		this.unidadeRnr = unidadeRnr;
	}

	public String getVadSiglaRnr() {
		return vadSiglaRnr;
	}

	public void setVadSiglaRnr(String vadSiglaRnr) {
		this.vadSiglaRnr = vadSiglaRnr;
	}

	public String getDescricaoPni() {
		return descricaoPni;
	}

	public void setDescricaoPni(String descricaoPni) {
		this.descricaoPni = descricaoPni;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((vadSiglaRnr == null) ? 0 : vadSiglaRnr
						.hashCode());
		result = prime
				* result
				+ ((vadSiglaRnr == null) ? 0 : vadSiglaRnr.hashCode());
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
		SumarioAtdRecemNascidoMedicamentosVO other = (SumarioAtdRecemNascidoMedicamentosVO) obj;
		if (vadSiglaRnr == null) {
			if (other.vadSiglaRnr != null) {
				return false;
			}
		} else if (!vadSiglaRnr.equals(other.vadSiglaRnr)) {
			return false;
		}
		if (vadSiglaRnr == null) {
			if (other.vadSiglaRnr != null) {
				return false;
			}
		} else if (!vadSiglaRnr.equals(other.vadSiglaRnr)) {
			return false;
		}
		return true;
	}

}
