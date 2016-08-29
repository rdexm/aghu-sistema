package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("snBooleanConverter")
public class SNBooleanConverter extends AbstractConverter {

	private static final long serialVersionUID = 2193073217409839534L;

	@Override
	public String getAsString(Object valor) {
		Boolean bool = (Boolean) valor;

		return bool ? "S" : "N";
	}

	@Override
	public Object getAsObject(String valor) {
		if (StringUtils.isBlank(valor)
				|| !StringUtils.equalsIgnoreCase("S", valor)) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

}