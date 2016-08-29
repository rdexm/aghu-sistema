package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.EncerramentoAutomaticoContaHospitalarJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class EncerramentoAutomaticoContaHospitalarScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return EncerramentoAutomaticoContaHospitalarJob.class;
	}

	public Trigger executar(Date time, String cron, String triggerName, RapServidores servidorAgendador, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		getParametros().put(EncerramentoAutomaticoContaHospitalarJob.NOME_MICRO_COMPUTADOR, nomeMicrocomputador);
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
