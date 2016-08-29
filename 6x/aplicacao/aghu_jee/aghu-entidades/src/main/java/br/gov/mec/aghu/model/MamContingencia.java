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
@Table(name = "MAM_CONTINGENCIAS", schema = "AGH")
public class MamContingencia extends BaseEntityId<MamContingenciaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8655067040288815990L;
	private MamContingenciaId id;

	public MamContingencia() {
	}

	public MamContingencia(MamContingenciaId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "data", column = @Column(name = "DATA", nullable = false, length = 29)),
			@AttributeOverride(name = "ba", column = @Column(name = "BA")),
			@AttributeOverride(name = "texto", column = @Column(name = "TEXTO", length = 4000)),
			@AttributeOverride(name = "usuario", column = @Column(name = "USUARIO", nullable = false, length = 240)),
			@AttributeOverride(name = "indLoginOk", column = @Column(name = "IND_LOGIN_OK", nullable = false, length = 1)),
			@AttributeOverride(name = "indAtualizado", column = @Column(name = "IND_ATUALIZADO", nullable = false, length = 1)),
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ")),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public MamContingenciaId getId() {
		return this.id;
	}

	public void setId(MamContingenciaId id) {
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
