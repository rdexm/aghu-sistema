package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerItemSusVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2377587336900141333L;

	private Short phoSeq;
	
	private Integer iphSeq;
	 
	private Long codSus;
	
	private Boolean retorno;

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Long getCodSus() {
		return codSus;
	}

	public void setCodSus(Long codSus) {
		this.codSus = codSus;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codSus == null) ? 0 : codSus.hashCode());
		result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
		result = prime * result + ((phoSeq == null) ? 0 : phoSeq.hashCode());
		result = prime * result + ((retorno == null) ? 0 : retorno.hashCode());
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
		RnCthcVerItemSusVO other = (RnCthcVerItemSusVO) obj;
		if (codSus == null) {
			if (other.codSus != null) {
				return false;
			}
		} else if (!codSus.equals(other.codSus)) {
			return false;
		}
		if (iphSeq == null) {
			if (other.iphSeq != null) {
				return false;
			}
		} else if (!iphSeq.equals(other.iphSeq)) {
			return false;
		}
		if (phoSeq == null) {
			if (other.phoSeq != null) {
				return false;
			}
		} else if (!phoSeq.equals(other.phoSeq)) {
			return false;
		}
		if (retorno == null) {
			if (other.retorno != null) {
				return false;
			}
		} else if (!retorno.equals(other.retorno)) {
			return false;
		}
		return true;
	}
	
}
