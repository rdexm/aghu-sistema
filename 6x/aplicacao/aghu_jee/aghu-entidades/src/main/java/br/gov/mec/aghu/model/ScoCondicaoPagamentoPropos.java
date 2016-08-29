package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


@Entity
@Table(name = "SCO_CONDICOES_PAGAMENTO_PROPOS", schema = "AGH")
@SequenceGenerator(name = "scoCdpSq1", sequenceName = "AGH.SCO_CDP_SQ1", allocationSize = 1)
public class ScoCondicaoPagamentoPropos extends BaseEntityNumero<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8156230638677874245L;
	private Integer numero;
	private Boolean indCondEscolhida;
	private BigDecimal percAcrescimo;
	private BigDecimal percDesconto;
	private ScoFormaPagamento formaPagamento;
	private ScoPropostaFornecedor propostaFornecedor;
	private ScoItemPropostaFornecedor itemPropostaFornecedor;
	private Set<ScoParcelasPagamento> parcelas;
	private Integer version;

	// construtores

	public ScoCondicaoPagamentoPropos() {
	}

	// getters & setters
	@Id
	@Column(name = "NUMERO", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoCdpSq1")
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "IND_COND_ESCOLHIDA", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCondEscolhida() {
		return this.indCondEscolhida;
	}

	public void setIndCondEscolhida(Boolean indCondEscolhida) {
		this.indCondEscolhida = indCondEscolhida;
	}

	@Column(name = "PERC_ACRESCIMO", precision = 5, scale = 2)
	@Digits(integer=3, fraction=2, message="Percentual de acréscimo dever ter no máximo 3 números inteiros e 2 decimais")
	public BigDecimal getPercAcrescimo() {
		return this.percAcrescimo;
	}

	public void setPercAcrescimo(BigDecimal percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}

	@Column(name = "PERC_DESCONTO",  precision = 5, scale = 2)
	@Digits(integer=3, fraction=2, message="Percentual de desconto dever ter no máximo 3 números inteiros e 2 decimais")
	public BigDecimal getPercDesconto() {
		return this.percDesconto;
	}

	public void setPercDesconto(BigDecimal percDesconto) {
		this.percDesconto = percDesconto;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FPG_CODIGO", referencedColumnName = "CODIGO")
	public ScoFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(ScoFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PFR_FRN_NUMERO", referencedColumnName = "FRN_NUMERO"),
			@JoinColumn(name = "PFR_LCT_NUMERO", referencedColumnName = "LCT_NUMERO") })
	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(
			ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "IPF_PFR_FRN_NUMERO", referencedColumnName = "PFR_FRN_NUMERO"),
			@JoinColumn(name = "IPF_PFR_LCT_NUMERO", referencedColumnName = "PFR_LCT_NUMERO"),
			@JoinColumn(name = "IPF_NUMERO", referencedColumnName = "NUMERO") })
	public ScoItemPropostaFornecedor getItemPropostaFornecedor() {
		return itemPropostaFornecedor;
	}

	public void setItemPropostaFornecedor(
			ScoItemPropostaFornecedor itemPropostasFornecedor) {
		this.itemPropostaFornecedor = itemPropostasFornecedor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "condicaoPagamentoPropos", cascade = { CascadeType.REMOVE })
	public Set<ScoParcelasPagamento> getParcelas() {
		return parcelas;
	}

	public void setParcelas(Set<ScoParcelasPagamento> parcelas) {
		this.parcelas = parcelas;
	}

	@Version
	@Column(name = "version", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("numero", this.numero)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoCondicaoPagamentoPropos)){
			return false;
		}
		ScoCondicaoPagamentoPropos castOther = (ScoCondicaoPagamentoPropos) other;
		return new EqualsBuilder().append(this.numero, castOther.getNumero())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.numero).toHashCode();
	}

	public enum Fields {
		NUMERO("numero"), 
		IND_COND_ESCOLHIDA("indCondEscolhida"), 
		PERC_ACRESCIMO("percAcrescimo"), 
		PERC_DESCONTO("percDesconto"), 
		FORMA_PAGAMENTO("formaPagamento"), 
		PROPOSTA_FORNECEDOR("propostaFornecedor"), 
		ITEM_PROPOSTA_FORNECEDOR("itemPropostaFornecedor"),
		IPF_PFR_FRN_NUMERO("itemPropostaFornecedor.id.pfrFrnNumero"),
		IPF_PFR_LCT_NUMERO("itemPropostaFornecedor.id.pfrLctNumero"),
		IPF_NUMERO("itemPropostaFornecedor.id.numero"),		
		PFR_FRN_NUMERO("propostaFornecedor.id.frnNumero"),
		PFR_LCT_NUMERO("propostaFornecedor.id.lctNumero"),
		PARCELAS("parcelas");
		
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
