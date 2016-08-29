package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class DadosSumarioPrescricaoEnfermagemJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(DadosSumarioPrescricaoEnfermagemJob.class);
	
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade = ServiceLocator.getBean(IPrescricaoEnfermagemFacade.class, "aghu-prescricaoenfermagem");
	
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		LOG.info("Executando processo de negocio " + nomeProcessoQuartz);
		
		String cron = getCron(jobExecutionContext);
		prescricaoEnfermagemFacade.agendarGerarDadosSumarioPrescricaoEnfermagem(cron, null, null);
		
	}

}
