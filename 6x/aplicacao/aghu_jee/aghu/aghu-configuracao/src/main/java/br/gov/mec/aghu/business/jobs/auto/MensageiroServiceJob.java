package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.mensageiro.IMensageiroFacade;
import br.gov.mec.aghu.model.AghJobDetail;

public class MensageiroServiceJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(MensageiroServiceJob.class);
	
	private IMensageiroFacade mensageiroFacade = ServiceLocator.getBean(IMensageiroFacade.class, "aghu-configuracao");

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		LOG.info("Executando processo de negocio - mensageiro - enviarWhatsappDeInternacoesExcedentes.");
		mensageiroFacade.enviarWhatsappDeInternacoesExcedentes(job);
	}

}
