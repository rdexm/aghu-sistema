package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2330860926020398938L;

	private Integer pacCodigo;

	private Date data;

	private Short cspCnvCodigo;

	private Byte cspSeq;

	private Short unfSeq;

	public CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO() {
	}

	public CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO(Integer pacCodigo, Date data, Short cspCnvCodigo,
			Byte cspSeq, Short unfSeq) {
		this.pacCodigo = pacCodigo;
		this.data = data;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.unfSeq = unfSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cspCnvCodigo == null) ? 0 : cspCnvCodigo.hashCode());
		result = prime * result + ((cspSeq == null) ? 0 : cspSeq.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
		CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO other = (CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO) obj;
		if (cspCnvCodigo == null) {
			if (other.cspCnvCodigo != null) {
				return false;
			}
		} else if (!cspCnvCodigo.equals(other.cspCnvCodigo)) {
			return false;
		}
		if (cspSeq == null) {
			if (other.cspSeq != null) {
				return false;
			}
		} else if (!cspSeq.equals(other.cspSeq)) {
			return false;
		}
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (pacCodigo == null) {
			if (other.pacCodigo != null) {
				return false;
			}
		} else if (!pacCodigo.equals(other.pacCodigo)) {
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
