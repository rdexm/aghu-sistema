package br.gov.mec.aghu.core.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

@FacesValidator("numberValidator")
public class NumberValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value==null){
			return;
		}
		if (StringUtils.isNotEmpty(value.toString()) && !StringUtils.isNumeric(value.toString().replace(".", "").replace(",", ""))){
			FacesMessage msg = new FacesMessage("O campo deve receber um valor numérico válido.", "O campo deve receber um valor numérico válido.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}