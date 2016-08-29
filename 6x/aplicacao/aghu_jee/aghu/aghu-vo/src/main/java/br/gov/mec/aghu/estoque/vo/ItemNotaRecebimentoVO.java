package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

import javax.persistence.Transient;

import br.gov.mec.aghu.dominio.DominioTipoItemNotaRecebimento;

public class ItemNotaRecebimentoVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3811639968698077207L;
	private Integer codigoItemAutorizacaoFornecimento;
	private DominioTipoItemNotaRecebimento tipo;
	private String nomeMaterialServico;
	private Integer codigoMarca;
	private String nomeMarcaComercial;
	private Integer quantidade;
	private String codigoUnidadeMedida;
	private String descricaoUnidadeMedida;
	private Double valor;
	private Double percDescontoItem;
	private Double percDesconto;
	private Double percAcrescimoItem;
	private Double percAcrescimo;
	private Double percIpi;
	private Double valorUnitarioItemAutorizacaoFornecimento;
	private Integer quantidadeRecebida;
	
	public enum Fields {
		CODIGO_ITEM_AUTORIZACAO_FORN("codigoItemAutorizacaoFornecimento"),
		TIPO("tipo"),
		NOME_MATERIAL_SERVICO("nomeMaterialServico"),
		NOME_MARCA_COMERCIAL("nomeMarcaComercial"),
		QUANTIDADE("quantidade"),
		CODIGO_UNIDADE_MEDIDA("codigoUnidadeMedida"),
		VALOR("valor"),
		PERCENTUAL_DESCONTO("percDesconto"),
		PERCENTUAL_DESCONTO_ITEM("percDescontoItem"),
		PERCENTUAL_ACRESCIMO("percAcrescimo"),
		PERCENTUAL_ACRESCIMO_ITEM("percAcrescimoItem"),
		PERCENTUAL_IPI("percIpi"),
		VALOR_UNITARIO_ITEM_AUTORIZACAO_FORN("valorUnitarioItemAutorizacaoFornecimento"),
		QUANTIDADE_RECEBIDA("quantidadeRecebida"), DESCRICAO_UNIDADE_MEDIDA("descricaoUnidadeMedida"),
		CODIGO_MARCA("codigoMarca");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}
	
	public Integer getCodigoItemAutorizacaoFornecimento() {
		return codigoItemAutorizacaoFornecimento;
	}
	public void setCodigoItemAutorizacaoFornecimento(
			Integer codigoItemAutorizacaoFornecimento) {
		this.codigoItemAutorizacaoFornecimento = codigoItemAutorizacaoFornecimento;
	}
	public DominioTipoItemNotaRecebimento getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoItemNotaRecebimento tipo) {
		this.tipo = tipo;
	}
	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}
	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}
	public String getNomeMarcaComercial() {
		return nomeMarcaComercial;
	}
	public void setNomeMarcaComercial(String nomeMarcaComercial) {
		this.nomeMarcaComercial = nomeMarcaComercial;
	}
	public Double getValorUnitarioItemAutorizacaoFornecimento() {
		return valorUnitarioItemAutorizacaoFornecimento;
	}
	public void setValorUnitarioItemAutorizacaoFornecimento(
			Double valorUnitarioItemAutorizacaoFornecimento) {
		this.valorUnitarioItemAutorizacaoFornecimento = valorUnitarioItemAutorizacaoFornecimento;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Double getPercDescontoItem() {
		return percDescontoItem;
	}
	public void setPercDescontoItem(Double percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}
	public Double getPercDesconto() {
		return percDesconto;
	}
	public void setPercDesconto(Double percDesconto) {
		this.percDesconto = percDesconto;
	}
	public Double getPercAcrescimoItem() {
		return percAcrescimoItem;
	}
	public void setPercAcrescimoItem(Double percAcrecimoItem) {
		this.percAcrescimoItem = percAcrecimoItem;
	}
	public Double getPercAcrescimo() {
		return percAcrescimo;
	}
	public void setPercAcrescimo(Double percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}
	public Double getPercIpi() {
		return percIpi;
	}
	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}
	
	public Integer getQuantidadeRecebida() {
		return quantidadeRecebida;
	}
	public void setQuantidadeRecebida(Integer quantidadeRecebida) {
		this.quantidadeRecebida = quantidadeRecebida;
	}

	/**
	 *     
	 * @ORADB: Function SCEK_INR_RN.INRC_VER_VAL_LIQ_IAF
	 */
	@Transient
	public Double getValorUnitarioLiquido() {
		Double valorBruto, 
			   valorLiquido, 
			   valorDescontoItem = 0.0,
			   valorDesconto, 
			   valorAcrescimoItem, 
			   valorAcrescimo, 
			   valorIpi, 
			   custoUnitLiq = 0.0;
		
		if (getValorUnitarioItemAutorizacaoFornecimento() != null && getQuantidadeRecebida() != null){
			valorBruto = (getQuantidadeRecebida() * getValorUnitarioItemAutorizacaoFornecimento());
			valorLiquido = valorBruto;
			if(getPercDescontoItem() != null && getPercDescontoItem() > 0){
				valorDescontoItem = (valorBruto*(valorDescontoItem / 100));
				valorLiquido = (valorLiquido - valorDescontoItem);
			}
			if (getPercDesconto() != null && getPercDesconto() > 0){
				valorDesconto = (valorBruto*(getPercDesconto() / 100));
				valorLiquido = (valorLiquido - valorDesconto);
			}
			if (getPercAcrescimoItem() != null && getPercAcrescimoItem() > 0){
				valorAcrescimoItem = (valorBruto*(getPercAcrescimoItem() / 100));
				valorLiquido = (valorLiquido + valorAcrescimoItem);
			}
			if (getPercAcrescimo() != null && getPercAcrescimo() > 0){
				valorAcrescimo = (valorBruto*(getPercAcrescimo() / 100));
				valorLiquido = (valorLiquido + valorAcrescimo);
			}
			if (getPercIpi() != null && getPercIpi() > 0){
				valorIpi = (valorLiquido * (getPercIpi() / 100));
				valorLiquido = (valorLiquido + valorIpi);
			}
			if (getQuantidadeRecebida() == 0){
				setQuantidadeRecebida(1);
			}
			custoUnitLiq =(valorLiquido / getQuantidadeRecebida());
		}
		return custoUnitLiq;
	}

	
	@Transient
	public Double getValorUnitarioLiquidoSemAF() {
		
		return (this.valor / this.quantidade);
		
	}
	
	public String getDescricaoUnidadeMedida() {
		return descricaoUnidadeMedida;
	}
	public void setDescricaoUnidadeMedida(String descricaoUnidadeMedida) {
		this.descricaoUnidadeMedida = descricaoUnidadeMedida;
	}
	public String getCodigoDescricaoUnidadeMedida() {
		return getCodigoUnidadeMedida().concat(" - ").concat(getDescricaoUnidadeMedida());
	}
	public Integer getCodigoMarca() {
		return codigoMarca;
	}
	public void setCodigoMarca(Integer codigoMarca) {
		this.codigoMarca = codigoMarca;
	}
}
