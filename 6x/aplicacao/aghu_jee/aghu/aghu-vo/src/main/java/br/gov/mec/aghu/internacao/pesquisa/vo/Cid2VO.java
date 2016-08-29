package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghCid;

public class Cid2VO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8170739695954463553L;
	
	private AghCid cid2;

	public AghCid getCid2() {
		return cid2;
	}

	public void setCid2(AghCid cid2) {
		this.cid2 = cid2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cid2 == null) ? 0 : cid2.hashCode());
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
		Cid2VO other = (Cid2VO) obj;
		if (cid2 == null) {
			if (other.cid2 != null) {
				return false;
			}
		} else if (!cid2.equals(other.cid2)) {
			return false;
		}
		return true;
	}
	
}
