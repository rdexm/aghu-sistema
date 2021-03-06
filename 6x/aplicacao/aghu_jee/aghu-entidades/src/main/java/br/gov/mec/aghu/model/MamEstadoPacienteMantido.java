package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MamEstadoPacienteMantido generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mamEpmSq1", sequenceName="AGH.MAM_EPM_SQ1", allocationSize = 1)
@Table(name = "MAM_ESTADO_PACIENTE_MANTIDOS", schema = "AGH")
public class MamEstadoPacienteMantido extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 595230146534485945L;
	private Long seq;
	private Integer version;
	private RapServidores rapServidores;
	private MamEstadoPaciente mamEstadoPaciente;
	private MamRegistro mamRegistro;
	private Date criadoEm;

	public MamEstadoPacienteMantido() {
	}

	public MamEstadoPacienteMantido(Long seq, RapServidores rapServidores, MamEstadoPaciente mamEstadoPaciente, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.mamEstadoPaciente = mamEstadoPaciente;
		this.criadoEm = criadoEm;
	}

	public MamEstadoPacienteMantido(Long seq, RapServidores rapServidores, MamEstadoPaciente mamEstadoPaciente,
			MamRegistro mamRegistro, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.mamEstadoPaciente = mamEstadoPaciente;
		this.mamRegistro = mamRegistro;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamEpmSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
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
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESA_SEQ", nullable = false)
	public MamEstadoPaciente getMamEstadoPaciente() {
		return this.mamEstadoPaciente;
	}

	public void setMamEstadoPaciente(MamEstadoPaciente mamEstadoPaciente) {
		this.mamEstadoPaciente = mamEstadoPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RGT_SEQ")
	public MamRegistro getMamRegistro() {
		return this.mamRegistro;
	}

	public void setMamRegistro(MamRegistro mamRegistro) {
		this.mamRegistro = mamRegistro;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MAM_ESTADO_PACIENTES("mamEstadoPaciente"),
		MAM_REGISTRO("mamRegistro"),
		MAM_REGISTRO_SEQ("mamRegistro.seq"),
		CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof MamEstadoPacienteMantido)) {
			return false;
		}
		MamEstadoPacienteMantido other = (MamEstadoPacienteMantido) obj;
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
