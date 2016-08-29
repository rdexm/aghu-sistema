package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_PROPOSTAS_FORNECEDORES", schema = "AGH")
public class ScoPropostaFornecedor extends BaseEntityId<ScoPropostaFornecedorId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8263032544240478393L;
	private ScoPropostaFornecedorId id;
	private Date dtDigitacao;
	private Date dtApresentacao;
	private BigDecimal valorTotalFrete;
	private Short prazoEntrega;
	private Boolean indExclusao;
	private Date dtExclusao;
	private ScoLicitacao licitacao;
	private List<ScoItemPropostaFornecedor> itens;
	private List<ScoCondicaoPagamentoPropos> condicoesPagamento;
	private ScoFornecedor fornecedor;
	private RapServidores servidor;

	// construtores

	public ScoPropostaFornecedor() {
	}

	public ScoPropostaFornecedor(ScoPropostaFornecedorId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "LCT_NUMERO", column = @Column(name = "LCT_NUMERO", nullable = false, length = 7)),
			@AttributeOverride(name = "FRN_NUMERO", column = @Column(name = "FRN_NUMERO", nullable = false, length = 5)) })
	public ScoPropostaFornecedorId getId() {
		return this.id;
	}

	public void setId(ScoPropostaFornecedorId id) {
		this.id = id;
	}

	@Column(name = "DT_DIGITACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name = "DT_APRESENTACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtApresentacao() {
		return this.dtApresentacao;
	}

	public void setDtApresentacao(Date dtApresentacao) {
		this.dtApresentacao = dtApresentacao;
	}

	@Column(name = "VALOR_TOTAL_FRETE", precision = 18, scale = 2)
	@Digits(integer=16, fraction=2, message="Valor total do frete dever ter no máximo 16 números inteiros e 2 decimais")
	public BigDecimal getValorTotalFrete() {
		return this.valorTotalFrete;
	}

	public void setValorTotalFrete(BigDecimal valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	@Column(name = "PRAZO_ENTREGA", length = 3)
	public Short getPrazoEntrega() {
		return this.prazoEntrega;
	}

	public void setPrazoEntrega(Short prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	@Column(name = "IND_EXCLUSAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusao() {
		return this.indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}

	@Column(name = "DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LCT_NUMERO", referencedColumnName = "NUMERO", insertable=false, updatable=false)
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO", insertable=false, updatable=false)
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "propostaFornecedor", cascade = { CascadeType.REMOVE })
	public List<ScoItemPropostaFornecedor> getItens() {
		return itens;
	}

	public void setItens(List<ScoItemPropostaFornecedor> itens) {
		this.itens = itens;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "propostaFornecedor", cascade = { CascadeType.REMOVE })
	public List<ScoCondicaoPagamentoPropos> getCondicoesPagamento() {
		return condicoesPagamento;
	}

	public void setCondicoesPagamento(List<ScoCondicaoPagamentoPropos> condicoesPagamento) {
		this.condicoesPagamento = condicoesPagamento;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoPropostaFornecedor)){
			return false;
		}
		ScoPropostaFornecedor castOther = (ScoPropostaFornecedor) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"),
		LICITACAO_ID("id.lctNumero"),
		FORNECEDOR_ID("id.frnNumero"),
		DT_DIGITACAO("dtDigitacao"),
		DT_APRESENTACAO("dtApresentacao"), 
		VALOR_TOTAL_FRETE("valorTotalFrete"), 
		PRAZO_ENTREGA("prazoEntrega"), 
		IND_EXCLUSAO("indExclusao"), 
		DT_EXCLUSAO("dtExclusao"), 
		LICITACAO("licitacao"), 
		FORNECEDOR("fornecedor"),
		ITEM("itens"),
		SERVIDOR("servidor");

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
