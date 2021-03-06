package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

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


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * FatItensGruposAtend generated by hbm2java
 */
@Entity
@Table(name = "FAT_ITENS_GRUPOS_ATEND", schema = "AGH")
public class FatItemGrupoAtend extends BaseEntityId<FatItemGrupoAtendId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3196086318829355531L;
	private FatItemGrupoAtendId id;
	private FatGrupoAtendimento fatGrupoAtendimento;
	private Integer version;

	public FatItemGrupoAtend() {
	}

	public FatItemGrupoAtend(FatItemGrupoAtendId id,
			FatGrupoAtendimento fatGrupoAtendimento) {
		this.id = id;
		this.fatGrupoAtendimento = fatGrupoAtendimento;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "graCnvCodigo", column = @Column(name = "GRA_CNV_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "graSeqp", column = @Column(name = "GRA_SEQP", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "iphPhoSeq", column = @Column(name = "IPH_PHO_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "iphSeq", column = @Column(name = "IPH_SEQ", nullable = false, precision = 8, scale = 0)) })
	public FatItemGrupoAtendId getId() {
		return this.id;
	}

	public void setId(FatItemGrupoAtendId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "GRA_CNV_CODIGO", referencedColumnName = "CNV_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "GRA_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public FatGrupoAtendimento getFatGrupoAtendimento() {
		return this.fatGrupoAtendimento;
	}

	public void setFatGrupoAtendimento(
			FatGrupoAtendimento fatGrupoAtendimento) {
		this.fatGrupoAtendimento = fatGrupoAtendimento;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		FatItemGrupoAtend other = (FatItemGrupoAtend) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {

		IPH_SEQ("id.iphSeq"),
		IPH_PHO_SEQ("id.iphPhoSeq"),
		GRA_CNV_CODIGO("id.graCnvCodigo"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
