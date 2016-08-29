package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class EnvioEmailPacienteGMRJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(EnvioEmailPacienteGMRJob.class);

	private IPacienteFacade pacienteFacade = ServiceLocator.getBean(IPacienteFacade.class, "aghu-paciente");
	private ISchedulerFacade schedulerFacade = ServiceLocator.getBean(ISchedulerFacade.class, "aghu-configuracao");

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {		
		LOG.info("Executando processo de notificação por e-mail de consultas do ambulatório de pacientes portadores de GMR.");
		try {
			pacienteFacade.enviarEmailPacienteGMR(job);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage());
			schedulerFacade.adicionarLog(job, e.getMessage());
		}		
	}

}
