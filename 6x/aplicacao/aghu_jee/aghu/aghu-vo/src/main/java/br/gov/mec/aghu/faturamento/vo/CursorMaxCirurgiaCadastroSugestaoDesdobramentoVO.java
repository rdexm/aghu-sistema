package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3052107222840875202L;

	private Date dthrRealizado;

	private Short ichUnf;

	public CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO() {
	}

	public CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO(Date dthrRealizado, Short ichUnf) {
		this.dthrRealizado = dthrRealizado;
		this.ichUnf = ichUnf;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public Short getIchUnf() {
		return ichUnf;
	}

	public void setIchUnf(Short ichUnf) {
		this.ichUnf = ichUnf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dthrRealizado == null) ? 0 : dthrRealizado.hashCode());
		result = prime * result + ((ichUnf == null) ? 0 : ichUnf.hashCode());
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
		CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO other = (CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO) obj;
		if (dthrRealizado == null) {
			if (other.dthrRealizado != null) {
				return false;
			}
		} else if (!dthrRealizado.equals(other.dthrRealizado)) {
			return false;
		}
		if (ichUnf == null) {
			if (other.ichUnf != null) {
				return false;
			}
		} else if (!ichUnf.equals(other.ichUnf)) {
			return false;
		}
		return true;
	}
	
}
