package br.gov.mec.aghu.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;


@FacesConverter("truncateConverter")
public class TruncateConverter extends AbstractConverter {
	
	private static final long serialVersionUID = -7396833918145394584L;

	@Override
	public String getAsString(FacesContext ctx, UIComponent ui, Object valor) {
		if (valor==null){
			return null;
		}
		String strValor = String.valueOf(valor);
		if (ui.getAttributes().get("limitDescr")!=null && strValor!=null){
			Long limit=0l;
			if (ui.getAttributes().get("limitDescr") instanceof String){
				limit = Long.valueOf((String)ui.getAttributes().get("limitDescr"));
			}else{
				limit = (Long) ui.getAttributes().get("limitDescr");
			}	
			if (limit>0 && strValor.length()>limit){
				strValor =  strValor.substring(0, limit.intValue())+"...";
			}	
		}		
		return strValor;		
	}

	@Override
	public Object getAsObject(String valor) {
		return valor;
	}
}