package br.gov.mec.aghu.core.business;

import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
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


@TransactionManagement(TransactionManagementType.BEAN)
public abstract class BaseBMTBusiness extends BaseLightBusiness {

	private static final Log LOG = LogFactory.getLog(BaseBMTBusiness.class);
	
	private enum BaseBMTBusinessExceptionCode implements BusinessExceptionCode {
		ERRO_TRANSACAO_BEGIN, ERRO_TRANSACAO_ROLLBACK, ERRO_TRANSACAO_COMMIT
	}
	
	@Resource
	protected UserTransaction userTransaction;
	
	
	protected void beginTransaction(){
		try {
			userTransaction.begin();
		} catch (NotSupportedException | SystemException e) {
			this.tratarExcecao(e, BaseBMTBusinessExceptionCode.ERRO_TRANSACAO_BEGIN);
		}
	}
	
	protected void beginTransaction(int timeout){
		try {
			userTransaction.setTransactionTimeout(timeout);
			userTransaction.begin();
		} catch (NotSupportedException | SystemException e) {
			this.tratarExcecao(e, BaseBMTBusinessExceptionCode.ERRO_TRANSACAO_BEGIN);
		}		
	}
	
	protected void commitTransaction(){
		try {
			userTransaction.commit();
		} catch (SecurityException | IllegalStateException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException
				| SystemException e) {
			this.rollbackTransaction();
			this.tratarExcecao(e, BaseBMTBusinessExceptionCode.ERRO_TRANSACAO_COMMIT);
		}
	}
	
	protected void rollbackTransaction(){
		try {
			userTransaction.rollback();
		} catch (IllegalStateException | SecurityException | SystemException e) {
			this.tratarExcecao(e, BaseBMTBusinessExceptionCode.ERRO_TRANSACAO_ROLLBACK);
		}
	}
	
	private void tratarExcecao(Exception e, BaseBMTBusinessExceptionCode code){
		LOG.error(e.getMessage(),e);
		throw new BaseRuntimeException(code,e);
	}
	
	
	

}
