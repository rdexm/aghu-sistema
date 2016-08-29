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
@Table(name = "V_AIN_INTERNACOES_EXCEDENTES", schema = "AGH")
@Immutable
public class VAinInternacoesExcedentes extends BaseEntityId<VAinInternacoesExcedentesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6168150320879948059L;
	private VAinInternacoesExcedentesId id;

	public VAinInternacoesExcedentes() {
	}

	public VAinInternacoesExcedentes(VAinInternacoesExcedentesId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "descricaoUnidadeFuncional", column = @Column(name = "UNIDADE_FUNCIONAL", length = 60)),
			@AttributeOverride(name = "quantDiasFaturamento", column = @Column(name = "QUANT_DIAS_FATURAMENTO")),
			@AttributeOverride(name = "dtHrInternacao", column = @Column(name = "DTHR_INTERNACAO"))})
	public VAinInternacoesExcedentesId getId() {
		return this.id;
	}

	public void setId(VAinInternacoesExcedentesId id) {
		this.id = id;
	}

	public enum Fields {

		ID("id"),
		UNIDADE_FUNCIONAL("id.descricaoUnidadeFuncional"),
		QUANT_DIAS_FATURAMENTO("id.quantDiasFaturamento"),
		DTHR_INTERNACAO("id.dtHrInternacao");

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
		if (!(obj instanceof VAinInternacoesExcedentes)) {
			return false;
		}
		VAinInternacoesExcedentes other = (VAinInternacoesExcedentes) obj;
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
