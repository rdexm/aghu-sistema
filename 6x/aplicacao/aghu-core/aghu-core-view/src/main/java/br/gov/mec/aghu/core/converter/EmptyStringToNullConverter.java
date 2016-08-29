package br.gov.mec.aghu.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("emptyStringToNullConverter")
public class EmptyStringToNullConverter implements Converter {

	@Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	return value.toString();
    }
    
	@Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return StringUtils.isBlank(value) ? null : value;    	
    }
}