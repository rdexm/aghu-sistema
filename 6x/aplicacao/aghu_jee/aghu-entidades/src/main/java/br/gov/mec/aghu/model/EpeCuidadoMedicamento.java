package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "EPE_CUIDADOS_MEDICAMENTOS", schema = "AGH")
public class EpeCuidadoMedicamento extends BaseEntityId<EpeCuidadoMedicamentoId>  {

	private static final long serialVersionUID = -1103921067201480269L;

	private EpeCuidadoMedicamentoId id;
	private Integer version;
	private DominioSituacao situacao;
	private Integer horasAntes;
	private Integer horasApos;
	private AfaMedicamento medicamento;
	private EpeCuidados cuidado;

	private enum EpeCuidadoMedicamentoExceptionCode implements BusinessExceptionCode {
		ERRO_CUIDADO_MEDICAMENTO_HORAS
	}

	public EpeCuidadoMedicamento() {
	}

	public EpeCuidadoMedicamento(EpeCuidadoMedicamentoId id, DominioSituacao situacao) {
		this.id = id;
		this.situacao = situacao;
	}

	public EpeCuidadoMedicamento(EpeCuidadoMedicamentoId id, DominioSituacao situacao, Integer horasAntes, Integer horasApos) {
		this.id = id;
		this.situacao = situacao;
		this.horasAntes = horasAntes;
		this.horasApos = horasApos;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cuiSeq", column = @Column(name = "CUI_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "medMatCodigo", column = @Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)) })
	public EpeCuidadoMedicamentoId getId() {
		return this.id;
	}

	public void setId(EpeCuidadoMedicamentoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "HORAS_ANTES", precision = 6, scale = 0)
	public Integer getHorasAntes() {
		return this.horasAntes;
	}

	public void setHorasAntes(Integer horasAntes) {
		this.horasAntes = horasAntes;
	}

	@Column(name = "HORAS_APOS", precision = 6, scale = 0)
	public Integer getHorasApos() {
		return this.horasApos;
	}

	public void setHorasApos(Integer horasApos) {
		this.horasApos = horasApos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUI_SEQ", nullable = false, insertable = false, updatable = false)
	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO", nullable = false, insertable = false, updatable = false)
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		EpeCuidadoMedicamento other = (EpeCuidadoMedicamento) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarHoras() {
		if (!(this.horasAntes != null || this.horasApos != null)) {
			throw new BaseRuntimeException(EpeCuidadoMedicamentoExceptionCode.ERRO_CUIDADO_MEDICAMENTO_HORAS);
		}
	}

	public enum Fields {
		ID("id"), 
		MED_MAT_CODIGO("id.medMatCodigo"), 
		CUI_SEQ("id.cuiSeq"), 
		SITUACAO("situacao"), 
		HORAS_ANTES("horasAntes"), 
		HORAS_APOS("horasApos"), 
		PRESC_CUID_MEDICAMENTOS("prescCuidMedicamentos"), 
		MEDICAMENTO("medicamento"), 
		CUIDADO("cuidado");

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
