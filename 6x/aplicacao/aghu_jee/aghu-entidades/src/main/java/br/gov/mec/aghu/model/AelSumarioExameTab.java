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
@Table(name = "AEL_SUMARIO_EXAMES_TAB", schema = "AGH")
public class AelSumarioExameTab extends BaseEntityId<AelSumarioExameTabId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3382009698430812229L;
	private AelSumarioExameTabId id;

	public AelSumarioExameTab() {
	}

	public AelSumarioExameTab(AelSumarioExameTabId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "prontuario", column = @Column(name = "PRONTUARIO")),
			@AttributeOverride(name = "ltoLtoId", column = @Column(name = "LTO_LTO_ID", length = 5)),
			@AttributeOverride(name = "recemNascido", column = @Column(name = "RECEM_NASCIDO", length = 1)),
			@AttributeOverride(name = "ufeEmaExaSigla", column = @Column(name = "UFE_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "ufeEmaManSeq", column = @Column(name = "UFE_EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "ufeUnfSeq", column = @Column(name = "UFE_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "pertenceSumario", column = @Column(name = "PERTENCE_SUMARIO", nullable = false, length = 1)),
			@AttributeOverride(name = "iseSoeSeqDept", column = @Column(name = "ISE_SOE_SEQ_DEPT")),
			@AttributeOverride(name = "iseSeqpDept", column = @Column(name = "ISE_SEQP_DEPT")),
			@AttributeOverride(name = "calSeq", column = @Column(name = "CAL_SEQ", nullable = false)),
			@AttributeOverride(name = "calNome", column = @Column(name = "CAL_NOME", nullable = false, length = 100)),
			@AttributeOverride(name = "reeValor", column = @Column(name = "REE_VALOR", precision = 17, scale = 17)),
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false)),
			@AttributeOverride(name = "cacSeq", column = @Column(name = "CAC_SEQ")),
			@AttributeOverride(name = "rcdGtcSeq", column = @Column(name = "RCD_GTC_SEQ")),
			@AttributeOverride(name = "rcdSeqp", column = @Column(name = "RCD_SEQP")),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO")),
			@AttributeOverride(name = "dthrEventoAreaExec", column = @Column(name = "DTHR_EVENTO_AREA_EXEC", nullable = false, length = 29)),
			@AttributeOverride(name = "ordem", column = @Column(name = "ORDEM")),
			@AttributeOverride(name = "calNomeSumario", column = @Column(name = "CAL_NOME_SUMARIO", length = 30)),
			@AttributeOverride(name = "indImprime", column = @Column(name = "IND_IMPRIME", length = 1)),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO")),
			@AttributeOverride(name = "serVinCodigoConsultado", column = @Column(name = "SER_VIN_CODIGO_CONSULTADO")),
			@AttributeOverride(name = "serMatriculaConsultado", column = @Column(name = "SER_MATRICULA_CONSULTADO")),
			@AttributeOverride(name = "dthrFim", column = @Column(name = "DTHR_FIM", length = 29)),
			@AttributeOverride(name = "secaoProntuario", column = @Column(name = "SECAO_PRONTUARIO")),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AelSumarioExameTabId getId() {
		return this.id;
	}

	public void setId(AelSumarioExameTabId id) {
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
