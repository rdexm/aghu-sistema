/**
 * 
 */
package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.dao.AelExameInternetStatusDAO;
import br.gov.mec.aghu.exames.dao.VAelReenvioExamesPortalDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameLiberadoGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameReenvioGrupoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por realizar a busca e inclusão dos exames oriundos do AGH
 * na fila
 * 
 * @author ghernandez
 * 
 */
@ApplicationScoped
@Startup
public class ExameInternetPoolON extends BaseBusiness {

	private static final String EXAME_INTERNET_POOL_ON_CARREGOU = "[ExameInternetPoolON] - Carregou ";

	private static final long serialVersionUID = 111167461223782587L;

	private static final Log LOG = LogFactory.getLog(ExameInternetPoolON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private AelExameInternetStatusDAO aelExameInternetStatusDAO;
	@Inject
	private VAelReenvioExamesPortalDAO vAelReenvioExamesPortalDAO;

	private List<MensagemSolicitacaoExameLiberadoGrupoVO> pool = new ArrayList<MensagemSolicitacaoExameLiberadoGrupoVO>();

	private List<MensagemSolicitacaoExameReenvioGrupoVO> poolErros = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();

	private List<MensagemSolicitacaoExameLiberadoGrupoVO> poolContigenciaLock = new ArrayList<MensagemSolicitacaoExameLiberadoGrupoVO>();

	private List<MensagemSolicitacaoExameReenvioGrupoVO> poolContigenciaLockErros = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();

	private Date dataInicioProcessamento = null;

	@PostConstruct
	protected void init() {
		LOG.info("Inicializando ExameInternetPoolON");
		dataInicioProcessamento = new Date();
		initPool();
		initPoolErros();
		LOG.info("ExameInternetPoolON iniciado com sucesso");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void initPool() {
		Integer maxResults = 100;
		if (parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_EXAMES_INTERNET)) {
			try {
				maxResults = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_EXAMES_INTERNET);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			maxResults = 100;
		}
		if (maxResults <= 0) {
			pool = new ArrayList<MensagemSolicitacaoExameLiberadoGrupoVO>();
			LOG.info(EXAME_INTERNET_POOL_ON_CARREGOU + pool.size() + " exames no POOL 'NORMAL'");
			return;
		} else if (maxResults <= 10) {
			maxResults = 10;
		}
		pool = aelExameInternetStatusDAO.buscarExamesAgrupadosPorSolicitacaoArea(DominioStatusExameInternet.LI,
				DominioSituacaoExameInternet.N, maxResults);
		if (poolContigenciaLock.size() > 0) {
			int initialSize = pool.size();
			pool.removeAll(poolContigenciaLock);
			if (initialSize > pool.size()) {
				LOG.warn("removendo " + (initialSize - pool.size())
						+ " do pool 'NORMAL' para evitar processamento duplicado");
			}
		}
		Calendar calIni = Calendar.getInstance();
		Calendar calFim = Calendar.getInstance();

		calIni.setTime(dataInicioProcessamento);
		int dayIni = calIni.get(Calendar.DAY_OF_WEEK);
		int horaIni = calIni.get(Calendar.HOUR_OF_DAY);
		int minIni = calIni.get(Calendar.MINUTE);

		calFim.setTime(new Date());
		int dayFim = calFim.get(Calendar.DAY_OF_WEEK);
		int horaFim = calFim.get(Calendar.HOUR_OF_DAY);
		int minFim = calFim.get(Calendar.MINUTE);

		if ((dayIni < dayFim || (dayIni == 7 && dayFim == 1))
				|| (dayIni == dayFim && (horaIni < horaFim || (horaIni == 23 && horaFim == 0)))
				|| (dayIni == dayFim && horaIni == horaFim && (minIni < minFim || (minIni == 59 && minFim == 0)))) {
			LOG.warn("Limpando cache de duplicadatas do pool 'NORMAL'");
			poolContigenciaLock.clear();
			dataInicioProcessamento = new Date();
		}

		poolContigenciaLock.addAll(pool);

		LOG.info(EXAME_INTERNET_POOL_ON_CARREGOU + pool.size() + " exames no POOL 'NORMAL'");

	}

	/**
	 * Inserir na fila ExamesLiberados (tanto um novo envio como também reenvio
	 * através da tela de gerenciamento dos log's
	 * 
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param isReenvio
	 * @return
	 */
	public synchronized MensagemSolicitacaoExameGrupoVO getExameParaProcessar() {
		if (pool.isEmpty() && poolErros.isEmpty()) {
			initPool();
			initPoolErros();
		}

		MensagemSolicitacaoExameGrupoVO retorno = getExameNovoParaProcessar();

		if (retorno == null) {
			retorno = getExameComErroParaReProcessar();
		}

		return retorno;
	}

	private MensagemSolicitacaoExameGrupoVO getExameNovoParaProcessar() {
		MensagemSolicitacaoExameGrupoVO retorno = null;
		if (!pool.isEmpty()) {
			retorno = pool.get(0);
			pool.remove(0);
			LOG.info("[ExameInternetPoolON] - retornando exame SOE_SEQ: " + retorno.getSeqSolicitacaoExame()
					+ " do POOL 'NORMAL'");
		}
		return retorno;
	}

	private MensagemSolicitacaoExameGrupoVO getExameComErroParaReProcessar() {
		MensagemSolicitacaoExameGrupoVO retorno = null;
		if (!poolErros.isEmpty()) {
			retorno = poolErros.get(0);
			poolErros.remove(0);
			LOG.info("[ExameInternetPoolON] - retornando exame SOE_SEQ: " + retorno.getSeqSolicitacaoExame()
					+ " do POOL 'ERROS'");
		}
		return retorno;
	}

	/**
	 * Faz nova tentativa em exames que tenham ficado com status de erro
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void initPoolErros() {
		Integer maxResults = 30;

		if (parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_RETRY_EXAMES_INTERNET)) {
			try {
				maxResults = parametroFacade
						.buscarValorInteiro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_RETRY_EXAMES_INTERNET);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			maxResults = 30;
		}

		if (maxResults <= 0) {
			poolErros = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();
			LOG.info(EXAME_INTERNET_POOL_ON_CARREGOU + poolErros.size() + " exames no POOL 'ERROS'");
			return;
		} else if (maxResults <= 10) {
			maxResults = 10;
		}
		try {
			poolErros = vAelReenvioExamesPortalDAO.buscarExamesParaReenvioDaView(maxResults
					+ poolContigenciaLockErros.size());
		} catch (Exception e) {
			LOG.error("Erro ao reprocessarExamesComErro", e);
			poolErros = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();
		}

		// remover os exames que ainda não estão na data de reenvio
		for (Iterator<MensagemSolicitacaoExameReenvioGrupoVO> iterator = poolErros.iterator(); iterator.hasNext();) {
			MensagemSolicitacaoExameReenvioGrupoVO vo = (MensagemSolicitacaoExameReenvioGrupoVO) iterator.next();
			if (vo.getReenviarEm().after(new Date())){
				iterator.remove();
			}
		}

		if (poolContigenciaLockErros.size() > 0) {
			int initialSize = poolErros.size();

			List<MensagemSolicitacaoExameReenvioGrupoVO> tempArray = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();
			for (MensagemSolicitacaoExameReenvioGrupoVO vo : poolContigenciaLockErros) {
				if (poolErros.contains(vo)) {
					tempArray.add(vo);
					poolErros.remove(vo);
				}
			}
			poolContigenciaLockErros.clear();
			poolContigenciaLockErros.addAll(tempArray);
			if (initialSize > poolErros.size()) {
				LOG.warn("removendo " + (initialSize - poolErros.size())
						+ " do pool 'ERROS' para evitar processamento duplicado");
			}
		} else {
			poolContigenciaLockErros.clear();
			poolContigenciaLockErros.addAll(poolErros);
		}

		LOG.info(EXAME_INTERNET_POOL_ON_CARREGOU + poolErros.size() + " exames no POOL 'ERROS'");
	}

}