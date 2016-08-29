package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * @author marcosilva
 *
 */

@Entity
@Table(name = "SCO_MODALIDADE_LICITACOES", schema = "AGH")
public class ScoModalidadeLicitacao extends BaseEntityCodigo<String> implements Serializable, Cloneable {

	private static final long serialVersionUID = 2684145978273606414L;

	public static final String CODIGO_MODALIDADE_PREGAO = "PG";
	
	private String codigo;
	private String descricao;
	private BigDecimal valorPermitido;
	private BigDecimal valorPermitidoEng;
	private Short prazoEntrega;
	private DominioSituacao situacao;
	private Boolean licitacao;
	private Boolean artigo;
	private String numDocLicit;
	private Integer numEdital;
	private Integer codigoSicon;
	private Integer version;
	private Boolean docLicitacao;
	private Boolean docLicitAno;
	private Boolean edital;
	private Boolean editalAno;
	private Boolean exigeTipo;

	// construtores

	public ScoModalidadeLicitacao() {
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 2, nullable = false)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "VALOR_PERMITIDO", length = 18)
	public BigDecimal getValorPermitido() {
		return this.valorPermitido;
	}

	public void setValorPermitido(BigDecimal valorPermitido) {
		this.valorPermitido = valorPermitido;
	}
	
	@Column(name = "VALOR_PERMITIDO_ENG", length = 18)
	public BigDecimal getValorPermitidoEng() {
		return this.valorPermitidoEng;
	}

	public void setValorPermitidoEng(BigDecimal valorPermitidoEng) {
		this.valorPermitidoEng = valorPermitidoEng;
	}

	@Column(name = "PRAZO_ENTREGA", length = 3)
	public Short getPrazoEntrega() {
		return this.prazoEntrega;
	}

	public void setPrazoEntrega(Short prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Column(name = "IND_LICITACAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(Boolean licitacao) {
		this.licitacao = licitacao;
	}

	@Column(name = "IND_ARTIGO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getArtigo() {
		return artigo;
	}

	public void setArtigo(Boolean artigo) {
		this.artigo = artigo;
	}

	@Column(name = "NUM_DOC_LICIT", length = 7)
	public String getNumDocLicit() {
		return this.numDocLicit;
	}

	public void setNumDocLicit(String numDocLicit) {
		this.numDocLicit = numDocLicit;
	}

	@Column(name = "NUM_EDITAL", length = 7)
	public Integer getNumEdital() {
		return this.numEdital;
	}

	public void setNumEdital(Integer numEdital) {
		this.numEdital = numEdital;
	}

	@Column(name = "CODIGO_SICON", length= 7)
	public Integer getCodigoSicon(){
		return this.codigoSicon;
	}
	
	public void setCodigoSicon(Integer codigoSicon){
		this.codigoSicon = codigoSicon;
	}
	
	@Column(name = "IND_DOC_LICITACAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDocLicitacao() {
		return docLicitacao;
	}

	public void setDocLicitacao(Boolean docLicitacao) {
		this.docLicitacao = docLicitacao;
	}

	@Column(name = "IND_DOC_LICIT_ANO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDocLicitAno() {
		return docLicitAno;
	}

	public void setDocLicitAno(Boolean docLicitAno) {
		this.docLicitAno = docLicitAno;
	}

	@Column(name = "IND_EDITAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEdital() {
		return edital;
	}

	public void setEdital(Boolean edital) {
		this.edital = edital;
	}

	@Column(name = "IND_EDITAL_ANO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEditalAno() {
		return editalAno;
	}

	public void setEditalAno(Boolean editalAno) {
		this.editalAno = editalAno;
	}
	
	@Column(name = "IND_EXIGE_TIPO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExigeTipo() {
		return exigeTipo;
	}

	public void setExigeTipo(Boolean exigeTipo) {
		this.exigeTipo = exigeTipo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoModalidadeLicitacao)){
			return false;
		}
		ScoModalidadeLicitacao castOther = (ScoModalidadeLicitacao) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}
	

	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public enum Fields {
		CODIGO("codigo"), 
		DESCRICAO("descricao"), 
		VALOR_PERMITIDO("valorPermitido"), 
		VALOR_PERMITIDO_ENG("valorPermitidoEng"),
		PRAZO_ENTREGA("prazoEntrega"), 
		SITUACAO("situacao"), 
		IND_LICITACAO("licitacao"), 
		IND_ARTIGO("artigo"), 
		NUM_DOC_LICIT("numDocLicit"), 
		NUM_EDITAL("numEdital"), 
		CODIGO_SICON("codigoSicon"),
		IND_DOC_LICITACAO("docLicitacao"), 
		IND_DOC_LICIT_ANO("docLicitAno"), 
		IND_EDITAL("edital"), 
		IND_EDITAL_ANO("editalAno"),
		IND_EXIGE_TIPO("exigeTipo"),
		VERSION("version");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Transient
	public String obtemCodigoModalidade() {
		if (getCodigo().equals("CV")) {				// 01 – Convite
			return "01";
		} else if (getCodigo().equals("TP")) {		// 02 - Tomada de Preços
			return "02";
		} else if (getCodigo().equals("CC")) {		// 03 - Concorrência
			return "03";
		} else if (getCodigo().equals("")) {		// 04 - Concorrência Internacional
			return "04";
		} else if (getCodigo().equals("PG")) {		// 05 – Pregão
			return "05";
		} else if (getCodigo().equals("DI")) {		// 06 – Dispensa de Licitação
			return "06";
		} else if (getCodigo().equals("IN")) {		// 07 – Inexigibilidade
			return "07";
		} else if (getCodigo().equals("RD")) {		// 08 – Regime Diferenciado de Contratação
			return "08";
		} else if (getCodigo().equals("CN")) {		// 20 – Concurso
			return "20";
		}
		return null;
	}

}
