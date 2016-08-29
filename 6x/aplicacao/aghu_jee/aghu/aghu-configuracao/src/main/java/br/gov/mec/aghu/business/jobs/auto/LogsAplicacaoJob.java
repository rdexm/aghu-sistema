package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class LogsAplicacaoJob extends AghuJob {

	private IAghuFacade aghuFacade = ServiceLocator.getBean(IAghuFacade.class, "aghu-configuracao");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job,
			JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		
		String cron = getCron(jobExecutionContext);
		this.aghuFacade.removerLogsAplicacao(Calendar.getInstance().getTime(), cron);
	}
}
