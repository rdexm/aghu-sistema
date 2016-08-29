package br.gov.mec.aghu.action.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.model.AelValorValidoQuestao;

@FacesConverter(value = "aelValorValidoQuestaoConverter")
public class AelValorValidoQuestaoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext,
			UIComponent uiComponent, String value) {
		if (value != null && !value.isEmpty()) {
			return (AelValorValidoQuestao) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext,
			UIComponent uiComponent, Object value) {
		if (value instanceof AelValorValidoQuestao) {
			AelValorValidoQuestao entity = (AelValorValidoQuestao) value;
			if (entity != null && entity instanceof AelValorValidoQuestao
					&& entity.getId() != null) {
				uiComponent.getAttributes().put(entity.getId().toString(),
						entity);
				return entity.getId().toString();
			}
		}
		return "";
	}
}
