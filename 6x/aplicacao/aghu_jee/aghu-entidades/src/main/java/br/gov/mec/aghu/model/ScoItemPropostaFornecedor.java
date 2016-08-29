package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_ITEM_PROPOSTAS_FORNECEDOR", schema = "AGH")
public class ScoItemPropostaFornecedor extends BaseEntityId<ScoItemPropostaFornecedorId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5274912006300792057L;
	private ScoItemPropostaFornecedorId id;
	private FcpMoeda moeda;
	private Long quantidade;
	private Boolean indEscolhido;
	private Boolean indComDesconto;
	private Boolean indNacional;
	private Boolean indDesclassificado;
	private Integer fatorConversao;
	private BigDecimal percAcrescimo;
	private BigDecimal percIpi;
	private BigDecimal percDesconto;
	private BigDecimal valorUnitario;
	private String observacao;
	private DominioMotivoDesclassificacaoItemProposta motDesclassif;
	private Boolean indExclusao;
	private Date dtExclusao;
	private Boolean indAnalisadoPt;
	private Boolean indAutorizUsr;
	private String justifAutorizUsr;
	private String apresentacao;
	private String nroOrcamento;
	private Date dtEscolha;
	private String codigoItemFornecedor;
	private ScoPropostaFornecedor propostaFornecedor;
	private ScoItemLicitacao itemLicitacao;
	private ScoUnidadeMedida unidadeMedida;
	private ScoMarcaComercial marcaComercial;
	private ScoNomeComercial nomeComercial;
	private ScoMarcaModelo modeloComercial;
	private ScoCriterioEscolhaProposta criterioEscolhaProposta;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropos;
	private Set<ScoItemAutorizacaoForn> itensAutorizacaoForns;
	private Set<ScoCondicaoPagamentoPropos> condicoesPagamento;
	private Date dtEntregaAmostra;
	
	
	
	// construtores

	public ScoItemPropostaFornecedor() {
	}

	public ScoItemPropostaFornecedor(ScoItemPropostaFornecedorId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "PFR_LCT_NUMERO", column = @Column(name = "PFR_LCT_NUMERO", nullable = false, length = 7)),
			@AttributeOverride(name = "PFR_FRN_NUMERO", column = @Column(name = "PFR_FRN_NUMERO", nullable = false, length = 5)),
			@AttributeOverride(name = "NUMERO", column = @Column(name = "NUMERO", nullable = false, length = 3)) })
	public ScoItemPropostaFornecedorId getId() {
		return this.id;
	}

	public void setId(ScoItemPropostaFornecedorId id) {
		this.id = id;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDA_CODIGO", referencedColumnName = "CODIGO")
	public FcpMoeda getMoeda() {
		return this.moeda;
	}

	public void setMoeda(FcpMoeda moeda) {
		this.moeda = moeda;
	}

	@Column(name = "QUANTIDADE", length = 10, nullable = false)
	public Long getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "IND_ESCOLHIDO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEscolhido() {
		return this.indEscolhido;
	}

	public void setIndEscolhido(Boolean indEscolhido) {
		this.indEscolhido = indEscolhido;
	}

	@Column(name = "IND_COM_DESCONTO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndComDesconto() {
		return this.indComDesconto;
	}

	public void setIndComDesconto(Boolean indComDesconto) {
		this.indComDesconto = indComDesconto;
	}

	@Column(name = "IND_NACIONAL", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNacional() {
		return this.indNacional;
	}

	public void setIndNacional(Boolean indNacional) {
		this.indNacional = indNacional;
	}

	@Column(name = "IND_DESCLASSIFICADO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDesclassificado() {
		return this.indDesclassificado;
	}

	public void setIndDesclassificado(Boolean indDesclassificado) {
		this.indDesclassificado = indDesclassificado;
	}

	@Column(name = "FATOR_CONVERSAO", length = 6, nullable = false)
	public Integer getFatorConversao() {
		return this.fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	@Column(name = "PERC_ACRESCIMO", precision = 6, scale = 2)
	@Digits(integer=4, fraction=2, message="Percentual de acréscimo dever ter no máximo 4 números inteiros e 2 decimais")
	public BigDecimal getPercAcrescimo() {
		return this.percAcrescimo;
	}

	public void setPercAcrescimo(BigDecimal percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}

	@Column(name = "PERC_IPI", precision = 6, scale = 2)
	@Digits(integer=4, fraction=2, message="Percentual de IPI dever ter no máximo 4 números inteiros e 2 decimais")
	public BigDecimal getPercIpi() {
		return this.percIpi;
	}

	public void setPercIpi(BigDecimal percIpi) {
		this.percIpi = percIpi;
	}

	@Column(name = "PERC_DESCONTO", precision = 6, scale = 2)
	@Digits(integer=4, fraction=2, message="Percentual de desconto dever ter no máximo 4 números inteiros e 2 decimais")
	public BigDecimal getPercDesconto() {
		return this.percDesconto;
	}

	public void setPercDesconto(BigDecimal percDesconto) {
		this.percDesconto = percDesconto;
	}

	@Column(name = "VALOR_UNITARIO", precision = 20, scale = 4)
	@Digits(integer=16, fraction=4, message="Valor unitário dever ter no máximo 16 números inteiros e 4 decimais")
	public BigDecimal getValorUnitario() {
		return this.valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(name = "OBSERVACAO", length = 60)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "MOT_DESCLASSIF", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioMotivoDesclassificacaoItemProposta getMotDesclassif() {
		return this.motDesclassif;
	}

	public void setMotDesclassif(DominioMotivoDesclassificacaoItemProposta motDesclassif) {
		this.motDesclassif = motDesclassif;
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

	@Column(name = "IND_ANALISADO_PT", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAnalisadoPt() {
		return this.indAnalisadoPt;
	}

	public void setIndAnalisadoPt(Boolean indAnalisadoPt) {
		this.indAnalisadoPt = indAnalisadoPt;
	}

	@Column(name = "IND_AUTORIZ_USR", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAutorizUsr() {
		return this.indAutorizUsr;
	}

	public void setIndAutorizUsr(Boolean indAutorizUsr) {
		this.indAutorizUsr = indAutorizUsr;
	}

	@Column(name = "JUSTIF_AUTORIZ_USR", length = 60)
	public String getJustifAutorizUsr() {
		return this.justifAutorizUsr;
	}

	public void setJustifAutorizUsr(String justifAutorizUsr) {
		this.justifAutorizUsr = justifAutorizUsr;
	}

	@Column(name = "CODIGO_ITEM_FORN", length = 20)
	public String getCodigoItemFornecedor() {
		return codigoItemFornecedor;
	}

	public void setCodigoItemFornecedor(String codigoItemFornecedor) {
		this.codigoItemFornecedor = codigoItemFornecedor;
	}
	
	@Column(name = "APRESENTACAO", length = 10)
	public String getApresentacao() {
		return this.apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	@Column(name = "NRO_ORCAMENTO", length = 10)
	public String getNroOrcamento() {
		return this.nroOrcamento;
	}

	public void setNroOrcamento(String nroOrcamento) {
		this.nroOrcamento = nroOrcamento;
	}

	@Column(name = "DT_ESCOLHA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEscolha() {
		return this.dtEscolha;
	}

	public void setDtEscolha(Date dtEscolha) {
		this.dtEscolha = dtEscolha;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PFR_LCT_NUMERO", referencedColumnName = "LCT_NUMERO", insertable=false, updatable=false),
			@JoinColumn(name = "PFR_FRN_NUMERO", referencedColumnName = "FRN_NUMERO", insertable=false, updatable=false) })
	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(
			ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ITL_LCT_NUMERO", referencedColumnName = "LCT_NUMERO"),
			@JoinColumn(name = "ITL_NUMERO", referencedColumnName = "NUMERO") })
	public ScoItemLicitacao getItemLicitacao() {
		return itemLicitacao;
	}

	public void setItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.itemLicitacao = itemLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MCM_CODIGO", referencedColumnName = "CODIGO")
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "NC_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
			@JoinColumn(name = "NC_NUMERO", referencedColumnName = "NUMERO") })
	public ScoNomeComercial getNomeComercial() {
		return nomeComercial;
	}

	public void setNomeComercial(ScoNomeComercial nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "MOM_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
			@JoinColumn(name = "MOM_SEQP", referencedColumnName = "SEQP") })
	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CEP_CODIGO", referencedColumnName = "CODIGO")
	public ScoCriterioEscolhaProposta getCriterioEscolhaProposta() {
		return criterioEscolhaProposta;
	}

	public void setCriterioEscolhaProposta(
			ScoCriterioEscolhaProposta criterioEscolhaProposta) {
		this.criterioEscolhaProposta = criterioEscolhaProposta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDP_NUMERO", referencedColumnName = "NUMERO")
	public ScoCondicaoPagamentoPropos getCondicaoPagamentoPropos() {
		return condicaoPagamentoPropos;
	}

	public void setCondicaoPagamentoPropos(
			ScoCondicaoPagamentoPropos condicaoPagamentoPropos) {
		this.condicaoPagamentoPropos = condicaoPagamentoPropos;
	}

	@OneToMany(mappedBy="itemPropostaFornecedor")	
	public Set<ScoItemAutorizacaoForn> getItensAutorizacaoForns() {
		return itensAutorizacaoForns;
	}

	public void setItensAutorizacaoForns(
			Set<ScoItemAutorizacaoForn> itensAutorizacaoForns) {
		this.itensAutorizacaoForns = itensAutorizacaoForns;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemPropostaFornecedor", cascade = { CascadeType.REMOVE })
	public Set<ScoCondicaoPagamentoPropos> getCondicoesPagamento() {
		return condicoesPagamento;
	}

	public void setCondicoesPagamento(
			Set<ScoCondicaoPagamentoPropos> condicoesPagamento) {
		this.condicoesPagamento = condicoesPagamento;
	}

	@Column(name = "DT_ENTREGA_AMOSTRA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEntregaAmostra() {
		return this.dtEntregaAmostra;
	}

	public void setDtEntregaAmostra(Date dtEntregaAmostra) {
		this.dtEntregaAmostra = dtEntregaAmostra;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoItemPropostaFornecedor)){
			return false;
		}
		ScoItemPropostaFornecedor castOther = (ScoItemPropostaFornecedor) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"),		
		PROPOSTA_FORNECEDOR_LICITACAO_ID("id.pfrLctNumero"), 
		PROPOSTA_FORNECEDOR_FORNECEDOR_ID("id.pfrFrnNumero"), 
		NUMERO("id.numero"),  
		MOEDA("moeda"), QUANTIDADE("quantidade"), IND_ESCOLHIDO("indEscolhido"), 
		IND_COM_DESCONTO("indComDesconto"), IND_NACIONAL("indNacional"), 
		IND_DESCLASSIFICADO("indDesclassificado"), FATOR_CONVERSAO("fatorConversao"),
		PERC_ACRESCIMO("percAcrescimo"), PERC_IPI("percIpi"), 
		PERC_DESCONTO("percDesconto"), VALOR_UNITARIO("valorUnitario"), 
		OBSERVACAO("observacao"), MOT_DESCLASSIF("motDesclassif"), 
		IND_EXCLUSAO("indExclusao"), DT_EXCLUSAO("dtExclusao"), 
		IND_ANALISADO_PT("indAnalisadoPt"), IND_AUTORIZ_USR("indAutorizUsr"), 
		JUSTIF_AUTORIZ_USR("justifAutorizUsr"), APRESENTACAO("apresentacao"), 
		NRO_ORCAMENTO("nroOrcamento"), DT_ESCOLHA("dtEscolha"), 
		PROPOSTA_FORNECEDOR("propostaFornecedor"), ITEM_LICITACAO("itemLicitacao"), 
		UNIDADE_MEDIDA("unidadeMedida"), MARCA_COMERCIAL("marcaComercial"), 
		NOME_COMERCIAL("nomeComercial"), CRITERIO_ESCOLHA_PROPOSTA("criterioEscolhaProposta"),
		CONDICAO_PAGAMENTO_PROPOS("condicaoPagamentoPropos"),
		NUMERO_CONDICAO_PAGAMENTO_PROPOS("condicaoPagamentoPropos.numero"),
		CODIGO_ITEM_FORNECEDOR("codigoItemFornecedor"),
		ITEM_LICITACAO_ID_LCT_NUMERO("itemLicitacao.id.lctNumero"),
		MARCA_MODELO("modeloComercial"),
		MOM_SEQP("modeloComercial.id.seqp"),
		MOM_MCM_CODIGO("modeloComercial.id.mcmCodigo"),
		MCM_CODIGO("marcaComercial.codigo"), NC_MCM_CODIGO("nomeComercial.id.mcmCodigo"),
		ITL_LCT_NUMERO("itemLicitacao.id.lctNumero"), ITL_NUMERO("itemLicitacao.id.numero"),
		UMD_CODIGO("unidadeMedida.codigo"), SLS("itemLicitacao.fasesSolicitacao.solicitacaoServico"),
		SLC("itemLicitacao.fasesSolicitacao.solicitacaoDeCompra"), FRN("propostaFornecedor.fornecedor"),
		NC_NUMERO("nomeComercial.id.numero"), DT_ENTREGA_AMOSTRA("dtEntregaAmostra"), 
		MDA_CODIGO("moeda.codigo"), AUTORIZACAO("autorizacao"), MODELO_COMERCIAL("modeloComercial"), ITENS_AUT_FORN("itensAutorizacaoForns");

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
