package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.manual.ProcessarCustoDiarioAutomatizadoJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class CustoDiarioAutomatizadoScheduler extends AghuScheduler {
	
	
	public Trigger processarCustoDiarioAutomatizado(Date expiration, String cron, final String nomeProcessoQuartz, final String descricao, final Boolean executar, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		getParametros().put(ProcessarCustoDiarioAutomatizadoJob.EXECUTAR, executar);
		getParametros().put(ProcessarCustoDiarioAutomatizadoJob.DESCRICAO, descricao);
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return ProcessarCustoDiarioAutomatizadoJob.class;
	}

}
