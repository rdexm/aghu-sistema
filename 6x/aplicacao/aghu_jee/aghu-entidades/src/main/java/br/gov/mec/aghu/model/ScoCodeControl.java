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
@Table(name = "SCO_CODE_CONTROLS", schema = "AGH")
public class ScoCodeControl extends BaseEntityId<ScoCodeControlId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -753613165194224647L;
	private ScoCodeControlId id;

	public ScoCodeControl() {
	}

	public ScoCodeControl(ScoCodeControlId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ccDomain", column = @Column(name = "CC_DOMAIN", nullable = false, length = 30)),
			@AttributeOverride(name = "ccComment", column = @Column(name = "CC_COMMENT", length = 240)),
			@AttributeOverride(name = "ccNextValue", column = @Column(name = "CC_NEXT_VALUE", nullable = false)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public ScoCodeControlId getId() {
		return this.id;
	}

	public void setId(ScoCodeControlId id) {
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
