package br.gov.mec.aghu.action.converter;

import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@FacesConverter(value = "mpmUnidadeMedidaMedicaConverter")
public class MpmUnidadeMedidaMedicaConverter implements Converter {
	
	private static final WeakHashMap<String, MpmUnidadeMedidaMedica> mapEntities = new WeakHashMap<>();
	

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
    	if (value==null || value.isEmpty()){
    		return null;
    	}
    	MpmUnidadeMedidaMedica entity = mapEntities.get(value);    	
        return entity;
		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value==null || value.toString().isEmpty()){
    		return "";
    	}
    	synchronized (mapEntities) {
    		MpmUnidadeMedidaMedica entity = (MpmUnidadeMedidaMedica) value;
	    	mapEntities.put(getKey(entity), entity);        
	        return getKey(entity);
    	}
    }    
    
    private String getKey(BaseEntity entity){
    	return ((MpmUnidadeMedidaMedica) entity).getDescricao();
    } 
}
