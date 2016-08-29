package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name="PTM_BEM_PROCESSO", schema = "AGH")
public class PtmBemProcesso extends BaseEntityId<PtmBemProcessoId>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1001212593226544989L;
	private PtmBemProcessoId id;
	private Date criadoEm;
	private RapServidores servidor;
	private Long version;
	
	
	@EmbeddedId
	public PtmBemProcessoId getId() {
		return id;
	}

	@Override
	public void setId(PtmBemProcessoId id) {
		this.id  = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA" , nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO" , nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores rapServidores) {
		this.servidor = rapServidores;
	}
	
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getId());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PtmBemProcesso)) {
			return false;
		}
		PtmBemProcesso other = (PtmBemProcesso) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getId(), other.getId());	
		return umEqualsBuilder.isEquals();
	}
	
	public enum Fields {
		BPE_SEQ("id.bpeSeq"),
		PRO_SEQ("id.proSeq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}