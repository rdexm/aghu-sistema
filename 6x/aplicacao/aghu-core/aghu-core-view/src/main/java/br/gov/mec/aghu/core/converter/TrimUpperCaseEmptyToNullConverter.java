package br.gov.mec.aghu.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

/**
 * Converter para remover espaços e, se string vazia, retorna null, senão
 * retorna toUpperCase() da mesma.
 * 
 * @author lcmoura
 * 
 */
@FacesConverter("trimUpperCaseEmptyToNullConverter")
public class TrimUpperCaseEmptyToNullConverter extends EmptyStringToNullConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4939990033136671330L;

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		String s = (valor == null) ? valor : valor.trim().toUpperCase();
		return super.getAsObject(ctx, ui, s);
	}

}
