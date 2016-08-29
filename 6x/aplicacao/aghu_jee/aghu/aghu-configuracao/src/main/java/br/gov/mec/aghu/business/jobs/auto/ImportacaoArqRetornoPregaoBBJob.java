package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ImportacaoArqRetornoPregaoBBJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(ImportacaoArqRetornoPregaoBBJob.class);

	private IComprasFacade comprasFacade = ServiceLocator.getBean(IComprasFacade.class, "aghu-compras");
	private ISchedulerFacade schedulerFacade = ServiceLocator.getBean(ISchedulerFacade.class, "aghu-configuracao");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		LOG.info("Executando processo de negocio - pregaoBB - importarArquivoRetorno.");
		try {
			comprasFacade.importarArqPregaoBBRetorno(job);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage());
			schedulerFacade.adicionarLog(job, e.getMessage());
		}
	}

}
