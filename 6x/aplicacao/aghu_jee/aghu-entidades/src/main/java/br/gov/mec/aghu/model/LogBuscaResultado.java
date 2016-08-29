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
@Table(name = "LOG_BUSCA_RESULTADO", schema = "AGH")
public class LogBuscaResultado extends BaseEntityId<LogBuscaResultadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 129387318957304426L;
	private LogBuscaResultadoId id;

	public LogBuscaResultado() {
	}

	public LogBuscaResultado(LogBuscaResultadoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "seq", column = @Column(name = "SEQ")),
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ")),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP")),
			@AttributeOverride(name = "pclVelEmaExaSigla", column = @Column(name = "PCL_VEL_EMA_EXA_SIGLA", length = 5)),
			@AttributeOverride(name = "pclVelEmaManSeq", column = @Column(name = "PCL_VEL_EMA_MAN_SEQ")),
			@AttributeOverride(name = "pclVelSeqp", column = @Column(name = "PCL_VEL_SEQP")),
			@AttributeOverride(name = "pclCalSeq", column = @Column(name = "PCL_CAL_SEQ")),
			@AttributeOverride(name = "pclSeqp", column = @Column(name = "PCL_SEQP")),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP")),
			@AttributeOverride(name = "valor", column = @Column(name = "VALOR")),
			@AttributeOverride(name = "rcdGtcSeq", column = @Column(name = "RCD_GTC_SEQ")),
			@AttributeOverride(name = "rcdSeqp", column = @Column(name = "RCD_SEQP")),
			@AttributeOverride(name = "cacSeq", column = @Column(name = "CAC_SEQ")) })
	public LogBuscaResultadoId getId() {
		return this.id;
	}

	public void setId(LogBuscaResultadoId id) {
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
