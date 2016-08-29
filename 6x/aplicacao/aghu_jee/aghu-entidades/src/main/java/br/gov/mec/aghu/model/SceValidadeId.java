package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_validades database table.
 * 
 */
@Embeddable
public class SceValidadeId implements EntityCompositeId {
	//default serial version id, required for serializable classes.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2313060872035604020L;
	private Integer ealSeq;
	private Date data;
	
	// FIXME Verificar a necessidade deste existir este atributo
	@SuppressWarnings("unused")
	private Long dataLong;

    public SceValidadeId() {
    }

	public SceValidadeId(Integer ealSeq, Date data) {
		super();
		this.ealSeq = ealSeq;
		this.data = data;
	}

	@Column(name="EAL_SEQ")
	public Integer getEalSeq() {
		return this.ealSeq;
	}
	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

    @Temporal( TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceValidadeId)) {
			return false;
		}
		SceValidadeId castOther = (SceValidadeId)other;
		return 
			this.ealSeq.equals(castOther.ealSeq)
			&& this.data.equals(castOther.data);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.ealSeq.hashCode();
		hash = hash * prime + this.data.hashCode();
		
		return hash;
    }

	@Transient
	public Long getDataLong() {
		return data.getTime();
	}

	public void setDataLong(Long dataLong) {
		this.dataLong = dataLong;
	}
}