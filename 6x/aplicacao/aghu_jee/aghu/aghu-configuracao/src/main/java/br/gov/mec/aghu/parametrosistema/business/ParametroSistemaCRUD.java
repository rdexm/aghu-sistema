/**
 * 
 */
package br.gov.mec.aghu.parametrosistema.business;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityContextAssociation;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghSistemasDAO;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.parametrosistema.vo.AghSistemaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * Parametros de Sistema.
 * 
 * @author rcorvalao
 * 
 */
@Stateless
public class ParametroSistemaCRUD extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(ParametroSistemaCRUD.class);
	private static final long serialVersionUID = 111167461223782587L;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private ParametroSistemaRN parametroSistemaRN;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	@Inject
	private AghSistemasDAO aghSistemasDAO;
	
	@EJB
	private ISchedulerFacade iSchedulerFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	//= ServiceLocator.getBean(IServidorLogadoFacade.class, "aghu-registrocolaborador");
	

	private enum ParametroSistemaCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_PARAMETRO_SISTEMA, ERRO_REMOVER_PARAMETRO_SISTEMA, AGH_00010, ERRO_ENCONTRADO_REGISTROS_DEPENDENTES
		, ERRO_REMOVER_PARAMETRO, AGH_00016, AGH_00017, MESSAGE_VALORPARAMETRO_OBRIGATORIO;
	}

	public AghSistemas buscaAghSistemaPorId(String id) {
		return this.getAghSistemasDAO().obterPorChavePrimaria(id);
	}

	public void persistirAghSistema(AghSistemas umAghSistema, boolean edicao) throws ApplicationBusinessException {
		this.validarInclusaoAlteracaoSistema(umAghSistema, edicao);
		if (edicao) {
			this.alterarSistema(umAghSistema);
		} else {
			this.salvarSistema(umAghSistema);
		}
	}

	private void alterarSistema(AghSistemas umAghSistema) {
		AghSistemasDAO aghSistemasDAO = this.getAghSistemasDAO();
		aghSistemasDAO.atualizar(umAghSistema);
		aghSistemasDAO.flush();
	}

	/**
	 * rcorvalao 25/08/2010
	 * 
	 * @param sistema
	 * @throws ApplicationBusinessException
	 */
	public void excluirAghSistema(AghSistemas sistema) throws ApplicationBusinessException {
		try {
			this.validarExclusaoSistema(sistema);
			aghSistemasDAO.remover(sistema);
			aghSistemasDAO.flush();
		} catch (PersistenceException e) {
			LOG.error("Erro ao remover a ParametroSistema.", e);
			throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.ERRO_REMOVER_PARAMETRO_SISTEMA);
		}

	}
	
	/**
	 * rcorvalao 25/08/2010
	 * 
	 * @param sistemaBD
	 * @throws ApplicationBusinessException
	 */
	private void validarExclusaoSistema(AghSistemas sistema) throws ApplicationBusinessException {
		List<AghParametros> parametrosList = this.pesquisaAghParametroListPorAghSistema(sistema);
		if (!parametrosList.isEmpty()) {
			throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.ERRO_ENCONTRADO_REGISTROS_DEPENDENTES);
		}
	}

	/**
	 * rcorvalao 25/08/2010
	 */
	private List<AghParametros> pesquisaAghParametroListPorAghSistema(AghSistemas sistema) {

		return getAghParametrosDAO().pesquisaAghParametroListPorAghSistema(sistema.getSigla());
	}

	private void salvarSistema(AghSistemas sistemaBD) throws ApplicationBusinessException {
		AghSistemasDAO aghSistemasDAO = this.getAghSistemasDAO();
		aghSistemasDAO.persistir(sistemaBD);
		aghSistemasDAO.flush();
	}

	private void validarInclusaoAlteracaoSistema(AghSistemas sistema, boolean edicao) throws ApplicationBusinessException {
		if (!edicao) {
			AghSistemas s = this.buscaAghSistemaPorId(sistema.getSigla());
			if (s != null) {
				throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.AGH_00010, sistema.getSigla());
			}
		}
	}

	

	/**
	 * rcorvalao 26/08/2010
	 */
	public void excluirAghParametro(Integer seq) throws ApplicationBusinessException {
		try {
			AghParametros parametro = aghParametrosDAO.obterPorChavePrimaria(seq);
			
			if (parametro == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			this.validarExclusaoParametro(parametro);

			aghParametrosDAO.remover(parametro);
			aghParametrosDAO.flush();
			
			this.getParametroSistemaRN().inserirJournal(parametro, DominioOperacoesJournal.DEL);
		} catch (PersistenceException e) {
			LOG.error("Erro ao remover a Parametro.", e);
			throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.ERRO_REMOVER_PARAMETRO);
		}
	}

	/**
	 * rcorvalao 26/08/2010
	 * 
	 * @param parametro
	 */
	private void validarExclusaoParametro(AghParametros parametro) throws ApplicationBusinessException {
		getParametroSistemaRN().verificarDependenciaModuloParametro(parametro);
		getParametroSistemaRN().verificarDependenciaPerfilParametro(parametro);
	}
	
	/**
	 * rcorvalao 26/08/2010
	 * 
	 * @param parametro
	 * @throws ApplicationBusinessException
	 */
	public void persistirAghParametro(AghParametros parametro) throws BaseException {
		this.validarInclusaoAlteracaoParametro(parametro);
		
		DominioOperacoesJournal operacaoJN;
		if (parametro.getSeq() != null && parametro.getSeq() > 0) {
			this.alterarParametro(parametro);
			operacaoJN = DominioOperacoesJournal.UPD;
		} else {
			this.salvarParametro(parametro);
			operacaoJN = DominioOperacoesJournal.INS;
		}
		
		this.getParametroSistemaRN().inserirJournal(parametro, operacaoJN);
	}

	private void alterarParametro(AghParametros parametro) throws BaseException {
		AghParametros old = aghParametrosDAO.obterOriginal(parametro);
		if(old == null){
			throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);    	
		}
		
		parametro.setAlteradoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		parametro.setAlteradoPor(servidorLogado.getPessoaFisica().getNome());

		aghParametrosDAO.merge(parametro);
		aghParametrosDAO.flush();
	}

	/**
	 * rcorvalao 26/08/2010
	 * 
	 * @param parametro
	 * @param edicao
	 */
	private void validarInclusaoAlteracaoParametro(AghParametros parametro) throws ApplicationBusinessException {
		List<AghParametros> list = this.pesquisarParametroPorNomne(parametro.getNome());
		if (!list.isEmpty()) {
			AghParametros p = list.get(0);
			if (!p.getSeq().equals(parametro.getSeq())) {
				throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.AGH_00016);
			}
		}
		
		if(parametro.getVlrTexto() == null && parametro.getVlrNumerico() == null && parametro.getVlrData() == null){
			throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.AGH_00017);
		}
	}

	/**
	 * rcorvalao 26/08/2010
	 * 
	 * @param parametro
	 *  
	 */
	private void salvarParametro(AghParametros parametro) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		parametro.setCriadoPor(servidorLogado.getPessoaFisica().getNome());
		parametro.setCriadoEm(new Date());
		
		AghParametrosDAO aghParametrosDAO = this.getAghParametrosDAO();
		aghParametrosDAO.persistir(parametro);
		aghParametrosDAO.flush();
	}

	/**
	 * rcorvalao 27/08/2010
	 * 
	 * @param valor
	 * @return
	 */
	public List<AghSistemas> pesquisarSistemaPorNome(String nomeSistema) {
		return getAghSistemasDAO().pesquisarSistemaPorNome(nomeSistema, 25);
	}
	
	public List<AghParametros> pesquisarParametroPorNomne(String nomeParametro) {
		return getAghParametrosDAO().pesquisarParametroPorNome(nomeParametro, 25);
	}

	
	
	
	/**
	 * rcorvalao
	 * 24/08/2010
	 * @param sigla
	 * @param nome
	 * @return
	 */
	public Long pesquisaParametroSistemaListCount(String sigla, String nome) {
		return getAghSistemasDAO().pesquisaParametroSistemaListCount(sigla, nome);
	}

	/**
	 * rcorvalao
	 * 24/08/2010
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param sigla
	 * @param nome
	 * @return
	 */
	public List<AghSistemaVO> pesquisaParametroSistemaList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String sigla,
			String nome) {
		
		List<AghSistemas> sistemaList = getAghSistemasDAO().pesquisaParametroSistemaList(firstResult, maxResult, orderProperty, asc,
				sigla, nome);
		
		List<AghSistemaVO> voList = new ArrayList<AghSistemaVO>(sistemaList.size());
		for (AghSistemas aghSistemas : sistemaList) {
			voList.add(new AghSistemaVO(aghSistemas));
		}
		
		return voList;
	}
	
	
	/**
	 * rcorvalao
	 * 26/08/2010
	 * @param parametro
	 * @return
	 */
	public Long pesquisaParametroListCount(AghParametros parametro) {
		return getAghParametrosDAO().pesquisaParametroListCount(parametro);
	}

	/**
	 * rcorvalao
	 * 26/08/2010
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param parametro
	 * @return
	 */
	public List<AghParametroVO> pesquisaParametroList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghParametros parametro) {

		List<AghParametros> list = getAghParametrosDAO().pesquisaParametroList(firstResult, maxResult, orderProperty, asc, parametro);
		
		List<AghParametroVO> voList = new ArrayList<AghParametroVO>(list.size());
		for (AghParametros p : list) {
			AghParametroVO vo = new AghParametroVO();
			vo.setNome(p.getNome());
			vo.setNomeParametro(p.getNome());
			vo.setNomeSistema(p.getSistema().getNome());
			vo.setSeq(p.getSeq());
			vo.setValor(p.getValor());
			vo.setVlrData(p.getVlrData());
			vo.setVlrNumerico(p.getVlrNumerico());
			vo.setVlrTexto(p.getVlrTexto());
			vo.setModulo(p.getSistema().getSigla());
			voList.add(vo);
		}
		return voList;
	}
	
	
	public boolean executarParametroSistema(AghJobDetail job, String descricao, Boolean executar, RapServidores servidor) throws ApplicationBusinessException {
		LOG.info("login: " + servidor != null ? servidor.getUsuario() : null);
		String login = obterLoginUsuarioLogado();
		LOG.info("obterLoginUsuarioLogado: " + login);
		Principal usuario = SecurityContextAssociation.getPrincipal();
		LOG.info("SecurityContextAssociation.getPrincipal: " + usuario != null ? usuario.getName() : "nao informado");
		
		getSchedulerFacade().adicionarLog(job, "Tarefa: executarParametroSistema - Inicio ...");
		
		if (executar) {
			String sigla = "T78";
			getSchedulerFacade().adicionarLog(job, "Tarefa: executarParametroSistema - buscando " + sigla);
			AghSistemas umAghSistema = buscaAghSistemaPorId(sigla);
			boolean edicao = true;
			
			if (umAghSistema == null) {
				getSchedulerFacade().adicionarLog(job, "Tarefa: executarParametroSistema - criando novo registro para " + sigla);
				umAghSistema = new AghSistemas();
				umAghSistema.setSigla(sigla);
				edicao = false;
			}
			umAghSistema.setNome(descricao + " - " + Math.random());
			
			persistirAghSistema(umAghSistema, edicao);
			getSchedulerFacade().adicionarLog(job, "Tarefa: executarParametroSistema - atualizou registro " + sigla);
		} else {
			LOG.info("Executou o metodo agendado parametro sistema.");
		}
		
		getSchedulerFacade().adicionarLog(job, "Tarefa: executarParametroSistema - Final.");
		return true;
	}

	/**
	 * rcorvalao 25/08/2010
	 * 
	 * @param sistema
	 * @throws ApplicationBusinessException
	 */
	public void excluirAghSistema(String siglaSistema) throws ApplicationBusinessException {
		if (StringUtils.isBlank(siglaSistema)) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informadao!!!");
		}
		try {
			AghSistemas sistemaBD = this.buscaAghSistemaPorId(siglaSistema);
			
			this.validarExclusaoSistema(sistemaBD);

			this.getAghSistemasDAO().remover(sistemaBD);
			this.getAghSistemasDAO().flush();
		} catch (PersistenceException e) {
			LOG.error("Erro ao remover a ParametroSistema.", e);
			throw new ApplicationBusinessException(ParametroSistemaCRUDExceptionCode.ERRO_REMOVER_PARAMETRO_SISTEMA);
		}
	}

	
	//@In(create = true, value="schedulerFacade")
	protected ISchedulerFacade getSchedulerFacade() {
		return this.iSchedulerFacade;
	}

	protected ParametroSistemaRN getParametroSistemaRN(){
		return parametroSistemaRN;
	}
	
	protected AghSistemasDAO getAghSistemasDAO(){
		return aghSistemasDAO;
	}
	
	protected AghParametrosDAO getAghParametrosDAO(){
		return aghParametrosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}