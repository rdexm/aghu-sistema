package br.gov.mec.aghu.model;

// Generated 18/03/2011 10:47:15 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "AEL_GRUPO_EXAME_PRIORIZAS", schema = "AGH")
public class AelGrupoExamePrioriza extends BaseEntityId<AelGrupoExamePriorizaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5467719258664813064L;
	private AelGrupoExamePriorizaId id;
	private Integer version;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AelGrupoExamePrioriza() {
	}

	public AelGrupoExamePrioriza(AelGrupoExamePriorizaId id, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "grxGrmSeq", column = @Column(name = "GRX_GRM_SEQ", nullable = false)),
			@AttributeOverride(name = "grxEmaExaSigla", column = @Column(name = "GRX_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "grxEmaManSeq", column = @Column(name = "GRX_EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "emaExaSigla", column = @Column(name = "EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "emaManSeq", column = @Column(name = "EMA_MAN_SEQ", nullable = false)) })
	public AelGrupoExamePriorizaId getId() {
		return this.id;
	}

	public void setId(AelGrupoExamePriorizaId id) {
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

		EMA_EXA_SIGLA("id.emaExaSigla"),
		EMA_MAN_SEQ("id.emaManSeq");

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
		if (!(obj instanceof AelGrupoExamePrioriza)) {
			return false;
		}
		AelGrupoExamePrioriza other = (AelGrupoExamePrioriza) obj;
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