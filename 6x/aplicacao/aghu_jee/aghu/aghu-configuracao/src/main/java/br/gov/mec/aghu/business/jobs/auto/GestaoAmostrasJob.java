package br.gov.mec.aghu.business.jobs.auto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class GestaoAmostrasJob extends AghuJob {

	public static final String EXECUTAR = "EXECUTAR";
	public static final String DESCRICAO = "DESCRICAO";
	public static final String NOME_MICROCOMPUTADOR = "NOME_MICROCOMPUTADOR";

	private static final Log LOG = LogFactory.getLog(GestaoAmostrasJob.class);

	private IExamesBeanFacade examesBeanFacade = ServiceLocator.getBean(IExamesBeanFacade.class, "aghu-exames");


	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		LOG.info("Executando processo de negocio ...");
		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		String nomeMicrocomputador = (String) map.get(GestaoAmostrasJob.NOME_MICROCOMPUTADOR);
		
		try {
			examesBeanFacade.processarComunicacaoModuloGestaoAmostra(nomeMicrocomputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
		
	}

}
