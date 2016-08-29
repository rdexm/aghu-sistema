package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * #989 - Calcular Nutrição Parenteral Total: VO Tipo Lipídio
 * 
 * @author aghu
 *
 */
public enum DominioTipoLipidio implements DominioString {

	/**
	 * Lipídio 10%
	 */
	LIPIDIO_10_PORCENTO("10"),
	/**
	 * Lipídio 20%
	 */
	LIPIDIO_20_PORCENTO("20");

	private String value;

	private DominioTipoLipidio(String value) {
		this.value = value;
	}

	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LIPIDIO_10_PORCENTO:
			return "10%";
		case LIPIDIO_20_PORCENTO:
			return "20%";
		default:
			return null;
		}
	}
}
