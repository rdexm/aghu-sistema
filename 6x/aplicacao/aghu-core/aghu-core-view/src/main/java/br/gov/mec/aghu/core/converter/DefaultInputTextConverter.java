package br.gov.mec.aghu.core.converter;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

/**
 * Converter para remover espaços e caraters especiais e, se string vazia,
 * retorna null, senão retorna toUpperCase() da mesma.
 * 
 * @author gmneto
 * 
 */
@FacesConverter("defaultInputTextConverter")
public class DefaultInputTextConverter implements Serializable, Converter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6683155769361076171L;
	
	private static final String[] CARACTERS_ESPECIAIS = { "/", "\\", "#",
			",", "*", "$", "<", ">", "@", "!", "?", "~", "^", "[", "]", "{",
			"}" };


	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {

		if (valor != null) {

			Boolean emptyStringToNull = Boolean.valueOf(ui.getAttributes().get("emptyStringToNull").toString());
			Boolean removerEspacosBrancos = Boolean.valueOf(ui.getAttributes().get("removerEspacosBrancos").toString());
			Boolean removerCaratersEspeciais = Boolean.valueOf(ui.getAttributes().get("removerCaratersEspeciais").toString());
			Boolean caixaAlta = Boolean.valueOf(ui.getAttributes().get("caixaAlta").toString());

			if (removerCaratersEspeciais) {
				for (String caracterEspecial : CARACTERS_ESPECIAIS) {
					if (valor.contains(caracterEspecial)){
						String errorMessage = "Não é permitido o caracter especial '"+caracterEspecial+"' neste campo."; 
						 throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage,errorMessage));
			        }
					//Mudou a validação: Cristiano. | valor = valor.replace(caracterEspecial, "");
				}
			}
			if (caixaAlta) {
				valor = valor.toUpperCase();
			}
			if (removerEspacosBrancos) {
				valor = valor.trim();
			}
			if (!emptyStringToNull) {
				valor = StringUtils.isBlank(valor) ? "" : valor;
			}
		}
		return valor;
	}


	
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}

}
