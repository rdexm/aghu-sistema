package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_classif_mat_niv4 database table.
 * 
 */
@Entity
@Table(name="SCO_CLASSIF_MAT_NIV4")
public class ScoClassifMatNiv4 extends BaseEntityId<ScoClassifMatNiv4Id> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 1138954274077393057L;
	private ScoClassifMatNiv4Id id;
	private Date criadoEm;
	private String descricao;
	private Integer serMatricula;
	private Integer serMatriculaAlterado;
	private Short serVinCodigo;
	private Short serVinCodigoAlterado;
	private Integer version;
	private ScoClassifMatNiv3 scoClassifMatNiv3;
	private List<ScoClassifMatNiv5> scoClassifMatNiv5s;

    public ScoClassifMatNiv4() {
    }
    public enum Fields {
    	SCO_CLASSIF_MAT_NIV3("scoClassifMatNiv3"),
    	CODIGO("id.codigo"),
    	CN3_CN2_CN1_GMT_CODIGO("id.cn3Cn2Cn1GmtCodigo"),
    	CN3_CN2_CN1_CODIGO("id.cn3Cn2Cn1Codigo"),
    	CN3_CN2_CODIGO("id.cn3Cn2Codigo"),
    	CN3_CODIGO("id.cn3Codigo"),
		DESCRICAO("descricao"),
		SCO_CLASSIF_MAT_NIV5("scoClassifMatNiv5s");;


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@EmbeddedId
	public ScoClassifMatNiv4Id getId() {
		return this.id;
	}

	public void setId(ScoClassifMatNiv4Id id) {
		this.id = id;
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


	//bi-directional many-to-one association to ScoClassifMatNiv3
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="CN3_CN2_CN1_CODIGO", referencedColumnName="CN2_CN1_CODIGO", insertable = false, updatable = false),
		@JoinColumn(name="CN3_CN2_CN1_GMT_CODIGO", referencedColumnName="CN2_CN1_GMT_CODIGO", insertable = false, updatable = false),
		@JoinColumn(name="CN3_CN2_CODIGO", referencedColumnName="CN2_CODIGO", insertable = false, updatable = false),
		@JoinColumn(name="CN3_CODIGO", referencedColumnName="CODIGO", insertable = false, updatable = false)
		})
	public ScoClassifMatNiv3 getScoClassifMatNiv3() {
		return this.scoClassifMatNiv3;
	}

	public void setScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3) {
		this.scoClassifMatNiv3 = scoClassifMatNiv3;
	}
	
	@OneToMany(mappedBy="scoClassifMatNiv4")
	@OrderBy("codigo")
	public List<ScoClassifMatNiv5> getScoClassifMatNiv5s() {
		return this.scoClassifMatNiv5s;
	}

	public void setScoClassifMatNiv5s(List<ScoClassifMatNiv5> scoClassifMatNiv5s) {
		this.scoClassifMatNiv5s = scoClassifMatNiv5s;
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
		if (!(obj instanceof ScoClassifMatNiv4)) {
			return false;
		}
		ScoClassifMatNiv4 other = (ScoClassifMatNiv4) obj;
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