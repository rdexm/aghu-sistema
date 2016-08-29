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
@Table(name = "AEL_SUMARIO_LEGENDA", schema = "AGH")
public class AelSumarioLegenda extends BaseEntityId<AelSumarioLegendaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2526904793007623229L;
	private AelSumarioLegendaId id;

	public AelSumarioLegenda() {
	}

	public AelSumarioLegenda(AelSumarioLegendaId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "prontuario", column = @Column(name = "PRONTUARIO")),
			@AttributeOverride(name = "recemNascido", column = @Column(name = "RECEM_NASCIDO", length = 1)),
			@AttributeOverride(name = "pertenceSumario", column = @Column(name = "PERTENCE_SUMARIO", nullable = false, length = 1)),
			@AttributeOverride(name = "dthrEvento", column = @Column(name = "DTHR_EVENTO", nullable = false, length = 29)),
			@AttributeOverride(name = "numeroLegenda", column = @Column(name = "NUMERO_LEGENDA", nullable = false)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", nullable = false, length = 120)),
			@AttributeOverride(name = "grupoLegenda", column = @Column(name = "GRUPO_LEGENDA", nullable = false)),
			@AttributeOverride(name = "ltoLtoId", column = @Column(name = "LTO_LTO_ID", length = 5)),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO")),
			@AttributeOverride(name = "serVinCodigoConsultado", column = @Column(name = "SER_VIN_CODIGO_CONSULTADO")),
			@AttributeOverride(name = "serMatriculaConsultado", column = @Column(name = "SER_MATRICULA_CONSULTADO")),
			@AttributeOverride(name = "dthrFim", column = @Column(name = "DTHR_FIM", length = 29)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AelSumarioLegendaId getId() {
		return this.id;
	}

	public void setId(AelSumarioLegendaId id) {
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
