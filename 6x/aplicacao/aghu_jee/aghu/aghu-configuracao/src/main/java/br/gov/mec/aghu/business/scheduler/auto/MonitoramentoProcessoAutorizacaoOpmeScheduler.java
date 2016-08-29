package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.MonitoramentoProcessoAutorizacaoOpmeJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MonitoramentoProcessoAutorizacaoOpmeScheduler extends AghuScheduler {
	
	public Trigger processarAcionamentoProcessoAutorizacaoOPME(Date expiration, String cron, String jobName, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		return agendar(expiration, cron, jobName, servidorLogado);
	}
	
	
	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return MonitoramentoProcessoAutorizacaoOpmeJob.class;
	}

}
