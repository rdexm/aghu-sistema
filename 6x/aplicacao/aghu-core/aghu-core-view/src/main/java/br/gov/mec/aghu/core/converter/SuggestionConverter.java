package br.gov.mec.aghu.core.converter;

import java.util.List;

import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.autocomplete.AutoComplete;

@FacesConverter(value = "suggestionConverter")
public class SuggestionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null || value.isEmpty()){
    		return null;
    	}
    	Object entity = component.getAttributes().get(value);
    	if (entity==null){
    		AutoComplete ac = (AutoComplete) component;
			MethodExpression method = (MethodExpression) ac.getAttributes().get("completeMethod");
			List result= (List) method.invoke(context.getELContext(), new String [] {value});
			
			if (result!=null){
				String label = (String) ac.getLabel();
				if (result.isEmpty()) {
	
					String msg= label + ": Nenhum item foi encontrado na pesquisa com o valor '" + value + "'.";
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,msg);
					throw new ConverterException(message);
					
				} else if (result.size() > 1) {
					String msg=label + ": Mais de um item foi encontrado na pesquisa com o valor '" + value+"'.";			
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,msg);
					
					throw new ConverterException(message);
				}else{
					entity = result.get(0);
				}
			}
    	}    	
        return entity;
    }
    

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return "";
    	}
    	String id=getKey(value);
    	component.getAttributes().put(id, value);
        return id;
    }
    
    private String getKey(Object entity){
    	return entity.getClass().getSimpleName()+entity.hashCode();
    }
}
