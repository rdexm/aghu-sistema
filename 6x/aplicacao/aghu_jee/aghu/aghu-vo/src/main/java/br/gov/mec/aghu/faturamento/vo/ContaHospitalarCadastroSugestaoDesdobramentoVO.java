package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class ContaHospitalarCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7745597685758710895L;

	private Integer seq;

	private Date dtIntAdministrativa;

	private Date dtAltaAdministrativa;

	private Short cspCnvCodigo;

	private Byte cspSeq;

	private Byte tahSeq;

	private Integer phiSeqRealizado;

	private Integer phiSeq;

	public ContaHospitalarCadastroSugestaoDesdobramentoVO() {
	}

	public ContaHospitalarCadastroSugestaoDesdobramentoVO(Integer seq, Date dtIntAdministrativa, Date dtAltaAdministrativa,
			Short cspCnvCodigo, Byte cspSeq, Byte tahSeq, Integer phiSeqRealizado, Integer phiSeq) {
		this.seq = seq;
		this.dtIntAdministrativa = dtIntAdministrativa;
		this.dtAltaAdministrativa = dtAltaAdministrativa;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.tahSeq = tahSeq;
		this.phiSeqRealizado = phiSeqRealizado;
		this.phiSeq = phiSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getDtIntAdministrativa() {
		return dtIntAdministrativa;
	}

	public void setDtIntAdministrativa(Date dtIntAdministrativa) {
		this.dtIntAdministrativa = dtIntAdministrativa;
	}

	public Date getDtAltaAdministrativa() {
		return dtAltaAdministrativa;
	}

	public void setDtAltaAdministrativa(Date dtAltaAdministrativa) {
		this.dtAltaAdministrativa = dtAltaAdministrativa;
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

	public Byte getTahSeq() {
		return tahSeq;
	}

	public void setTahSeq(Byte tahSeq) {
		this.tahSeq = tahSeq;
	}

	public Integer getPhiSeqRealizado() {
		return phiSeqRealizado;
	}

	public void setPhiSeqRealizado(Integer phiSeqRealizado) {
		this.phiSeqRealizado = phiSeqRealizado;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cspCnvCodigo == null) ? 0 : cspCnvCodigo.hashCode());
		result = prime * result + ((cspSeq == null) ? 0 : cspSeq.hashCode());
		result = prime * result + ((dtAltaAdministrativa == null) ? 0 : dtAltaAdministrativa.hashCode());
		result = prime * result + ((dtIntAdministrativa == null) ? 0 : dtIntAdministrativa.hashCode());
		result = prime * result + ((phiSeq == null) ? 0 : phiSeq.hashCode());
		result = prime * result + ((phiSeqRealizado == null) ? 0 : phiSeqRealizado.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((tahSeq == null) ? 0 : tahSeq.hashCode());
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
		ContaHospitalarCadastroSugestaoDesdobramentoVO other = (ContaHospitalarCadastroSugestaoDesdobramentoVO) obj;
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
		if (dtAltaAdministrativa == null) {
			if (other.dtAltaAdministrativa != null) {
				return false;
			}
		} else if (!dtAltaAdministrativa.equals(other.dtAltaAdministrativa)) {
			return false;
		}
		if (dtIntAdministrativa == null) {
			if (other.dtIntAdministrativa != null) {
				return false;
			}
		} else if (!dtIntAdministrativa.equals(other.dtIntAdministrativa)) {
			return false;
		}
		if (phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!phiSeq.equals(other.phiSeq)) {
			return false;
		}
		if (phiSeqRealizado == null) {
			if (other.phiSeqRealizado != null) {
				return false;
			}
		} else if (!phiSeqRealizado.equals(other.phiSeqRealizado)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (tahSeq == null) {
			if (other.tahSeq != null) {
				return false;
			}
		} else if (!tahSeq.equals(other.tahSeq)) {
			return false;
		}
		return true;
	}

}
