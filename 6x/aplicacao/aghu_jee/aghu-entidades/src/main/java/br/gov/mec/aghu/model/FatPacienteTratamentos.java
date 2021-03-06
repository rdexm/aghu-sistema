package br.gov.mec.aghu.model;

// Generated 08/02/2010 17:25:25 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * FatPacienteTratamentos generated by hbm2java
 */
@Entity
@Table(name = "FAT_PACIENTE_TRATAMENTOS", schema = "AGH")
public class FatPacienteTratamentos extends BaseEntityId<FatPacienteTratamentosId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5945180296445569840L;
	private FatPacienteTratamentosId id;
	private Integer cidSeq;

	public FatPacienteTratamentos() {
	}

	public FatPacienteTratamentos(FatPacienteTratamentosId id) {
		this.id = id;
	}

	public FatPacienteTratamentos(FatPacienteTratamentosId id, Integer cidSeq) {
		this.id = id;
		this.cidSeq = cidSeq;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "dtDiagnosticoTumor", column = @Column(name = "DT_DIAGNOSTICO_TUMOR", nullable = false, length = 7)),
			@AttributeOverride(name = "dtInicioTratamento", column = @Column(name = "DT_INICIO_TRATAMENTO", nullable = false, length = 7)),
			@AttributeOverride(name = "indTipoTratamento", column = @Column(name = "IND_TIPO_TRATAMENTO", nullable = false, precision = 2, scale = 0)) })
	public FatPacienteTratamentosId getId() {
		return this.id;
	}

	public void setId(FatPacienteTratamentosId id) {
		this.id = id;
	}

	@Column(name = "CID_SEQ", precision = 5, scale = 0)
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public enum Fields {
		PAC_CODIGO("id.pacCodigo");

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
		if (!(obj instanceof FatPacienteTratamentos)) {
			return false;
		}
		FatPacienteTratamentos other = (FatPacienteTratamentos) obj;
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
