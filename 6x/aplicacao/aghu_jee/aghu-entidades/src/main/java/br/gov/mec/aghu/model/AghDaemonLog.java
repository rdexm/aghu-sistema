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
@Table(name = "AGH_DAEMON_LOG", schema = "AGH")
public class AghDaemonLog extends BaseEntityId<AghDaemonLogId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2487914100747575440L;
	private AghDaemonLogId id;

	public AghDaemonLog() {
	}

	public AghDaemonLog(AghDaemonLogId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "lineCall", column = @Column(name = "LINE_CALL")),
			@AttributeOverride(name = "ownerCall", column = @Column(name = "OWNER_CALL", length = 50)),
			@AttributeOverride(name = "nameCall", column = @Column(name = "NAME_CALL", length = 50)),
			@AttributeOverride(name = "typeCall", column = @Column(name = "TYPE_CALL", length = 50)),
			@AttributeOverride(name = "objOrigLog", column = @Column(name = "OBJ_ORIG_LOG", length = 30)),
			@AttributeOverride(name = "objLineCall", column = @Column(name = "OBJ_LINE_CALL")),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", length = 29)),
			@AttributeOverride(name = "instance", column = @Column(name = "INSTANCE", length = 20)),
			@AttributeOverride(name = "tamTexto", column = @Column(name = "TAM_TEXTO")),
			@AttributeOverride(name = "texto", column = @Column(name = "TEXTO", length = 4000)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AghDaemonLogId getId() {
		return this.id;
	}

	public void setId(AghDaemonLogId id) {
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
