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
@Table(name = "AEL_FIGURA_LAUDO_UNICOS", schema = "AGH")
public class AelFiguraLaudoUnico extends BaseEntityId<AelFiguraLaudoUnicoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8314595338167474732L;
	private AelFiguraLaudoUnicoId id;
	private Integer version;
	private AelCadFigLaudoUnico aelCadFigLaudoUnico;
	private byte[] figura;

	public AelFiguraLaudoUnico() {
	}

	public AelFiguraLaudoUnico(AelFiguraLaudoUnicoId id, AelCadFigLaudoUnico aelCadFigLaudoUnico) {
		this.id = id;
		this.aelCadFigLaudoUnico = aelCadFigLaudoUnico;
	}

	public AelFiguraLaudoUnico(AelFiguraLaudoUnicoId id, AelCadFigLaudoUnico aelCadFigLaudoUnico, byte[] figura) {
		this.id = id;
		this.aelCadFigLaudoUnico = aelCadFigLaudoUnico;
		this.figura = figura;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "lu7Seq", column = @Column(name = "LU7_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelFiguraLaudoUnicoId getId() {
		return this.id;
	}

	public void setId(AelFiguraLaudoUnicoId id) {
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
	@JoinColumn(name = "LU7_SEQ", nullable = false, insertable = false, updatable = false)
	public AelCadFigLaudoUnico getAelCadFigLaudoUnico() {
		return this.aelCadFigLaudoUnico;
	}

	public void setAelCadFigLaudoUnico(AelCadFigLaudoUnico aelCadFigLaudoUnico) {
		this.aelCadFigLaudoUnico = aelCadFigLaudoUnico;
	}

	@Column(name = "FIGURA")
	public byte[] getFigura() {
		return this.figura;
	}

	public void setFigura(byte[] figura) {
		this.figura = figura;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AEL_CAD_FIG_LAUDO_UNICOS("aelCadFigLaudoUnico"),
		FIGURA("figura");

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
		if (!(obj instanceof AelFiguraLaudoUnico)) {
			return false;
		}
		AelFiguraLaudoUnico other = (AelFiguraLaudoUnico) obj;
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
