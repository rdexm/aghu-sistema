package br.gov.mec.aghu.prescricaomedica.vo;

public class ProcedimentoHospitalarInternoVO {

	private Integer seq;

	private Integer phiSeq;

	public ProcedimentoHospitalarInternoVO() {
	}

	public ProcedimentoHospitalarInternoVO(Integer seq, Integer phiSeq) {
		this.seq = seq;
		this.phiSeq = phiSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phiSeq == null) ? 0 : phiSeq.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		ProcedimentoHospitalarInternoVO other = (ProcedimentoHospitalarInternoVO) obj;
		if (phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!phiSeq.equals(other.phiSeq)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
