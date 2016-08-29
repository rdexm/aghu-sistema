package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * The persistent class for the sco_grupos_materiais database table.
 * 
 */

@Entity
@Table(name = "sco_grupos_materiais")
@SequenceGenerator(name = "scoGmtSq1", sequenceName = "agh.sco_gmt_sq1", allocationSize = 1)
public class ScoGrupoMaterial extends BaseEntityCodigo<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2565051862575360551L;
	public static final Integer GRUPO_MATERIAL_MEDICAMENTO = 2;
	public static final Integer GRUPO_MATERIAL_ORTESE_PROTESE = 13;
	
	private Integer codigo;
	private String descricao;
	private Boolean controleValidade;
	private Boolean patrimonio;
	private Boolean engenhari;
	private Boolean nutricao;
	private Boolean exigeForn;
	private Boolean geraMovEstoque;
	private Boolean dispensario;
	private Integer ntdCodigo;
	private Integer codMercadoriaBb;
	private DominioDiaSemanaMes diaFavEntgMaterial;
	private Integer version;
	private Set<ScoMaterial> materiais;
	private FcuAgrupaGrupoMaterial agrupaGrupoMaterial;
	private Boolean geraMvtoCondVlr; 
	
	private List<ScoClassifMatNiv1> scoClassifMatNiv1s;
	
	private enum ScoGrupoMaterialExceptionCode implements BusinessExceptionCode {
		ENGENHARIA_E_NUTRICAO_INVALIDOS
	}


	
	public ScoGrupoMaterial() {
	}
	
	public ScoGrupoMaterial(Integer codigo) {
		this.codigo = codigo;
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 2, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoGmtSq1")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_CONTROLE_VALIDADE", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getControleValidade() {
		return controleValidade;
	}

	public void setControleValidade(Boolean controleValidade) {
		this.controleValidade = controleValidade;
	}

	@Column(name = "IND_PATRIMONIO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(Boolean patrimonio) {
		this.patrimonio = patrimonio;
	}

	@Column(name = "IND_ENGENHARI", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEngenhari() {
		return engenhari;
	}

	public void setEngenhari(Boolean engenhari) {
		this.engenhari = engenhari;
	}

	@Column(name = "IND_NUTRICAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getNutricao() {
		return nutricao;
	}

	public void setNutricao(Boolean nutricao) {
		this.nutricao = nutricao;
	}

	@Column(name = "IND_EXIGE_FORN", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExigeForn() {
		return exigeForn;
	}

	public void setExigeForn(Boolean exigeForn) {
		this.exigeForn = exigeForn;
	}

	@Column(name = "IND_GERA_MOV_ESTOQUE", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getGeraMovEstoque() {
		return geraMovEstoque;
	}

	public void setGeraMovEstoque(Boolean geraMovEstoque) {
		this.geraMovEstoque = geraMovEstoque;
	}

	@Column(name = "IND_DISPENSARIO", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDispensario() {
		return dispensario;
	}

	public void setDispensario(Boolean dispensario) {
		this.dispensario = dispensario;
	}

	@Column(name = "NTD_CODIGO", length = 2)
	public Integer getNtdCodigo() {
		return this.ntdCodigo;
	}

	public void setNtdCodigo(Integer ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}

	@Column(name = "COD_MERCADORIA_BB", length = 9)
	public Integer getCodMercadoriaBb() {
		return this.codMercadoriaBb;
	}

	public void setCodMercadoriaBb(Integer codMercadoriaBb) {
		this.codMercadoriaBb = codMercadoriaBb;
	}
	
	@Column(name = "DIA_FAV_ENTG_MATERIAL", length = 2)
	@Enumerated(EnumType.ORDINAL)
	public DominioDiaSemanaMes getDiaFavEntgMaterial() {
		return this.diaFavEntgMaterial;
	}

	public void setDiaFavEntgMaterial(DominioDiaSemanaMes diaFavEntgMaterial) {
		this.diaFavEntgMaterial = diaFavEntgMaterial;
	}

	@ManyToOne
	@JoinColumn(name = "AMT_SEQ", referencedColumnName = "SEQ")
	public FcuAgrupaGrupoMaterial getAgrupaGrupoMaterial() {
		return agrupaGrupoMaterial;
	}

	public void setAgrupaGrupoMaterial(
			FcuAgrupaGrupoMaterial agrupaGrupoMaterial) {
		this.agrupaGrupoMaterial = agrupaGrupoMaterial;
	}
	
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// bi-directional many-to-one association to ScoMaterial
	@OneToMany(mappedBy = "grupoMaterial", fetch=FetchType.LAZY)
	public Set<ScoMaterial> getMateriais() {
		return materiais;
	}

	public void setMateriais(Set<ScoMaterial> materiais) {
		this.materiais = materiais;
	}
	
	@Column(name = "ind_gera_mov_cond_vlr", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getGeraMvtoCondVlr() {
		return geraMvtoCondVlr;
	}

	public void setGeraMvtoCondVlr(Boolean geraMvtoCondVlr) {
		this.geraMvtoCondVlr = geraMvtoCondVlr;
	}
	
	
	@OneToMany(mappedBy = "grupoMaterial")
	@OrderBy("id.codigo")
	public List<ScoClassifMatNiv1> getScoClassifMatNiv1s() {
		return scoClassifMatNiv1s;
	}

	public void setScoClassifMatNiv1s(List<ScoClassifMatNiv1> scoClassifMatNiv1s) {
		this.scoClassifMatNiv1s = scoClassifMatNiv1s;
	}

	@Transient
	public DominioIndicadorParametrosOrcamento getIndicador() {
		return getPatrimonio() ? DominioIndicadorParametrosOrcamento.P
				: getNutricao() ? DominioIndicadorParametrosOrcamento.N
						: getEngenhari() ? DominioIndicadorParametrosOrcamento.E
								: null;
	}

	@PrePersist
	@PreUpdate
	protected void validacoes() {
       /* QMS$ENFORCE_ARC_2 */      
		if (!((this.getEngenhari() && !this.getNutricao()) || (!this.getEngenhari() && this.getNutricao()) || (!this.getEngenhari() && !this.getNutricao()))) {
			throw new BaseRuntimeException(
					ScoGrupoMaterialExceptionCode.ENGENHARIA_E_NUTRICAO_INVALIDOS);
		}
	}
	
	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof ScoGrupoMaterial)) {
			return false;
		}
		ScoGrupoMaterial castOther = (ScoGrupoMaterial) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		IND_ENGENHARI("engenhari"), 
		COD_MERCADORIA_BB("codMercadoriaBb"), 
		DIA_FAV_ENTG_MATERIAL("diaFavEntgMaterial"),
		AGRUPA_GRUPO_MATERIAL("agrupaGrupoMaterial"),
		CODIGO("codigo"), DESCRICAO("descricao"),
		IND_GERA_MOV_COND_VLR("geraMvtoCondVlr"),
		COD_MERCADORIA_Bb("codMercadoriaBb"), 
		DIA_FAV_ENTREGA("diaFavEntgMaterial"), 
		IND_CONTROLE_VALIDADE("controleValidade"), 
		IND_ENG("engenhari"), 
		IND_EXIGE_FORN("exigeForn"), 
		IND_GERA_MOV_ESTOQUE("geraMovEstoque"), 
		IND_NUTRICAO("nutricao"), 
		IND_PATRIMONIO("patrimonio"), 
		IND_DISPENSARIO("dispensario"),
		NTD_CODIGO("ntdCodigo"), 
		VERSION("version"), 
		SCO_MATERIAL("materiais"),
		SCO_CLASSIF_MAT_NIV1S("scoClassifMatNiv1s");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}