package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MpmControlImpParada generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mpmPdaSq1", sequenceName="AGH.MPM_PDA_SQ1", allocationSize = 1)
@Table(name = "MPM_CONTROL_IMP_PARADAS", schema = "AGH")
public class MpmControlImpParada extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6627800740940236772L;
	private Integer seq;
	private AghAtendimentos aghAtendimentos;
	private RapServidores rapServidores;
	private String ltoLtoId;
	private Integer prontuario;
	private Date criadoEm;

	public MpmControlImpParada() {
	}

	public MpmControlImpParada(Integer seq, AghAtendimentos aghAtendimentos, RapServidores rapServidores, Date criadoEm) {
		this.seq = seq;
		this.aghAtendimentos = aghAtendimentos;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
	}

	public MpmControlImpParada(Integer seq, AghAtendimentos aghAtendimentos, String ltoLtoId,
			Integer prontuario, RapServidores rapServidores,
			Date criadoEm) {
		this.seq = seq;
		this.aghAtendimentos = aghAtendimentos;
		this.ltoLtoId = ltoLtoId;
		this.prontuario = prontuario;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmPdaSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAghAtendimentos() {
		return this.aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	@Column(name = "LTO_LTO_ID", length = 14)
	@Length(max = 14)
	public String getLtoLtoId() {
		return this.ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "PRONTUARIO", precision = 8, scale = 0)
	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public enum Fields {
		
		ATENDIMENTO("aghAtendimentos"),
		RAP_SERVIDOR("rapServidores"),
		PRONTUARIO("prontuario"),
		LTO_LTO_ID("ltoLtoId"),
		CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
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
		if (!(obj instanceof MpmControlImpParada)) {
			return false;
		}
		MpmControlImpParada other = (MpmControlImpParada) obj;
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
