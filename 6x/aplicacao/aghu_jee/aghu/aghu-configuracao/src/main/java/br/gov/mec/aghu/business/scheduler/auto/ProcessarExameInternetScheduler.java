package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.ProcessarExameInternetJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ProcessarExameInternetScheduler extends AghuScheduler {
	
	
	public Trigger processarExameInternet(Date expiration, String cron, final String nomeProcessoQuartz, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return ProcessarExameInternetJob.class;
	}

}
