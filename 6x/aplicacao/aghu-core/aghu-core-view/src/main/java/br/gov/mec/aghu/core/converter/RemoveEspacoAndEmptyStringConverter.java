package br.gov.mec.aghu.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

/**
 * Converter para remover espaços e, se string vazia, retorna null, senão
 * retorna a mesma.
 */
@FacesConverter("removeEspacoAndEmptyStringToNullConverter")
public class RemoveEspacoAndEmptyStringConverter extends
		EmptyStringToNullConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -321953732263671742L;

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		String s = (valor == null) ? valor : valor.trim();
		return super.getAsObject(ctx, ui, s);
	}

}
