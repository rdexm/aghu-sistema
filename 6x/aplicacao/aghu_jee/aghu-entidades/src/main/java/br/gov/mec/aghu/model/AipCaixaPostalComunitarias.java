package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "AIP_CAIXA_POSTAL_COMUNITARIAS", schema = "AGH")
@Indexed
public class AipCaixaPostalComunitarias  extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 6055656231009153704L;
	
	private Integer codigo;
	private AipCidades cidade;
	private String ufSigla;
	private String nome;
	private Integer cep;
	private Integer codigoBaseCorreio;
	private DominioSituacao indSituacao;
	private String endereco;
	private Integer version;
	private Integer codigoEsperanto;
	
	@Id
	@Column(name = "CODIGO", nullable = false, precision = 6, scale = 0)
	@DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDD_CODIGO")
	public AipCidades getCidade() {
		return cidade;
	}

	public void setCidade(AipCidades cidade) {
		this.cidade = cidade;
	}

	@Column(name = "UF_SIGLA", nullable = true)
	public String getUfSigla() {
		return ufSigla;
	}

	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = true)
	@Enumerated(EnumType.STRING)
	@Field(index = Index.YES, store = Store.YES)
	@Analyzer(impl=BrazilianAnalyzer.class)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "NOME", length = 100)
	@Length(max = 100)
	@Field(index = Index.YES, store = Store.YES)
	@Analyzer(impl=BrazilianAnalyzer.class)
	public String getNome() {
		return this.nome;
	}

	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getNomeFonetico() {
		return Fonetizador.fonetizar(nome);
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "ENDERECO", length = 100)
	@Length(max = 100)
	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	@Column(name = "CEP")
	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	@Column(name = "CODIGO_BASE_CORREIO")
	public Integer getCodigoBaseCorreio() {
		return codigoBaseCorreio;
	}

	public void setCodigoBaseCorreio(Integer codigoBaseCorreio) {
		this.codigoBaseCorreio = codigoBaseCorreio;
	}

	@Column(name="VERSION", nullable=true)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name="CODIGO_ESPERANTO", nullable=true)
	public Integer getCodigoEsperanto() {
		return codigoEsperanto;
	}

	public void setCodigoEsperanto(Integer codigoEsperanto) {
		this.codigoEsperanto = codigoEsperanto;
	}


	public enum Fields {
		
		CODIGO("codigo"),
		CIDADE("cidade"),
		NOME("nome"),
		NOME_FONETICO("nomeFonetico"),
		ENDERECO("endereco"),
		CEP("cep"),
		UF_SIGLA("ufSigla"),
		IND_SITUACAO("indSituacao"),
		VERSION("version"),
		CODIGO_ESPERANTO("codigoEsperanto"),
		CODIGO_BASE_CORREIO("codigoBaseCorreio");
		
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
		if (!(obj instanceof AipCaixaPostalComunitarias)) {
			return false;
		}
		AipCaixaPostalComunitarias other = (AipCaixaPostalComunitarias) obj;
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
