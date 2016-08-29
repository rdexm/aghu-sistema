package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Removida propriedade "@Immutable".
 * Tela implementada no AGHU.
 * Estória: #24940 
 * 
 * @author aghu
 * 
 */

@Entity
@Table(name = "MBC_ESCALA_PROF_UNID_CIRGS", schema = "AGH")
public class MbcEscalaProfUnidCirg extends BaseEntityId<MbcEscalaProfUnidCirgId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7014497047377485860L;
	private MbcEscalaProfUnidCirgId id;
	private Integer version;
	private RapServidores rapServidores;
	private MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs;
	private Date criadoEm;

	public MbcEscalaProfUnidCirg() {
	}

	public MbcEscalaProfUnidCirg(MbcEscalaProfUnidCirgId id, RapServidores rapServidores,
			MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mbcCaracteristicaSalaCirg = mbcCaracteristicaSalaCirg;
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
	}

	public MbcEscalaProfUnidCirg(MbcEscalaProfUnidCirgId id, RapServidores rapServidores,
			MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mbcCaracteristicaSalaCirg = mbcCaracteristicaSalaCirg;
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "casSeq", column = @Column(name = "CAS_SEQ", nullable = false)),
			@AttributeOverride(name = "pucSerMatricula", column = @Column(name = "PUC_SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "pucSerVinCodigo", column = @Column(name = "PUC_SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "pucUnfSeq", column = @Column(name = "PUC_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "pucIndFuncaoProf", column = @Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)) })
	public MbcEscalaProfUnidCirgId getId() {
		return this.id;
	}

	public void setId(MbcEscalaProfUnidCirgId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAS_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcCaracteristicaSalaCirg getMbcCaracteristicaSalaCirg() {
		return this.mbcCaracteristicaSalaCirg;
	}

	public void setMbcCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg) {
		this.mbcCaracteristicaSalaCirg = mbcCaracteristicaSalaCirg;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PUC_SER_MATRICULA", referencedColumnName = "SER_MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_IND_FUNCAO_PROF", referencedColumnName = "IND_FUNCAO_PROF", nullable = false, insertable = false, updatable = false) })
	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgs() {
		return this.mbcProfAtuaUnidCirgs;
	}

	public void setMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MBC_CARACTERISTICA_SALA_CIRGS("mbcCaracteristicaSalaCirg"),
		MBC_PROF_ATUA_UNID_CIRGS("mbcProfAtuaUnidCirgs"),
		CRIADO_EM("criadoEm"),
		IND_FUNCAO_PROF("id.pucIndFuncaoProf"),
		SERVIDOR_MATRICULA("id.pucSerMatricula"),
		SERVIDOR_CODIGO("id.pucSerVinCodigo"),
		UNF_SEQ("id.pucUnfSeq");

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
		if (!(obj instanceof MbcEscalaProfUnidCirg)) {
			return false;
		}
		MbcEscalaProfUnidCirg other = (MbcEscalaProfUnidCirg) obj;
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
