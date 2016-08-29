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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "AEL_NOMENC_TOPO_CIDS", schema = "AGH")
public class AelNomencTopoCid extends BaseEntityId<AelNomencTopoCidId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5322004065480142218L;
	private AelNomencTopoCidId id;
	private Integer version;
	private AghCid aghCid;
	private RapServidores rapServidores;
	private AelNomenclaturaEspecs aelNomenclaturaEspecs;
	private AelTopografiaAparelhos aelTopografiaAparelhos;
	private Date criadoEm;

	public AelNomencTopoCid() {
	}

	public AelNomencTopoCid(AelNomencTopoCidId id, AghCid aghCid, RapServidores rapServidores,
			AelNomenclaturaEspecs aelNomenclaturaEspecs, AelTopografiaAparelhos aelTopografiaAparelhos, Date criadoEm) {
		this.id = id;
		this.aghCid = aghCid;
		this.rapServidores = rapServidores;
		this.aelNomenclaturaEspecs = aelNomenclaturaEspecs;
		this.aelTopografiaAparelhos = aelTopografiaAparelhos;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "lueLugSeq", column = @Column(name = "LUE_LUG_SEQ", nullable = false)),
			@AttributeOverride(name = "lueSeqp", column = @Column(name = "LUE_SEQP", nullable = false)),
			@AttributeOverride(name = "luaLutSeq", column = @Column(name = "LUA_LUT_SEQ", nullable = false)),
			@AttributeOverride(name = "luaSeqp", column = @Column(name = "LUA_SEQP", nullable = false)),
			@AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)) })
	public AelNomencTopoCidId getId() {
		return this.id;
	}

	public void setId(AelNomencTopoCidId id) {
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
	@JoinColumn(name = "CID_SEQ", nullable = false, insertable = false, updatable = false)
	public AghCid getAghCid() {
		return this.aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
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
	@JoinColumns({
			@JoinColumn(name = "LUE_LUG_SEQ", referencedColumnName = "LUG_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "LUE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelNomenclaturaEspecs getAelNomenclaturaEspecs() {
		return this.aelNomenclaturaEspecs;
	}

	public void setAelNomenclaturaEspecs(AelNomenclaturaEspecs aelNomenclaturaEspecs) {
		this.aelNomenclaturaEspecs = aelNomenclaturaEspecs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "LUA_LUT_SEQ", referencedColumnName = "LUT_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "LUA_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelTopografiaAparelhos getAelTopografiaAparelhos() {
		return this.aelTopografiaAparelhos;
	}

	public void setAelTopografiaAparelhos(AelTopografiaAparelhos aelTopografiaAparelhos) {
		this.aelTopografiaAparelhos = aelTopografiaAparelhos;
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

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		RAP_SERVIDORES("rapServidores"),
		AEL_NOMENCLATURA_ESPECS("aelNomenclaturaEspecs"),
		AEL_TOPOGRAFIA_APARELHOS("aelTopografiaAparelhos"),
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
		if (!(obj instanceof AelNomencTopoCid)) {
			return false;
		}
		AelNomencTopoCid other = (AelNomencTopoCid) obj;
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
