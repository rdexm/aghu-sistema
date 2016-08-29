package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ParametroSistemaJob extends AghuJob {
	
	public static final String EXECUTAR = "EXECUTAR";
	public static final String DESCRICAO = "DESCRICAO";
	
	private static final Log LOG = LogFactory.getLog(ParametroSistemaJob.class);

	private IParametroSistemaFacade parametroSistemaFacade = ServiceLocator.getBean(IParametroSistemaFacade.class, "aghu-configuracao");
	
	


	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		RapServidores servidorLogado = this.getServidorAgendador(jobExecutionContext);
		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		Boolean executar = (Boolean)map.get(ParametroSistemaJob.EXECUTAR); 
		String descricao = (String)map.get(ParametroSistemaJob.DESCRICAO);
		
		LOG.info("Executando processo de negocio " + nomeProcessoQuartz);

		parametroSistemaFacade.executarParametroSistema(job, descricao, executar, servidorLogado);
	}
	
}
