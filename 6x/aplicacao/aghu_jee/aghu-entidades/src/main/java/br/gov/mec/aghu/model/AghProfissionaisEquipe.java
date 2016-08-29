package br.gov.mec.aghu.model;

// Generated 14/11/2011 17:39:15 by Hibernate Tools 3.4.0.CR1

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
 * AghProfissionaisEquipe generated by hbm2java
 */

@Entity
@Table(name = "AGH_PROFISSIONAIS_EQUIPE", schema = "AGH")
public class AghProfissionaisEquipe extends BaseEntityId<AghProfissionaisEquipeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2950459964350856099L;
	private AghProfissionaisEquipeId id;
	private AghEquipes equipe;
	private RapServidores servidor;
	private Integer version;
	

	public AghProfissionaisEquipe() {
	}

	public AghProfissionaisEquipe(AghProfissionaisEquipeId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "eqpSeq", column = @Column(name = "EQP_SEQ", nullable = false, precision = 4, scale = 0)) })
	public AghProfissionaisEquipeId getId() {
		return this.id;
	}

	public void setId(AghProfissionaisEquipeId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQP_SEQ", referencedColumnName="SEQ", nullable = false, insertable=false, updatable=false)
	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false, insertable=false, updatable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false, insertable=false, updatable=false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	
	public enum Fields {
		SERVIDOR("servidor"), 
		EQUIPE("equipe"),
		EQUIPE_SEQ("equipe.seq"),
		EQP_SEQ("id.eqpSeq");

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
		if (!(obj instanceof AghProfissionaisEquipe)) {
			return false;
		}
		AghProfissionaisEquipe other = (AghProfissionaisEquipe) obj;
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