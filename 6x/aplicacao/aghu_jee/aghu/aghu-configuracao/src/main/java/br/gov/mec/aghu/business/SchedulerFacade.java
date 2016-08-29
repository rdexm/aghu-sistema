package br.gov.mec.aghu.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.business.scheduler.JobEnum;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;


@SuppressWarnings("PMD.NomeclaturaFacade")
@Stateless
public class SchedulerFacade extends BaseFacade implements ISchedulerFacade {

	@EJB
	private SchedulerRN schedulerRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6735016337133376407L;

	/**
 	 * Inicia execucao quando a tarefa nao esta em execucao.<br>
	 * Retorna false quando AghJobDetail.indSituacao =  DominioSituacaoJobDetail.E,<br>
	 * (executando) caso contrario retorna true.<br>
	 * 
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean iniciarExecucao(AghJobDetail job) {
		return getSchedulerRN().iniciarExecucao(job);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void finalizarExecucao(AghJobDetail job, DominioSituacaoJobDetail situacao, String log) {
		getSchedulerRN().finalizarExecucao(job, situacao, log);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void adicionarLog(AghJobDetail job, String appendLog) {
		getSchedulerRN().adicionarLog(job, appendLog);
	}

	@Override
	public AghJobDetail persistirAghJobDetail(AghJobDetail jobDetail) throws BaseException {
		return getSchedulerRN().persistir(jobDetail);
	}
	
	@Override
	public AghJobDetail persistirJobControleSimples(AghJobDetail jobDetail){
		return getSchedulerRN().persistirJobControleSimples(jobDetail);
	}

	@Override
	public AghJobDetail atualizarAghJobDetail(AghJobDetail jobDetail) throws BaseException {
		return getSchedulerRN().atualizar(jobDetail);
	}

	@Override
	public List<AghJobDetail> pesquisarAghJobDetailPaginator(Map<Object, Object> filtersMap, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getSchedulerRN().pesquisarAghJobDetailPaginator(filtersMap, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long countAghJobDetailPaginator(Map<Object, Object> filtersMap) {
		return getSchedulerRN().countAghJobDetailPaginado(filtersMap);
	}

	@Override
	public AghJobDetail obterAghJobDetailPorNome(String nomeProcesso) {
		return getSchedulerRN().obterAghJobDetailPorNome(nomeProcesso);
	}

	@Override
	public void removerAghJobDetail(AghJobDetail jobDetail, Boolean cameFromTelaNotificacao) throws BaseException {
		getSchedulerRN().remover(jobDetail, cameFromTelaNotificacao);
	}

	@Override
	public AghJobDetail obterAghJobDetailPorId(Integer seq) throws BaseException {
		return getSchedulerRN().obterAghJobDetailPorId(seq);
	}

	@Override
	public void verificarNomeDuplicado(final String nomeProcesso) throws ApplicationBusinessException {
		getSchedulerRN().verificarNomeDuplicado(nomeProcesso);
	}

	@Override
	public void verificarIntervaloData(final String nomeJobEncerramento, final Date dtExecucao,  final Integer hrProcessamento)
			throws ApplicationBusinessException {
		getSchedulerRN().verificarIntervaloData(nomeJobEncerramento, dtExecucao, hrProcessamento);
	}

	protected SchedulerRN getSchedulerRN() {
		return schedulerRN;
	}

	@Override
	public boolean pausarAghJobDetail(AghJobDetail jobDetail) throws BaseException {
		return getSchedulerRN().pausar(jobDetail);
	}

	@Override
	public boolean continuarAghJobDetail(AghJobDetail jobDetail) throws BaseException {
		return getSchedulerRN().continuar(jobDetail);
	}
	
	/**
	 * Busca os Jobs gerenciados pelo Quartz atraves de Scheduler. <br>
	 * Jobs que estão ativos em memória.
	 *
	 * 
	 * @see org.quartz.Scheduler
	 */
	@Override
	public List<AgendamentoJobVO> listarTodosQuartzJobs() throws BaseException {
		return getSchedulerRN().listarTodosQuartzJobs();
	}
	
	@Override
	public void removerQuartzJob(AgendamentoJobVO vo) throws BaseException {
		getSchedulerRN().removerQuartzJob(vo);
	}

	@Override
	public void reAgendar(AghJobDetail job, String cronExpression, Date dataAgendada) throws BaseException {
		getSchedulerRN().reAgendar( job,  cronExpression,  dataAgendada);
	}

	@Override
	public void agendarRotinaAutomatica(IAutomaticJobEnum enumSelecionado, String cronExpression, Date dataAgendada, String nomeMicrocomputador, final RapServidores servidorAgendador) throws BaseException {
		getSchedulerRN().agendarRotinaAutomatica(enumSelecionado, cronExpression, dataAgendada, nomeMicrocomputador, servidorAgendador);
	}
	
	@Override
	public void agendarTarefa(JobEnum jobEnum, String cronExpression,
			Date dataAgendada, final RapServidores servidorAgendador, Map<String, Object> parametros)
			throws ApplicationBusinessException {
		getSchedulerRN().agendarTarefa(jobEnum, cronExpression, dataAgendada,
				servidorAgendador,  parametros);
	}

	
	
	
}
