package br.gov.mec.aghu.business.jobs.manual;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PassivarProntuarioJob extends AghuJob {
	
	public static final String SECAO = "SECAO";
	public static final String DATA_HORA = "DATA_HORA";
	public static final String INCLUIR_PASSIVOS = "INCLUIR_PASSIVOS";
	
	private static final Log LOG = LogFactory.getLog(PassivarProntuarioJob.class);

	
	private IPacienteFacade pacienteFacade = ServiceLocator.getBean(IPacienteFacade.class, "aghu-paciente");
	
	


	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String nomeProcessoQuartz = this.getNomeProcessoQuartz(jobExecutionContext); 
		LOG.info("Executando processo de negocio: " + nomeProcessoQuartz);
		
		RapServidores servidorLogado = this.getServidorAgendador(jobExecutionContext);
		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		String secao = (String) map.get(PassivarProntuarioJob.SECAO);
		Date dataHora = (Date) map.get(PassivarProntuarioJob.DATA_HORA);
		Boolean incluirPassivos = (Boolean) map.get(PassivarProntuarioJob.INCLUIR_PASSIVOS);
		
		
		this.pacienteFacade.agendarPassivarProntuario(secao, dataHora, incluirPassivos, servidorLogado);
		
	}
	
}
