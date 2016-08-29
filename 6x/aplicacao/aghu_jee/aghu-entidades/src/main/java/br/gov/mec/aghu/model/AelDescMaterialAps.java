package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Version;


import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the AEL_DESC_MATERIAL_APS database table.
 * 
 */
@Entity
@Table(name = "AEL_DESC_MATERIAL_APS", schema = "AGH")
@SequenceGenerator(name = "aelDmtSq1", sequenceName = "AEL_DMT_SQ1", allocationSize = 1)
public class AelDescMaterialAps extends BaseEntitySeq<Long> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3039622435091772883L;
	private Long seq;
	private Date criadoEm;
	private String descrMaterial;
	private AelExameAp aelExameAps;
	private RapServidores servidor;
	private Integer version;

	public AelDescMaterialAps(Long seq, String descrMaterial, AelExameAp aelExameAps, RapServidores servidor, Date criadoEm) {
		super();
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.descrMaterial = descrMaterial;
		this.aelExameAps = aelExameAps;
		this.servidor = servidor;
	}

	public AelDescMaterialAps() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aelDmtSq1")
	@Column(unique = true, nullable = false, precision = 14)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCR_MATERIAL", nullable = false, length = 2000)
	public String getDescrMaterial() {
		return this.descrMaterial;
	}

	public void setDescrMaterial(String descrMaterial) {
		this.descrMaterial = descrMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LUX_SEQ", nullable = false)
	@NotNull
	public AelExameAp getAelExameAps() {
		return this.aelExameAps;
	}

	public void setAelExameAps(AelExameAp aelExameAps) {
		this.aelExameAps = aelExameAps;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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

	public enum Fields {
		SEQ("seq"), DESCR_MATERIAL("descrMaterial"), AEL_EXAME_APS("aelExameAps"), AEL_EXAME_APS_SEQ("aelExameAps.seq"), SERVIDOR(
				"servidor"), CRIADO_EM("criadoEm"), LUX_SEQ("aelExameAps.seq"), SER_MATRICULA("servidor.id.matricula"), SER_VIN_CODIGO(
				"servidor.id.vinCodigo"), VERSION("version");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof AelDescMaterialAps)) {
			return false;
		}
		AelDescMaterialAps other = (AelDescMaterialAps) obj;
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