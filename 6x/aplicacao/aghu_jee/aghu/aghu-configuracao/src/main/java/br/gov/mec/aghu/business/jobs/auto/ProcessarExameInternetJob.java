package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ProcessarExameInternetJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(ProcessarExameInternetJob.class);
	
	private ISolicitacaoExameFacade solicitacaoExameFacade = ServiceLocator.getBean(ISolicitacaoExameFacade.class, "aghu-exames");
	

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		LOG.info("Executando processo de negocio - solicitacaoExame - processarExameInternet.");
		solicitacaoExameFacade.processarExameInternet(job);
		
	}

	

}
