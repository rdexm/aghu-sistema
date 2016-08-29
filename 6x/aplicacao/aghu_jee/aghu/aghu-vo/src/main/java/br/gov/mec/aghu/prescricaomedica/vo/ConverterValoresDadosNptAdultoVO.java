package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

/**
 * #989 - Calcular Nutrição Parenteral Total
 * 
 * @author aghu
 *
 */
public class ConverterValoresDadosNptAdultoVO {

	private BigDecimal novoValor;
	private String novoValorEd;

	public BigDecimal getNovoValor() {
		return novoValor;
	}

	public void setNovoValor(BigDecimal novoValor) {
		this.novoValor = novoValor;
	}

	public String getNovoValorEd() {
		return novoValorEd;
	}

	public void setNovoValorEd(String novoValorEd) {
		this.novoValorEd = novoValorEd;
	}

}