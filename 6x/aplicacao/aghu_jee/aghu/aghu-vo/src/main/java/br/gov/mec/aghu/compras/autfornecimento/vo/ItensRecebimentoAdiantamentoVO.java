package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItensRecebimentoAdiantamentoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204946066804440306L;

	private Short itlNumero;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String descricaoSolicitacao;
	private String unidade;
	private Integer saldoAF;
	private Double valorUnitario;
	private Float percVarPreco;
	private Integer afnNumero;
	private Integer numero;
	private Integer saldoQtd;
	private Integer eslPendenteAF;
	private Integer qtdEntregue;
	private Integer qtdEntregueAnterior;
	private BigDecimal valorTotal;
	private BigDecimal valorUnitarioConvertido;
	
	
	public ItensRecebimentoAdiantamentoVO() {
		super();
	}
	
	public enum Fields {
		ITL_NUMERO("itlNumero"),
		COD_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		DESCRICAO_SOLICITACAO("descricaoSolicitacao"),
		UNIDADE("unidade"),
		SALDO_AF("saldoAF"),
		VALOR_UNITARIO("valorUnitario"),
		PERC_VAR_PRECO("percVarPreco"),
		AFN_NUMERO("afnNumero"),
		NUMERO("numero"),
		SALDO_QTD("saldoQtd"),
		ESL_PENDENTE_AF("eslPendenteAF"),
		QTD_ENTREGUE("qtdEntregue"),
		QTD_ENTREGUE_ANTERIOR("qtdEntregueAnterior"),
		VALOR_TOTAL("valorTotal"),
		VLR_UNITARIO_CONVERTIDO("valorUnitarioConvertido");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Short getItlNumero() {
		return itlNumero;
	}
	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}
	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Integer getSaldoAF() {
		return saldoAF;
	}
	public void setSaldoAF(Integer saldoAF) {
		this.saldoAF = saldoAF;
	}
	public Double getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public Float getPercVarPreco() {
		return percVarPreco;
	}
	public void setPercVarPreco(Float percVarPreco) {
		this.percVarPreco = percVarPreco;
	}
	public Integer getAfnNumero() {
		return afnNumero;
	}
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Integer getSaldoQtd() {
		return saldoQtd;
	}
	public void setSaldoQtd(Integer saldoQtd) {
		this.saldoQtd = saldoQtd;
	}
	public Integer getEslPendenteAF() {
		return eslPendenteAF;
	}
	public void setEslPendenteAF(Integer eslPendenteAF) {
		this.eslPendenteAF = eslPendenteAF;
	}
	public Integer getQtdEntregue() {
		return qtdEntregue;
	}
	public void setQtdEntregue(Integer qtdEntregue) {
		this.qtdEntregue = qtdEntregue;
	}
	public Integer getQtdEntregueAnterior() {
		return qtdEntregueAnterior;
	}
	public void setQtdEntregueAnterior(Integer qtdEntregueAnterior) {
		this.qtdEntregueAnterior = qtdEntregueAnterior;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public BigDecimal getValorUnitarioConvertido() {
		return valorUnitarioConvertido;
	}
	public void setValorUnitarioConvertido(BigDecimal valorUnitarioConvertido) {
		this.valorUnitarioConvertido = valorUnitarioConvertido;
	}
}
