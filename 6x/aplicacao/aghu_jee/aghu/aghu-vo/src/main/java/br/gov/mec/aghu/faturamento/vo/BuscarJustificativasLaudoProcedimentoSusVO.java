package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class BuscarJustificativasLaudoProcedimentoSusVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4783473327469683771L;

	private Integer seq;

	private Integer phiSeq;

	private Integer atdSeq;
	
	private Integer soeSeq;

	public BuscarJustificativasLaudoProcedimentoSusVO() {
	}

	public BuscarJustificativasLaudoProcedimentoSusVO(Integer seq,
			Integer phiSeq) {
		this.seq = seq;
		this.phiSeq = phiSeq;
	}

	public BuscarJustificativasLaudoProcedimentoSusVO(Integer seq,
			Integer phiSeq, Integer atdSeq) {
		this.seq = seq;
		this.phiSeq = phiSeq;
		this.atdSeq = atdSeq;
	}
	
	public BuscarJustificativasLaudoProcedimentoSusVO(Integer seq,
			Integer phiSeq, Integer atdSeq, Integer soeSeq) {
		this.seq = seq;
		this.phiSeq = phiSeq;
		this.atdSeq = atdSeq;
		this.soeSeq = soeSeq;
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

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((phiSeq == null) ? 0 : phiSeq.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
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
		BuscarJustificativasLaudoProcedimentoSusVO other = (BuscarJustificativasLaudoProcedimentoSusVO) obj;
		if (atdSeq == null) {
			if (other.atdSeq != null) {
				return false;
			}
		} else if (!atdSeq.equals(other.atdSeq)) {
			return false;
		}
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
		if (soeSeq == null) {
			if (other.soeSeq != null) {
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)) {
			return false;
		}
		return true;
	}
	
}
