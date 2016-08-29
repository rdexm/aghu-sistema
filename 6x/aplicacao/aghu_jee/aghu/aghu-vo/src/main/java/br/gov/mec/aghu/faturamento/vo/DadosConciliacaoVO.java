package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class DadosConciliacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2330860926020398938L;

	private String codENomeMat;
	
	private String codENomeMatTrunc;

	private String compativel;

	private String autorizado;

	private Integer qtdSolicitada;
	
	private Integer qtdConsumida;

	private BigDecimal valorUnitario;
	
	private BigDecimal valorTotalSolicitado;
	
	private String justificativa;
	
	private String justificativaBoolean;

	public DadosConciliacaoVO() {
	}

	public DadosConciliacaoVO(String codENomeMat, String compativel, String autorizado,
			Integer qtdSolicitada, Integer qtdConsumida, BigDecimal valorUnitario, BigDecimal valorTotalSolicitado, String justificativa) {
		this.codENomeMat = codENomeMat;
		this.compativel = compativel;
		this.autorizado = autorizado;
		this.qtdSolicitada = qtdSolicitada;
		this.qtdConsumida = qtdConsumida;
		this.valorUnitario = valorUnitario;
		this.valorTotalSolicitado = valorTotalSolicitado;
		this.justificativa = justificativa;
	}

	public String getCodENomeMat() {
		return codENomeMat;
	}

	public void setCodENomeMat(String codENomeMat) {
		this.codENomeMat = codENomeMat;
	}

	public String getCompativel() {
		return compativel;
	}

	public void setCompativel(String compativel) {
		this.compativel = compativel;
	}

	public String getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(String autorizado) {
		this.autorizado = autorizado;
	}

	public Integer getQtdSolicitada() {
		return qtdSolicitada;
	}

	public void setQtdSolicitada(Integer qtdSolicitada) {
		this.qtdSolicitada = qtdSolicitada;
	}

	public Integer getQtdConsumida() {
		return qtdConsumida;
	}

	public void setQtdConsumida(Integer qtdConsumida) {
		this.qtdConsumida = qtdConsumida;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorTotalSolicitado() {
		return valorTotalSolicitado;
	}

	public void setValorTotalSolicitado(BigDecimal valorTotalSolicitado) {
		this.valorTotalSolicitado = valorTotalSolicitado;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getJustificativaBoolean() {
		return justificativaBoolean;
	}

	public void setJustificativaBoolean(String justificativaBoolean) {
		this.justificativaBoolean = justificativaBoolean;
	}

	public String getCodENomeMatTrunc() {
		return codENomeMatTrunc;
	}

	public void setCodENomeMatTrunc(String codENomeMatTrunc) {
		this.codENomeMatTrunc = codENomeMatTrunc;
	}

	
	
}
