package br.gov.mec.aghu.core.quartz;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;

/**
 * Classe responsavel pela Manipulacao dos agendamentos do Quartz.<br>
 * 
 * Implementacao Singleton.<br>
 * 
 * <i>
 * MECScheduler agendadorAghu = MECScheduler.getInstance();<br>
 * JobDetail job = criar job.<br>
 * Trigger aTrigger = criar trigger.<br>
 * agendadorAghu.scheduleJobComCronTrigger(job, "0/30 * * * * ?");<br>
 * agendadorAghu.start();<br>
 * </i>
 * 		
 * 
 * 
 * @author rcorvalao
 * 
 */
public class MECScheduler {
	
	private static final Log LOG = LogFactory.getLog(MECScheduler.class);

	private static MECScheduler instance = new MECScheduler();

	/**
	 * 
	 * 
	 * @return
	 */
	public static final MECScheduler getInstance() {
		// get instance com dupla verificacao.
//		if (instance == null) {
//			synchronized (MECScheduler.class) {
//				if (instance == null) {
//					instance = new MECScheduler();
//				}
//			}
//		}
		return instance;
	}

	public enum AghuSchedulerExceptionCode implements BusinessExceptionCode {
		ERRO_AO_AGENDAR_TAREFA, ERRO_AO_INICIAR_TAREFA, ERRO_AO_RECUPERAR_DETALHES_JOB
		, ERRO_AO_RECUPERAR_TRIGGERS
		, ERRO_AO_RECUPERAR_JOBS
		, ERRO_AO_DELETAR_JOBS
		, ERRO_AO_ATUALIZAR_JOBS
		, ERRO_AO_PERSISTIR_JOBS
		, ERRO_NOME_JOB_DUPLICADO
		, ERRO_PESQUISA_NOME_JOB_DUPLICADO
		, ERRO_AO_RESUME_JOBS
		, ERRO_AO_PAUSE_JOBS
		, ERRO_AO_REAGENDAR_JOB
		, ERRO_CRON_INVALIDO
		, ERRO_AO_PARAR_SCHEDULER
		;
	}

	private Scheduler scheduler;

	private MECScheduler() {
		// comentado pois possui dependencia com o seam.
		//	this.scheduler = QuartzDispatcher.instance().getScheduler();
		
		try {
			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
			this.scheduler = schedFact.getScheduler();
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Agenda um Job (jobDetail) para ser executado numa determinada situacao (trigger).<br>
	 * 
	 * Exemplos de criacao de Trigger:<br>
	 * 
	 * Trigger diaria a meia noite:<br>
	 * Trigger umaTriggerDiariaMeiaNoite = TriggerUtils.makeDailyTrigger(0, 0);<br>
	 * 
	 * De 10 em 10 segundos:<br>
	 * Trigger aTrigger = new CronTrigger("10_segundos", "grupo", "0/10 * * * * ?");<br> 
	 * 
	 * Ver tambem: org.quartz.SimpleTrigger.<br>
	 * 
	 * @param jobDetail
	 * @param trigger
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws ApplicationBusinessException {
		Date returnValeu = null;

		try {
			this.scheduler.start();
						
			returnValeu = this.scheduler.scheduleJob(jobDetail, trigger);
			
			// quando utiliza quartz persistente para forcar a gravacao no banco
			// eh preciso dar este shutdown
			//this.scheduler.shutdown();
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA);
			mecException.initCause(e);
			throw mecException;
		}

		return returnValeu;
	}

	public void setJobFactory(JobFactory factory) {
		try {
			this.scheduler.setJobFactory(factory);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, "Erro ao criar Quartz Scheduler.");
		}
	}
	
