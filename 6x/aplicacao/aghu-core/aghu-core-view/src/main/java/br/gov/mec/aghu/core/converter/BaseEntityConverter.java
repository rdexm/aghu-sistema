package br.gov.mec.aghu.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "baseEntityConverter")
public class BaseEntityConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null || value.isEmpty()){
    		return null;
    	}
    	Object entity = component.getAttributes().get(value);
        return entity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return "";
    	}
    	component.getAttributes().put(getKey(value), value);
        return getKey(value);
    }
    
    private String getKey(Object entity){
    	return entity.getClass().getSimpleName()+entity.hashCode();
    }
}