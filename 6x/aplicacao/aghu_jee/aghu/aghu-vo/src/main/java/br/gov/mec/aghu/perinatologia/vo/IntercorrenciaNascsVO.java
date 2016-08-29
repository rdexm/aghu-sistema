package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.model.McoIntercorrenciaNascs;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que representa dados de {@link McoIntercorrenciaNascs}
 * 
 * @author luismoura
 * 
 */
public class IntercorrenciaNascsVO implements BaseBean {
	private static final long serialVersionUID = 8408755846778519304L;

	private McoIntercorrenciaNascs mcoIntercorrenciaNascs;
	private String codigoCid;

	public IntercorrenciaNascsVO() {

	}

	public IntercorrenciaNascsVO(String codigoCid, McoIntercorrenciaNascs mcoIntercorrenciaNascs) {
		this.codigoCid = codigoCid;
		this.mcoIntercorrenciaNascs = mcoIntercorrenciaNascs;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}

	public McoIntercorrenciaNascs getMcoIntercorrenciaNascs() {
		return mcoIntercorrenciaNascs;
	}

	public void setMcoIntercorrenciaNascs(McoIntercorrenciaNascs mcoIntercorrenciaNascs) {
		this.mcoIntercorrenciaNascs = mcoIntercorrenciaNascs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mcoIntercorrenciaNascs == null) ? 0 : mcoIntercorrenciaNascs.hashCode());
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
		IntercorrenciaNascsVO other = (IntercorrenciaNascsVO) obj;
		if (mcoIntercorrenciaNascs == null) {
			if (other.mcoIntercorrenciaNascs != null) {
				return false;
			}
		} else if (!mcoIntercorrenciaNascs.equals(other.mcoIntercorrenciaNascs)) {
			return false;
		}
		return true;
	}
}
