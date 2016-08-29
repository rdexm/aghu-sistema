package br.gov.mec.aghu.core.business.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Trigger;

import br.gov.mec.aghu.core.business.jobs.AppJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AppScheduler {
	
	private static final Log LOG = LogFactory.getLog(AppScheduler.class);
	
	
	public Trigger agendarJob(Date expiration
			, String cron, final String nomeProcessoQuartz, Class<? extends AppJob> classeJob
			, Map<String, Object> parametros
		) throws ApplicationBusinessException {
		
		LOG.info("Iniciando agendamento...");
		
		AppSchedulingBuilder builder = new AppSchedulingBuilder();
		
		LOG.info("Cron: " + cron);
		builder.withCron(cron);
		builder.withNomeProcessoQuartz(nomeProcessoQuartz);
		builder.withJobClass(classeJob);
		
		Map<String, Object> map = new HashMap<>();
		//Adicionar parametros genericos. caso necessario
		if (parametros != null && !parametros.isEmpty()) {
			map.putAll(parametros);
		}
		
		builder.withJobDataMap(map);
		
		Trigger trigger = builder.build();
		
		LOG.info("Agendamento concluído.");
		return trigger;
	}
	
	
	
}
