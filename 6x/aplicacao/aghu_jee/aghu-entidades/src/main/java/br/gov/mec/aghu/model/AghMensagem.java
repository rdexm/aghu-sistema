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
@Table(name = "AGH_MENSAGEM", schema = "AGH")
public class AghMensagem extends BaseEntityId<AghMensagemId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8513948308801674940L;
	private AghMensagemId id;

	public AghMensagem() {
	}

	public AghMensagem(AghMensagemId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "PTexto", column = @Column(name = "P_TEXTO", length = 2000)),
			@AttributeOverride(name = "PNomeArq", column = @Column(name = "P_NOME_ARQ", length = 2000)),
			@AttributeOverride(name = "PTipoGrav", column = @Column(name = "P_TIPO_GRAV", length = 2000)),
			@AttributeOverride(name = "PCaminho", column = @Column(name = "P_CAMINHO", length = 2000)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AghMensagemId getId() {
		return this.id;
	}

	public void setId(AghMensagemId id) {
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
