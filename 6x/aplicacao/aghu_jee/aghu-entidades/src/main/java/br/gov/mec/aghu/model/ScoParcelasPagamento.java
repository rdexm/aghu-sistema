package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SCO_PARCELAS_PAGAMENTO", schema = "AGH")
@SequenceGenerator(name = "scoPcgSq1", sequenceName = "AGH.SCO_PCG_SQ1", allocationSize = 1)
public class ScoParcelasPagamento extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */ 
	private static final long serialVersionUID = -4498916464410706382L;
	private Integer seq;
	private ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropos;
	private Short parcela;
	private Short prazo;
	private BigDecimal percPagamento;
	private BigDecimal valorPagamento;
	private Integer version;
	//transient
	private Boolean emEdicao = Boolean.FALSE;
	private Boolean ultimaParcela = Boolean.FALSE;
	
	
	
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoPcgSq1")	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPL_SEQ", referencedColumnName = "SEQ")		
	public ScoCondicaoPgtoLicitacao getCondicaoPgtoLicitacao() {
		return condicaoPgtoLicitacao;
	}
	public void setCondicaoPgtoLicitacao(ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao) {
		this.condicaoPgtoLicitacao = condicaoPgtoLicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDP_NUMERO", referencedColumnName = "NUMERO")		
	public ScoCondicaoPagamentoPropos getCondicaoPagamentoPropos() {
		return condicaoPagamentoPropos;
	}
	
	public void setCondicaoPagamentoPropos(ScoCondicaoPagamentoPropos condicaoPagamentoPropos) {
		this.condicaoPagamentoPropos = condicaoPagamentoPropos;
	}
	
	@Column(name = "PARCELA", nullable = false)
	public Short getParcela() {
		return parcela;
	}
	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}
	
	@Column(name = "PRAZO", nullable = false)
	public Short getPrazo() {
		return prazo;
	}
	public void setPrazo(Short prazo) {
		this.prazo = prazo;
	}
	
	@Column(name = "Perc_Pagamento", precision = 5, scale = 2)
	public BigDecimal getPercPagamento() {
		return percPagamento;
	}
	public void setPercPagamento(BigDecimal percPagamento) {
		this.percPagamento = percPagamento;
	}
	
	@Column(name = "Valor_Pagamento", precision = 15, scale = 2)
	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}
	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}
	
	@Column(name = "VERSION", length = 7, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}	
	
	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}
	
	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
	
	@Transient
	public Boolean getUltimaParcela() {
		return ultimaParcela;
	}
	public void setUltimaParcela(Boolean ultimaParcela) {
		this.ultimaParcela = ultimaParcela;
	}


	public enum Fields {
		SEQ("seq"),		
		CPL_SEQ("condicaoPgtoLicitacao.seq"),		
		CPF_SEQ("condicaoPagamentoPropos.numero"),		
		CDP_NUMERO("condicaoPagamentoPropos"),
		PARCELA("parcela"),
		PRAZO("prazo"),
		PERC_PAGAMENTO("percPagamento"),
		VALOR_PAGAMENTO("valorPagamento"),		
		VERSION("version");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		ScoParcelasPagamento other = (ScoParcelasPagamento) o;

		if (seq != null) {
			return new EqualsBuilder()
					.append(seq, other.seq)
					.isEquals();
		} else {
			return new EqualsBuilder()
					.append(condicaoPagamentoPropos, other.condicaoPagamentoPropos)
					.append(condicaoPgtoLicitacao, other.condicaoPgtoLicitacao)
					.append(parcela, other.parcela)
					.isEquals();
		}
	}
}
