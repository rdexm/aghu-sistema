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

@Entity
@Table(name = "MAM_TIPO_COOPERACOES_JN", schema = "AGH")
@Immutable
public class MamTipoCooperacaoJn extends BaseEntityId<MamTipoCooperacaoJnId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5696141868267047128L;
	private MamTipoCooperacaoJnId id;

	public MamTipoCooperacaoJn() {
	}

	public MamTipoCooperacaoJn(MamTipoCooperacaoJnId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "jnUser", column = @Column(name = "JN_USER", nullable = false, length = 30)),
			@AttributeOverride(name = "jnDateTime", column = @Column(name = "JN_DATE_TIME", nullable = false, length = 29)),
			@AttributeOverride(name = "jnOperation", column = @Column(name = "JN_OPERATION", nullable = false, length = 3)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", length = 45)),
			@AttributeOverride(name = "cor", column = @Column(name = "COR", length = 45)),
			@AttributeOverride(name = "indSituacao", column = @Column(name = "IND_SITUACAO", length = 1)),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", length = 29)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA")),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO")) })
	public MamTipoCooperacaoJnId getId() {
		return this.id;
	}

	public void setId(MamTipoCooperacaoJnId id) {
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
