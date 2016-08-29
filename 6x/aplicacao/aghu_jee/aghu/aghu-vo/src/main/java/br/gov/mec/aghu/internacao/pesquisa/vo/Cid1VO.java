package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.io.Serializable;
import java.util.List;
import br.gov.mec.aghu.model.AghCid;

public class Cid1VO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3095634784471412932L;
	
	private AghCid cid1;
	private List<Cid2VO> cid2List;
	private boolean expandido = false;

	public boolean isExpandido() {
		return expandido;
	}

	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}

	public AghCid getCid1() {
		return cid1;
	}

	public void setCid1(AghCid cid1) {
		this.cid1 = cid1;
	}

	public List<Cid2VO> getCid2List() {
		return cid2List;
	}

	public void setCid2List(List<Cid2VO> cid2List) {
		this.cid2List = cid2List;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cid1 == null) ? 0 : cid1.hashCode());
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
		Cid1VO other = (Cid1VO) obj;
		if (cid1 == null) {
			if (other.cid1 != null) {
				return false;
			}
		} else if (!cid1.equals(other.cid1)) {
			return false;
		}
		return true;
	}
	
}