package br.gov.mec.aghu.core.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("dominioConverter")
public class DominioConverter implements Converter {

	private static final String ATTRIBUTE_ENUM_TYPE = "DominioConverter.enumType";

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return null;
    	}
        if (value instanceof Enum) {
            component.getAttributes().put(ATTRIBUTE_ENUM_TYPE, value.getClass());
            return ((Enum<?>) value).name();
        } else {
            throw new ConverterException(new FacesMessage("Value is not an enum: " + value.getClass() + "[" + value + "]"));
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null  || value.isEmpty()){
    		return null;
    	}  
        Class<Enum> enumType = (Class<Enum>) component.getAttributes().get(ATTRIBUTE_ENUM_TYPE);
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }  

}