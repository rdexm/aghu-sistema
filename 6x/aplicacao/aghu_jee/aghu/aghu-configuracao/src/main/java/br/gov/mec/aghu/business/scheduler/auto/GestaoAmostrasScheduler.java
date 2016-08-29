package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.GestaoAmostrasJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class GestaoAmostrasScheduler extends AghuScheduler {
	
	
	public Trigger agendarGestaoAmostra(Date expiration, String cron, final String nomeProcessoQuartz, final String descricao,
			final Boolean executar, String nomeMicrocomputador, RapServidores servidorLogado) throws ApplicationBusinessException  {
		
		getParametros().put(GestaoAmostrasJob.EXECUTAR, executar);
		getParametros().put(GestaoAmostrasJob.DESCRICAO, descricao);
		getParametros().put(GestaoAmostrasJob.NOME_MICROCOMPUTADOR, nomeMicrocomputador);
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return GestaoAmostrasJob.class;
	}


}
