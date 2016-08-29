package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.core.commons.CoreUtil;

@FacesConverter("cepConverter")
public class CEPConverter extends IntegerMaskConverter {
	
	private static final long serialVersionUID = 6111692605737000053L;

	/**
	 * Formata com a máscara  99999-999
	 */
	@Override
	public String getAsString(Object valor) {
		return CoreUtil.formataCEP(valor);
	}
}
