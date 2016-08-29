package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.PerfisAcessoSistemaComPendenciasBloqueantesJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PerfisAcessoSistemaComPendenciasBloqueantesScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return PerfisAcessoSistemaComPendenciasBloqueantesJob.class;
	}

	public Trigger remover(Date time, String cron, String triggerName, RapServidores servidorAgendador) throws ApplicationBusinessException {
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
