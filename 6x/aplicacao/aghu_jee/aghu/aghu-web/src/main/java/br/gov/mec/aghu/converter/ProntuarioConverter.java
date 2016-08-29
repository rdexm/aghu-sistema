package br.gov.mec.aghu.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "prontuarioConverter")
public class ProntuarioConverter implements Converter {
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null  || value.isEmpty()){
    		return null;
    	}
    	return value.trim().replace("/", "");
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value!=null){
			return value.toString();
		}else{
			return null;
		}
	} 	
}