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
@Table(name = "PTM_NOTA_ITEM_RECEB", schema = "AGH")
public class PtmNotaItemReceb extends BaseEntityId<PtmNotaItemRecebId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5245455727460377091L;
	
	private PtmNotaItemRecebId id;
	private Date criadoEm;
	private RapServidores servidor;
	private Long version;
	private PtmItemRecebProvisorios itemRecebProvisorio;
	
	@EmbeddedId
	public PtmNotaItemRecebId getId() {
		return id;
	}

	@Override
	public void setId(PtmNotaItemRecebId id) {
		this.id  = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ",updatable=false,insertable=false)
	public PtmItemRecebProvisorios getItemRecebProvisorio() {
		return itemRecebProvisorio;
	}

	public void setItemRecebProvisorio(PtmItemRecebProvisorios itemRecebProvisorio) {
		this.itemRecebProvisorio = itemRecebProvisorio;
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
		if (!(obj instanceof PtmNotaItemReceb)) {
			return false;
		}
		PtmNotaItemReceb other = (PtmNotaItemReceb) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getId(), other.getId());	
		return umEqualsBuilder.isEquals();
	}
	
	public enum Fields {
		NOT_SEQ("id.notSeq"),
		PTM_ITEM_RECEB_PROVISORIOS("itemRecebProvisorio"),
		NOTIFICACAO_TECNICA("notificacaoTecnica")
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
