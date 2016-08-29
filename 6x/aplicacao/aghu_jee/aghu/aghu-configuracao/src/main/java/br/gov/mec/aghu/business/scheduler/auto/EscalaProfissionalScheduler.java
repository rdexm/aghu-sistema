package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.EscalaProfissionalJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class EscalaProfissionalScheduler extends AghuScheduler {

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return EscalaProfissionalJob.class;
	}

	public Trigger verificar(Date time, String cron, String triggerName,
			RapServidores servidorAgendador, String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws ApplicationBusinessException {
		
		getParametros().put(EscalaProfissionalJob.NOME_MICROCOMPUTADOR, nomeMicrocomputador);
		getParametros().put(EscalaProfissionalJob.DATA_FIM_VINCULO_SERVIDOR, dataFimVinculoServidor);
		
		return agendar(time, cron, triggerName, servidorAgendador);
	}

}
