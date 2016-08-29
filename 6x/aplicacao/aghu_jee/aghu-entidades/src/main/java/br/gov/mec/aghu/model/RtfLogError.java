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
@Table(name = "RTF_LOG_ERRORS", schema = "AGH")
public class RtfLogError extends BaseEntityId<RtfLogErrorId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1085032208397189342L;
	private RtfLogErrorId id;

	public RtfLogError() {
	}

	public RtfLogError(RtfLogErrorId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "erro", column = @Column(name = "ERRO", length = 300)),
			@AttributeOverride(name = "modulo", column = @Column(name = "MODULO", length = 30)),
			@AttributeOverride(name = "programa", column = @Column(name = "PROGRAMA", length = 70)),
			@AttributeOverride(name = "indRespProc", column = @Column(name = "IND_RESP_PROC", length = 4)),
			@AttributeOverride(name = "situacao", column = @Column(name = "SITUACAO", length = 1)),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", length = 29)),
			@AttributeOverride(name = "qtd", column = @Column(name = "QTD")),
			@AttributeOverride(name = "indPrincipal", column = @Column(name = "IND_PRINCIPAL", length = 1)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", length = 4000)),
			@AttributeOverride(name = "query", column = @Column(name = "QUERY", length = 4000)),
			@AttributeOverride(name = "posIni", column = @Column(name = "POS_INI")),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public RtfLogErrorId getId() {
		return this.id;
	}

	public void setId(RtfLogErrorId id) {
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
