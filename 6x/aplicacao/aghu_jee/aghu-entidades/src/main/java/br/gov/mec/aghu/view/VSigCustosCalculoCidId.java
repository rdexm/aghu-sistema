package br.gov.mec.aghu.view;

import javax.persistence.Column;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
public class VSigCustosCalculoCidId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4289901457518489220L;
	private Integer atdSeq;
	private Integer cacSeq;
	private Boolean principal;
	private String cidCodigo;
	private String cidDescricao;
	private Integer pmuSeq;
	
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
	
	@Column(name="cid_ind_principal")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrincipal() {
		return principal;
	}
	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	@Column(name="cid_codigo")
	public String getCidCodigo() {
		return cidCodigo;
	}
	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
	
	@Column(name="cid_descricao")
	public String getCidDescricao() {
		return cidDescricao;
	}
	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}
	
	@Column(name="pmu_seq")
	public Integer getPmuSeq() {
		return pmuSeq;
	}
	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((cacSeq == null) ? 0 : cacSeq.hashCode());
		result = prime * result
				+ ((cidCodigo == null) ? 0 : cidCodigo.hashCode());
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
		if (!(obj instanceof VSigCustosCalculoCidId)) {
			return false;
		}
		VSigCustosCalculoCidId other = (VSigCustosCalculoCidId) obj;
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
		if (cidCodigo == null) {
			if (other.cidCodigo != null) {
				return false;
			}
		} else if (!cidCodigo.equals(other.cidCodigo)) {
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
