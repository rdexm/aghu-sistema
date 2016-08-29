package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
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
@SequenceGenerator(name="rapMmvSq1", sequenceName="AGH.RAP_MMV_SQ1", allocationSize = 1)
@Table(name = "RAP_MARCAS_MODELOS_VEICULOS", schema = "AGH")
public class RapMarcaModeloVeiculo extends BaseEntityCodigo<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3037630589815625298L;
	private Short codigo;
	private Integer version;
	private String descricao;
	private Set<RapVeiculo> rapVeiculoes = new HashSet<RapVeiculo>(0);

	public RapMarcaModeloVeiculo() {
	}

	public RapMarcaModeloVeiculo(Short codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public RapMarcaModeloVeiculo(Short codigo, String descricao, Set<RapVeiculo> rapVeiculoes) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.rapVeiculoes = rapVeiculoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapMmvSq1")
	@Column(name = "CODIGO", unique = true, nullable = false)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@Length(max = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rapMarcaModeloVeiculo")
	public Set<RapVeiculo> getRapVeiculoes() {
		return this.rapVeiculoes;
	}

	public void setRapVeiculoes(Set<RapVeiculo> rapVeiculoes) {
		this.rapVeiculoes = rapVeiculoes;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		DESCRICAO("descricao"),
		RAP_VEICULOES("rapVeiculoes");

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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof RapMarcaModeloVeiculo)) {
			return false;
		}
		RapMarcaModeloVeiculo other = (RapMarcaModeloVeiculo) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
