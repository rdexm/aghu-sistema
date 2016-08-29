package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "RAP_TMP_GFIP", schema = "AGH")
public class RapTmpGfip extends BaseEntityId<RapTmpGfipId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1681331858032024785L;
	private RapTmpGfipId id;
	private Integer version;
	private Date dataFim;

	public RapTmpGfip() {
	}

	public RapTmpGfip(RapTmpGfipId id) {
		this.id = id;
	}

	public RapTmpGfip(RapTmpGfipId id, Date dataFim) {
		this.id = id;
		this.dataFim = dataFim;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "codStarh", column = @Column(name = "COD_STARH", nullable = false)),
			@AttributeOverride(name = "dataInicio", column = @Column(name = "DATA_INICIO", nullable = false, length = 29)),
			@AttributeOverride(name = "gfip", column = @Column(name = "GFIP", nullable = false)) })
	public RapTmpGfipId getId() {
		return this.id;
	}

	public void setId(RapTmpGfipId id) {
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
	@Column(name = "DATA_FIM", length = 29)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		DATA_FIM("dataFim");

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
		if (!(obj instanceof RapTmpGfip)) {
			return false;
		}
		RapTmpGfip other = (RapTmpGfip) obj;
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
