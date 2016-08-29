package br.gov.mec.aghu.action.converter;

import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.model.AelSitItemSolicitacoes;

@FacesConverter(value = "aelSitItemSolicitacoesSolicExConverter")
public class AelSitItemSolicitacoesSolicExConverter implements Converter {
	private static final WeakHashMap<String, AelSitItemSolicitacoes> mapEntities = new WeakHashMap<>();
	

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value==null || value.isEmpty()){
    		return null;
    	}
    	AelSitItemSolicitacoes entity = mapEntities.get(value);    	
        return entity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value==null || value.toString().isEmpty()){
    		return "";
    	}
    	synchronized (mapEntities) {
    		AelSitItemSolicitacoes entity = (AelSitItemSolicitacoes) value;
	    	mapEntities.put(entity.getCodigo(), entity);        
	        return entity.getCodigo();
    	}
    }
}