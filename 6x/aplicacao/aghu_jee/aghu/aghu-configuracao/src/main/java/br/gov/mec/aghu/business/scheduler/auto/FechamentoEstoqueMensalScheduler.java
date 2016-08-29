package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.FechamentoEstoqueMensalJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class FechamentoEstoqueMensalScheduler extends AghuScheduler {
	
	public Trigger gerarFechamentoEstoqueMensal(Date expiration, String cron, String nomeProcessoQuartz, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return FechamentoEstoqueMensalJob.class;
	}
	
}
