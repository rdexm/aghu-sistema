package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioItemNotaPreRecebimento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * 
 * Entidade para a tabela SCE_ITEM_NOTA_PRE_RECEBIMENTO.
 *
 */
@Entity
@Table(name="SCE_ITEM_NOTA_PRE_RECEBIMENTO", schema = "AGH")
@SequenceGenerator(name = "sceIprSq1", sequenceName = "AGH.SCE_IPR_SQ1", allocationSize = 1)
public class SceItemNotaPreRecebimento extends BaseEntitySeq<Long> implements Serializable {

	private static final long serialVersionUID = -7414419798629684293L;

	private Long seq;
	
	private Long quantidade;
	
	private BigDecimal valor;
	
	private Date dataGeracao;
	
	private DominioItemNotaPreRecebimento situacao;
	
	private Integer version;
	
	private SceE660IncForn sceE660IncForn;
	
	private ScoItemAutorizacaoForn scoItemAutorizacaoForn;
	
	private SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada;
	
	private Short iafNumero;
	
	private Integer iafAfnNumero;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceIprSq1")
	@Column(name="SEQ")
	public Long getSeq() {
		return seq;
	}
	
	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name="QUANTIDADE")
	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name="VALOR")
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name="DT_GERACAO")
	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioItemNotaPreRecebimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioItemNotaPreRecebimento situacao) {
		this.situacao = situacao;
	}

	@Version
	@Column(name="VERSION", nullable=false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SEQ_INC", referencedColumnName="SEQ_INC")
	public SceE660IncForn getSceE660IncForn() {
		return sceE660IncForn;
	}

	public void setSceE660IncForn(SceE660IncForn sceE660IncForn) {
		this.sceE660IncForn = sceE660IncForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({  
        @JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO"),  
        @JoinColumn(name="IAF_NUMERO", referencedColumnName="NUMERO")
    })
	public ScoItemAutorizacaoForn getScoItemAutorizacaoForn() {
		return scoItemAutorizacaoForn;
	}

	public void setScoItemAutorizacaoForn(
			ScoItemAutorizacaoForn scoItemAutorizacaoForn) {
		this.scoItemAutorizacaoForn = scoItemAutorizacaoForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="DFE_SEQ", referencedColumnName="SEQ")
	public SceDocumentoFiscalEntrada getSceDocumentoFiscalEntrada() {
		return sceDocumentoFiscalEntrada;
	}

	public void setSceDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.sceDocumentoFiscalEntrada = sceDocumentoFiscalEntrada;
	}
	
	@Column(name = "IAF_NUMERO", insertable = false, updatable = false)
	public Short getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Short iafNumero) {
		this.iafNumero = iafNumero;
	}
	
	@Column(name = "IAF_AFN_NUMERO", insertable = false, updatable = false)
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}
	
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SceItemNotaPreRecebimento)) {
			return false;
		}
		SceItemNotaPreRecebimento other = (SceItemNotaPreRecebimento) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());	
		return umEqualsBuilder.isEquals();
	}
	

    public enum Fields {
		DOCUMENTO_FISCAL_ENTRADA("sceDocumentoFiscalEntrada"),
		DFE_SEQ("sceDocumentoFiscalEntrada.seq"),
		QUANTIDADE("quantidade"),
		IAF_AFN_NUMERO("iafAfnNumero"),
		IAF_NUMERO("iafNumero"),
		SEQ_INC("sceE660IncForn.seq"),
		SCE_INC_FORN("sceE660IncForn"),
		IND_SITUACAO("situacao");
		
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
