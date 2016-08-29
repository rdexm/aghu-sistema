package br.gov.mec.aghu.model;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "V_MAM_DIFER_CUID_SERVIDOR", schema = "AGH")
@Immutable
public class VMamDiferCuidServidores extends BaseEntityId<VMamDiferCuidServidoresId> implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8929150358308415411L;
	private VMamDiferCuidServidoresId id;
	private Short vinCodigo;
	private Integer matricula;
	private String pesNome;	


	public VMamDiferCuidServidores() {
	}

	public VMamDiferCuidServidores(VMamDiferCuidServidoresId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "vinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "matricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "pesNome", column = @Column(name = "PES_NOME", nullable = false, length = 60)),
	})
	public VMamDiferCuidServidoresId getId() {
		return this.id;
	}

	public void setId(VMamDiferCuidServidoresId id) {
		this.id = id;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, updatable=false, insertable=false, precision = 3, scale = 0)
	public Short getVinCodigo() {
		return this.vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "SER_MATRICULA", nullable = false, updatable=false, insertable=false, precision = 2, scale = 0)
	public Integer getMatricula() {
		return this.matricula;
	}
	
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	
	@Column(name = "PES_NOME", nullable = false, updatable=false, insertable=false, precision = 2, scale = 0)
	public String getPesNome() {
		return pesNome;
	}

	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}
	
	
	@Transient
	public String getMatriculaVinculo() {
		return this.getVinCodigo() + " - " + this.getMatricula();
	}

	@Transient
	public String getVinculoMatricula() {
		return this.getMatricula() + " - " + this.getVinCodigo();
	}

	public enum Fields {
		VIN_CODIGO	("id.vinCodigo"),
		MATRICULA	("id.matricula"),
		PESNOME	("id.pesNome");
		
		
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
		if (!(obj instanceof VMamDiferCuidServidores)) {
			return false;
		}
		VMamDiferCuidServidores other = (VMamDiferCuidServidores) obj;
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
