package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "AGH_CID_MORFOLOGIAS", schema = "AGH")
public class AghCidMorfologia extends BaseEntityId<AghCidMorfologiaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2786112855863825248L;
	private AghCidMorfologiaId id;
	private Integer version;
	private AghCid aghCid;
	private AghMorfologia aghMorfologia;

	public AghCidMorfologia() {
	}

	public AghCidMorfologia(AghCidMorfologiaId id, AghCid aghCid, AghMorfologia aghMorfologia) {
		this.id = id;
		this.aghCid = aghCid;
		this.aghMorfologia = aghMorfologia;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)),
			@AttributeOverride(name = "mrfSeq", column = @Column(name = "MRF_SEQ", nullable = false)),
			@AttributeOverride(name = "mrfGmoSeq", column = @Column(name = "MRF_GMO_SEQ", nullable = false)) })
	public AghCidMorfologiaId getId() {
		return this.id;
	}

	public void setId(AghCidMorfologiaId id) {
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
	@JoinColumns({
			@JoinColumn(name = "MRF_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "MRF_GMO_SEQ", referencedColumnName = "GMO_SEQ", nullable = false, insertable = false, updatable = false) })
	public AghMorfologia getAghMorfologia() {
		return this.aghMorfologia;
	}

	public void setAghMorfologia(AghMorfologia aghMorfologia) {
		this.aghMorfologia = aghMorfologia;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		AGH_MORFOLOGIA("aghMorfologia");

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
		if (!(obj instanceof AghCidMorfologia)) {
			return false;
		}
		AghCidMorfologia other = (AghCidMorfologia) obj;
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
