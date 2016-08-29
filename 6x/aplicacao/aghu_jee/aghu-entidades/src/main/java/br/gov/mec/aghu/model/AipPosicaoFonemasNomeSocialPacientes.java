package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "AIP_POSI_FONEMAS_NOME_SOCIAL_PACIENTES", schema = "AGH")
public class AipPosicaoFonemasNomeSocialPacientes extends BaseEntityId<AipPosicaoFonemasNomeSocialPacientesId> implements java.io.Serializable {

	private static final long serialVersionUID = -1202768663125597972L;

	private AipPosicaoFonemasNomeSocialPacientesId id;
	
	private AipFonemaNomeSocialPacientes fonema;
	
	private Integer version;
	
	public AipPosicaoFonemasNomeSocialPacientes() {
	}

	public AipPosicaoFonemasNomeSocialPacientes(AipPosicaoFonemasNomeSocialPacientesId id) {
		this.id = id;
	}
	
	public AipPosicaoFonemasNomeSocialPacientes(AipPosicaoFonemasNomeSocialPacientesId id, AipFonemaNomeSocialPacientes fonema) {
		this.id = id;
		this.fonema = fonema;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "seq", column = @Column(name = "FNP_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "posicao", column = @Column(name = "POSICAO", nullable = false, precision = 2, scale = 0))})
	@NotNull
	public AipPosicaoFonemasNomeSocialPacientesId getId() {
		return this.id;
	}

	public void setId(AipPosicaoFonemasNomeSocialPacientesId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FNP_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public AipFonemaNomeSocialPacientes getFonema() {
		return fonema;
	}

	public void setFonema(AipFonemaNomeSocialPacientes fonema) {
		this.fonema = fonema;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		CODIGO_PACIENTE("id.seq"),
		FONEMA("fonema.fonema");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AipPosicaoFonemasNomeSocialPacientes)) {
			return false;
		}
		AipPosicaoFonemasNomeSocialPacientes other = (AipPosicaoFonemasNomeSocialPacientes) obj;
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
