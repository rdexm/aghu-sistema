package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Immutable
@Table(name = "ael_item_solic_consultados", schema = "hist")
public class AelItemSolicConsultadoHist extends BaseEntityId<AelItemSolicConsultadoHistId> implements java.io.Serializable {

	private static final long serialVersionUID = -3686386609170369634L;
	private AelItemSolicConsultadoHistId id;

	public AelItemSolicConsultadoHist() {
	}

	public AelItemSolicConsultadoHist(AelItemSolicConsultadoHistId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", nullable = false, length = 7)) })
	public AelItemSolicConsultadoHistId getId() {
		return this.id;
	}

	public void setId(AelItemSolicConsultadoHistId id) {
		this.id = id;
	}
	
	public enum Fields {
		ID("id"),
		ISE_SOE_SEQ("id.iseSoeSeq"),
		ISE_SEQP("id.iseSeqp"),
		SERVIDOR_MATRICULA("id.serMatricula"),
		SERVIDOR_CODIGO_VINCULO("id.serVinCodigo"),
		CRIADO_EM("id.criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelItemSolicConsultado)) {
			return false;
		}
		AelItemSolicConsultado other = (AelItemSolicConsultado) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
