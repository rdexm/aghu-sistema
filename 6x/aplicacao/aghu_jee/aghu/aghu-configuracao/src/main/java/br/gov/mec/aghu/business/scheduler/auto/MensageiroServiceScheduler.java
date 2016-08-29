package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.MensageiroServiceJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.RapServidores;


public class MensageiroServiceScheduler extends AghuScheduler {
	
	public Trigger enviarWhatsappDeInternacoesExcedentes(Date time, String cron, String triggerName,
			RapServidores servidorAgendador) throws ApplicationBusinessException {
		return agendar(time, cron, triggerName, servidorAgendador);
	}
	
	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return MensageiroServiceJob.class;
	}

}
