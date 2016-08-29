package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ProcessamentoCustoBMTBusiness extends BaseBusiness {

	private static final long serialVersionUID = 3741920517718553668L;
	private static final int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24;
	private static final Log LOG = LogFactory.getLog(ProcessamentoCustoBMTBusiness.class);
	
	@Resource
	private UserTransaction userTransaction;
	
	/**
	 * Método que inicia uma nova transação que será controlada manualmente
	 */
	public void iniciarProcessamentoCusto() throws ApplicationBusinessException{
		try {
			this.getUserTransaction().setTransactionTimeout(TRANSACTION_TIMEOUT_24_HORAS);
			this.getUserTransaction().begin();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_INICIANDO_TRANSACAO_PROCESSAMENTO, e);
		}
	}
	
	/**
	 * Método que reinicia a transação do bean quando ela não estiver ativa
	 * @throws ApplicationBusinessException
	 */
	public void reiniciarProcessamentoQuandoNecessario() throws ApplicationBusinessException{
		try {
			if(this.getUserTransaction().getStatus() != Status.STATUS_ACTIVE){
				this.iniciarProcessamentoCusto();
			}
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_REINICIANDO_TRANSACAO_PROCESSAMENTO, e);
		}
	}
	
	/**
	 * Método que faz o commit da transação atual e cria uma nova transaçao para continuar o processamento
	 * @throws ApplicationBusinessException
	 */
	public void commitProcessamentoCusto() throws ApplicationBusinessException {
		this.commit(true);
	}

	/**
	 * Método que finaliza o processamento com um último commit e não inicia mais nenhuma transação
	 * @throws ApplicationBusinessException
	 */
	public void finalizarProcessamentoCusto() throws ApplicationBusinessException {
		this.commit(false);
	}
	
	/**
	 * Método responsável por fazer o commit e criar uma transação nova de acordo com o parâmetro informado
	 * @param reiniciarTransacao 
	 * @throws ApplicationBusinessException
	 */
	private void commit(boolean reiniciarTransacao) throws ApplicationBusinessException {
		
		try {
			LOG.debug("Iniciando commit");
			this.getUserTransaction().commit();
			LOG.debug("Realizou commit");
			if(reiniciarTransacao){
				this.iniciarProcessamentoCusto();
			}
		} catch (Exception e){
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_COMMIT_TRANSACAO_PROCESSAMENTO, e);
		}
	}
	
	protected UserTransaction getUserTransaction(){
		return this.userTransaction;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
}