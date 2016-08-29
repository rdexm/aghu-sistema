package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AbsEnvioNaoPermitido generated by hbm2java
 */
@Entity
@Table(name = "ABS_ENVIOS_NAO_PERMITIDOS", schema = "AGH")
public class AbsEnvioNaoPermitido extends BaseEntityId<AbsEnvioNaoPermitidoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1008510375137806875L;
	private AbsEnvioNaoPermitidoId id;

	public AbsEnvioNaoPermitido() {
	}

	public AbsEnvioNaoPermitido(AbsEnvioNaoPermitidoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "csaCodigo", column = @Column(name = "CSA_CODIGO", nullable = false, length = 2)),
			@AttributeOverride(name = "grupoSangEnviado", column = @Column(name = "GRUPO_SANG_ENVIADO", nullable = false, length = 2)),
			@AttributeOverride(name = "grupoSangPaciente", column = @Column(name = "GRUPO_SANG_PACIENTE", nullable = false, length = 2)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AbsEnvioNaoPermitidoId getId() {
		return this.id;
	}

	public void setId(AbsEnvioNaoPermitidoId id) {
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