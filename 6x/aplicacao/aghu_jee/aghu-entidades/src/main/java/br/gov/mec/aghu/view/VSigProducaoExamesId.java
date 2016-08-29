package br.gov.mec.aghu.view;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VSigProducaoExamesId implements EntityCompositeId {

	private static final long serialVersionUID = -8681734693223237608L;
	
	private Integer cctCodigo;
	private Integer phiSeq;
	private String origem;
	
	
	public VSigProducaoExamesId() {}

	public VSigProducaoExamesId(Integer cctCodigo, Integer phiSeq, String origem) {
		this.cctCodigo = cctCodigo;
		this.phiSeq = phiSeq;
		this.origem = origem;
	}
	
	@Column(name = "CCT_CODIGO")
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	
	@Column(name = "PHI_SEQ")
	public Integer getPhiSeq() {
		return phiSeq;
	}
	
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	@Column(name = "ORIGEM", length = 1)
	@Length(max = 1)
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cctCodigo == null) ? 0 : cctCodigo.hashCode());
		result = prime * result + ((origem == null) ? 0 : origem.hashCode());
		result = prime * result + ((phiSeq == null) ? 0 : phiSeq.hashCode());
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
		VSigProducaoExamesId other = (VSigProducaoExamesId) obj;
		if (cctCodigo == null) {
			if (other.cctCodigo != null) {
				return false;
			}
		} else if (!cctCodigo.equals(other.cctCodigo)) {
			return false;
		}
		if (origem == null) {
			if (other.origem != null) {
				return false;
			}
		} else if (!origem.equals(other.origem)) {
			return false;
		}
		if (phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!phiSeq.equals(other.phiSeq)) {
			return false;
		}
		return true;
	}

}
