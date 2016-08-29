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
@Table(name = "AGH_STATUS_SGA", schema = "AGH")
public class AghStatusSga extends BaseEntityId<AghStatusSgaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1209116664841112983L;
	private AghStatusSgaId id;

	public AghStatusSga() {
	}

	public AghStatusSga(AghStatusSgaId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "tamSgaMb", column = @Column(name = "TAM_SGA_MB", nullable = false, precision = 17, scale = 17)),
			@AttributeOverride(name = "livre", column = @Column(name = "LIVRE", nullable = false, precision = 17, scale = 17)),
			@AttributeOverride(name = "data", column = @Column(name = "DATA", nullable = false, length = 29)),
			@AttributeOverride(name = "tipo", column = @Column(name = "TIPO", length = 1)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AghStatusSgaId getId() {
		return this.id;
	}

	public void setId(AghStatusSgaId id) {
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
