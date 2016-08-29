package br.gov.mec.aghu.faturamento.vo;

public class FatLogErrorVO {

	private String dsProcedimento2;
	
	private String dsProcedimento3;
	
	private String dsProcedimento4;
	
	private String dsProcedimentoSolicitado;

	public String getDsProcedimento2() {
		return dsProcedimento2;
	}

	public void setDsProcedimento2(String dsProcedimento2) {
		this.dsProcedimento2 = dsProcedimento2;
	}

	public String getDsProcedimento3() {
		return dsProcedimento3;
	}

	public void setDsProcedimento3(String dsProcedimento3) {
		this.dsProcedimento3 = dsProcedimento3;
	}

	public String getDsProcedimento4() {
		return dsProcedimento4;
	}

	public void setDsProcedimento4(String dsProcedimento4) {
		this.dsProcedimento4 = dsProcedimento4;
	}

	public String getDsProcedimentoSolicitado() {
		return dsProcedimentoSolicitado;
	}

	public void setDsProcedimentoSolicitado(String dsProcedimentoSolicitado) {
		this.dsProcedimentoSolicitado = dsProcedimentoSolicitado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dsProcedimento2 == null) ? 0 : dsProcedimento2.hashCode());
		result = prime * result
				+ ((dsProcedimento3 == null) ? 0 : dsProcedimento3.hashCode());
		result = prime * result
				+ ((dsProcedimento4 == null) ? 0 : dsProcedimento4.hashCode());
		result = prime
				* result
				+ ((dsProcedimentoSolicitado == null) ? 0
						: dsProcedimentoSolicitado.hashCode());
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
		if (!(obj instanceof FatLogErrorVO)) {
			return false;
		}
		FatLogErrorVO other = (FatLogErrorVO) obj;
		if (dsProcedimento2 == null) {
			if (other.dsProcedimento2 != null) {
				return false;
			}
		} else if (!dsProcedimento2.equals(other.dsProcedimento2)) {
			return false;
		}
		if (dsProcedimento3 == null) {
			if (other.dsProcedimento3 != null) {
				return false;
			}
		} else if (!dsProcedimento3.equals(other.dsProcedimento3)) {
			return false;
		}
		if (dsProcedimento4 == null) {
			if (other.dsProcedimento4 != null) {
				return false;
			}
		} else if (!dsProcedimento4.equals(other.dsProcedimento4)) {
			return false;
		}
		if (dsProcedimentoSolicitado == null) {
			if (other.dsProcedimentoSolicitado != null) {
				return false;
			}
		} else if (!dsProcedimentoSolicitado
				.equals(other.dsProcedimentoSolicitado)) {
			return false;
		}
		return true;
	}

}
