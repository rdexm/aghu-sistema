package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.RequisicaoMaterialAutomaticaJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class RequisicaoMaterialAutomaticaScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return RequisicaoMaterialAutomaticaJob.class;
	}

	public Trigger efetivar(Date time, String cron, String triggerName, RapServidores servidorAgendador, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		getParametros().put(RequisicaoMaterialAutomaticaJob.NOME_MICRO_COMPUTADOR, nomeMicrocomputador);
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
