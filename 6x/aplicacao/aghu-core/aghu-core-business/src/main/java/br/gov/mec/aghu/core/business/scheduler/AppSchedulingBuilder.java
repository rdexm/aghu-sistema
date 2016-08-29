package br.gov.mec.aghu.core.business.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import br.gov.mec.aghu.core.business.jobs.AppJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.quartz.MECScheduler;

public class AppSchedulingBuilder {
	
	private MECScheduler scheduler = MECScheduler.getInstance();
	
	private String cron;
	private String nomeProcessoQuartz;
	private Class<? extends AppJob> jobClass;
	private Map<String, Object> jobDataMap;
	
	
	public final void withCron(String cron) {
		this.cron = cron;
	}
	public final void withJobClass(Class<? extends AppJob> jobClass) {
		this.jobClass = jobClass;
	}
	public void withNomeProcessoQuartz(String nomeProcessoQuartz) {
		this.nomeProcessoQuartz = nomeProcessoQuartz;
	}
	public void withJobDataMap(Map<String, Object> jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	
	
	public Trigger build() throws ApplicationBusinessException {
		if (this.getJobClass() == null) {
			throw new IllegalStateException("Job de agendamento nao informado.");
		}
		if (this.getCron() == null) {
			throw new IllegalStateException("Cron para agendamento nao informada.");
		}
		
		return this.makeScheduling();
	}
	
	
	private Trigger makeScheduling() throws ApplicationBusinessException {
		Trigger trigger = scheduler.createTrigger(this.getNomeProcessoQuartz(), this.getCron(), null); 
		
		String jobName = this.getNomeProcessoQuartz() + "Job";
		JobDetail jobDetail = scheduler.createJobDetail(getJobClass(), jobName, getJobDataMap());			
		
		scheduler.scheduleJob(jobDetail, trigger);
		
		return trigger;
	}
	
	

	

	protected final String getCron() {
		return cron;
	}
	protected final Class<? extends AppJob> getJobClass() {
		return jobClass;
	}
	protected final String getNomeProcessoQuartz() {
		if (this.nomeProcessoQuartz == null) {
			this.nomeProcessoQuartz = this.getJobClass().getSimpleName() + "-" + Math.random();
		}
		return nomeProcessoQuartz;
	}
	protected final Map<String, ?> getJobDataMap() {
		if (this.jobDataMap == null) {
			this.jobDataMap = new HashMap<>();
		}
		
		jobDataMap.put(AppSchedulingConstants.CRON, this.getCron());
		jobDataMap.put(AppSchedulingConstants.NOMEPROCESSOQUARTZ, this.getNomeProcessoQuartz());
		
		return jobDataMap;
	}
	
}
