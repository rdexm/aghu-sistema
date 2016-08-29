package br.gov.mec.aghu.core.converter;

import java.util.Map;
import java.util.WeakHashMap;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("dominioMapConverter")
public class DominioMapConverter implements Converter {

	private static Map<String, Class<?>> mapConverter = new WeakHashMap<>();

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return null;
    	}
        if (value instanceof Enum) {
            //component.getAttributes().put(ATTRIBUTE_ENUM_TYPE, value.getClass());
        	String cvalue = getKey(value);
            mapConverter.put(cvalue, value.getClass());
            return cvalue;
        } else {
            throw new ConverterException(new FacesMessage("Value is not an enum: " + value.getClass() + "[" + value + "]"));
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null  || value.isEmpty() || !value.contains("!")){
    		return null;
    	}  
        Class<Enum> enumType = (Class<Enum>) mapConverter.get(value);
        try {
            return Enum.valueOf(enumType, value.split("!")[1]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }  
    
    private String getKey(Object value){
    	return value.getClass().getSimpleName().concat("!").concat(((Enum<?>) value).name());
    }    

}