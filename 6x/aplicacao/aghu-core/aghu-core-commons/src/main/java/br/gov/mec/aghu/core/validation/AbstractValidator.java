package br.gov.mec.aghu.core.validation;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author murillo.marinho
 *
 */
@SuppressWarnings({"PMD.UncommentedEmptyMethod"})
public abstract class AbstractValidator <A extends Annotation> implements ConstraintValidator<A, Object> {

	public void initialize(A Annotation) {
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) {
		
		try {
			if (isEmpty(value)) {
				return true;
			}
			
			return validate(value.toString());
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}

		if (value.toString().trim().length() == 0) {
			return true;
		}

		return false;
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

	protected abstract boolean validate(Object value);
	
}
