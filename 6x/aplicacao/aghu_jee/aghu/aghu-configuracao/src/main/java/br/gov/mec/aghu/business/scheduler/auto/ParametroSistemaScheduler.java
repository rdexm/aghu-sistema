package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.ParametroSistemaJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ParametroSistemaScheduler extends AghuScheduler {
	
	public Trigger agendar(Date expiration, String cron, final String nomeProcessoQuartz,
			final String descricao, final Boolean executar, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		getParametros().put(ParametroSistemaJob.EXECUTAR, executar);
		getParametros().put(ParametroSistemaJob.DESCRICAO, descricao);
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return ParametroSistemaJob.class;
	}

}
