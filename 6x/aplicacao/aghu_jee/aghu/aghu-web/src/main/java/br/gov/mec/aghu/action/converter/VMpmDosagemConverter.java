package br.gov.mec.aghu.action.converter;

import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@FacesConverter(value = "vMpmDosagemConverter")
public class VMpmDosagemConverter implements Converter {
    private static final WeakHashMap<String, VMpmDosagem> mapEntities = new WeakHashMap<>();	

    @Override
    public Object  getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null || value.isEmpty()){
    		return null;
    	}
    	VMpmDosagem entity = mapEntities.get(value);    	
        return entity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return "";
    	}
    	synchronized (mapEntities) {
    		VMpmDosagem entity = (VMpmDosagem) value;
	    	mapEntities.put(getKey(entity), entity);        
	        return getKey(entity);
    	}
    }    
    
    private String getKey(BaseEntity entity){
    	return ((VMpmDosagem) entity).getSiglaUnidadeMedidaMedica();
    }  
}