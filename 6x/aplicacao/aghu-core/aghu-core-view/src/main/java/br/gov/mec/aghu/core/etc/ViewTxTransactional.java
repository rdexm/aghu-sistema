package br.gov.mec.aghu.core.etc;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Interceptor
@ViewTransacional
public class ViewTxTransactional implements Serializable {
	
	private static final long serialVersionUID = -3986722379421262566L;
	
	private static final Log LOG = LogFactory.getLog(ViewTxTransactional.class);
	
	public enum ViewTxTransactionalExceptionCode implements BusinessExceptionCode {
		ERROR_VIEW_BEGIN_COMMIT
		;
	}
	
	
	@Inject 
	private UserTransaction usertx;
	    
    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws ApplicationBusinessException {
    	
    	boolean newTransaction=false;
    	Object result=null;
    	try {
	    	if (usertx.getStatus() != Status.STATUS_ACTIVE) {
	    		usertx.begin();
	    		newTransaction=true;
	    	}
	        result = context.proceed();
	        if (newTransaction){
	        	usertx.commit();
	        }
    	}catch (Exception se){
    		LOG.error(se.getMessage(), se);
    		throw new ApplicationBusinessException(ViewTxTransactionalExceptionCode.ERROR_VIEW_BEGIN_COMMIT);
    	}
        return result;
    }
}