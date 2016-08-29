package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_MAM_MEDICAMENTOS", schema = "AGH")
@Immutable
public class VMamMedicamentos extends BaseEntityId<VMamMedicamentosId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5564952223690277164L;
	private VMamMedicamentosId id;
	private	String	descricaoUmm;
	private	Double	concentracao;
	@EmbeddedId
	@AttributeOverrides( {
		@AttributeOverride(name = "descricaoMat", column = @Column(name = "DESCRICAO_MAT", nullable = false, length = 60)),
		@AttributeOverride(name = "indSituacao", column = @Column(name = "IND_SITUACAO",  nullable = false,length = 1)),
		@AttributeOverride(name = "matCodigo", column = @Column(name = "MAT_CODIGO", nullable = false, precision = 6)),
		@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", nullable = false, length = 60))
		})
	public VMamMedicamentosId getId() {
		return id;
	}

	public void setId(VMamMedicamentosId id) {
		this.id = id;
	}
	
	public enum Fields {
		DESCRICAO_MAT("id.descricaoMat"),
		CONCENTRACAO("concentracao"),
		DESCRICAOUMM("descricaoUmm"),
		IND_SITUACAO("id.indSituacao"),
		MAT_CODIGO("id.matCodigo"),
		DESCRICAO("id.descricao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	@Column(name = "DESCRICAO_UMM",  length = 60)
	public String getDescricaoUmm() {
		return descricaoUmm;
	}

	public void setDescricaoUmm(String descricaoUmm) {
		this.descricaoUmm = descricaoUmm;
	}
	
	@Column(name = "CONCENTRACAO", precision = 14, scale = 4)
	public Double getConcentracao() {
		return concentracao;
	}

	public void setConcentracao(Double concentracao) {
		this.concentracao = concentracao;
	}
	
	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getId());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VMamMedicamentos)) {
			return false;
		}
		VMamMedicamentos other = (VMamMedicamentos) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getId(), other.getId());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####

}
