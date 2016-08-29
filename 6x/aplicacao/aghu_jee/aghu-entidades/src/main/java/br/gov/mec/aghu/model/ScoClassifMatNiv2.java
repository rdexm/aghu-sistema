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
 * The persistent class for the sco_classif_mat_niv2 database table.
 * 
 */
@Entity
@Table(name="SCO_CLASSIF_MAT_NIV2")
public class ScoClassifMatNiv2 extends BaseEntityId<ScoClassifMatNiv2Id> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 3211272143644167716L;
	private ScoClassifMatNiv2Id id;
	private Integer codClassifNatureza;
	private Date criadoEm;
	private String descricao;
	private Integer serMatricula;
	private Integer serMatriculaAlterado;
	private Short serVinCodigo;
	private Short serVinCodigoAlterado;
	private Integer version;
	private ScoClassifMatNiv1 scoClassifMatNiv1;
	private List<ScoClassifMatNiv3> scoClassifMatNiv3s;

    public ScoClassifMatNiv2() {
    }

    public enum Fields {
    	SCO_CLASSIF_MAT_NIV1("scoClassifMatNiv1"),
    	CODIGO("id.codigo"),
    	CN1_CODIGO("id.cn1Codigo"),
    	CN1_GMT_CODIGO("id.cn1GmtCodigo"),
		DESCRICAO("descricao"),
		SCO_CLASSIF_MAT_NIV3("scoClassifMatNiv3s");

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
	public ScoClassifMatNiv2Id getId() {
		return this.id;
	}

	public void setId(ScoClassifMatNiv2Id id) {
		this.id = id;
	}
	

	@Column(name="COD_CLASSIF_NATUREZA")
	public Integer getCodClassifNatureza() {
		return this.codClassifNatureza;
	}

	public void setCodClassifNatureza(Integer codClassifNatureza) {
		this.codClassifNatureza = codClassifNatureza;
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


	//bi-directional many-to-one association to ScoClassifMatNiv1
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="CN1_CODIGO", referencedColumnName="CODIGO", insertable = false, updatable = false),
		@JoinColumn(name="CN1_GMT_CODIGO", referencedColumnName="GMT_CODIGO", insertable = false, updatable = false)
		})
	public ScoClassifMatNiv1 getScoClassifMatNiv1() {
		return this.scoClassifMatNiv1;
	}

	public void setScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1) {
		this.scoClassifMatNiv1 = scoClassifMatNiv1;
	}
	

	//bi-directional many-to-one association to ScoClassifMatNiv3
	@OneToMany(mappedBy="scoClassifMatNiv2")
	@OrderBy("id.codigo")
	public List<ScoClassifMatNiv3> getScoClassifMatNiv3s() {
		return this.scoClassifMatNiv3s;
	}

	public void setScoClassifMatNiv3s(List<ScoClassifMatNiv3> scoClassifMatNiv3s) {
		this.scoClassifMatNiv3s = scoClassifMatNiv3s;
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
		if (!(obj instanceof ScoClassifMatNiv2)) {
			return false;
		}
		ScoClassifMatNiv2 other = (ScoClassifMatNiv2) obj;
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