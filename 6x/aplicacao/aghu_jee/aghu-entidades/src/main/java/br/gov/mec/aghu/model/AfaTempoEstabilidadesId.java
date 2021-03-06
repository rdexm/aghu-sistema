package br.gov.mec.aghu.model;

// Generated 15/03/2012 12:28:14 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AfaTempoEstabilidadesId generated by hbm2java
 */
@Embeddable
public class AfaTempoEstabilidadesId implements EntityCompositeId {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3524004043666392046L;
	private Integer medMatCodigo;
	private String condicaoEstabilidade;

	public AfaTempoEstabilidadesId() {
	}

	public AfaTempoEstabilidadesId(Integer medMatCodigo, String condicaoEstabilidade) {
		this.medMatCodigo = medMatCodigo;
		this.condicaoEstabilidade = condicaoEstabilidade;
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false)
	public Integer getMedMatCodigo() {
		return this.medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "CONDICAO_ESTABILIDADE", nullable = false, length = 1)
	@Length(max = 1)
	public String getCondicaoEstabilidade() {
		return this.condicaoEstabilidade;
	}

	public void setCondicaoEstabilidade(String condicaoEstabilidade) {
		this.condicaoEstabilidade = condicaoEstabilidade;
	}

	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof AfaTempoEstabilidadesId)) {
			return false;
		}
		AfaTempoEstabilidadesId castOther = (AfaTempoEstabilidadesId) other;

		return (this.getMedMatCodigo() == castOther.getMedMatCodigo())
				&& ((this.getCondicaoEstabilidade() == castOther
						.getCondicaoEstabilidade()) || (this
						.getCondicaoEstabilidade() != null
						&& castOther.getCondicaoEstabilidade() != null && this
						.getCondicaoEstabilidade().equals(
								castOther.getCondicaoEstabilidade())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getMedMatCodigo();
		result = 37
				* result
				+ (getCondicaoEstabilidade() == null ? 0 : this
						.getCondicaoEstabilidade().hashCode());
		return result;
	}

}
