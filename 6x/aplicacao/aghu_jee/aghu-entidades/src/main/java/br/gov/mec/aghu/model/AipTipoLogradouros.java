package br.gov.mec.aghu.model;

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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "AIP_TIPO_LOGRADOUROS", schema = "AGH")
@Indexed
@SequenceGenerator(name="aipTipoLogradourosSq1", sequenceName="AGH.AIP_TIP_SQ1", allocationSize = 1)
public class AipTipoLogradouros extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7544259373769397779L;
	private Integer codigo;
	private String abreviatura;
	private String descricao;
	private Integer version;

	private Set<AipLogradouros> aipLogradouros = new HashSet<AipLogradouros>(0);

	public AipTipoLogradouros() {
	}

	public AipTipoLogradouros(Integer codigo, String abreviatura,
			String descricao) {
		this.codigo = codigo;
		this.abreviatura = abreviatura;
		this.descricao = descricao;
	}

	public AipTipoLogradouros(Integer codigo, String abreviatura,
			String descricao, Set<AipLogradouros> aipLogradouros) {
		this.codigo = codigo;
		this.abreviatura = abreviatura;
		this.descricao = descricao;
		this.aipLogradouros = aipLogradouros;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipTipoLogradourosSq1")
	@Column(name = "CODIGO", nullable = false, precision = 3, scale = 0)
	@DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "ABREVIATURA", unique = true, nullable = false, length = 12)
	@Length(max = 12, message = "Abreviatura do tipo de logradouro tem tamanho máximo de 12 caracteres")
	public String getAbreviatura() {
		return this.abreviatura;
	}

	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	@Column(name = "DESCRICAO", unique = true, nullable = false, length = 60)
	@Length(max = 60)
	@Field(index = Index.YES, store = Store.YES)
	@Analyzer(impl=BrazilianAnalyzer.class)	
	public String getDescricao() {
		return this.descricao;
	}
	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricaoFonetica() {
		return Fonetizador.fonetizar(descricao);
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipTipoLogradouro")
	public Set<AipLogradouros> getAipLogradouros() {
		return this.aipLogradouros;
	}

	public void setAipLogradouros(Set<AipLogradouros> aipLogradouros) {
		this.aipLogradouros = aipLogradouros;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		CODIGO("codigo"), ABREVIATURA("abreviatura"), DESCRICAO("descricao"), LOGRADOUROS(
				"aipLogradouros"), DESCRICAO_FONETICA("descricaoFonetica");

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
		if (!(obj instanceof AipTipoLogradouros)) {
			return false;
		}
		AipTipoLogradouros other = (AipTipoLogradouros) obj;
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
