package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoItemNotaRecebimento;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VSceItemNotaRecebimentoAutorizacaoFornecimentoId implements EntityCompositeId{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4539754192168875567L;
	/**
	 * 
	 */
	
	private Integer numeroAutorizacaoFornecimento;
	private Integer codigoMarca;
	private Integer codigoMarcaNome;
	private Integer codigoMaterialServico;
	private String descricaoMaterialServico;
	private Integer fatorConversao;
	private Boolean recebimento;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Integer numeroItemLicitacao;
	private String marcaNome;
	private String nome;
	private Integer numero;
	private Double percAcrescimo;
	private Double percAcrescimoItem;
	private Double percDesconto;
	private Double percDescontoItem;
	private Double percIpi;
	private Integer qtdeRecebida;
	private Integer qtdeSolicitada;
	private DominioTipoItemNotaRecebimento tipo;
	private String unidadeMedidaItemAutorizacaoFornecimento;
	private ScoUnidadeMedida unidadeMedidaMaterialServico;
	private String unidadeMedidaSolicitacaoCompras;
	private Double valorEfetivado;
	private Double valorUnitarioItemAutorizacaoFornecimento;
	
	@Column(name="AFN_NUMERO")
	public Integer getNumeroAutorizacaoFornecimento() {
		return this.numeroAutorizacaoFornecimento;
	}

	public void setNumeroAutorizacaoFornecimento(Integer numeroAutorizacaoFornecimento) {
		this.numeroAutorizacaoFornecimento = numeroAutorizacaoFornecimento;
	}


	@Column(name="COD_MARCA")
	public Integer getCodigoMarca() {
		return this.codigoMarca;
	}

	public void setCodigoMarca(Integer codMarca) {
		this.codigoMarca = codMarca;
	}


	@Column(name="COD_MARCA_NOME")
	public Integer getCodigoMarcaNome() {
		return this.codigoMarcaNome;
	}

	public void setCodigoMarcaNome(Integer codMarcaNome) {
		this.codigoMarcaNome = codMarcaNome;
	}


	@Column(name="CODIGO")
	public Integer getCodigoMaterialServico() {
		return this.codigoMaterialServico;
	}

	public void setCodigoMaterialServico(Integer codigo) {
		this.codigoMaterialServico = codigo;
	}


	@Column(name="DESCR_MAT_SRV")
	public String getDescricaoMaterialServico() {
		return this.descricaoMaterialServico;
	}

	public void setDescricaoMaterialServico(String descrMatSrv) {
		this.descricaoMaterialServico = descrMatSrv;
	}


	@Column(name="FATOR_CONVERSAO")
	public Integer getFatorConversao() {
		return this.fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}


	@Column(name="IND_RECEBIMENTO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getRecebimento() {
		return this.recebimento;
	}

	public void setRecebimento(Boolean recebimento) {
		this.recebimento = recebimento;
	}

	@Column(name = "IND_SITUACAO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	@Column(name="ITL_NUMERO")
	public Integer getNumeroItemLicitacao() {
		return this.numeroItemLicitacao;
	}

	public void setNumeroItemLicitacao(Integer itlNumero) {
		this.numeroItemLicitacao = itlNumero;
	}


	@Column(name="MARCA_NOME")
	public String getMarcaNome() {
		return this.marcaNome;
	}

	public void setMarcaNome(String marcaNome) {
		this.marcaNome = marcaNome;
	}


	@Column(name="NOME")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	@Column(name="NUMERO")
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	@Column(name="PERC_ACRESCIMO")
	public Double getPercAcrescimo() {
		return this.percAcrescimo;
	}

	public void setPercAcrescimo(Double percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}


	@Column(name="PERC_ACRESCIMO_ITEM")
	public Double getPercAcrescimoItem() {
		return this.percAcrescimoItem;
	}

	public void setPercAcrescimoItem(Double percAcrescimoItem) {
		this.percAcrescimoItem = percAcrescimoItem;
	}


	@Column(name="PERC_DESCONTO")
	public Double getPercDesconto() {
		return this.percDesconto;
	}

	public void setPercDesconto(Double percDesconto) {
		this.percDesconto = percDesconto;
	}


	@Column(name="PERC_DESCONTO_ITEM")
	public Double getPercDescontoItem() {
		return this.percDescontoItem;
	}

	public void setPercDescontoItem(Double percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}


	@Column(name="PERC_IPI")
	public Double getPercIpi() {
		return this.percIpi;
	}

	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}


	@Column(name="QTDE_RECEBIDA")
	public Integer getQtdeRecebida() {
		return this.qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}


	@Column(name="QTDE_SOLICITADA")
	public Integer getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	@Column(name="TIPO")
	@Enumerated(EnumType.STRING)
	public DominioTipoItemNotaRecebimento getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoItemNotaRecebimento tipo) {
		this.tipo = tipo;
	}

	@Column(name="UNID_IAF")
	public String getUnidadeMedidaItemAutorizacaoFornecimento() {
		return this.unidadeMedidaItemAutorizacaoFornecimento;
	}

	public void setUnidadeMedidaItemAutorizacaoFornecimento(String unidIaf) {
		this.unidadeMedidaItemAutorizacaoFornecimento = unidIaf;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNID_MAT_SRV", referencedColumnName = "CODIGO", nullable = false)
	public ScoUnidadeMedida getUnidadeMedidaMaterialServico() {
		return unidadeMedidaMaterialServico;
	}

	public void setUnidadeMedidaMaterialServico(ScoUnidadeMedida unidadeMedidaMaterialServico) {
		this.unidadeMedidaMaterialServico = unidadeMedidaMaterialServico;
	}

	@Column(name="UNID_SC")
	public String getUnidadeMedidaSolicitacaoCompras() {
		return this.unidadeMedidaSolicitacaoCompras;
	}

	public void setUnidadeMedidaSolicitacaoCompras(String unidSc) {
		this.unidadeMedidaSolicitacaoCompras = unidSc;
	}


	@Column(name="VALOR_EFETIVADO")
	public Double getValorEfetivado() {
		return this.valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}


	@Column(name="VALOR_UNIT_IAF")
	public Double getValorUnitarioItemAutorizacaoFornecimento() {
		return this.valorUnitarioItemAutorizacaoFornecimento;
	}

	public void setValorUnitarioItemAutorizacaoFornecimento(Double valorUnitIaf) {
		this.valorUnitarioItemAutorizacaoFornecimento = valorUnitIaf;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getFatorConversao());
		umHashCodeBuilder.append(this.getValorEfetivado());
		umHashCodeBuilder.append(this.getSituacao());
		umHashCodeBuilder.append(this.getTipo());
		umHashCodeBuilder.append(this.getNome());
		umHashCodeBuilder.append(this.getNumero());
		umHashCodeBuilder.append(this.getPercAcrescimo());
		umHashCodeBuilder.append(this.getPercDesconto());
		umHashCodeBuilder.append(this.getQtdeSolicitada());
		umHashCodeBuilder.append(this.getValorUnitarioItemAutorizacaoFornecimento());
		umHashCodeBuilder.append(this.getPercAcrescimoItem());
		umHashCodeBuilder.append(this.getPercDescontoItem());
		umHashCodeBuilder.append(this.getPercIpi());
		umHashCodeBuilder.append(this.getQtdeRecebida());
		umHashCodeBuilder.append(this.getNumeroItemLicitacao());
		umHashCodeBuilder.append(this.getNumeroAutorizacaoFornecimento());
		umHashCodeBuilder.append(this.getCodigoMarca());
		umHashCodeBuilder.append(this.getCodigoMarcaNome());
		umHashCodeBuilder.append(this.getCodigoMaterialServico());
		umHashCodeBuilder.append(this.getDescricaoMaterialServico());
		umHashCodeBuilder.append(this.getRecebimento());
		umHashCodeBuilder.append(this.getMarcaNome());
		umHashCodeBuilder.append(this.getUnidadeMedidaItemAutorizacaoFornecimento());
		umHashCodeBuilder.append(this.getUnidadeMedidaMaterialServico());
		umHashCodeBuilder.append(this.getUnidadeMedidaSolicitacaoCompras());
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
		if (!(obj instanceof VSceItemNotaRecebimentoAutorizacaoFornecimentoId)) {
			return false;
		}
		VSceItemNotaRecebimentoAutorizacaoFornecimentoId other = (VSceItemNotaRecebimentoAutorizacaoFornecimentoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getFatorConversao(), other.getFatorConversao());
		umEqualsBuilder.append(this.getValorEfetivado(), other.getValorEfetivado());
		umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
		umEqualsBuilder.append(this.getTipo(), other.getTipo());
		umEqualsBuilder.append(this.getNome(), other.getNome());
		umEqualsBuilder.append(this.getNumero(), other.getNumero());
		umEqualsBuilder.append(this.getPercAcrescimo(), other.getPercAcrescimo());
		umEqualsBuilder.append(this.getPercDesconto(), other.getPercDesconto());
		umEqualsBuilder.append(this.getQtdeSolicitada(), other.getQtdeSolicitada());
		umEqualsBuilder.append(this.getValorUnitarioItemAutorizacaoFornecimento(), other.getValorUnitarioItemAutorizacaoFornecimento());
		umEqualsBuilder.append(this.getPercAcrescimoItem(), other.getPercAcrescimoItem());
		umEqualsBuilder.append(this.getPercDescontoItem(), other.getPercDescontoItem());
		umEqualsBuilder.append(this.getPercIpi(), other.getPercIpi());
		umEqualsBuilder.append(this.getQtdeRecebida(), other.getQtdeRecebida());
		umEqualsBuilder.append(this.getNumeroItemLicitacao(), other.getNumeroItemLicitacao());
		umEqualsBuilder.append(this.getNumeroAutorizacaoFornecimento(), other.getNumeroAutorizacaoFornecimento());
		umEqualsBuilder.append(this.getCodigoMarca(), other.getCodigoMarca());
		umEqualsBuilder.append(this.getCodigoMarcaNome(), other.getCodigoMarcaNome());
		umEqualsBuilder.append(this.getCodigoMaterialServico(), other.getCodigoMaterialServico());
		umEqualsBuilder.append(this.getDescricaoMaterialServico(), other.getDescricaoMaterialServico());
		umEqualsBuilder.append(this.getRecebimento(), other.getRecebimento());
		umEqualsBuilder.append(this.getMarcaNome(), other.getMarcaNome());
		umEqualsBuilder.append(this.getUnidadeMedidaItemAutorizacaoFornecimento(), other.getUnidadeMedidaItemAutorizacaoFornecimento());
		umEqualsBuilder.append(this.getUnidadeMedidaMaterialServico(), other.getUnidadeMedidaMaterialServico());
		umEqualsBuilder.append(this.getUnidadeMedidaSolicitacaoCompras(), other.getUnidadeMedidaSolicitacaoCompras());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
