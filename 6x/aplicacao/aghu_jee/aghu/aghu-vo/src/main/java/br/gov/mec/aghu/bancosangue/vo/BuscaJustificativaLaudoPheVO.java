package br.gov.mec.aghu.bancosangue.vo;

import java.io.Serializable;


public class BuscaJustificativaLaudoPheVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1561552012814602453L;

	private Integer sheSeq;

	private String justificativa;

	public BuscaJustificativaLaudoPheVO() {
	}

	public BuscaJustificativaLaudoPheVO(Integer sheSeq, String justificativa) {
		this.sheSeq = sheSeq;
		this.justificativa = justificativa;
	}

	public Integer getSheSeq() {
		return sheSeq;
	}

	public void setSheSeq(Integer sheSeq) {
		this.sheSeq = sheSeq;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((justificativa == null) ? 0 : justificativa.hashCode());
		result = prime * result + ((sheSeq == null) ? 0 : sheSeq.hashCode());
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
		BuscaJustificativaLaudoPheVO other = (BuscaJustificativaLaudoPheVO) obj;
		if (justificativa == null) {
			if (other.justificativa != null) {
				return false;
			}
		} else if (!justificativa.equals(other.justificativa)) {
			return false;
		}
		if (sheSeq == null) {
			if (other.sheSeq != null) {
				return false;
			}
		} else if (!sheSeq.equals(other.sheSeq)) {
			return false;
		}
		return true;
	}

}
