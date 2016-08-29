package br.gov.mec.aghu.business.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.JobParameterUtils;
import br.gov.mec.aghu.core.business.scheduler.AppScheduler;
import br.gov.mec.aghu.core.business.scheduler.AppSchedulingConstants;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.RapServidores;

/**
 * Classe base para a criacao de agendamentos
 *  
 *
 */
public abstract class AghuScheduler {
	
	@Inject
	private AppScheduler aghuScheduler;
	
	private Map<String, Object> parametros;
	
	
	protected Trigger agendar(Date expiration, String cron, final String nomeProcessoQuartz, RapServidores servidorLogado) throws ApplicationBusinessException {
		getParametros().put(
				AppSchedulingConstants.SERVIDORAGENDADOR
				, JobParameterUtils.servidorIdToString(servidorLogado)
		);
		
		return aghuScheduler.agendarJob(expiration, cron, nomeProcessoQuartz, getClasseJob(), getParametros());
	}
	
	
	
	/**
	 * Responsabilidade da sub-class informar o Job referente ao agendamento.
	 * 
	 * @return
	 */
	protected abstract Class<? extends AghuJob> getClasseJob();



	protected Map<String, Object> getParametros() {
		if (this.parametros == null) {
			this.parametros = new HashMap<>();
		}
		return this.parametros;
		
	}
	
	
}
