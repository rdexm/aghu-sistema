package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;


@FacesConverter("removeMaskConverter")
public class RemoveMaskConverter extends AbstractConverter {
	

	private static final long serialVersionUID = -7396833918145394084L;

	@Override
	public Object getAsObject(String value) {
		return value.replace(".", "").replace("-", "").replace("_", "").replace("/", "").replace("\\", "");
	}
}
