package br.gov.mec.aghu.action.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.gov.mec.aghu.model.AghCid;

@FacesConverter(value = "AghCidConverter")
public class AghCidConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext,
			UIComponent uiComponent, String value) {
		if (value != null && !value.isEmpty()) {
			return (AghCid) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext,
			UIComponent uiComponent, Object value) {
		if (value instanceof AghCid) {
			AghCid entity = (AghCid) value;
			if (entity != null && entity instanceof AghCid
					&& entity.getSeq() != null) {
				uiComponent.getAttributes().put(entity.getSeq().toString(),
						entity);
				return entity.getSeq().toString();
			}
		}
		return "";
	}
}







