package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PerfisAcessoSistemaComPendenciasBloqueantesJob extends AghuJob {

	private ICascaFacade cascaFacade = ServiceLocator.getBean(ICascaFacade.class, "aghu-casca");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		this.cascaFacade.removerPerfisAcessoSistemaComPendenciasBloqueantes(Calendar.getInstance().getTime(), getCron(jobExecutionContext));
	}
}
