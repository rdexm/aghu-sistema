package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class AinMovimentosInternacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5496524475176064088L;

	private Integer intSeq;

	private Date criadoEm;

	private Date dthrLancamento;

	private Byte tmiSeq;

	private Short qrtNumero;

	private String ltoLtoId;

	private Short unfSeq;

	private Short espSeq;

	public AinMovimentosInternacaoVO() {
	}

	public AinMovimentosInternacaoVO(Integer intSeq, Date criadoEm, Date dthrLancamento, Byte tmiSeq, Short qrtNumero,
			String ltoLtoId, Short unfSeq, Short espSeq) {
		this.intSeq = intSeq;
		this.criadoEm = criadoEm;
		this.dthrLancamento = dthrLancamento;
		this.tmiSeq = tmiSeq;
		this.qrtNumero = qrtNumero;
		this.ltoLtoId = ltoLtoId;
		this.unfSeq = unfSeq;
		this.espSeq = espSeq;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public Byte getTmiSeq() {
		return tmiSeq;
	}

	public void setTmiSeq(Byte tmiSeq) {
		this.tmiSeq = tmiSeq;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((criadoEm == null) ? 0 : criadoEm.hashCode());
		result = prime * result + ((dthrLancamento == null) ? 0 : dthrLancamento.hashCode());
		result = prime * result + ((espSeq == null) ? 0 : espSeq.hashCode());
		result = prime * result + ((intSeq == null) ? 0 : intSeq.hashCode());
		result = prime * result + ((ltoLtoId == null) ? 0 : ltoLtoId.hashCode());
		result = prime * result + ((qrtNumero == null) ? 0 : qrtNumero.hashCode());
		result = prime * result + ((tmiSeq == null) ? 0 : tmiSeq.hashCode());
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
		AinMovimentosInternacaoVO other = (AinMovimentosInternacaoVO) obj;
		if (criadoEm == null) {
			if (other.criadoEm != null) {
				return false;
			}
		} else if (!criadoEm.equals(other.criadoEm)) {
			return false;
		}
		if (dthrLancamento == null) {
			if (other.dthrLancamento != null) {
				return false;
			}
		} else if (!dthrLancamento.equals(other.dthrLancamento)) {
			return false;
		}
		if (espSeq == null) {
			if (other.espSeq != null) {
				return false;
			}
		} else if (!espSeq.equals(other.espSeq)) {
			return false;
		}
		if (intSeq == null) {
			if (other.intSeq != null) {
				return false;
			}
		} else if (!intSeq.equals(other.intSeq)) {
			return false;
		}
		if (ltoLtoId == null) {
			if (other.ltoLtoId != null) {
				return false;
			}
		} else if (!ltoLtoId.equals(other.ltoLtoId)) {
			return false;
		}
		if (qrtNumero == null) {
			if (other.qrtNumero != null) {
				return false;
			}
		} else if (!qrtNumero.equals(other.qrtNumero)) {
			return false;
		}
		if (tmiSeq == null) {
			if (other.tmiSeq != null) {
				return false;
			}
		} else if (!tmiSeq.equals(other.tmiSeq)) {
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
