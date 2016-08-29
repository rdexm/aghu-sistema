package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.annotations.Immutable;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "V_AIP_CEPS", schema = "AGH")
@Immutable
public class VAipCeps extends BaseEntityId<VAipCepsId> implements java.io.Serializable {

	private static final long serialVersionUID = -1579345921024916378L;
	
	private VAipCepsId id;
	private Integer codigoLogradouro;        
	private Integer codigoBairroLogradouro;        
	private String logradouro; 
	private String numeroInicialLogradouro;  
	private String numeroFinalLogradouro;
	private String ladoLogradouro;
	private String nome; 
	private Integer codigoCidade;     
	private String cidade;  
	private Integer codigoIbge;    
	private String bairro; 
	private String ufSigla;
	private DominioSituacao indSituacao;   
	private String tipo;  
	private String classificacao;       
	private Integer codigoBaseCorreio;
	private AipBairrosCepLogradouro aipBairrosCepLogradouro;
	private AipCidades aipCidade;
	private AipLogradouros aipLogradouro;
	private AipBairros aipBairro;
	
	public VAipCeps() {
	}

	public VAipCeps(VAipCepsId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cep", column = @Column(name = "CEP")),
			@AttributeOverride(name = "rowId", column = @Column(name = "ROW_ID"))})
	public VAipCepsId getId() {
		return this.id;
	}

	public void setId(VAipCepsId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LGR_CODIGO", insertable = false, updatable = false, nullable = true)
	public AipLogradouros getAipLogradouro() {
		return aipLogradouro;
	}

	public void setAipLogradouro(AipLogradouros aipLogradouro) {
		this.aipLogradouro = aipLogradouro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LGR_BAI_CODIGO", insertable = false, updatable = false, nullable = true)
	public AipBairros getAipBairro() {
		return aipBairro;
	}

	public void setAipBairro(AipBairros aipBairro) {
		this.aipBairro = aipBairro;
	}

	@Transient
	public AipBairrosCepLogradouro getAipBairrosCepLogradouro() {
		return this.aipBairrosCepLogradouro;
	}

	public void setAipBairrosCepLogradouro(
			AipBairrosCepLogradouro aipBairrosCepLogradouro) {
		this.aipBairrosCepLogradouro = aipBairrosCepLogradouro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDD_CODIGO", nullable = true, insertable = false, updatable = false)
	public AipCidades getAipCidade() {
		return aipCidade;
	}

	public void setAipCidade(AipCidades aipCidade) {
		this.aipCidade = aipCidade;
	}

	@Column(name = "BAIRRO", nullable = true)
	public String getBairro() {
		return this.bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name = "LOGRADOURO", nullable = true)
	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(name = "LGR_NRO_INICIAL", nullable = true)
	public String getNumeroInicialLogradouro() {
		return numeroInicialLogradouro;
	}

	public void setNumeroInicialLogradouro(String numeroInicialLogradouro) {
		this.numeroInicialLogradouro = numeroInicialLogradouro;
	}

	@Column(name = "LGR_NRO_FINAL", nullable = true)
	public String getNumeroFinalLogradouro() {
		return numeroFinalLogradouro;
	}

	public void setNumeroFinalLogradouro(String numeroFinalLogradouro) {
		this.numeroFinalLogradouro = numeroFinalLogradouro;
	}

	@Column(name = "LGR_LADO", nullable = true)
	public String getLadoLogradouro() {
		return ladoLogradouro;
	}

	public void setLadoLogradouro(String ladoLogradouro) {
		this.ladoLogradouro = ladoLogradouro;
	}

	@Column(name = "NOME", nullable = true)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "CDD_CODIGO", nullable = true)
	public Integer getCodigoCidade() {
		return codigoCidade;
	}

	public void setCodigoCidade(Integer codigoCidade) {
		this.codigoCidade = codigoCidade;
	}

	@Column(name = "CIDADE", nullable = true)
	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@Column(name = "COD_IBGE", nullable = true)
	public Integer getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(Integer codigoIbge) {
		this.codigoIbge = codigoIbge;
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

	@Column(name = "TIPO", nullable = true)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "CLASSIFICACAO", nullable = true)
	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	@Column(name = "CODIGO_BASE_CORREIO", nullable = true)
	public Integer getCodigoBaseCorreio() {
		return codigoBaseCorreio;
	}

	public void setCodigoBaseCorreio(Integer codigoBaseCorreio) {
		this.codigoBaseCorreio = codigoBaseCorreio;
	}

	@Column(name = "LGR_CODIGO", nullable = true)
	public Integer getCodigoLogradouro() {
		return codigoLogradouro;
	}

	public void setCodigoLogradouro(Integer codigoLogradouro) {
		this.codigoLogradouro = codigoLogradouro;
	}

	@Column(name = "LGR_BAI_CODIGO", nullable = true)
	public Integer getCodigoBairroLogradouro() {
		return codigoBairroLogradouro;
	}

	public void setCodigoBairroLogradouro(Integer codigoBairroLogradouro) {
		this.codigoBairroLogradouro = codigoBairroLogradouro;
	}

	@Transient
	public String getCepFormatado(){
		return CoreUtil.formataCEP(this.id.getCep());
	}

	@Transient

	public String getLogradouroSuggestion(){
		String logradouroSuggestion = "";
		if(logradouro != null && !logradouro.trim().equals("")){
			logradouroSuggestion = logradouro;
		}else if (nome != null && !nome.trim().equals("")){
			logradouroSuggestion = nome;
		}
		return logradouroSuggestion.replace("'", "");
	}

	public enum Fields {

		CEP("id.cep"),
		ROW_ID("id.rowId"),
		LOGRADOURO("logradouro"),
		LOGRADOURO_FONETICO("logradouroFonetico"),
		LGR_CODIGO("codigoLogradouro"),
		LGR_BAI_CODIGO("codigoBairroLogradouro"),
		LGR_NRO_INICIAL("numeroInicialLogradouro"),
		LGR_NRO_FINAL("numeroFinalLogradouro"),
		LGR_LADO("ladoLogradouro"),
		NOME("nome"),
		CDD_CODIGO("codigoCidade"),
		CIDADE("cidade"),
		COD_IBGE("codigoIbge"),
		BAIRRO("bairro"),
		UF_SIGLA("ufSigla"),
		IND_SITUACAO("indSituacao"),
		TIPO("tipo"),
		CLASSIFICACAO("classificacao"),
		CODIGO_BASE_CORREIO("codigoBaseCorreio");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode())
				+ ((codigoLogradouro == null) ? 0 : codigoLogradouro.hashCode())
				+ ((codigoBairroLogradouro == null) ? 0 : codigoBairroLogradouro.hashCode())
				+ ((codigoCidade == null) ? 0 : codigoCidade.hashCode());
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
		if (!(obj instanceof VAipCeps)) {
			return false;
		}
		VAipCeps other = (VAipCeps) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (codigoBairroLogradouro == null) {
			if (other.codigoBairroLogradouro != null) {
				return false;
			}
		} else if (!codigoBairroLogradouro.equals(other.codigoBairroLogradouro)) {
			return false;
		}
		if (codigoLogradouro == null) {
			if (other.codigoLogradouro != null) {
				return false;
			}
		} else if (!codigoLogradouro.equals(other.codigoLogradouro)) {
			return false;
		}
		return true;
	}
}
