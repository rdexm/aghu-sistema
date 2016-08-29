package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;


@FacesConverter("longMaskConverter")
public class LongMaskConverter extends RemoveMaskConverter {
	
	private static final long serialVersionUID = 4349730119131029305L;

	@Override
	public Object getAsObject(String value) {
		try {
			String str = (String)super.getAsObject(value);
			
			return Long.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}
	
}
