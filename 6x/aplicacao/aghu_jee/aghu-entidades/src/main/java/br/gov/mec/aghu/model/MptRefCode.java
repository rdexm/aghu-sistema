package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


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
@Table(name = "MPT_REF_CODES", schema = "AGH")
public class MptRefCode extends BaseEntityId<MptRefCodeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9132329091977913959L;
	private MptRefCodeId id;

	public MptRefCode() {
	}

	public MptRefCode(MptRefCodeId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "rvLowValue", column = @Column(name = "RV_LOW_VALUE", nullable = false, length = 240)),
			@AttributeOverride(name = "rvHighValue", column = @Column(name = "RV_HIGH_VALUE", length = 240)),
			@AttributeOverride(name = "rvAbbreviation", column = @Column(name = "RV_ABBREVIATION", length = 240)),
			@AttributeOverride(name = "rvDomain", column = @Column(name = "RV_DOMAIN", nullable = false, length = 100)),
			@AttributeOverride(name = "rvMeaning", column = @Column(name = "RV_MEANING", length = 240)),
			@AttributeOverride(name = "rvType", column = @Column(name = "RV_TYPE", length = 10)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public MptRefCodeId getId() {
		return this.id;
	}

	public void setId(MptRefCodeId id) {
		this.id = id;
	}

	public enum Fields {

		ID("id");

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
