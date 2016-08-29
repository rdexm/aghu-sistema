package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class FechamentoEstoqueMensalJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(FechamentoEstoqueMensalJob.class);


	private IEstoqueFacade estoqueFacade = ServiceLocator.getBean(IEstoqueFacade.class, "aghu-estoque");


	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		LOG.info("Executando processo de negecio ...");
		
		try {
			estoqueFacade.gerarFechamentoEstoqueMensalManual();
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
		
	}

}
