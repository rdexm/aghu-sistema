package br.gov.mec.aghu.core.commons;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.exception.Severity;

/**
 * Responsavel por metodos utilitarios da camada de View.
 * 
 * @author rcorvalao
 *
 */
public class WebUtil {
	
	public static final String RESOURCE_BUNDLE = "messages";
	
	
	public static String initLocalizedMessage(String codeBundle, String bundleFileName, Object... params) {
		if (codeBundle == null || "".equals(codeBundle.trim())) {
			throw new IllegalArgumentException("Core: bundle code is mandatory.");
		}
		try {
			String resourceBundle = WebUtil.RESOURCE_BUNDLE;
			if (StringUtils.isNotBlank(bundleFileName)) {
				resourceBundle = bundleFileName;
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			ResourceBundle bundle = context.getApplication().getResourceBundle(context, resourceBundle);
			
			String msg = bundle.getString(codeBundle);

			// Se for null ou vazio, mostra a propria chave
			if (msg == null || msg.length() == 0) {
				msg = codeBundle;
			}

			// Faz a interpolacao de parametros na mensagem
			msg = MessageFormat.format(msg, params);
			//msg = java.text.MessageFormat.format(msg, params);

			return msg;
		} catch (Exception e) {
			// Se não houver messagem correspondente em algum message bundle do servidor
			// Neste caso lança a string do exception code mesmo.
			return codeBundle;
		}
		
	}
	
	public static javax.faces.application.FacesMessage.Severity getSeverity (Severity aghuSeverity) {
		javax.faces.application.FacesMessage.Severity returnValue = javax.faces.application.FacesMessage.SEVERITY_INFO;
		if (Severity.INFO == aghuSeverity) {
			returnValue = javax.faces.application.FacesMessage.SEVERITY_INFO;
		} else if (Severity.WARN == aghuSeverity) {
			returnValue = javax.faces.application.FacesMessage.SEVERITY_WARN;
		} else if (Severity.ERROR == aghuSeverity) {
			returnValue = javax.faces.application.FacesMessage.SEVERITY_ERROR;
		} else if (Severity.FATAL == aghuSeverity) {
			returnValue = javax.faces.application.FacesMessage.SEVERITY_FATAL;
		}
		return returnValue;
	}

	public static Cookie getCookie(String name) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		Cookie cookie = null;
		
		Cookie[] userCookies = request.getCookies();
		if (userCookies != null && userCookies.length > 0 ) {
		    for (int i = 0; i < userCookies.length; i++) {
		        if (userCookies[i].getName().equals(name)) {
		            cookie = userCookies[i];
		            return cookie;
		        }
		    }
		}
		return null;
	}
	
	public static void setCookie(String name, String value, int expiry) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		Cookie cookie = getCookie(name);
		
		if (cookie != null) {
		    cookie.setValue(value);
		} else {
		    cookie = new Cookie(name, value);
		    cookie.setPath(request.getContextPath());
		}
		
		cookie.setMaxAge(expiry);
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.addCookie(cookie);
	}
}