package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5593030698199251560L;

	private Integer phiSeq;

	private Date dthrRealizado;
	
	public CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO() {
	}

	public CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO(Integer phiSeq, Date dthrRealizado) {
		this.phiSeq = phiSeq;
		this.dthrRealizado = dthrRealizado;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dthrRealizado == null) ? 0 : dthrRealizado.hashCode());
		result = prime * result + ((phiSeq == null) ? 0 : phiSeq.hashCode());
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
		CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO other = (CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO) obj;
		if (dthrRealizado == null) {
			if (other.dthrRealizado != null) {
				return false;
			}
		} else if (!dthrRealizado.equals(other.dthrRealizado)) {
			return false;
		}
		if (phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!phiSeq.equals(other.phiSeq)) {
			return false;
		}
		return true;
	}
	
}
