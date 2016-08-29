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
 * The persistent class for the sco_classif_mat_niv3 database table.
 * 
 */
@Entity
@Table(name="SCO_CLASSIF_MAT_NIV3")
public class ScoClassifMatNiv3 extends BaseEntityId<ScoClassifMatNiv3Id> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -8128575099704166135L;
	private ScoClassifMatNiv3Id id;
	private Date criadoEm;
	private String descricao;
	private Integer serMatricula;
	private Integer serMatriculaAlterado;
	private Short serVinCodigo;
	private Short serVinCodigoAlterado;
	private Integer version;
	private ScoClassifMatNiv2 scoClassifMatNiv2;
	private List<ScoClassifMatNiv4> scoClassifMatNiv4s;

    public ScoClassifMatNiv3() {
    }

    public enum Fields {
    	SCO_CLASSIF_MAT_NIV2("scoClassifMatNiv2"),
    	CODIGO("id.codigo"),
    	CN2_CODIGO("id.cn2Codigo"),
    	CN2_CN1_CODIGO("id.cn2Cn1Codigo"),
    	CN2_CN1_GMT_CODIGO("id.cn2Cn1GmtCodigo"),
		DESCRICAO("descricao"),
		SCO_CLASSIF_MAT_NIV4("scoClassifMatNiv4s");
		


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
	public ScoClassifMatNiv3Id getId() {
		return this.id;
	}

	public void setId(ScoClassifMatNiv3Id id) {
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


	//bi-directional many-to-one association to ScoClassifMatNiv2
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="CN2_CN1_CODIGO", referencedColumnName="CN1_CODIGO", insertable = false, updatable = false ),
		@JoinColumn(name="CN2_CN1_GMT_CODIGO", referencedColumnName="CN1_GMT_CODIGO", insertable = false, updatable = false),
		@JoinColumn(name="CN2_CODIGO", referencedColumnName="CODIGO", insertable = false, updatable = false)
		})
	public ScoClassifMatNiv2 getScoClassifMatNiv2() {
		return this.scoClassifMatNiv2;
	}

	public void setScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2) {
		this.scoClassifMatNiv2 = scoClassifMatNiv2;
	}
	

	//bi-directional many-to-one association to ScoClassifMatNiv4
	@OneToMany(mappedBy="scoClassifMatNiv3")
	@OrderBy("id.codigo")
	public List<ScoClassifMatNiv4> getScoClassifMatNiv4s() {
		return this.scoClassifMatNiv4s;
	}

	public void setScoClassifMatNiv4s(List<ScoClassifMatNiv4> scoClassifMatNiv4s) {
		this.scoClassifMatNiv4s = scoClassifMatNiv4s;
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
		if (!(obj instanceof ScoClassifMatNiv3)) {
			return false;
		}
		ScoClassifMatNiv3 other = (ScoClassifMatNiv3) obj;
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