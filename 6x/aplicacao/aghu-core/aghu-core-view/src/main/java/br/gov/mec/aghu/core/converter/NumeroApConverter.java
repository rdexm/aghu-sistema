package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.core.commons.NumberUtil;

@FacesConverter("numeroApConverter")
public class NumeroApConverter extends RemoveMaskConverter {

	private static final long serialVersionUID = -4807800641171097557L;

	/**
	 * Coloca no formato da mascara 999999/99
	 */
	@Override
	public String getAsString(Object valor) {
		return NumberUtil.formatarNumeroAP(valor.toString());
	}

	@Override
	public Object getAsObject(String value) {
		try {
			String str = (String) super.getAsObject(value);

			return Integer.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}
}