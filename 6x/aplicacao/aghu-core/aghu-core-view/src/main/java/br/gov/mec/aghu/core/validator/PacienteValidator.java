package br.gov.mec.aghu.core.validator;

import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;

/**
 * Validator da suggestion box.
 * 
 * @author cqsilva - v8.1
 * 
 */

@FacesValidator("pacienteValidator")
public class PacienteValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		ActionController mbean = (ActionController) component.getAttributes()
				.get("mbean");
		Boolean singleMethod = Boolean.valueOf((String) component
				.getAttributes().get("singleMethod"));
		String property = (String) component.getAttributes().get("property");
		String type = (String) component.getAttributes().get("type");
		String metodo1 = (String) component.getAttributes().get("method1");
		String metodo2 = (String) component.getAttributes().get("method2");
		String metodo3 = (String) component.getAttributes().get("method3");
		Object obj = mbean.getProperty(property);
		Object result = null;
		Method methodPesquisa = null;

		if (value == null || obj != null) {
			return;
		}

		try {
			if (singleMethod) {
				if (metodo3 == null) {
					methodPesquisa = mbean.getClass().getMethod(metodo1,
							Object.class, Object.class);
				} else {
					methodPesquisa = mbean.getClass().getMethod(metodo1,
							Object.class, Object.class, Object.class);
				}
				if (type.equalsIgnoreCase("PRONTUARIO")) {
					result = methodPesquisa.invoke(mbean, new Object[] { value,
							null });
				} else if (type.equalsIgnoreCase("LEITO")) {
					result = methodPesquisa.invoke(mbean, new Object[] { null,
							value });
				} else {
					result = methodPesquisa.invoke(mbean, new Object[] { null,
							null, value });
				}
			} else {
				if (type.equalsIgnoreCase("PRONTUARIO")) {
					methodPesquisa = mbean.getClass().getMethod(metodo1,
							Object.class);
				} else if (type.equalsIgnoreCase("LEITO")) {
					methodPesquisa = mbean.getClass().getMethod(metodo2,
							Object.class);
				} else {
					methodPesquisa = mbean.getClass().getMethod(metodo3,
							Object.class);
				}
				result = methodPesquisa.invoke(mbean, new Object[] { value });
			}
		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof AuthorizationException) {
				// FacesMessage message = new
				// FacesMessage(super.getResourceBundleValue("NAO_POSSUI_DIREITO_ACESSO"));
				FacesMessage message = new FacesMessage(
						"NAO_POSSUI_DIREITO_ACESSO");
				throw new ValidatorException(message);

			} else {
				FacesMessage message = new FacesMessage("Erro na validação");
				throw new ValidatorException(message);
			}
		}
		if (result == null) {
			String msg = "Nenhum valor foi encontrado na pesquisa com o valor "
					+ value + ".";
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
					msg, msg);
			throw new ValidatorException(message);
		} else {
			try {
				mbean.setProperty(property, result);
			} catch (Exception e) {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_WARN, e.getMessage(),
						e.getMessage());
				throw new ValidatorException(message);

			}
		}
	}
}