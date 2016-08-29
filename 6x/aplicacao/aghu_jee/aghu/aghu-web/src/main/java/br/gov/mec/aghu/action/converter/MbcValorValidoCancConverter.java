package br.gov.mec.aghu.action.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.model.MbcValorValidoCanc;

@FacesConverter(value = "mbcValorValidoCancConverter")
public class  MbcValorValidoCancConverter implements Converter {
	
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value != null && !value.isEmpty()) {
                return (MbcValorValidoCanc) uiComponent.getAttributes().get(value);
            }
            return null;
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
            if (value instanceof MbcValorValidoCanc) {
            	MbcValorValidoCanc entity= (MbcValorValidoCanc) value;
                if (entity != null && entity instanceof MbcValorValidoCanc && entity.getId() != null) {
                    uiComponent.getAttributes().put( entity.getId().toString(), entity);
                    return entity.getId().toString();
                }
            }
            return "";
        }
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

