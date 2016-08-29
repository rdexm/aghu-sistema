package br.gov.mec.aghu.business.jobs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityContextAssociation;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.casca.autenticacao.AghuQuartzPrincipal;
import br.gov.mec.aghu.core.business.jobs.AppJob;
import br.gov.mec.aghu.core.business.scheduler.AppSchedulingConstants;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public abstract class AghuJob  extends AppJob {
	
	private static final Log LOG = LogFactory.getLog(AghuJob.class);
	
	private ISchedulerFacade schedulerFacade = ServiceLocator.getBean(ISchedulerFacade.class, "aghu-configuracao");
	
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	private IRegistroColaboradorFacade registroColaboradorFacade = ServiceLocator.getBean(IRegistroColaboradorFacade.class, "aghu-registrocolaborador");
	
	
	//@Inject
	private MessagesUtils messagesUtil;

	
	protected MessagesUtils getMessagesUtils() {
//		if (this.messagesUtil == null) {
//			this.messagesUtil = new MessagesUtils(ResourceBundle.getBundle("br.gov.mec.aghu.bundle.MessagesResourceBundle", Locale.getDefault()));
//		}
		return this.messagesUtil;
	}


	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		//RapServidores servidorLogado = this.getServidorAgendador(jobExecutionContext);
		
		if (nomeProcessoQuartz == null || "".equals(nomeProcessoQuartz.trim())) {
			throw new IllegalArgumentException("Nome do job não pode ser nulo.");
		}
		LOG.info("Quartz Job " + " [" + nomeProcessoQuartz + "] " + DateFormatUtil.formataTimeStamp(new Date()) + ".");
		
		AghJobDetail job = getSchedulerFacade().obterAghJobDetailPorNome(nomeProcessoQuartz);
		boolean iniciouExecucao = getSchedulerFacade().iniciarExecucao(job);
		if (!iniciouExecucao) {
			return;
		}
		
		String stringException = "";
		boolean finalizouSucesso = false;
		DominioSituacaoJobDetail situacao;
		try {
			injetarLogin();
			
			doExecutarProcessoNegocio(job, jobExecutionContext);
			
			finalizouSucesso = true;
			situacao = DominioSituacaoJobDetail.C;
		} catch (ApplicationBusinessException e) {
			LOG.error("Falha no inserirFilaExamesLiberados", e);
			finalizouSucesso = false;
			situacao = DominioSituacaoJobDetail.N;
			stringException = stackTraceToString(e);								
		} catch (Throwable e) { //NOPMD
			LOG.error("Falha no inserirFilaExamesLiberados", e);
			finalizouSucesso = false;
			situacao = DominioSituacaoJobDetail.F;
			stringException = stackTraceToString(e);											
		}
		
		int size = 100 + stringException.length();
		StringBuffer mensagem = new StringBuffer(size);
		mensagem.append("Quartz Job " + " [" + nomeProcessoQuartz + "]" +" finalizou com: ");
		
		if (finalizouSucesso) {
			mensagem.append("SUCESSO");
		} else {
			mensagem.append("FALHA <br />");
			mensagem.append(stringException);
		}
		
		LOG.info(mensagem.toString());
		getSchedulerFacade().finalizarExecucao(job, situacao, mensagem.toString());
	}


	private String stackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}


	private void injetarLogin() throws ApplicationBusinessException {
		//@InjetarLogin
		AghParametros aghParametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR);
		String usuario = aghParametro.getVlrTexto();
		SecurityContextAssociation.setPrincipal(new AghuQuartzPrincipal(usuario));
	}
	
	
	protected abstract void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException;


	/**
	 * Obtem parâmetro do resource bundle.
	 * 
	 * @param key Chave.
	 * @param params Parâmetros.
	 * @return Mensagem.
	 */
	protected String getMessage(String key, Object... params) {
		return getMessagesUtils().getResourceBundleValue(key, params);
	}
	

	protected String getCron(JobExecutionContext jobExecutionContext) {
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		return (String) map.get(AppSchedulingConstants.CRON); 
	}
	
	protected String getNomeProcessoQuartz(JobExecutionContext jobExecutionContext) {
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		return (String) map.get(AppSchedulingConstants.NOMEPROCESSOQUARTZ);
	}
	
	protected RapServidores getServidorAgendador(JobExecutionContext jobExecutionContext) {
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		
		String strServidorId = (String) map.get(AppSchedulingConstants.SERVIDORAGENDADOR);
		RapServidoresId id = JobParameterUtils.stringToServidorId(strServidorId);
		
		return registroColaboradorFacade.buscaServidor(id);
	}
	

	protected ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}

	

}
