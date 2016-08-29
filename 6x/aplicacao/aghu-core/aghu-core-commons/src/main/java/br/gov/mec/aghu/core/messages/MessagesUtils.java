package br.gov.mec.aghu.core.messages;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.gov.mec.aghu.core.exception.BaseException;


public class MessagesUtils implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7876547128968566214L;
	
	private ResourceBundle bundle;	
	

	public String getResourceBundleValue(String key) {
		if (bundle == null){
			inicializarBundle();
		}
		return this.bundle.getString(key);		
	}	
	
	

	public String getResourceBundleValue(BaseException exc) {
		if (bundle == null){
			inicializarBundle();
		}
		String message = exc.getMessage();
		
		String messagePattern = bundle.getString(exc.getCode().toString());
		if (exc.getParameters() != null && exc.getParameters().length > 0) {
			message = MessageFormat.format(messagePattern, exc.getParameters());
		} else {
			message = messagePattern;
		}
		
		return message;
	}
	
	public String getResourceBundleValue(String key, Object... params) {
		if (bundle == null){
			inicializarBundle();
		}
		String msg = bundle.getString(key);
		return MessageFormat.format(msg, params);
	}
	
	private void inicializarBundle() {
		bundle = ResourceBundle.getBundle("bundle.messages_business");		
	}
	
	


}
