package br.gov.mec.aghu.action.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "radioTipoLoteConverter")
public class RadioTipoLoteConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return (value != null && !"".equals(value.trim())) ? Integer.valueOf(value) : null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value != null ? value.toString() : null;
	}

}