	/**
	 * Agenda um Job (jobDetail) para ser executado conforme a expressao Cron (strCrontrigger).<br>
	 * 
	 *<p>
	 * <b>Exemplo de uso de Job</b>
	 * JobDetail job = new JobDetail("Test1QuartzJob", MECJobGroupNames.TESTE_01.toString(), Test1QuartzJob.class);<br>
	 * Onde Test1QuartzJob eh:<br>
	 *<i>
	 * import org.quartz.Job;<br>
	 * import org.quartz.JobExecutionContext;<br>
	 * import org.quartz.JobExecutionException;<br>
	 * public class Test1QuartzJob implements Job {<br>
	 * 	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {<br>
	 * 		System.out.println("Test1QuartzJob execute: " + jobExecutionContext);<br>
	 * 	}<br>
	 * }
	 *</i>
	 * 
	 *</p>
	 * 
	 * @param jobDetail
	 * @param strCrontrigger
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date scheduleJobComCronTrigger(JobDetail jobDetail, String strCrontrigger) throws ApplicationBusinessException {
		Date returnValeu = null;

		try {
			CronTrigger aTrigger =
					TriggerBuilder.newTrigger()
					.withIdentity(("cronTriggerName" + Math.random()), jobDetail.getKey().getGroup())
					.withSchedule(CronScheduleBuilder.cronSchedule(strCrontrigger))
					.build();
			
			returnValeu = this.scheduler.scheduleJob(jobDetail, aTrigger);
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA);
			mecException.initCause(e);
			throw mecException;
		}

		return returnValeu;
	}

	public void stop() throws ApplicationBusinessException {
		try {
			this.scheduler.shutdown();
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(
					AghuSchedulerExceptionCode.ERRO_AO_PARAR_SCHEDULER);
			mecException.initCause(e);
			throw mecException;
		}
	}

	public void start() throws ApplicationBusinessException {
		try {
			this.scheduler.start();
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(
					AghuSchedulerExceptionCode.ERRO_AO_INICIAR_TAREFA);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	/**
	 * Busca JobDetail do Grupo de Jobs com o nome informado.<br>
	 * 
	 * @param groupName
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<JobDetail> getJobDetailByGroupName(MECJobGroupNames groupName) throws ApplicationBusinessException {
		List<JobDetail> returnList = new LinkedList<JobDetail>();

		try {
			Set<JobKey> jobKeyNames = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName.toString()));
			for (JobKey jobName : jobKeyNames) {
				JobDetail jobDetail = scheduler.getJobDetail(jobName);
				returnList.add(jobDetail);
			}
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_RECUPERAR_JOBS);
			mecException.initCause(e);
			throw mecException;
		}

		return returnList;
	}

	/**
	 * Busca Triggers pertencentes ao Group de Trigger informado.<br>
	 * 
	 * @param triggerGroupName
	 * @throws ApplicationBusinessException 
	 */
	public List<Trigger> getTriggerByGroupName(MECJobGroupNames triggerGroupName) throws ApplicationBusinessException {
		List<Trigger> returnList = new LinkedList<Trigger>();

		try {
			Set<TriggerKey> triggerKeyNames = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName.toString()));
			for (TriggerKey triggerName : triggerKeyNames) {
				Trigger trigger = scheduler.getTrigger(triggerName);
				returnList.add(trigger);
			}
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_RECUPERAR_TRIGGERS);
			mecException.initCause(e);
			throw mecException;
		}

		return returnList;
	}
	
	/**
	 * Retorna todos os nomes de Grupos de jobs.<br>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<String> getAllJobGroupNames() throws ApplicationBusinessException {
		try {
			return scheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_RECUPERAR_JOBS);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	
	/**
	 * Retorna todos os nomes de Grupos de Triggers.<br>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<String> getAllTriggerGroupNames() throws ApplicationBusinessException {
		try {
			return scheduler.getTriggerGroupNames();
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_RECUPERAR_TRIGGERS);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	
	public boolean deleteJob(String jobName, String groupName) throws ApplicationBusinessException {
		try {
			return scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_DELETAR_JOBS);
			mecException.initCause(e);
			throw mecException;
		}		
	}
	
	public void deleteAllJobs() throws ApplicationBusinessException {
		try {
			
			List<String> jobGroupNames = this.getAllJobGroupNames();
			for (String groupName : jobGroupNames) {
				Set<JobKey> jobNames = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
				for (JobKey jobKeyName : jobNames) {
					
					scheduler.deleteJob(jobKeyName);
				}
			}
			
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_DELETAR_JOBS);
			mecException.initCause(e);
			throw mecException;
		}		
	}
	
	public List<JobDetail> findAllJobs() throws ApplicationBusinessException {
		List<JobDetail> list = new LinkedList<JobDetail>();
		
		try {	
			List<String> jobGroupNames = this.getAllJobGroupNames();
			for (String groupName : jobGroupNames) {
				Set<JobKey> jobKeyNames = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
				for (JobKey jobKeyName : jobKeyNames) {		
					JobDetail job = scheduler.getJobDetail(jobKeyName);
					if (job != null) {
						list.add(job);
					}
				}
			}
			
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_DELETAR_JOBS);
			mecException.initCause(e);
			throw mecException;
		}
		
		return list;
	}
	
	public boolean isStatePause(String triggerName) throws ApplicationBusinessException {
		try {
			TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(triggerName, null));
			
			return (TriggerState.PAUSED == state);
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	/**
	 * Remove (delete) the org.quartz.Trigger  with the given name, and store the new given one - which must be associated
	 * with the same job (the new trigger must have the job name & group specified) 
	 * - however, the new trigger need not have the same name as the old trigger.
	 * 
	 * @param triggerName
	 * @param groupName
	 * @param newTrigger
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date rescheduleJob(TriggerKey key, Trigger newTrigger) throws ApplicationBusinessException {
		try {
			
			return scheduler.rescheduleJob(key, newTrigger);
			
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_REAGENDAR_JOB);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	
	public void unscheduleJob(TriggerKey key) throws ApplicationBusinessException {
		try {
			//TriggerKey.triggerKey(triggerName, groupName)
			scheduler.unscheduleJob(key);
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_REAGENDAR_JOB);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	public void pauseTrigger(TriggerKey key) throws ApplicationBusinessException {
		try {
			//TriggerKey.triggerKey(triggerName, groupName)
			scheduler.pauseTrigger(key);
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_REAGENDAR_JOB);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	public void resumeTrigger(TriggerKey key) throws ApplicationBusinessException {
		try {
			//TriggerKey.triggerKey(triggerName, groupName)
			scheduler.resumeTrigger(key);
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_REAGENDAR_JOB);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	
	
	
	
	
	/**
	 * Get all Triggers that are associated with the identified org.quartz.JobDetail.<br>
	 * 
	 * 
	 * @param jobName
	 * @param groupName
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<? extends Trigger> getTriggersOfJob(JobKey key) throws ApplicationBusinessException {
		try {
			//JobKey.jobKey(jobName, groupName)
			return scheduler.getTriggersOfJob(key);
			
		} catch (SchedulerException e) {
			ApplicationBusinessException mecException = new ApplicationBusinessException(AghuSchedulerExceptionCode.ERRO_AO_AGENDAR_TAREFA);
			mecException.initCause(e);
			throw mecException;
		}
	}
	
	
	public Trigger createTrigger(String myTriggerName, String cronExp, JobKey jobKey) {
		TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger();
		
		builder.withIdentity(myTriggerName)
		       .withSchedule(CronScheduleBuilder.cronSchedule(cronExp));
		
		if (jobKey != null) {
			builder.forJob(jobKey);
		}
		       
		return builder.build();
	}
	
	public <T extends Job> JobDetail createJobDetail(Class<T> clazzJob, String jobName) {
		return JobBuilder.newJob(clazzJob)
	                .withIdentity(jobName)
	                .build();
	}
	
	public <T extends Job> JobDetail createJobDetail(Class<T> clazzJob, String jobName, Map<String, ?> dataMap) {
		JobBuilder builder = JobBuilder.newJob(clazzJob)
	                .withIdentity(jobName);
		
		if (dataMap != null && !dataMap.isEmpty()) {
			JobDataMap aJobDataMap = new JobDataMap(dataMap);
			builder.setJobData(aJobDataMap);
		}
		
		return builder.build();
	}


	public Trigger createTrigger(String myTriggerName, Date dataAgendada, JobKey jobKey) {
		TriggerBuilder builder = TriggerBuilder.newTrigger();
		
		builder.withIdentity(myTriggerName)
			    .startAt(dataAgendada);
		
		if (jobKey != null) {
			builder.forJob(jobKey);
		}
		
		return builder.build();
	}

}
