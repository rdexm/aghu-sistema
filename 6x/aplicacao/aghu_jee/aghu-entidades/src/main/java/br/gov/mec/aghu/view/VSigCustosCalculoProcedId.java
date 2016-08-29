package br.gov.mec.aghu.view;

import javax.persistence.Column;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
public class VSigCustosCalculoProcedId implements EntityCompositeId {

	private static final long serialVersionUID = -976139352900855702L;
	private Integer atdSeq;
	private Integer cacSeq;
	private Integer pmuSeq;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Boolean principal;	
	
	@Column(name="atd_seq")
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	@Column(name="cac_seq")
	public Integer getCacSeq() {
		return cacSeq;
	}
	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}
	
	@Column(name="ccp_ind_principal")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrincipal() {
		return principal;
	}
	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	@Column(name="pmu_seq")
	public Integer getPmuSeq() {
		return pmuSeq;
	}
	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}
	
	@Column(name="iph_pho_seq")
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	
	@Column(name="iph_seq")
	public Integer getIphSeq() {
		return iphSeq;
	}
	
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((cacSeq == null) ? 0 : cacSeq.hashCode());
		result = prime * result
				+ ((iphPhoSeq == null) ? 0 : iphPhoSeq.hashCode());
		result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
		result = prime * result + ((pmuSeq == null) ? 0 : pmuSeq.hashCode());
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
		if (!(obj instanceof VSigCustosCalculoProcedId)) {
			return false;
		}
		VSigCustosCalculoProcedId other = (VSigCustosCalculoProcedId) obj;
		if (atdSeq == null) {
			if (other.atdSeq != null) {
				return false;
			}
		} else if (!atdSeq.equals(other.atdSeq)) {
			return false;
		}
		if (cacSeq == null) {
			if (other.cacSeq != null) {
				return false;
			}
		} else if (!cacSeq.equals(other.cacSeq)) {
			return false;
		}
		if (iphPhoSeq == null) {
			if (other.iphPhoSeq != null) {
				return false;
			}
		} else if (!iphPhoSeq.equals(other.iphPhoSeq)) {
			return false;
		}
		if (iphSeq == null) {
			if (other.iphSeq != null) {
				return false;
			}
		} else if (!iphSeq.equals(other.iphSeq)) {
			return false;
		}
		if (pmuSeq == null) {
			if (other.pmuSeq != null) {
				return false;
			}
		} else if (!pmuSeq.equals(other.pmuSeq)) {
			return false;
		}
		return true;
	}
}
