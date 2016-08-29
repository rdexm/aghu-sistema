package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "SCE_FORMULA_PRODUCAO_INTERNAS", schema = "AGH")
public class SceFormulaProducaoInterna extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4892144921480087692L;
	private Integer matCodigo;
	private Integer version;
	private ScoMaterial scoMaterial;
	private Float percCustoProducao;
	private String indUsoLiberado;

	public SceFormulaProducaoInterna() {
	}

	public SceFormulaProducaoInterna(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	public SceFormulaProducaoInterna(ScoMaterial scoMaterial, Float percCustoProducao, String indUsoLiberado) {
		this.scoMaterial = scoMaterial;
		this.percCustoProducao = percCustoProducao;
		this.indUsoLiberado = indUsoLiberado;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "scoMaterial"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "MAT_CODIGO", unique = true, nullable = false)
	public Integer getMatCodigo() {
		return this.matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@Column(name = "PERC_CUSTO_PRODUCAO", precision = 8, scale = 8)
	public Float getPercCustoProducao() {
		return this.percCustoProducao;
	}

	public void setPercCustoProducao(Float percCustoProducao) {
		this.percCustoProducao = percCustoProducao;
	}

	@Column(name = "IND_USO_LIBERADO", length = 1)
	@Length(max = 1)
	public String getIndUsoLiberado() {
		return this.indUsoLiberado;
	}

	public void setIndUsoLiberado(String indUsoLiberado) {
		this.indUsoLiberado = indUsoLiberado;
	}

	public enum Fields {

		MAT_CODIGO("matCodigo"),
		VERSION("version"),
		SCO_MATERIAL("scoMaterial"),
		PERC_CUSTO_PRODUCAO("percCustoProducao"),
		IND_USO_LIBERADO("indUsoLiberado");

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
		result = prime * result + ((getMatCodigo() == null) ? 0 : getMatCodigo().hashCode());
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
		if (!(obj instanceof SceFormulaProducaoInterna)) {
			return false;
		}
		SceFormulaProducaoInterna other = (SceFormulaProducaoInterna) obj;
		if (getMatCodigo() == null) {
			if (other.getMatCodigo() != null) {
				return false;
			}
		} else if (!getMatCodigo().equals(other.getMatCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getCodigo(){ return this.getMatCodigo();} 
 public void setCodigo(Integer codigo){ this.setMatCodigo(codigo);}
}
