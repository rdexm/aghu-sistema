package br.gov.mec.aghu.business.jobs.auto;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

public class DadosSumarioPrescricaoMedicaJob extends AghuJob {

	IPrescricaoMedicaFacade prescricaoMedicaFacade = ServiceLocator.getBean(IPrescricaoMedicaFacade.class, "aghu-prescricaomedica");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String cron = this.getCron(jobExecutionContext);
		RapServidores servidor = this.getServidorAgendador(jobExecutionContext);
		String nomeProcesso = this.getNomeProcessoQuartz(jobExecutionContext);
		
		this.prescricaoMedicaFacade.agendarGerarDadosSumarioPrescricaoMedica(cron, null, null, servidor, nomeProcesso);
	}
}
