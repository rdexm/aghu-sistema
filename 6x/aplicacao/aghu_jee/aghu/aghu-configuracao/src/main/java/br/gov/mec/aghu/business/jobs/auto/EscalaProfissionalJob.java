package br.gov.mec.aghu.business.jobs.auto;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class EscalaProfissionalJob extends AghuJob {
	
	public static final String NOME_MICROCOMPUTADOR = "NOME_MICROCOMPUTADOR";
	public static final String DATA_FIM_VINCULO_SERVIDOR = "DATA_FIM_VINCULO_SERVIDOR";
	
	
	private IInternacaoFacade internacaoFacade = ServiceLocator.getBean(IInternacaoFacade.class, "aghu-internacao");
	

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.getServidorAgendador(jobExecutionContext);
		
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String nomeMicrocomputador = (String) jobDataMap.get(EscalaProfissionalJob.NOME_MICROCOMPUTADOR);
		Date dataFimVinculoServidor = (Date) jobDataMap.get(EscalaProfissionalJob.DATA_FIM_VINCULO_SERVIDOR);
		
		internacaoFacade.verificarEscalaProfissional(nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		
	}

}
