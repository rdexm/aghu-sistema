package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "FAT_TIPO_TRAT_FINALIDADES_JN", schema = "AGH")
@Immutable
public class FatTipoTratFinalidadeJn extends BaseEntityId<FatTipoTratFinalidadeJnId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1583274785802722239L;
	private FatTipoTratFinalidadeJnId id;

	public FatTipoTratFinalidadeJn() {
	}

	public FatTipoTratFinalidadeJn(FatTipoTratFinalidadeJnId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "jnUser", column = @Column(name = "JN_USER", nullable = false, length = 30)),
			@AttributeOverride(name = "jnDateTime", column = @Column(name = "JN_DATE_TIME", nullable = false, length = 7)),
			@AttributeOverride(name = "jnOperation", column = @Column(name = "JN_OPERATION", nullable = false, length = 3)),
			@AttributeOverride(name = "tptSeq", column = @Column(name = "TPT_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "ftrSeq", column = @Column(name = "FTR_SEQ", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", nullable = false, length = 7)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)) })
	public FatTipoTratFinalidadeJnId getId() {
		return this.id;
	}

	public void setId(FatTipoTratFinalidadeJnId id) {
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
