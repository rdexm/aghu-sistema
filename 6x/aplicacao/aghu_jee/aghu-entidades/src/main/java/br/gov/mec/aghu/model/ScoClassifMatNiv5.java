package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


/**
 * The persistent class for the sco_classif_mat_niv5 database table.
 * 
 */
@Entity
@Table(name="SCO_CLASSIF_MAT_NIV5")
public class ScoClassifMatNiv5 extends BaseEntityNumero<Long> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 4708608984708337712L;
	private Long numero;
	private Integer codigo;
	private Date criadoEm;
	private String descricao;
	private Integer serMatricula;
	private Integer serMatriculaAlterado;
	private Short serVinCodigo;
	private Short serVinCodigoAlterado;
	private Integer version;
	private ScoClassifMatNiv4 scoClassifMatNiv4;
	private Set<SceControleSolicMaterial> sceControleSolicMateriais;
	
	// FIXME Implementar este relacionamento
//	private Set<ScoMateriaisClassificacoes> scoMateriaisClassificacoes;
	
	public enum Fields {
		CONTROLE_SOLIC_MATERIAL("sceControleSolicMateriais"),
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		SCO_CLASSIF_MAT_NIV4("scoClassifMatNiv4"),
		NUMERO("numero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public ScoClassifMatNiv5() {
    }


	@Id
	@Column(name = "NUMERO")
	public Long getNumero() {
		return this.numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	@Column(name = "CODIGO")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	@Column(name="CRIADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Column(name = "DESCRICAO")
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


	//bi-directional many-to-one association to SceControleSolicMaterial
	@OneToMany(mappedBy="scoClassifMatNiv5")
	public Set<SceControleSolicMaterial> getSceControleSolicMateriais() {
		return this.sceControleSolicMateriais;
	}

	public void setSceControleSolicMateriais(Set<SceControleSolicMaterial> sceControleSolicMateriais) {
		this.sceControleSolicMateriais = sceControleSolicMateriais;
	}
	
//	//bi-directional many-to-one association to SceControleSolicMaterial
//	@OneToMany(mappedBy="id.cn5Numero")
//	public Set<ScoMateriaisClassificacoes> getScoMateriaisClassificacoes() {
//		return this.scoMateriaisClassificacoes;
//	}
//
//	public void setScoMateriaisClassificacoes(Set<ScoMateriaisClassificacoes> scoMateriaisClassificacoes) {
//		this.scoMateriaisClassificacoes = scoMateriaisClassificacoes;
//	}
	
	//bi-directional many-to-one association to ScoClassifMatNiv4
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="CN4_CN3_CN2_CN1_CODIGO", referencedColumnName="CN3_CN2_CN1_CODIGO"),
		@JoinColumn(name="CN4_CN3_CN2_CN1_GMT_CODIGO", referencedColumnName="CN3_CN2_CN1_GMT_CODIGO"),
		@JoinColumn(name="CN4_CN3_CN2_CODIGO", referencedColumnName="CN3_CN2_CODIGO"),
		@JoinColumn(name="CN4_CN3_CODIGO", referencedColumnName="CN3_CODIGO"),
		@JoinColumn(name="CN4_CODIGO", referencedColumnName="CODIGO")
		})	
	public ScoClassifMatNiv4 getScoClassifMatNiv4() {
		return this.scoClassifMatNiv4;
	}

	public void setScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4) {
		this.scoClassifMatNiv4 = scoClassifMatNiv4;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNumero() == null) ? 0 : getNumero().hashCode());
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
		if (!(obj instanceof ScoClassifMatNiv5)) {
			return false;
		}
		ScoClassifMatNiv5 other = (ScoClassifMatNiv5) obj;
		if (getNumero() == null) {
			if (other.getNumero() != null) {
				return false;
			}
		} else if (!getNumero().equals(other.getNumero())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}