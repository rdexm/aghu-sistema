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
@Table(name = "RAP_TMP_REFEITORIO", schema = "AGH")
public class RapTmpRefeitorio extends BaseEntityId<RapTmpRefeitorioId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7183039614557869404L;
	private RapTmpRefeitorioId id;

	public RapTmpRefeitorio() {
	}

	public RapTmpRefeitorio(RapTmpRefeitorioId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "codStarh", column = @Column(name = "COD_STARH", nullable = false)),
			@AttributeOverride(name = "dtRef", column = @Column(name = "DT_REF", nullable = false, length = 10)),
			@AttributeOverride(name = "horario", column = @Column(name = "HORARIO", nullable = false, length = 4)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public RapTmpRefeitorioId getId() {
		return this.id;
	}

	public void setId(RapTmpRefeitorioId id) {
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
