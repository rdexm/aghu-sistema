package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class EncerramentoAutomaticoContaHospitalarJob extends AghuJob {
	
	public static final String NOME_MICRO_COMPUTADOR = "NOME_MICRO_COMPUTADOR";

	IFaturamentoFacade faturamentoFacade = ServiceLocator.getBean(IFaturamentoFacade.class, "aghu-faturamento");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String nomeMicrocomputador = (String) jobDataMap.get(EncerramentoAutomaticoContaHospitalarJob.NOME_MICRO_COMPUTADOR);
		this.faturamentoFacade.agendarEncerramentoAutomaticoContaHospitalar(Calendar.getInstance().getTime(), getCron(jobExecutionContext), nomeMicrocomputador, getServidorAgendador(jobExecutionContext) , getNomeProcessoQuartz(jobExecutionContext));
	}
}
