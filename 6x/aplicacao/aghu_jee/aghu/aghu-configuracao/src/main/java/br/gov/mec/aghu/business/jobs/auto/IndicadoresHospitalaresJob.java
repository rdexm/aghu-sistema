package br.gov.mec.aghu.business.jobs.auto;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class IndicadoresHospitalaresJob extends AghuJob {
	
	private IIndicadoresFacade indicadoresFacade = ServiceLocator.getBean(IIndicadoresFacade.class, "aghu-indicadores");

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String cron = getCron(jobExecutionContext);
		indicadoresFacade.gerarIndicadoresHospitalares(null, cron);
	}
}
