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
@Table(name = "FAT_CONV_PROCED_GRUPOS_JN", schema = "AGH")
@Immutable
public class FatConvProcedGrupoJn extends BaseEntityId<FatConvProcedGrupoJnId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4137477893150587093L;
	private FatConvProcedGrupoJnId id;

	public FatConvProcedGrupoJn() {
	}

	public FatConvProcedGrupoJn(FatConvProcedGrupoJnId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "jnUser", column = @Column(name = "JN_USER", nullable = false, length = 30)),
			@AttributeOverride(name = "jnDateTime", column = @Column(name = "JN_DATE_TIME", nullable = false, length = 7)),
			@AttributeOverride(name = "jnOperation", column = @Column(name = "JN_OPERATION", nullable = false, length = 3)),
			@AttributeOverride(name = "cphCspCnvCodigo", column = @Column(name = "CPH_CSP_CNV_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "cphCspSeq", column = @Column(name = "CPH_CSP_SEQ", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "cphPhoSeq", column = @Column(name = "CPH_PHO_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "grcSeq", column = @Column(name = "GRC_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "descAutomatico", column = @Column(name = "DESC_AUTOMATICO", length = 1)),
			@AttributeOverride(name = "percDesconto", column = @Column(name = "PERC_DESCONTO", precision = 4)),
			@AttributeOverride(name = "percAcrescimo", column = @Column(name = "PERC_ACRESCIMO", precision = 5)) })
	public FatConvProcedGrupoJnId getId() {
		return this.id;
	}

	public void setId(FatConvProcedGrupoJnId id) {
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
