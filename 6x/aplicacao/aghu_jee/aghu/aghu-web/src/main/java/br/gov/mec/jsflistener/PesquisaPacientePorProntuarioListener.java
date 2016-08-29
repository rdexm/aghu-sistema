package br.gov.mec.jsflistener;

import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PesquisaPacientePorProntuarioListener implements ValueChangeListener {

	private static final String PARAMETRO_MBEAN = "mbean";

	private static final String PARAMETRO_PACIENTE = "paciente";

	private static final String PARAMETRO_CODIGO_PACIENTE = "codigoPaciente";

	private static final String PARAMETRO_PRONTUARIO_PACIENTE = "prontuarioPaciente";
	
	private static final String PARAMETRO_ON_UPDATE_ACTION = "onUpdateAction";
	
	private IPacienteFacade getPacienteFacade() {
		return ServiceLocator.getBean(IPacienteFacade.class, "aghu-paciente");		
	}

	@Override
	public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
		HtmlInputText inputText = (HtmlInputText) event.getSource();
		Integer newValue = (Integer) event.getNewValue();

		UIComponent component = event.getComponent();

		Object controller = (Object) component.getAttributes().get(PARAMETRO_MBEAN);
		String parametroCodigo = (String) component.getAttributes().get(PARAMETRO_CODIGO_PACIENTE);
		String parametroProntuario = (String) component.getAttributes().get(PARAMETRO_PRONTUARIO_PACIENTE);
		String parametroPaciente = (String) component.getAttributes().get(PARAMETRO_PACIENTE);
		String onUpdateAction = (String) component.getAttributes().get(PARAMETRO_ON_UPDATE_ACTION);
		
		AipPacientes _paciente = null;
		try {
			if (newValue != null) {
				_paciente = this.getPacienteFacade().obterPacientePorProntuario(newValue);

				if (_paciente != null) {
					// seta o paciente no mbean
					PropertyUtils.setProperty(controller, parametroCodigo, _paciente.getCodigo());
					PropertyUtils.setProperty(controller, parametroProntuario, _paciente.getProntuario());
					PropertyUtils.setProperty(controller, parametroPaciente, _paciente);
				} else {
					PropertyUtils.setProperty(controller, parametroCodigo, null);
					PropertyUtils.setProperty(controller, parametroProntuario, null);
					PropertyUtils.setProperty(controller, parametroPaciente, null);

					inputText.setValue(null);
					
					//TODO
					String mensagem = WebUtil.initLocalizedMessage("MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO",null, formataProntuario(newValue));
					FacesContext.getCurrentInstance().addMessage("Messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem));
					//StatusMessages.instance().addToControlFromResourceBundle(component.getId(), StatusMessage.Severity.ERROR,
					//		"MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO", formataProntuario(newValue));
				}
			} else {
				PropertyUtils.setProperty(controller, parametroCodigo, null);
				PropertyUtils.setProperty(controller, parametroProntuario, null);
				PropertyUtils.setProperty(controller, parametroPaciente, null);
			}

			if (StringUtils.isNotBlank(onUpdateAction)) {
				Method method = controller.getClass().getMethod(onUpdateAction);
				method.invoke(controller);
			}
		} catch (Exception e) {
			if (e.getCause() != null && e.getCause() instanceof AuthorizationException) {
				//TODO  
				FacesMessage message = new FacesMessage(WebUtil.initLocalizedMessage("NAO_POSSUI_DIREITO_ACESSO",null));
				throw new ValidatorException(message);
				//FacesMessage message = new FacesMessage(SeamResourceBundle.getBundle().getString("NAO_POSSUI_DIREITO_ACESSO"));
				//throw new ValidatorException(message);
			} else {
				FacesMessage message = new FacesMessage("Erro na validação");
				throw new ValidatorException(message, e);
			}
		}
	}

	private String formataProntuario(Integer newValue) {
		String aux = newValue.toString();
		int length = aux.length();
		if (length > 1) {
			return aux.substring(0, length - 1) + "/" + aux.charAt(length - 1);
		} else {
			return aux;
		}
	}
	
}
