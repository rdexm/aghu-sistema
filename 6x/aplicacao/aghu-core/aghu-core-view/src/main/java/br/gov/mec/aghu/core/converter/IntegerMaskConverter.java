package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;


@FacesConverter("integerMaskConverter")
public class IntegerMaskConverter extends RemoveMaskConverter {
	
	private static final long serialVersionUID = -236321778762419925L;

	@Override
	public Object getAsObject(String value) {
		try {
			String str = (String)super.getAsObject(value);
			
			return Integer.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}
	
}
