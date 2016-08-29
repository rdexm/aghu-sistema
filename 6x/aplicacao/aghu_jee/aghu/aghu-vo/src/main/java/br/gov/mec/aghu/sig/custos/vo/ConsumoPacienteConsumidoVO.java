package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ConsumoPacienteConsumidoVO implements Serializable {

	private static final long serialVersionUID = -1268630495367612421L;

	private Integer ccaSeq;
	private Integer ocvSeq;
	private Integer phiSeq;
	private Integer cctCodigo;
	private BigDecimal qtdeConsumida;
	private BigDecimal qtdeProduzida;
	private BigDecimal valorAtvInsumo = BigDecimal.ZERO;
	private BigDecimal valorAtvPessoal = BigDecimal.ZERO;
	private BigDecimal valorAtvEquipamento = BigDecimal.ZERO;
	private BigDecimal valorAtvServico = BigDecimal.ZERO;
	private BigDecimal valorRateioInsumo = BigDecimal.ZERO;
	private BigDecimal valorRateioPessoal = BigDecimal.ZERO;
	private BigDecimal valorRateioEquipamento = BigDecimal.ZERO;
	private BigDecimal valorRateioServico = BigDecimal.ZERO;
	private BigDecimal valorIndiretoInsumo = BigDecimal.ZERO;
	private BigDecimal valorIndiretoPessoal = BigDecimal.ZERO;
	private BigDecimal valorIndiretoEquipamento = BigDecimal.ZERO;
	private BigDecimal valorIndiretoServico = BigDecimal.ZERO;

	public ConsumoPacienteConsumidoVO() {
	}

	public Integer getCcaSeq() {
		return ccaSeq;
	}

	public void setCcaSeq(Integer ccaSeq) {
		this.ccaSeq = ccaSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public BigDecimal getQtdeConsumida() {
		return qtdeConsumida;
	}

	public void setQtdeConsumida(BigDecimal qtdeConsumida) {
		this.qtdeConsumida = qtdeConsumida;
	}

	public BigDecimal getQtdeProduzida() {
		return qtdeProduzida;
	}

	public void setQtdeProduzida(BigDecimal qtdeProduzida) {
		this.qtdeProduzida = qtdeProduzida;
	}

	public BigDecimal getValorAtvInsumo() {
		return valorAtvInsumo;
	}

	public void setValorAtvInsumo(BigDecimal valorAtvInsumo) {
		this.valorAtvInsumo = valorAtvInsumo;
	}

	public BigDecimal getValorAtvPessoal() {
		return valorAtvPessoal;
	}

	public void setValorAtvPessoal(BigDecimal valorAtvPessoal) {
		this.valorAtvPessoal = valorAtvPessoal;
	}

	public BigDecimal getValorAtvEquipamento() {
		return valorAtvEquipamento;
	}

	public void setValorAtvEquipamento(BigDecimal valorAtvEquipamento) {
		this.valorAtvEquipamento = valorAtvEquipamento;
	}

	public BigDecimal getValorAtvServico() {
		return valorAtvServico;
	}

	public void setValorAtvServico(BigDecimal valorAtvServico) {
		this.valorAtvServico = valorAtvServico;
	}

	public BigDecimal getValorRateioInsumo() {
		return valorRateioInsumo;
	}

	public void setValorRateioInsumo(BigDecimal valorRateioInsumo) {
		this.valorRateioInsumo = valorRateioInsumo;
	}

	public BigDecimal getValorRateioPessoal() {
		return valorRateioPessoal;
	}

	public void setValorRateioPessoal(BigDecimal valorRateioPessoal) {
		this.valorRateioPessoal = valorRateioPessoal;
	}

	public BigDecimal getValorRateioEquipamento() {
		return valorRateioEquipamento;
	}

	public void setValorRateioEquipamento(BigDecimal valorRateioEquipamento) {
		this.valorRateioEquipamento = valorRateioEquipamento;
	}

	public BigDecimal getValorRateioServico() {
		return valorRateioServico;
	}

	public void setValorRateioServico(BigDecimal valorRateioServico) {
		this.valorRateioServico = valorRateioServico;
	}

	public BigDecimal getValorIndiretoInsumo() {
		return valorIndiretoInsumo;
	}

	public void setValorIndiretoInsumo(BigDecimal valorIndiretoInsumo) {
		this.valorIndiretoInsumo = valorIndiretoInsumo;
	}

	public BigDecimal getValorIndiretoPessoal() {
		return valorIndiretoPessoal;
	}

	public void setValorIndiretoPessoal(BigDecimal valorIndiretoPessoal) {
		this.valorIndiretoPessoal = valorIndiretoPessoal;
	}

	public BigDecimal getValorIndiretoEquipamento() {
		return valorIndiretoEquipamento;
	}

	public void setValorIndiretoEquipamento(BigDecimal valorIndiretoEquipamento) {
		this.valorIndiretoEquipamento = valorIndiretoEquipamento;
	}

	public BigDecimal getValorIndiretoServico() {
		return valorIndiretoServico;
	}

	public void setValorIndiretoServico(BigDecimal valorIndiretoServico) {
		this.valorIndiretoServico = valorIndiretoServico;
	}

}
