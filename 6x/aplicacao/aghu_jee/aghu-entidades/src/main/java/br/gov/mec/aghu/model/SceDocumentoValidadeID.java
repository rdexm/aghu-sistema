package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * The primary key class for the sce_documento_validades database table.
 * 
 */
@Embeddable
public class SceDocumentoValidadeID implements EntityCompositeId {
	//default serial version id, required for serializable classes.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1366964535143595537L;
	private Integer tmvSeq;
	private Integer tmvComplemento;
	private Integer ealSeq;
	private Integer nroDocumento;
	private java.util.Date data;

    public SceDocumentoValidadeID() {
    }

	@Column(name="TMV_SEQ")
	public Integer getTmvSeq() {
		return this.tmvSeq;
	}
	public void setTmvSeq(Integer tmvSeq) {
		this.tmvSeq = tmvSeq;
	}

	@Column(name="TMV_COMPLEMENTO")
	public Integer getTmvComplemento() {
		return this.tmvComplemento;
	}
	public void setTmvComplemento(Integer tmvComplemento) {
		this.tmvComplemento = tmvComplemento;
	}

	@Column(name="EAL_SEQ")
	public Integer getEalSeq() {
		return this.ealSeq;
	}
	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

	@Column(name="NRO_DOCUMENTO")
	public Integer getNroDocumento() {
		return this.nroDocumento;
	}
	public void setNroDocumento(Integer nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

    @Temporal( TemporalType.TIMESTAMP)
	public java.util.Date getData() {
		return this.data;
	}
	public void setData(java.util.Date data) {
		this.data = data;
	}
	
	@Transient
	public String getDataFormatadaDiaMesAno() {
		return DateUtil.obterDataFormatada(this.data, "dd/MM/yyyy");
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceDocumentoValidadeID)) {
			return false;
		}
		SceDocumentoValidadeID castOther = (SceDocumentoValidadeID)other;
		return 
			this.tmvSeq.equals(castOther.tmvSeq)
			&& this.tmvComplemento.equals(castOther.tmvComplemento)
			&& this.ealSeq.equals(castOther.ealSeq)
			&& this.nroDocumento.equals(castOther.nroDocumento)
			&& this.data.equals(castOther.data);

    }
    
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.tmvSeq);
		umHashCodeBuilder.append(this.tmvComplemento);
		umHashCodeBuilder.append(this.ealSeq);
		umHashCodeBuilder.append(this.nroDocumento);
		umHashCodeBuilder.append(this.data);
		return umHashCodeBuilder.toHashCode();
	}
	
	/*@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		
		hash = hash * prime + this.tmvSeq.hashCode();
		hash = hash * prime + this.tmvComplemento.hashCode();
		hash = hash * prime + this.ealSeq.hashCode();
		hash = hash * prime + this.nroDocumento.hashCode();
		hash = hash * prime + this.data.hashCode();
		
		return hash;
    }*/
	
	
	
	
	
	public enum Fields {

		TMV_SEQ("tmvSeq"),
		TMV_COMPLEMENTO("tmvComplemento"),
		EAL_SEQ("ealSeq"),
		NRO_DOCUMENTO("nroDocumento"),
		DATA("data");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}