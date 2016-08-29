package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.IndicadoresHospitalaresJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class IndicadoresHospitalaresScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return IndicadoresHospitalaresJob.class;
	}

	public Trigger gerar(Date time, String cron, String triggerName,
			RapServidores servidorAgendador) throws ApplicationBusinessException {
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
