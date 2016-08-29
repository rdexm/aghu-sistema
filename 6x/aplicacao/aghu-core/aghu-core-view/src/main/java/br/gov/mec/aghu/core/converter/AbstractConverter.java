package br.gov.mec.aghu.core.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractConverter implements Serializable, Converter {
	private static final long serialVersionUID = 9004313146386877399L;

	private static final String noSelectionValue = "org.jboss.seam.ui.NoSelectionConverter.noSelectionValue";
	
	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		if (StringUtils.isBlank(valor) || noSelectionValue.equals(valor)) {
			return null;
		}
		
		return getAsObject(valor);
	}

	public abstract Object getAsObject(String valor);

	public String getAsString(FacesContext ctx, UIComponent ui, Object valor) {
		
		try {
			return getAsString(valor);
		} catch (Exception e) {
			return "";
		}
		
	}

	public String getAsString(Object valor) {
			return valor.toString();
	}
	
	/**
	 * Método que retorna o logger.
	 * 
	 * @return
	 */
	private Log getLogger() {
		return LogFactory.getLog(this.getClass());
	}


	public final boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	
	public final boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	
	public final boolean isWarnEnabled() {
		return getLogger().isWarnEnabled();
	}

	
	public final void logDebug(Object arg0) {
		if (isDebugEnabled()) {
			getLogger().debug(arg0);
		}
	}

	
	public final void logInfo(Object arg0) {
		if (isInfoEnabled()) {
			getLogger().info(arg0);
		}
	}


	public final void logWarn(Object arg0, Throwable arg1) {
		if (isWarnEnabled()) {
			getLogger().warn(arg0, arg1);
		}
	}

	
	public final void logWarn(Object arg0) {
		if (isWarnEnabled()) {
			getLogger().warn(arg0);
		}
	}

	
	public final void logError(Object arg0, Throwable arg1) {
		getLogger().error(arg0, arg1);
	}


	public final void logError(Object arg0) {
		getLogger().error(arg0);
	}
}
