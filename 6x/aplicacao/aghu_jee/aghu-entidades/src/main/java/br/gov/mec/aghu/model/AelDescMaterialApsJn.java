package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * The persistent class for the AEL_DESC_MATERIAL_APS_JN database table.
 * 
 */
@Entity
@Table(name = "AEL_DESC_MATERIAL_APS_JN", schema = "AGH")
@SequenceGenerator(name = "aelDmtJnSq1", sequenceName = "AEL_DMT_JN_SQ1", allocationSize = 1 )
@Immutable
public class AelDescMaterialApsJn extends BaseJournal implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2200286459718667428L;
	private Integer seqJn;
	private Date criadoEm;
	private String descrMaterial;
	private Long luxSeq;
	private Long seq;
	private RapServidores servidor;

	public AelDescMaterialApsJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aelDmtJnSq1")
	@Column(name = "SEQ_JN", unique = true, nullable = false, precision = 14)
	@Override
	public Integer getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCR_MATERIAL", length = 2000)
	public String getDescrMaterial() {
		return this.descrMaterial;
	}

	public void setDescrMaterial(String descrMaterial) {
		this.descrMaterial = descrMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "LUX_SEQ", precision = 14)
	public Long getLuxSeq() {
		return this.luxSeq;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
	}

	@Column(nullable = false, precision = 14)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public enum Fields {
		SEQ("seq"),
		DESCR_MATERIAL("descrMaterial"),
		CRIADO_EM("criadoEm"),
		LUX_SEQ("luxSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof AelDescMaterialApsJn)) {
			return false;
		}
		AelDescMaterialApsJn other = (AelDescMaterialApsJn) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
}