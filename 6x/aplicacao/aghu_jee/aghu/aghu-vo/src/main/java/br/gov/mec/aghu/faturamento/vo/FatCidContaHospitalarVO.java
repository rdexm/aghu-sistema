package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;


public class FatCidContaHospitalarVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1880649885559402644L;

	private Integer cidSeq;

	private DominioPrioridadeCid prioridadeCid;

	public FatCidContaHospitalarVO() {
	}

	public FatCidContaHospitalarVO(Integer cidSeq, DominioPrioridadeCid prioridadeCid) {
		this.cidSeq = cidSeq;
		this.prioridadeCid = prioridadeCid;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public DominioPrioridadeCid getPrioridadeCid() {
		return prioridadeCid;
	}

	public void setPrioridadeCid(DominioPrioridadeCid prioridadeCid) {
		this.prioridadeCid = prioridadeCid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cidSeq == null) ? 0 : cidSeq.hashCode());
		result = prime * result + ((prioridadeCid == null) ? 0 : prioridadeCid.hashCode());
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
		FatCidContaHospitalarVO other = (FatCidContaHospitalarVO) obj;
		if (cidSeq == null) {
			if (other.cidSeq != null) {
				return false;
			}
		} else if (!cidSeq.equals(other.cidSeq)) {
			return false;
		}
		if (prioridadeCid != other.prioridadeCid) {
			return false;
		}
		return true;
	}

}
