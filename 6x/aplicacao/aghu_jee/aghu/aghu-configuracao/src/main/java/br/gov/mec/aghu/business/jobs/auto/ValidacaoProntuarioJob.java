package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ValidacaoProntuarioJob extends AghuJob {

	private IPacienteFacade pacienteFacade = ServiceLocator.getBean(IPacienteFacade.class, "aghu-paciente");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		pacienteFacade.validaProntuarioScheduler(Calendar.getInstance().getTime(), getCron(jobExecutionContext));
	}
}
