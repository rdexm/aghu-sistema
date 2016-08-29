package br.gov.mec.aghu.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.JobParameterUtils;
import br.gov.mec.aghu.business.jobs.manual.PassivarProntuarioJob;
import br.gov.mec.aghu.business.jobs.manual.ProcessadorArquivosContratualizacaoJob;
import br.gov.mec.aghu.business.scheduler.AutomaticJobScheduler;
import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.business.scheduler.JobEnum;
import br.gov.mec.aghu.business.scheduler.NotificacaoJobEnum;
import br.gov.mec.aghu.configuracao.dao.AghJobDetailDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.scheduler.AppScheduler;
import br.gov.mec.aghu.core.business.scheduler.AppSchedulingConstants;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.quartz.MECScheduler;
import br.gov.mec.aghu.core.quartz.QuartzUtils;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * 
 * @author rcorvalao
 * 
 */
@Stateless
public class SchedulerRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SchedulerRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private AutomaticJobScheduler automaticJobScheduler;

	@Inject
	private AghJobDetailDAO aghJobDetailDAO;

	@Inject
	private AppScheduler aghuScheduler;

	private static final long serialVersionUID = 4045763606270533913L;
	
	private static final String QUEBRA_LINHA = System.getProperty("line.separator");
	
	private static final Integer MAX_LOG_SIZE = 100000;

	public enum SchedulerRNExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSO_AGENDADO_INTERVALO_PROCESSAMENTO
		, ERRO_REAGENDAMENTO_TRIGGER_NAO_ENCONTRADA
		, ERRO_REAGENDAMENTO_CRON_EXPRESSION
		, ERRO_AO_REAGENDAR_TAREFA
		, MSG_REAGENTAMENTO_JOB_JAH_FINALIZADO
		, ERRO_LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO
		, ERRO_EXCLUIR_PROCESSO_NOTIFICACAO
		;
	}
	
	/**
	 * Inicia execucao quando a tarefa nao esta em execucao.<br>
	 * Retorna false quando AghJobDetail.indSituacao =  DominioSituacaoJobDetail.E,<br>
	 * (executando) caso contrario retorna true.<br>
	 * Deve ser chamada com uma nova transacao.<br>
	 * 
	 * @param jobP
	 * @return
	 */
	public boolean iniciarExecucao(AghJobDetail jobP) {
		boolean returnValue = true;
		
		if (jobP == null) {
			LOG.warn("SchedulerRN.iniciarExecucao: AghJobDetail nao encontrado.");
		} else {
			AghJobDetail job = obterAghJobDetailPorNome(jobP.getNomeProcesso());
			getAghJobDetailDAO().refreshAndLock(job);
			
			if (DominioSituacaoJobDetail.E == job.getIndSituacao())  {
				this.appendLog(job, "Jah executando - " + DateFormatUtil.formataTimeStamp(new Date()) + ".");
				returnValue = false;
			} else {
				job.setIndSituacao(DominioSituacaoJobDetail.E);
				this.appendLog(job, "Inicio Execucao - " + DateFormatUtil.formataTimeStamp(new Date()) + "...");
			}
			
			getAghJobDetailDAO().atualizar(job);
			getAghJobDetailDAO().flush();
		}
		
		return returnValue;
	}
	
	/**
	 * deve ser chamada com uma nova transacao.
	 * 
	 * @param jobP
	 * @param situacao
	 * @param log
	 */
	public void finalizarExecucao(AghJobDetail jobP, DominioSituacaoJobDetail situacao, String log) {
		if (jobP == null) {
			LOG.warn("SchedulerRN.finalizarExecucao: AghJobDetail nao encontrado.");
		} else {
			AghJobDetail job = obterAghJobDetailPorNome(jobP.getNomeProcesso());
			if(job != null) {
				getAghJobDetailDAO().refresh(job);
				
				job.setIndSituacao(situacao);
				if (log != null) {
					this.appendLog(job, log);
				}
				this.appendLog(job, "Fim Execucao - " + DateFormatUtil.formataTimeStamp(new Date()) + ".");
				
				try {
					this.atualizar(job);
				} catch (ApplicationBusinessException e) {
					LOG.error("Nao foi possivel finalizar execucao!!!!", e);
					LOG.error(e.getMessage(), e);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * deve ser chamada com uma nova transacao.
	 * 
	 * @param jobP
	 * @param appendLog
	 */
	public void adicionarLog(AghJobDetail jobP, String appendLog) {
		if (jobP == null) {
			LOG.warn("SchedulerRN.adicionarLog: AghJobDetail nao encontrado.");
		} else {
			AghJobDetail job = obterAghJobDetailPorNome(jobP.getNomeProcesso());
			getAghJobDetailDAO().refresh(job);
			
			this.appendLog(job, appendLog);

			getAghJobDetailDAO().atualizar(job);
			getAghJobDetailDAO().flush();
		}
	}
	
	/**
	 * Tenta executa o <code>pauseTrigger</code><br> 
	 * da classe <code>Scheduler</code> do Quartz<br>
	 * quando existir Trigger no JobDetail do AGH.<br>
	 * 
	 * @param jobDetail
	 * @return true quando o <code>pauseTrigger</code> for executado.
	 * @throws BaseException
	 */
	public boolean pausar(AghJobDetail jobDetail) throws BaseException {
		if (jobDetail == null || jobDetail.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}

		AghJobDetail job = getAghJobDetailDAO().obterPorChavePrimaria(jobDetail.getSeq());
		boolean pauseOk = false;
		if (job != null) {
			Trigger triggerHandle = job.getTrigger();
			if (triggerHandle != null) {
				
				MECScheduler agendadorAghu = MECScheduler.getInstance();
				agendadorAghu.pauseTrigger(triggerHandle.getKey());
				pauseOk = true;
			}
		}
		
		return pauseOk;
	}

	/**
	 * Tenta executa o <code>resumeTrigger</code><br> 
	 * da classe <code>Scheduler</code> do Quartz<br>
	 * quando existir Trigger no JobDetail do AGH.<br>
	 * 
	 * @param jobDetail
	 * @return true quando o <code>resumeTrigger</code> for executado.
	 * @throws BaseException
	 */
	public boolean continuar(AghJobDetail jobDetail) throws BaseException {
		if (jobDetail == null || jobDetail.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}

		AghJobDetail job = getAghJobDetailDAO().obterPorChavePrimaria(jobDetail.getSeq());
		boolean resumeOk = false;
		if (job != null) {
			Trigger trigger = job.getTrigger();
			if (trigger != null) {
				MECScheduler agendadorAghu = MECScheduler.getInstance();
				
				agendadorAghu.resumeTrigger(trigger.getKey());
				resumeOk = true;
			}
		}
		
		return resumeOk;
	}

	public List<AghJobDetail> pesquisarAghJobDetailPaginator(Map<Object, Object> filtersMap, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getAghJobDetailDAO().pesquisarAghJobDetailPaginator(filtersMap, firstResult, maxResult, orderProperty, asc);
	}

	public AghJobDetail obterAghJobDetailPorNome(String nomeProcesso) {
		return getAghJobDetailDAO().obterAghJobDetailPorNome(nomeProcesso);
	}

	/**
	 * Busca por id. Faz refresh.
	 * 
	 * @param seq
	 * @return
	 */
	public AghJobDetail obterAghJobDetailPorId(Integer seq) {
		AghJobDetail job = getAghJobDetailDAO().obterAghJobDetailPorId(seq);
		return job;
	}

	public AghJobDetail atualizar(AghJobDetail jobDetail) throws ApplicationBusinessException {
		if (jobDetail == null) {
			throw new IllegalArgumentException("parametro obrigatorio nao informado.");
		}

		this.verificarNomeDuplicado(jobDetail);

		this.doSetDataAgendadoPara(jobDetail);

		AghJobDetail retorno = getAghJobDetailDAO().atualizar(jobDetail);
		getAghJobDetailDAO().flush();
		return retorno;
	}

	private void doSetDataAgendadoPara(AghJobDetail jobDetail) throws ApplicationBusinessException {
		if (jobDetail.getAgendado() == null) {
			jobDetail.setAgendado(new Date());
		}
//		try {
//			if (jobDetail.getTrigger() != null && jobDetail.getTrigger().getNextFireTime() != null) {
//				jobDetail.setAgendadoPara(jobDetail.getTrigger().getNextFireTime());
//			} else {
//				if (jobDetail.getAgendadoPara() == null) {
//					jobDetail.setAgendadoPara(new Date());
//				}
//			}
//		} catch (SchedulerException e) {
//			LOG.error(e.getMessage(), e);
//			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_AO_ATUALIZAR_JOBS);
//		}
	}

	public AghJobDetail persistir(AghJobDetail jobDetail) throws ApplicationBusinessException {
		if (jobDetail == null) {
			jobDetail = new AghJobDetail();
		}

		if (jobDetail.getNomeProcesso() == null) {
			jobDetail.setNomeProcesso("AghJobDetail " + new Date());
		}

		this.verificarNomeDuplicado(jobDetail);

		jobDetail.setIndSituacao(DominioSituacaoJobDetail.A);

		if (jobDetail.getServidor() == null) {
			RapServidores servidor = this.obterServidorDefault();
			jobDetail.setServidor(servidor);
		}
		jobDetail.setLog("");

		this.doSetDataAgendadoPara(jobDetail);

		this.getAghJobDetailDAO().persistir(jobDetail);
		this.getAghJobDetailDAO().flush();
		return jobDetail;
	}
	
	/**
	 * Método para criar um SIMPLES JOB para ser utilizado como alternativa à variaveis de sessao.
	 * Não serve para agendamento de processos, para tal @see SchedulerRN.persistir(AghJobDetail jobDetail)
	 */
	public AghJobDetail persistirJobControleSimples(AghJobDetail jobDetail){
		this.getAghJobDetailDAO().persistir(jobDetail);
		this.getAghJobDetailDAO().flush();
		return jobDetail;
	}

	private RapServidores obterServidorDefault() throws ApplicationBusinessException {
		RapServidores servidor = null;

		try {
			AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR);
			String userName = aghParametro.getVlrTexto();

			if (userName != null) {
				servidor = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(userName);
			} else {
				throw new IllegalStateException("Nao encontrou Servidor com userName: " + AghuParametrosEnum.P_USUARIO_AGENDADOR);
			}

			if (servidor == null) {
				throw new IllegalStateException("Nao encontrou Servidor com userName: " + userName);
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(e.getCode());
		}

		return servidor;
	}
	
	/**
	 * Busca os Jobs gerenciados pelo Quartz atraves de Scheduler.<br>
	 * Jobs que estão ativos em memória.
	 * 
	 * TODO quando possivel criar um VO para representar este retorno.
	 * VO deve ter atributos:
	 * nomeJob, proximoAgendamento, quantTriggers, jobDataMapAsync
	 * @return list of <code>AgendamentoJobVO</code>
	 * 
	 * @see org.quartz.Scheduler
	 */
	public List<AgendamentoJobVO> listarTodosQuartzJobs() throws BaseException {
		MECScheduler agendadorAghu = MECScheduler.getInstance();
		
		List<AgendamentoJobVO> quartzJobs = new LinkedList<AgendamentoJobVO>();
		List<JobDetail> list = null;
		try {
			list = agendadorAghu.findAllJobs();
			
			for (JobDetail jobDetail : list) {
				LOG.info(" # " + jobDetail);
				
				AgendamentoJobVO job = new AgendamentoJobVO();
				job.setSeq(jobDetail.hashCode());
				job.setNomeGrupo(QuartzUtils.getJobDetailGroup(jobDetail));
				job.setNomeTarefa(QuartzUtils.getJobDetailName(jobDetail));
				
				Date nextFireTime = null;
				Integer quantTriggers = 0;
				List<? extends Trigger> trg = agendadorAghu.getTriggersOfJob(jobDetail.getKey());
				if (trg != null && trg.size() > 0) {
					nextFireTime = trg.get(0).getFireTimeAfter(new Date());
					quantTriggers = trg.size();
				}
				
				job.setProximoAgendamento(nextFireTime);
				//job.setJobDataMapAsync(jobDetail.getJobDataMap().get("async").toString());
				job.setJobDataMapAsync(jobDetail.getJobClass().getName());
				job.setQuantTriggers(quantTriggers);
				
				quartzJobs.add(job);
			}
			
			return quartzJobs;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_AO_RECUPERAR_DETALHES_JOB);
		}
	}
	
	public void removerQuartzJob(AgendamentoJobVO vo) throws BaseException {
		MECScheduler agendadorAghu = MECScheduler.getInstance();
		
		agendadorAghu.deleteJob(vo.getNomeTarefa(), vo.getNomeGrupo());
		
	}
	
	
	
	
	public void agendarRotinaAutomatica(IAutomaticJobEnum enumSelecionado, String cronExpression, Date dataAgendada, String nomeMicrocomputador, final RapServidores servidorAgendador) throws BaseException {
		if (enumSelecionado == null) {
			throw new IllegalArgumentException("A tarefa deve ser informada.");			
		}
		if (cronExpression == null && dataAgendada == null) {
			throw new IllegalArgumentException("Um dos parametros eh obrigatorio: (cronExpression ou  dataAgendada).");
		}
		
		if (StringUtils.isNotBlank(cronExpression) && !QuartzUtils.isValidCronExpression(cronExpression)) {
			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_CRON_INVALIDO);
		}
		
		AghJobDetail job = this.obterAghJobDetailPorNome(enumSelecionado.getTriggerName());
		if (job != null) {
			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_NOME_JOB_DUPLICADO);
		}
		
		// Quando for agendamento por Cron e limite do intervalo mínimo de agendamento estiver ativo.
		if (!enumSelecionado.isIgnorarLimiteMinIntervaloAgendamento() && StringUtils.isNotBlank(cronExpression)) {
			// E tiver o parametro de limite minino defindo.
			if (this.getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO)) {
				// Valor do parametro em horas.
				AghParametros param = this.getParametroFacade().getAghParametro(AghuParametrosEnum.LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO);
				Integer diffMinAceitavel = ((BigDecimal) param.getValor()).intValue();
				// Valida se o intervalo entre agendamentos eh aceitavel.
				boolean isValido = QuartzUtils.isValidoPeriodoEntreExecucoes(cronExpression, diffMinAceitavel);
				if (!isValido) {
					throw new ApplicationBusinessException(SchedulerRNExceptionCode.ERRO_LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO, diffMinAceitavel, AghuParametrosEnum.LIMITE_MIN_INTERVALOS_ENTRE_AGENDAMENTO);			
				}
			}
		}
		
		String cron = null;
		if (StringUtils.isNotBlank(cronExpression)) {
			cron = cronExpression;
		} else if (dataAgendada != null) {
			cron = QuartzUtils.dataAsCronExpression(dataAgendada, false);
		}
		
		enumSelecionado.setCron(cron);
		enumSelecionado.setServidor(servidorAgendador);
		getAutomaticJobScheduler().agendarTarefa(enumSelecionado, nomeMicrocomputador, servidorAgendador);
	}
	
	/**
	 * Metodo para agendar tarefas que não são controlados de forma automatica.<br>
	 * Não gera AghJobDetail.<br>
	 * 
	 * 
	 * @param jobEnum
	 * @param cronExpression
	 * @param dataAgendada
	 * @param servidorAgendador
	 * @param parametros
	 * @throws ApplicationBusinessException
	 */
	public void agendarTarefa(JobEnum jobEnum, String cronExpression,
			Date dataAgendada, final RapServidores servidorAgendador,Map<String, Object> parametros)
			throws ApplicationBusinessException {
		if (jobEnum == null) {
			throw new IllegalArgumentException("A tarefa deve ser informada.");
		}
		if (cronExpression == null && dataAgendada == null) {
			throw new IllegalArgumentException(
					"Um dos parametros eh obrigatorio: (cronExpression ou  dataAgendada).");
		}

		if (StringUtils.isNotBlank(cronExpression)
				&& !QuartzUtils.isValidCronExpression(cronExpression)) {
			throw new ApplicationBusinessException(
					MECScheduler.AghuSchedulerExceptionCode.ERRO_CRON_INVALIDO);
		}

		String cron = null;
		if (StringUtils.isNotBlank(cronExpression)) {
			cron = cronExpression;
		} else if (dataAgendada != null) {
			cron = QuartzUtils.dataAsCronExpression(dataAgendada, true);
		}
		
		Class<? extends AghuJob> classeJob = escolherJobClass(jobEnum);

		parametros.put(AppSchedulingConstants.SERVIDORAGENDADOR, JobParameterUtils.servidorIdToString(servidorAgendador));	

		aghuScheduler.agendarJob(Calendar.getInstance().getTime(), cron,
				null, classeJob, parametros);
	}

	private Class<? extends AghuJob> escolherJobClass(JobEnum jobEnum) {
		Class<? extends AghuJob> classeJob;
		
		switch (jobEnum) {
		case PASSIVAR_PRONTUARIO:
			classeJob = PassivarProntuarioJob.class;
			break;
		case PROCESSADOR_ARQUIVOS_CONTRATUALIZACAO:
			classeJob = ProcessadorArquivosContratualizacaoJob.class;
			break;
		default:
			classeJob = null;
			break;
		}
		
		return classeJob;
	}
	
	
	
	
	public void reAgendar(AghJobDetail paramJob, String cronExpression, Date dataAgendada) throws BaseException {
		AghJobDetail old = aghJobDetailDAO.obterOriginal(paramJob.getSeq());
		
		if(old == null){
			throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
		}
		
		if (paramJob == null || paramJob.getNomeProcesso() == null) {
			throw new IllegalArgumentException("A tarefa deve ser informada.");			
		}
		if (cronExpression == null && dataAgendada == null) {
			throw new IllegalArgumentException("Um dos parametros eh obrigatorio: (cronExpression ou  dataAgendada).");
		}
		
		try {
			AghJobDetail job = this.obterAghJobDetailPorNome(paramJob.getNomeProcesso());
			
			if (DominioSituacaoJobDetail.E == job.getIndSituacao()) {
				String erroMessage = "Tarefa " + job.getNomeProcesso() + " esta " + DominioSituacaoJobDetail.E.getDescricao();
				throw new ApplicationBusinessException(SchedulerRN.SchedulerRNExceptionCode.ERRO_AO_REAGENDAR_TAREFA, erroMessage);				
			}
			
			Trigger trigger = job.getTrigger();
			if (trigger != null) {
				if (job.getDataProximaExecucao() == null) {
					throw new ApplicationBusinessException(SchedulerRN.SchedulerRNExceptionCode.MSG_REAGENTAMENTO_JOB_JAH_FINALIZADO);				
				}
				MECScheduler agendadorAghu = MECScheduler.getInstance();
				
				Trigger newTrigger = null;
				if (dataAgendada != null) {
					newTrigger = agendadorAghu.createTrigger(job.getNomeProcesso()+Math.random(), dataAgendada, trigger.getJobKey()); 
				} else if (cronExpression != null) {
					newTrigger = agendadorAghu.createTrigger(job.getNomeProcesso()+Math.random(), cronExpression, trigger.getJobKey());
				}
				
				if (newTrigger != null) {
					agendadorAghu.rescheduleJob(trigger.getKey(), newTrigger);
					
					job.setTrigger(newTrigger);
					this.atualizar(job);	
				}
			} else {
				throw new ApplicationBusinessException(SchedulerRN.SchedulerRNExceptionCode.ERRO_REAGENDAMENTO_TRIGGER_NAO_ENCONTRADA, job.getNomeProcesso());
			}
			
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			throw e;
//		} catch (ParseException e) {
//			LOG.error(e.getMessage(), e);
//			throw new ApplicationBusinessException(SchedulerRN.SchedulerRNExceptionCode.ERRO_REAGENDAMENTO_CRON_EXPRESSION);
		}
	}

	public void remover(AghJobDetail jobDetail, Boolean cameFromTelaNotificacao) throws BaseException {
		if (jobDetail == null || jobDetail.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		if(!cameFromTelaNotificacao) {
			validarProcessoNotificacao(jobDetail);
		}

		AghJobDetail job = getAghJobDetailDAO().obterPorChavePrimaria(jobDetail.getSeq());

		if (job != null) {
			Trigger trigger = job.getTrigger();
			if (trigger != null) {
				MECScheduler agendadorAghu = MECScheduler.getInstance();
				agendadorAghu.unscheduleJob(trigger.getKey());				
			}
			getAghJobDetailDAO().remover(job);
			getAghJobDetailDAO().flush();
		}
	}

	private void validarProcessoNotificacao(AghJobDetail jobDetail)
			throws ApplicationBusinessException {
		String[] processo = jobDetail.getNomeProcesso().split("-");
		for(NotificacaoJobEnum notificacao : NotificacaoJobEnum.values()) {
			if(notificacao.getDescricao().equals(processo[0])) {
				throw new ApplicationBusinessException(SchedulerRNExceptionCode.ERRO_EXCLUIR_PROCESSO_NOTIFICACAO);
			}
		}
	}

	public void verificarNomeDuplicado(final String nomeProcesso) throws ApplicationBusinessException {
		List<AghJobDetail> list = getAghJobDetailDAO().pesquisarAghJobDetailPorNome(nomeProcesso);
		if (list != null && !list.isEmpty()) {
			throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_PESQUISA_NOME_JOB_DUPLICADO);
		}
	}

	public void verificarIntervaloData(final String nomeJobEncerramento, final Date dtExecucao, final Integer hrProcessamento) throws ApplicationBusinessException {
		
		List<AghJobDetail> listaJobDetail = getAghJobDetailDAO().buscarAghJobDetail(nomeJobEncerramento, new DominioSituacaoJobDetail[]{DominioSituacaoJobDetail.A,DominioSituacaoJobDetail.E});
		
		for (AghJobDetail jobDetail : listaJobDetail) {
			if (jobDetail.getDataProximaExecucao() != null) {
				
				Calendar dataInicial = new GregorianCalendar();
				dataInicial.setTime(jobDetail.getDataProximaExecucao());
				dataInicial.add(Calendar.HOUR_OF_DAY, -hrProcessamento);
				Calendar dataFinal = new GregorianCalendar();
				dataFinal.setTime(jobDetail.getDataProximaExecucao());
				dataFinal.add(Calendar.HOUR_OF_DAY, hrProcessamento);

				
				if (dtExecucao.before(dataFinal.getTime()) && dtExecucao.after(dataInicial.getTime())) {
					throw new ApplicationBusinessException(SchedulerRNExceptionCode.ERRO_PROCESSO_AGENDADO_INTERVALO_PROCESSAMENTO, hrProcessamento);
				}

			}
		}
	}

	private void verificarNomeDuplicado(AghJobDetail job) throws ApplicationBusinessException {
		String nomeProcesso = job.getNomeProcesso();
		List<AghJobDetail> list = getAghJobDetailDAO().pesquisarAghJobDetailPorNome(nomeProcesso);

		if (list != null && !list.isEmpty()) {
			if (list.size() > 1) {
				// Se encontrar mais q uma entidade com o mesmo nome. Erro
				throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_PESQUISA_NOME_JOB_DUPLICADO);
			} else {
				// Tem apenas uma entidade com este nome.
				if (job.getSeq() != null) {
					// Verificacao na Atualizacao. Se for a mesma entidade ok.
					AghJobDetail jobEncontrado = list.get(0);
					if (!jobEncontrado.equals(job)) {
						throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_NOME_JOB_DUPLICADO);
					}
				} else {
					// Verificacao na Inclusao; Erro
					throw new ApplicationBusinessException(MECScheduler.AghuSchedulerExceptionCode.ERRO_NOME_JOB_DUPLICADO);
				}
			}
		}
	}

	public Long countAghJobDetailPaginado(Map<Object, Object> filtersMap) {
		return this.getAghJobDetailDAO().countAghJobDetailPaginator(filtersMap);
	}

	protected void appendLog(AghJobDetail job, String novoLog) {
		if (novoLog == null) {
			novoLog = "log informado eh nulo.";
		}
		if ("".equals(novoLog.trim())) {
			novoLog = "log informado esta em branco.";
		}

		String log = "";
		if (job.getLog() != null) {
			String auxLog = job.getLog();
			if (auxLog.length() > MAX_LOG_SIZE) {
				auxLog = auxLog.substring(auxLog.length() - MAX_LOG_SIZE, auxLog.length() - 1);
			}
			log = auxLog + novoLog;
		} else {
			log = novoLog;
		}
		job.setLog(log + QUEBRA_LINHA);
	}

	protected AghJobDetailDAO getAghJobDetailDAO() {
		return aghJobDetailDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AutomaticJobScheduler getAutomaticJobScheduler() {
		return automaticJobScheduler;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
