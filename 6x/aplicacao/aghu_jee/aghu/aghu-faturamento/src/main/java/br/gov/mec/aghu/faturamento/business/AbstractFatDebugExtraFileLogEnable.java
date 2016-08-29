package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;

import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;

/**
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
public abstract class AbstractFatDebugExtraFileLogEnable extends AbstractFatDebugLogEnableRN {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6517938213530444018L;
	protected static final int MAGIC_INT_TIMEOUT_TRANSACTION_EQ_1DIA = 60 * 60 * 24;
//	private static final String MAGIC_STRING_ORG_JBOSS_SEAM_TRANSACTION_TRANSACTION = "org.jboss.seam.transaction.transaction";
		
	@SuppressWarnings("PMD.AtributoEmSeamContextManager")
	private final GeracaoArquivoLog fileLogger;
	
	protected String logFile(FaturamentoDebugCode code, Object... params) throws IOException {
		
		String result = null;
		
		result = this.logDebug(code, params);
		this.fileLogger.log(result);
		
		return result;
	}

//	protected static UserTransaction obterTransacao() {
//	
//		UserTransaction result = null;
//		
//		result = (UserTransaction) org.jboss.seam.Component.getInstance(MAGIC_STRING_ORG_JBOSS_SEAM_TRANSACTION_TRANSACTION);
//		
//		return result;
//	}

	protected void atualizarTxTimeout()
			throws IOException {

//		UserTransaction userTx = null;
//		EntityManager em = null;
//
//		userTx = obterTransacao();
//		if (userTx == null) {
//			this.logFile(FaturamentoDebugCode.ARQ_ERRO_TX_NAO_ENCONTRADA, MAGIC_STRING_ORG_JBOSS_SEAM_TRANSACTION_TRANSACTION);			
//		} else {
//			try {
//				userTx.setTransactionTimeout(MAGIC_INT_TIMEOUT_TRANSACTION_EQ_1DIA); // um dia
//				if (!userTx.isActive()) {
//					userTx.begin();					
//				}
//				em = (EntityManager) Component.getInstance("entityManager", true);
//				if (em != null) {
//					em.joinTransaction();					
//				}
//			} catch (Exception e) {
//				this.logFile(FaturamentoDebugCode.ARQ_ERRO_AJUSTE_TX_TIMEOUT, e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
//			}			
//		}
	}

	protected AbstractFatDebugExtraFileLogEnable() {

		this(new GeracaoArquivoLog());
	}

	protected AbstractFatDebugExtraFileLogEnable(GeracaoArquivoLog logger) {

		super();
		
		if (logger == null) {
			throw new IllegalArgumentException("Parametro logger nao informado!!!");
		}
		this.fileLogger = logger;
	}
	
	
	public GeracaoArquivoLog getFileLogger() {

		return this.fileLogger;
	}
}
