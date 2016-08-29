package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.ValidacaoProntuarioJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ValidacaoProntuarioScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return ValidacaoProntuarioJob.class;
	}

	public Trigger validar(Date time, String cron, String triggerName, RapServidores servidorAgendador) throws ApplicationBusinessException {
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
