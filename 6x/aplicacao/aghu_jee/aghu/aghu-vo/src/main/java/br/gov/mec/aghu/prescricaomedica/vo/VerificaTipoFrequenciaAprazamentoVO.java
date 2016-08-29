package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class VerificaTipoFrequenciaAprazamentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6978828702663043945L;

	private Boolean digitaFrequencia;

	private Boolean retorno;

	public VerificaTipoFrequenciaAprazamentoVO() {
	}

	public VerificaTipoFrequenciaAprazamentoVO(Boolean digitaFrequencia,
			Boolean retorno) {
		this.digitaFrequencia = digitaFrequencia;
		this.retorno = retorno;
	}

	public Boolean getDigitaFrequencia() {
		return digitaFrequencia;
	}

	public void setDigitaFrequencia(Boolean digitaFrequencia) {
		this.digitaFrequencia = digitaFrequencia;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

}
