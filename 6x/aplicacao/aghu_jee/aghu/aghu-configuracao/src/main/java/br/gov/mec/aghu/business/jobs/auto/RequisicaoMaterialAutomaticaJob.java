package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class RequisicaoMaterialAutomaticaJob extends AghuJob {
	
	public static final String NOME_MICRO_COMPUTADOR = "NOME_MICRO_COMPUTADOR";

	private IEstoqueFacade estoqueFacade = ServiceLocator.getBean(IEstoqueFacade.class, "aghu-estoque");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job,	JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		
		//RequisicaoMaterialAutomaticaScheduler
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String nomeMicrocomputador = (String) jobDataMap.get(RequisicaoMaterialAutomaticaJob.NOME_MICRO_COMPUTADOR);
		this.estoqueFacade.efetivarRequisicaoMaterialAutomatica(Calendar.getInstance().getTime(), getCron(jobExecutionContext), nomeMicrocomputador);
	}
}
