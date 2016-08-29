package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;


@FacesConverter("lowercaseCaptalizeConverter")
public class LowercaseCaptalizeConverter extends LongMaskConverter{

	/**
	 * Coloca no formato Aaaaaaaaaaa aaaa aaaaaa
	 */
	private static final long serialVersionUID = 2307790738626468875L;

	@Override
	public String getAsString(Object valor){
		return StringUtils.capitalize(valor.toString().toLowerCase());
	}
}
