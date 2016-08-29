package br.gov.mec.aghu.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.business.scheduler.JobEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;

@Local
@SuppressWarnings("PMD.NomeclaturaFacade")
public interface ISchedulerFacade extends Serializable {
	
	/**
	 * Inicia execucao quando a tarefa nao esta em execucao.<br>
	 * Retorna false quando AghJobDetail.indSituacao =  DominioSituacaoJobDetail.E,<br>
	 * (executando) caso contrario retorna true.<br>
	 * 
	 * @param job
	 * @return
	 */
	boolean iniciarExecucao(AghJobDetail job);
	
	void finalizarExecucao(AghJobDetail job, DominioSituacaoJobDetail situacao, String log);
	
	void adicionarLog(AghJobDetail job, String appendLog);

	
	
	
	AghJobDetail persistirAghJobDetail(AghJobDetail jobDetail) throws BaseException;
	AghJobDetail persistirJobControleSimples(AghJobDetail jobDetail);
	
	AghJobDetail atualizarAghJobDetail(AghJobDetail jobDetail) throws BaseException;
	
	AghJobDetail obterAghJobDetailPorNome(String nomeProcesso);
	
	AghJobDetail obterAghJobDetailPorId(Integer seq) throws BaseException;
	
	
	
	List<AghJobDetail> pesquisarAghJobDetailPaginator(Map<Object, Object> filtersMap, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long countAghJobDetailPaginator(Map<Object, Object> filtersMap);
	
	void verificarNomeDuplicado(String nomeProcesso) throws ApplicationBusinessException;

	void verificarIntervaloData(final String nomeJobEncerramento, Date dtExecucao, final Integer hrProcessamento) throws ApplicationBusinessException;
	
	
	
	
	boolean pausarAghJobDetail(AghJobDetail jobDetail) throws BaseException;
	
	boolean continuarAghJobDetail(AghJobDetail jobDetail) throws BaseException;
	
	
	
	/**
	 * Busca os Jobs gerenciados pelo Quartz atraves de Scheduler.
	 * 
	 * 
	 * @return list of <code>AgendamentoJobVO</code>
	 * 
	 * @see org.quartz.Scheduler
	 */
	List<AgendamentoJobVO> listarTodosQuartzJobs() throws BaseException;
	
	void removerQuartzJob(AgendamentoJobVO vo) throws BaseException;
	
	

	void reAgendar(AghJobDetail job, String cronExpression, Date dataAgendada) throws BaseException;

	void agendarRotinaAutomatica(IAutomaticJobEnum enumSelecionado, String cronExpression, Date dataAgendada, String nomeMicrocomputador, final RapServidores servidor) throws BaseException;


	void agendarTarefa(JobEnum jobEnum, String cronExpression,
			Date dataAgendada, RapServidores servidorAgendador, Map<String, Object> parametros)
			throws ApplicationBusinessException;

	void removerAghJobDetail(AghJobDetail jobDetail,
			Boolean cameFromTelaNotificacao) throws BaseException;
	
	
	
	
}
