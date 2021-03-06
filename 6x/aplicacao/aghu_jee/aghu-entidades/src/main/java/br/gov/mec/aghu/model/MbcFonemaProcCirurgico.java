package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MbcFonemaProcCirurgico generated by hbm2java
 */
@Entity
@Table(name = "MBC_FONEMAS_PROC_CIRURGICO", schema = "AGH")
public class MbcFonemaProcCirurgico extends BaseEntityId<MbcFonemaProcCirurgicoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4915538515436469176L;
	private MbcFonemaProcCirurgicoId id;
	private Integer version;
	private AghFonema aghFonema;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;

	public MbcFonemaProcCirurgico() {
	}

	public MbcFonemaProcCirurgico(MbcFonemaProcCirurgicoId id, AghFonema aghFonema,
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.id = id;
		this.aghFonema = aghFonema;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false)),
			@AttributeOverride(name = "fonFonema", column = @Column(name = "FON_FONEMA", nullable = false, length = 6)) })
	public MbcFonemaProcCirurgicoId getId() {
		return this.id;
	}

	public void setId(MbcFonemaProcCirurgicoId id) {
		this.id = id;
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
	@JoinColumn(name = "FON_FONEMA", nullable = false, insertable = false, updatable = false)
	public AghFonema getAghFonema() {
		return this.aghFonema;
	}

	public void setAghFonema(AghFonema aghFonema) {
		this.aghFonema = aghFonema;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_FONEMAS("aghFonema"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos");

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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof MbcFonemaProcCirurgico)) {
			return false;
		}
		MbcFonemaProcCirurgico other = (MbcFonemaProcCirurgico) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
