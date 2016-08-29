package br.gov.mec.aghu.business.scheduler.auto;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.DadosSumarioPrescricaoMedicaJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class DadosSumarioPrescricaoMedicaScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return DadosSumarioPrescricaoMedicaJob.class;
	}

	public Trigger agendarGerar(String cron, String triggerName, RapServidores servidorAgendador) throws ApplicationBusinessException {
		return agendar(null, cron, triggerName, servidorAgendador);
	}

}
