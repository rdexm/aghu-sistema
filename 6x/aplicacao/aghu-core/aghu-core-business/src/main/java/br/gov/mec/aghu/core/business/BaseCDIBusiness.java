package br.gov.mec.aghu.core.business;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public abstract class BaseCDIBusiness extends BaseLightBusiness {

	private static final Log LOG = LogFactory.getLog(BaseCDIBusiness.class);
	
	private enum BaseCDIBusinessExceptionCode implements BusinessExceptionCode {
		ERRO_TRANSACAO_BEGIN, ERRO_TRANSACAO_ROLLBACK, ERRO_TRANSACAO_COMMIT
	}
	
	@Inject 
	private UserTransaction userTransaction;	
	
	
	public void beginTransaction(){
		try {
			userTransaction.begin();
		} catch (NotSupportedException | SystemException e) {
			this.tratarExcecao(e, BaseCDIBusinessExceptionCode.ERRO_TRANSACAO_BEGIN);
		}
	}
	
	public void beginTransaction(int timeout){
		try {
			userTransaction.setTransactionTimeout(timeout);
			userTransaction.begin();
		} catch (NotSupportedException | SystemException e) {
			this.tratarExcecao(e, BaseCDIBusinessExceptionCode.ERRO_TRANSACAO_BEGIN);
		}		
	}
	
	public void commitTransaction(){
		try {
			userTransaction.commit();
		} catch (SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException
				| SystemException e) {
			this.rollbackTransaction();
			this.tratarExcecao(e, BaseCDIBusinessExceptionCode.ERRO_TRANSACAO_COMMIT);
		}
	}
	
	public void rollbackTransaction(){
		try {
			userTransaction.rollback();
		} catch (IllegalStateException | SecurityException | SystemException e) {
			this.tratarExcecao(e, BaseCDIBusinessExceptionCode.ERRO_TRANSACAO_ROLLBACK);
		}
	}
	
	private void tratarExcecao(Exception e, BaseCDIBusinessExceptionCode code){
		LOG.error(e.getMessage(),e);
		throw new BaseRuntimeException(code,e);
	}

}