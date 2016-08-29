package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the ael_resultados_codificados database table.
 */
@Entity
@Table(name="AEL_ESPEC_CAMPO_LAUDOS", schema = "AGH")
public class AelEspecialidadeCampoLaudo extends BaseEntityId<AelEspecialidadeCampoLaudoId> implements Serializable {

	private static final long serialVersionUID = 3505275481571895064L;

	private AelEspecialidadeCampoLaudoId id;
	private Short ordem;
	private String nomeSumario;
	//private Integer version;
	private AelCampoLaudo campoLaudo;
	private AghEspecialidades especialidade;

    public AelEspecialidadeCampoLaudo() {
    }

    @EmbeddedId
	public AelEspecialidadeCampoLaudoId getId() {
		return this.id;
	}

	public void setId(AelEspecialidadeCampoLaudoId id) {
		this.id = id;
	}

	@Column(name="ORDEM")
	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name="NOME_SUMARIO", length=30)
	public String getNomeSumario() {
		return nomeSumario;
	}

	public void setNomeSumario(String nomeSumario) {
		this.nomeSumario = nomeSumario;
	}
	/*
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CAL_SEQ", nullable = false, insertable=false, updatable=false)
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ESP_SEQ", nullable = false, insertable=false, updatable=false)
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public enum Fields {

		ID("id"), //
		CAL_SEQ("id.calSeq"),//
		ESP_SEQ("id.espSeq"),//
		ORDEM("ordem"),//
		NOME_SUMARIO("nomeSumario"),//
		CAMPO_LAUDO("campoLaudo"),//
		ESPECIALIDADE("especialidade");//

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
		if (!(obj instanceof AelEspecialidadeCampoLaudo)) {
			return false;
		}
		AelEspecialidadeCampoLaudo other = (AelEspecialidadeCampoLaudo) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}