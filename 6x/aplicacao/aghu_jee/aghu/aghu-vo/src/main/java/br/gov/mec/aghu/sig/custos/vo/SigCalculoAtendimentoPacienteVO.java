package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class SigCalculoAtendimentoPacienteVO {
	private Integer sigCalculoAtdConsumoSeq;
	private Integer sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
	private BigDecimal sigCalculoAtdConsumoQuantidade;
	private Integer sigCalculoAtdConsumoFccCentroCustoSeq;
	private Integer sigCalculoDetalheConsumoScoMaterialCodigo;
	private BigDecimal sigCalculoDetalheConsumoQuantidadePrevisto;
	private BigDecimal sigCalculoDetalheConsumoQuantidadeDebitado;
	private BigDecimal sigCalculoDetalheConsumoQuantidadeConsumo;
	private BigDecimal valorPrevisto;
	private BigDecimal valorDebitado;
	private BigDecimal valorConsumido;
	
	public SigCalculoAtendimentoPacienteVO(
			/*1*/Integer sigCalculoAtdConsumoSeq,
			/*2*/Integer sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq,
			/*3*/BigDecimal sigCalculoAtdConsumoQuantidade,
			/*4*/Integer sigCalculoAtdConsumoFccCentroCustoSeq,
			/*5*/Integer sigCalculoDetalheConsumoScoMaterialCodigo,
			/*6*/BigDecimal sigCalculoDetalheConsumoQuantidadePrevisto,
			/*7*/BigDecimal sigCalculoDetalheConsumoQuantidadeDebitado,
			/*8*/BigDecimal valorPrevisto, 
			/*9*/BigDecimal valorDebitado,
			/*10*/BigDecimal quantidadePrevisto, 
			/*11*/BigDecimal quantidadePrevistoTotal,
			/*12*/BigDecimal quantidadeDebitado, 
			/*13*/Integer sigCalculoDetalheConsumoSeq,
			/*14*/BigDecimal valorConsumido,
			/*15*/BigDecimal quantidadeConsumido){
		super();
		this.sigCalculoAtdConsumoSeq = sigCalculoAtdConsumoSeq;
		this.sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq = sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
		this.sigCalculoAtdConsumoQuantidade = sigCalculoAtdConsumoQuantidade;
		this.sigCalculoAtdConsumoFccCentroCustoSeq = sigCalculoAtdConsumoFccCentroCustoSeq;
		this.sigCalculoDetalheConsumoScoMaterialCodigo = sigCalculoDetalheConsumoScoMaterialCodigo;
		this.sigCalculoDetalheConsumoQuantidadePrevisto = sigCalculoDetalheConsumoQuantidadePrevisto;
		this.sigCalculoDetalheConsumoQuantidadeDebitado = sigCalculoDetalheConsumoQuantidadeDebitado;
		this.valorPrevisto = valorPrevisto;
		this.valorDebitado = valorDebitado;
		this.quantidadePrevisto = quantidadePrevisto;
		this.quantidadePrevistoTotal = quantidadePrevistoTotal;
		this.quantidadeDebitado = quantidadeDebitado;
		this.sigCalculoDetalheConsumoSeq = sigCalculoDetalheConsumoSeq;
		this.valorConsumido = valorConsumido;
		this.quantidadeConsumido = quantidadeConsumido;
	}

	private BigDecimal quantidadeConsumido;
	private BigDecimal quantidadePrevisto;
	private BigDecimal quantidadePrevistoTotal;
	private BigDecimal quantidadeDebitado;
	private Integer sigCalculoDetalheConsumoSeq;
	
	public SigCalculoAtendimentoPacienteVO(){}
	
	public SigCalculoAtendimentoPacienteVO(Integer sigCalculoAtdConsumoSeq, Integer sigCalculoDetalheConsumoScoMaterialCodigo, BigDecimal valorPrevisto, BigDecimal quantidadePrevisto){
		this.sigCalculoAtdConsumoSeq = sigCalculoAtdConsumoSeq;
		this.sigCalculoDetalheConsumoScoMaterialCodigo = sigCalculoDetalheConsumoScoMaterialCodigo;
		this.valorPrevisto = valorPrevisto;
		this.quantidadePrevisto = quantidadePrevisto;
	}
	
	public SigCalculoAtendimentoPacienteVO(Integer sigCalculoAtdConsumoSeq,
			Integer sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq,
			BigDecimal sigCalculoAtdConsumoQuantidade,
			Integer sigCalculoAtdConsumoFccCentroCustoSeq,
			Integer sigCalculoDetalheConsumoScoMaterialCodigo,
			BigDecimal sigCalculoDetalheConsumoQuantidadePrevisto,
			BigDecimal sigCalculoDetalheConsumoQuantidadeDebitado) {
		super();
		this.sigCalculoAtdConsumoSeq = sigCalculoAtdConsumoSeq;
		this.sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq = sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
		this.sigCalculoAtdConsumoQuantidade = sigCalculoAtdConsumoQuantidade;
		this.sigCalculoAtdConsumoFccCentroCustoSeq = sigCalculoAtdConsumoFccCentroCustoSeq;
		this.sigCalculoDetalheConsumoScoMaterialCodigo = sigCalculoDetalheConsumoScoMaterialCodigo;
		this.sigCalculoDetalheConsumoQuantidadePrevisto = sigCalculoDetalheConsumoQuantidadePrevisto;
		this.sigCalculoDetalheConsumoQuantidadeDebitado = sigCalculoDetalheConsumoQuantidadeDebitado;
	}
	
	public SigCalculoAtendimentoPacienteVO(Integer sigCalculoAtdConsumoSeq,
			Integer sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq,
			BigDecimal sigCalculoAtdConsumoQuantidade,
			Integer sigCalculoAtdConsumoFccCentroCustoSeq,
			Integer sigCalculoDetalheConsumoScoMaterialCodigo,
			BigDecimal sigCalculoDetalheConsumoQuantidadePrevisto,
			BigDecimal sigCalculoDetalheConsumoQuantidadeDebitado,
			BigDecimal valorPrevisto) {
		super();
		this.sigCalculoAtdConsumoSeq = sigCalculoAtdConsumoSeq;
		this.sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq = sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
		this.sigCalculoAtdConsumoQuantidade = sigCalculoAtdConsumoQuantidade;
		this.sigCalculoAtdConsumoFccCentroCustoSeq = sigCalculoAtdConsumoFccCentroCustoSeq;
		this.sigCalculoDetalheConsumoScoMaterialCodigo = sigCalculoDetalheConsumoScoMaterialCodigo;
		this.sigCalculoDetalheConsumoQuantidadePrevisto = sigCalculoDetalheConsumoQuantidadePrevisto;
		this.sigCalculoDetalheConsumoQuantidadeDebitado = sigCalculoDetalheConsumoQuantidadeDebitado;
		this.setValorPrevisto(valorPrevisto);
	}

	public Integer getSigCalculoAtdConsumoSeq() {
		return sigCalculoAtdConsumoSeq;
	}

	public void setSigCalculoAtdConsumoSeq(Integer sigCalculoAtdConsumoSeq) {
		this.sigCalculoAtdConsumoSeq = sigCalculoAtdConsumoSeq;
	}

	public Integer getSigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq() {
		return sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
	}

	public void setSigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq(Integer sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq) {
		this.sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq = sigCalculoAtdPacienteSigCalculoAtdPermanenciasSeq;
	}

	public BigDecimal getSigCalculoAtdConsumoQuantidade() {
		return sigCalculoAtdConsumoQuantidade;
	}

	public void setSigCalculoAtdConsumoQuantidade(BigDecimal sigCalculoAtdConsumoQuantidade) {
		this.sigCalculoAtdConsumoQuantidade = sigCalculoAtdConsumoQuantidade;
	}

	public Integer getSigCalculoAtdConsumoFccCentroCustoSeq() {
		return sigCalculoAtdConsumoFccCentroCustoSeq;
	}

	public void setSigCalculoAtdConsumoFccCentroCustoSeq(Integer sigCalculoAtdConsumoFccCentroCustoSeq) {
		this.sigCalculoAtdConsumoFccCentroCustoSeq = sigCalculoAtdConsumoFccCentroCustoSeq;
	}

	public Integer getSigCalculoDetalheConsumoScoMaterialCodigo() {
		return sigCalculoDetalheConsumoScoMaterialCodigo;
	}

	public void setSigCalculoDetalheConsumoScoMaterialCodigo(Integer sigCalculoDetalheConsumoScoMaterialCodigo) {
		this.sigCalculoDetalheConsumoScoMaterialCodigo = sigCalculoDetalheConsumoScoMaterialCodigo;
	}

	public BigDecimal getSigCalculoDetalheConsumoQuantidadePrevisto() {
		return sigCalculoDetalheConsumoQuantidadePrevisto;
	}

	public void setSigCalculoDetalheConsumoQuantidadePrevisto(BigDecimal sigCalculoDetalheConsumoQuantidadePrevisto) {
		this.sigCalculoDetalheConsumoQuantidadePrevisto = sigCalculoDetalheConsumoQuantidadePrevisto;
	}

	public BigDecimal getSigCalculoDetalheConsumoQuantidadeDebitado() {
		return sigCalculoDetalheConsumoQuantidadeDebitado;
	}

	public void setSigCalculoDetalheConsumoQuantidadeDebitado(BigDecimal sigCalculoDetalheConsumoQuantidadeDebitado) {
		this.sigCalculoDetalheConsumoQuantidadeDebitado = sigCalculoDetalheConsumoQuantidadeDebitado;
	}
	
	public void setValorPrevisto(BigDecimal valorPrevisto) {
		this.valorPrevisto = valorPrevisto;
	}

	public BigDecimal getValorPrevisto() {
		return valorPrevisto;
	}

	public void setQuantidadePrevisto(BigDecimal quantidadePrevisto) {
		this.quantidadePrevisto = quantidadePrevisto;
	}

	public BigDecimal getQuantidadePrevisto() {
		return quantidadePrevisto;
	}

	public void setSigCalculoDetalheConsumoSeq(Integer sigCalculoDetalheConsumoSeq) {
		this.sigCalculoDetalheConsumoSeq = sigCalculoDetalheConsumoSeq;
	}

	public Integer getSigCalculoDetalheConsumoSeq() {
		return sigCalculoDetalheConsumoSeq;
	}

	public void setValorDebitado(BigDecimal valorDebitado) {
		this.valorDebitado = valorDebitado;
	}

	public BigDecimal getValorDebitado() {
		return valorDebitado;
	}

	public void setQuantidadeDebitado(BigDecimal quantidadeDebitado) {
		this.quantidadeDebitado = quantidadeDebitado;
	}

	public BigDecimal getQuantidadeDebitado() {
		return quantidadeDebitado;
	}

	public void setQuantidadePrevistoTotal(BigDecimal quantidadePrevistoTotal) {
		this.quantidadePrevistoTotal = quantidadePrevistoTotal;
	}

	public BigDecimal getQuantidadePrevistoTotal() {
		return quantidadePrevistoTotal;
	}

	public void setValorConsumido(BigDecimal valorConsumido) {
		this.valorConsumido = valorConsumido;
	}

	public BigDecimal getValorConsumido() {
		return valorConsumido;
	}

	public void setQuantidadeConsumido(BigDecimal quantidadeConsumido) {
		this.quantidadeConsumido = quantidadeConsumido;
	}

	public BigDecimal getQuantidadeConsumido() {
		return quantidadeConsumido;
	}

	public BigDecimal getSigCalculoDetalheConsumoQuantidadeConsumo() {
		return sigCalculoDetalheConsumoQuantidadeConsumo;
	}

	public void setSigCalculoDetalheConsumoQuantidadeConsumo(
			BigDecimal sigCalculoDetalheConsumoQuantidadeConsumo) {
		this.sigCalculoDetalheConsumoQuantidadeConsumo = sigCalculoDetalheConsumoQuantidadeConsumo;
	}

	public enum Fields{
		SIG_CALCULO_ATD_CONSUMO_SEQ("sigCalculoAtdConsumoSeq"),
		SIG_CALCULO_ATD_CONSUMO_SIG_CALCULO_ATD_PERMANENCIAS_SEQ("sigCalculoAtdConsumoSigCalculoAtdPermanenciasSeq"),
		SIG_CALCULO_ATD_CONSUMO_QUANTIDADE("sigCalculoAtdConsumoQuantidade"),
		SIG_CALCULO_ATD_CONSUMO_FCC_CENTRO_CUSTRO_SEQ("SigCalculoAtdConsumoFccCentroCustoSeq"),
		SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ("sigCalculoDetalheConsumoScoMaterialCodigo"),
		SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_PREVISTO("sigCalculoDetalheConsumoQuantidadePrevisto"),
		SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_DEBITADO("sigCalculoDetalheConsumoQuantidadeDebitado"),
		SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_CONSUMO("sigCalculoDetalheConsumoQuantidadeConsumo"),
		VALOR_PREVISTO("valorPrevisto"),
		VALOR_DEBITADO("valorDebitado"),
		QUANTIDADE_PREVISTO("quantidadePrevisto"),
		QUANTIDADE_DEBITADO("quantidadeDebitado"),
		SIG_CALCULO_DETALHE_CONSUMO_SEQ("sigCalculoDetalheConsumoSeq"), QUANTIDADE_PREVISTO_TOTAL("quantidadePrevistoTotal"),
		VALOR_CONSUMIDO("valorConsumido"),
		QUANTIDADE_CONSUMIDO("quantidadeConsumido");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
