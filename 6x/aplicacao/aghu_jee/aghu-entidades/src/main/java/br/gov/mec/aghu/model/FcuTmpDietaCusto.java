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
@Table(name = "FCU_TMP_DIETAS_CUSTOS", schema = "AGH")
public class FcuTmpDietaCusto extends BaseEntityId<FcuTmpDietaCustoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737648123877567744L;
	private FcuTmpDietaCustoId id;

	public FcuTmpDietaCusto() {
	}

	public FcuTmpDietaCusto(FcuTmpDietaCustoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)),
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ")),
			@AttributeOverride(name = "gqdSeq", column = @Column(name = "GQD_SEQ")),
			@AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ")),
			@AttributeOverride(name = "profissional", column = @Column(name = "PROFISSIONAL", length = 9)),
			@AttributeOverride(name = "espSeq", column = @Column(name = "ESP_SEQ")),
			@AttributeOverride(name = "gpgCodigo", column = @Column(name = "GPG_CODIGO")),
			@AttributeOverride(name = "cpgCodigo", column = @Column(name = "CPG_CODIGO")),
			@AttributeOverride(name = "hauSeq", column = @Column(name = "HAU_SEQ")),
			@AttributeOverride(name = "refSeq", column = @Column(name = "REF_SEQ")),
			@AttributeOverride(name = "tipo", column = @Column(name = "TIPO", length = 15)),
			@AttributeOverride(name = "total", column = @Column(name = "TOTAL")),
			@AttributeOverride(name = "dtReferencia", column = @Column(name = "DT_REFERENCIA", length = 29)),
			@AttributeOverride(name = "dtCompetencia", column = @Column(name = "DT_COMPETENCIA", length = 29)),
			@AttributeOverride(name = "peso", column = @Column(name = "PESO", precision = 17, scale = 17)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", length = 60)),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO")),
			@AttributeOverride(name = "matriculaProf", column = @Column(name = "MATRICULA_PROF")),
			@AttributeOverride(name = "vinculoProf", column = @Column(name = "VINCULO_PROF")),
			@AttributeOverride(name = "cpcSeq", column = @Column(name = "CPC_SEQ")),
			@AttributeOverride(name = "gcdSeq", column = @Column(name = "GCD_SEQ")),
			@AttributeOverride(name = "cidCodigo", column = @Column(name = "CID_CODIGO", length = 5)),
			@AttributeOverride(name = "atiSeq", column = @Column(name = "ATI_SEQ")),
			@AttributeOverride(name = "ftdSeq", column = @Column(name = "FTD_SEQ")),
			@AttributeOverride(name = "atuSeq", column = @Column(name = "ATU_SEQ")),
			@AttributeOverride(name = "ssm", column = @Column(name = "SSM")),
			@AttributeOverride(name = "cnvCodigo", column = @Column(name = "CNV_CODIGO")),
			@AttributeOverride(name = "gccSeq", column = @Column(name = "GCC_SEQ")),
			@AttributeOverride(name = "cctCodigo", column = @Column(name = "CCT_CODIGO")),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public FcuTmpDietaCustoId getId() {
		return this.id;
	}

	public void setId(FcuTmpDietaCustoId id) {
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
