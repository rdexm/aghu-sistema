package br.gov.mec.aghu.action.converter;

import br.gov.mec.aghu.model.MbcTurnos;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.WeakHashMap;

/**
 * Created by renan_boni on 03/12/14.
 */
@FacesConverter(value = "mbcTurnosConverter")
public class MbcTurnosConverter implements Converter {
    private static final WeakHashMap<String, MbcTurnos> mapEntities = new WeakHashMap<>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value==null || value.isEmpty()){
            return null;
        }
        MbcTurnos entity = mapEntities.get(value);
        return entity;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value==null || value.toString().isEmpty()){
            return "";
        }
        synchronized (mapEntities) {
            MbcTurnos entity = (MbcTurnos) value;
            mapEntities.put(entity.getDescricao(), entity);
            return entity.getDescricao();
        }
    }
}
