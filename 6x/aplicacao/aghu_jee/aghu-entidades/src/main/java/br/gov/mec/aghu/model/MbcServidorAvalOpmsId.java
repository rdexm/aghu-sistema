package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MbcServidorAvalOpmsId implements EntityCompositeId{

	private static final long serialVersionUID = 7575944967441994109L;

	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	private Short seq;
	
	public MbcServidorAvalOpmsId() {
		super();
	}

	public MbcServidorAvalOpmsId(Short seq) {
		super();
		this.seq = seq;
	}

	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
}
