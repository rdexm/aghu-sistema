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
@Table(name = "AEL_SUMARIO_EXAMES_MASC", schema = "AGH")
public class AelSumarioExameMasc extends BaseEntityId<AelSumarioExameMascId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6488445639027250737L;
	private AelSumarioExameMascId id;

	public AelSumarioExameMasc() {
	}

	public AelSumarioExameMasc(AelSumarioExameMascId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "prontuario", column = @Column(name = "PRONTUARIO")),
			@AttributeOverride(name = "ltoLtoId", column = @Column(name = "LTO_LTO_ID", length = 5)),
			@AttributeOverride(name = "recemNascido", column = @Column(name = "RECEM_NASCIDO", length = 1)),
			@AttributeOverride(name = "ufeEmaExaSigla", column = @Column(name = "UFE_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "ufeEmaManSeq", column = @Column(name = "UFE_EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "ufeUnfSeq", column = @Column(name = "UFE_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "pertenceSumario", column = @Column(name = "PERTENCE_SUMARIO", nullable = false, length = 1)),
			@AttributeOverride(name = "iseSoeSeqDept", column = @Column(name = "ISE_SOE_SEQ_DEPT")),
			@AttributeOverride(name = "iseSeqpDept", column = @Column(name = "ISE_SEQP_DEPT")),
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO")),
			@AttributeOverride(name = "dthrEventoLib", column = @Column(name = "DTHR_EVENTO_LIB", nullable = false, length = 29)),
			@AttributeOverride(name = "ordemRelatorio", column = @Column(name = "ORDEM_RELATORIO", nullable = false)),
			@AttributeOverride(name = "ordemAgrupamento", column = @Column(name = "ORDEM_AGRUPAMENTO", nullable = false)),
			@AttributeOverride(name = "turno", column = @Column(name = "TURNO")),
			@AttributeOverride(name = "zona", column = @Column(name = "ZONA", length = 10)),
			@AttributeOverride(name = "sala", column = @Column(name = "SALA")),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO")),
			@AttributeOverride(name = "serVinCodigoConsultado", column = @Column(name = "SER_VIN_CODIGO_CONSULTADO")),
			@AttributeOverride(name = "serMatriculaConsultado", column = @Column(name = "SER_MATRICULA_CONSULTADO")),
			@AttributeOverride(name = "dthrFim", column = @Column(name = "DTHR_FIM", length = 29)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AelSumarioExameMascId getId() {
		return this.id;
	}

	public void setId(AelSumarioExameMascId id) {
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
