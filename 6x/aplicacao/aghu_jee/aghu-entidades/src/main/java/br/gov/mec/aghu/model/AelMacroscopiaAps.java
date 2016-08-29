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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="aelLuoSq1", sequenceName="AGH.AEL_LUO_SQ1", allocationSize = 1)
@Table(name = "AEL_MACROSCOPIA_APS")
public class AelMacroscopiaAps extends BaseEntitySeq<Long> implements java.io.Serializable {

	private static final long serialVersionUID = -2350700536209342578L;
	private Long seq;
	private String macroscopia;
	private Date criadoEm;
	private AelExameAp aelExameAps;
	private RapServidores servidor;
	private Integer version;
	
	public AelMacroscopiaAps() {
		super();
	}

	public AelMacroscopiaAps(Long seq, String macroscopia, Date criadoEm,
			AelExameAp aelExameAps, RapServidores servidor) {
		super();
		this.seq = seq;
		this.macroscopia = macroscopia;
		this.criadoEm = criadoEm;
		this.aelExameAps = aelExameAps;
		this.servidor = servidor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLuoSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LUX_SEQ", nullable = false)
	public AelExameAp getAelExameAps() {
		return this.aelExameAps;
	}

	public void setAelExameAps(AelExameAp aelExameAps) {
		this.aelExameAps = aelExameAps;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "MACROSCOPIA", nullable = false, length = 32000)
	@Lob
	@Type(type="text")
	public String getMacroscopia() {
		return macroscopia;
	}

	public void setMacroscopia(String macroscopia) {
		this.macroscopia = macroscopia;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		SEQ("seq"),
		MACROSCOPIA("macroscopia"),
		AEL_EXAME_APS("aelExameAps"),
		AEL_EXAME_APS_SEQ("aelExameAps.seq"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		LUX_SEQ("aelExameAps.seq"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		VERSION("version");

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
		if (!(obj instanceof AelMacroscopiaAps)) {
			return false;
		}
		AelMacroscopiaAps other = (AelMacroscopiaAps) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}