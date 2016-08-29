package br.gov.mec.aghu.model;

import java.util.ArrayList;
import java.util.List;

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
import org.hibernate.annotations.Cascade;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;



@Entity
@Indexed
@SequenceGenerator(name="aipOcupacoesSq1", sequenceName="AGH.AIP_OCP_SQ1", allocationSize = 1)
@Table(name = "AIP_OCUPACOES", schema = "AGH")
public class AipOcupacoes extends BaseEntityCodigo<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3575899759451191796L;

	/**
	 * chave primária no banco.
	 */
	private Integer codigo;

	/**
	 * descricao da ocupação.
	 */
	private String descricao;

	private Integer version;

	/**
	 * sinonimos da ocupação.
	 */
	private List<AipSinonimosOcupacao> sinonimos = new ArrayList<AipSinonimosOcupacao>(
			0);

	public AipOcupacoes() {
	}

	public AipOcupacoes(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public AipOcupacoes(Integer codigo, String descricao,
			List<AipSinonimosOcupacao> sinonimos) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.sinonimos = sinonimos;
	}

	@Id
	@Column(name = "CODIGO", nullable = false, precision = 6, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipOcupacoesSq1")
	@DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", unique = true, nullable = false, length = 60)
	@Length(max = 60)
	@Field(index = Index.YES, store = Store.YES, termVector=TermVector.WITH_POSITION_OFFSETS)
	@Analyzer(impl=BrazilianAnalyzer.class)
	public String getDescricao() {
		return this.descricao;
	}
	
	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricaoFonetica() {
		return Fonetizador.fonetizar(descricao);
	}
	
	@Field(index=Index.YES, store=Store.YES)
	@Transient
	public String getOrdenacaoLucene() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipOcupacoes")
	@Cascade( {org.hibernate.annotations.CascadeType.DELETE })
	public List<AipSinonimosOcupacao> getSinonimos() {
		return this.sinonimos;
	}

	public void setSinonimos(List<AipSinonimosOcupacao> sinonimos) {
		this.sinonimos = sinonimos;
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

		CODIGO("codigo"), DESCRICAO("descricao"),ORDENACAOLUCENE("ordenacaoLucene"),DESCRICAOFONETICA("descricaoFonetica"), SINONIMOS("sinonimos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}

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
		if (!(obj instanceof AipOcupacoes) ){
			return false;
		}
		AipOcupacoes other = (AipOcupacoes) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

}
