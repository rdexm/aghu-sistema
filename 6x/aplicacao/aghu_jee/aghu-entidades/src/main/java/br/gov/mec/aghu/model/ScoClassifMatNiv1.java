package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_classif_mat_niv1 database table.
 * 
 */
@Entity
@Table(name="SCO_CLASSIF_MAT_NIV1")
public class ScoClassifMatNiv1 extends BaseEntityId<ScoClassifMatNiv1Id> implements Serializable {
	
	private static final long serialVersionUID = 2791781106317776289L;
	
	private ScoClassifMatNiv1Id id;
	private Date criadoEm;
	private String descricao;
	private Integer serMatricula;
	private Integer serMatriculaAlterado;
	private Short serVinCodigo;
	private Short serVinCodigoAlterado;
	private Integer version;
	private ScoGrupoMaterial grupoMaterial;
	private List<ScoClassifMatNiv2> scoClassifMatNiv2s;

    public ScoClassifMatNiv1() {
    }

	@EmbeddedId
	public ScoClassifMatNiv1Id getId() {
		return this.id;
	}

	public void setId(ScoClassifMatNiv1Id id) {
		this.id = id;
	}
	
//	@Column(name="GMT_CODIGO",insertable = false, updatable = false)
	@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="GMT_CODIGO", referencedColumnName="CODIGO", insertable = false, updatable = false)
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	

	@Column(name="CRIADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Column(name="SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}


	@Column(name="SER_MATRICULA_ALTERADO")
	public Integer getSerMatriculaAlterado() {
		return this.serMatriculaAlterado;
	}

	public void setSerMatriculaAlterado(Integer serMatriculaAlterado) {
		this.serMatriculaAlterado = serMatriculaAlterado;
	}


	@Column(name="SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	@Column(name="SER_VIN_CODIGO_ALTERADO")
	public Short getSerVinCodigoAlterado() {
		return this.serVinCodigoAlterado;
	}

	public void setSerVinCodigoAlterado(Short serVinCodigoAlterado) {
		this.serVinCodigoAlterado = serVinCodigoAlterado;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to ScoClassifMatNiv2
	@OneToMany(mappedBy="scoClassifMatNiv1")
	@OrderBy("id.codigo")
	public List<ScoClassifMatNiv2> getScoClassifMatNiv2s() {
		return this.scoClassifMatNiv2s;
	}

	public void setScoClassifMatNiv2s(List<ScoClassifMatNiv2> scoClassifMatNiv2s) {
		this.scoClassifMatNiv2s = scoClassifMatNiv2s;
	}
    
    public enum Fields {
    	CODIGO("id.codigo"),
    	GMT_CODIGO("id.gmtCodigo"),
		DESCRICAO("descricao"),
		GMT("grupoMaterial"),
		SCO_CLASSIF_MAT_NIV2("scoClassifMatNiv2s");


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
		if (!(obj instanceof ScoClassifMatNiv1)) {
			return false;
		}
		ScoClassifMatNiv1 other = (ScoClassifMatNiv1) obj;
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