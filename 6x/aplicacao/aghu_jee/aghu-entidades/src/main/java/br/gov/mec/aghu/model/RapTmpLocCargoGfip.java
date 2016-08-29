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
@Table(name = "RAP_TMP_LOC_CARGOS_GFIP", schema = "AGH")
public class RapTmpLocCargoGfip extends BaseEntityId<RapTmpLocCargoGfipId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6754477077526053725L;
	private RapTmpLocCargoGfipId id;

	public RapTmpLocCargoGfip() {
	}

	public RapTmpLocCargoGfip(RapTmpLocCargoGfipId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "codStarh", column = @Column(name = "COD_STARH", nullable = false)),
			@AttributeOverride(name = "dataInicio", column = @Column(name = "DATA_INICIO", nullable = false, length = 29)),
			@AttributeOverride(name = "dataFim", column = @Column(name = "DATA_FIM", length = 29)),
			@AttributeOverride(name = "codOrganograma", column = @Column(name = "COD_ORGANOGRAMA", nullable = false)),
			@AttributeOverride(name = "local", column = @Column(name = "LOCAL", length = 60)),
			@AttributeOverride(name = "codClh", column = @Column(name = "COD_CLH", nullable = false)),
			@AttributeOverride(name = "cargo", column = @Column(name = "CARGO", length = 100)),
			@AttributeOverride(name = "codCbo2002", column = @Column(name = "COD_CBO_2002", length = 8)),
			@AttributeOverride(name = "gfip", column = @Column(name = "GFIP", nullable = false)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public RapTmpLocCargoGfipId getId() {
		return this.id;
	}

	public void setId(RapTmpLocCargoGfipId id) {
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
