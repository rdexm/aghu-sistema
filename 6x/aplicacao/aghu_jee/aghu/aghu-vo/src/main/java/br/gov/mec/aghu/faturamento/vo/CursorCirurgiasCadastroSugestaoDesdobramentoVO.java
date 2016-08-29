package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorCirurgiasCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6088252199303557341L;

	private Date dthrRealizado;

	private Short unfSeq;

	public CursorCirurgiasCadastroSugestaoDesdobramentoVO() {
	}

	public CursorCirurgiasCadastroSugestaoDesdobramentoVO(Date dthrRealizado, Short unfSeq) {
		this.dthrRealizado = dthrRealizado;
		this.unfSeq = unfSeq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dthrRealizado == null) ? 0 : dthrRealizado.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
		CursorCirurgiasCadastroSugestaoDesdobramentoVO other = (CursorCirurgiasCadastroSugestaoDesdobramentoVO) obj;
		if (dthrRealizado == null) {
			if (other.dthrRealizado != null) {
				return false;
			}
		} else if (!dthrRealizado.equals(other.dthrRealizado)) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}

}
