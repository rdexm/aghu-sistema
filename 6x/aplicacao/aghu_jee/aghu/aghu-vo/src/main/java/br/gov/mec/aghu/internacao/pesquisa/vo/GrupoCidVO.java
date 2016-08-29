package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AghGrupoCids;

public class GrupoCidVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6938377652541934682L;
	
	private AghGrupoCids grupoCid;
	private List<Cid1VO> cid1List;
	private boolean expandido = false;

	public boolean isExpandido() {
		return expandido;
	}

	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}

	public AghGrupoCids getGrupoCid() {
		return grupoCid;
	}

	public void setGrupoCid(AghGrupoCids grupoCid) {
		this.grupoCid = grupoCid;
	}

	public List<Cid1VO> getCid1List() {
		return cid1List;
	}

	public void setCid1List(List<Cid1VO> cid1List) {
		this.cid1List = cid1List;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((grupoCid == null) ? 0 : grupoCid.hashCode());
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
		GrupoCidVO other = (GrupoCidVO) obj;
		if (grupoCid == null) {
			if (other.grupoCid != null) {
				return false;
			}
		} else if (!grupoCid.equals(other.grupoCid)) {
			return false;
		}
		return true;
	}
	
}
