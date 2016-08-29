package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfilJnDAO;
import br.gov.mec.aghu.casca.dao.PerfisPermissoesDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfilJn;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.vo.FiltroPerfilJnVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

// FIXME Arrumar comentarios dos metodos
@Stateless
public class PerfilON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PerfilON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;  

	@Inject
	private PerfilJnDAO perfilJnDAO;
	
	@Inject
	private PerfilDAO perfilDAO;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@Inject
	private PerfisPermissoesDAO perfisPermissoesDAO;	

	private static final long serialVersionUID = -4196478039144537225L;

	protected enum PerfilONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_PERFIL_EXISTENTE, CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO, CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, CASCA_VIOLACAO_FK_PERFIL;
	}

	public void salvarPerfil(Perfil perfil) throws ApplicationBusinessException {
		
		//String msg = null;
		//msg = getResourceBundleValue("CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO");
		
		if (perfil == null) {
			//msg=null;
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		

		if (perfil.getId() == null) { // inclusao
			if (isPerfilExistente(null, perfil.getNome())) {
				throw new ApplicationBusinessException(
						PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			}
			perfil.setDataCriacao(new Date());
			perfilDAO.persistir(perfil);
			perfilDAO.flush();
			
			PerfilJn perfilJn = this.criarJournal(perfil, DominioOperacoesJournal.INS);
			perfilJnDAO.persistir(perfilJn);
			
		} else { // alteracao
			if (isPerfilExistente(perfil.getId(), perfil.getNome())) {
				throw new ApplicationBusinessException(
						PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			}

			Perfil perfilOriginal = perfilDAO.obterOriginal(perfil);
			
			if(perfilOriginal == null){
	    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);  
			}
			
			PerfilJn perfilJn = null;
			boolean alterado = this.alterado(perfil, perfilOriginal);
			if(alterado){
				perfilJn = this.criarJournal(perfilOriginal, DominioOperacoesJournal.UPD);
			}
						
			perfilDAO.merge(perfil);
			
			if(alterado){
				perfilJnDAO.persistir(perfilJn);
				perfilJnDAO.flush();
			}
		}
	}

	public List<Perfil> pesquisarPerfis(String nome){
		return getPerfilDAO().pesquisarPerfis(nome);
	}

	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws ApplicationBusinessException {
		return getPerfilDAO().pesquisarPerfisSuggestionBox(nome);
	}
	
	
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws ApplicationBusinessException {
		if (perfis == null || perfis.isEmpty()) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		return getPerfilDAO().pesquisarPerfis(perfis);
	}

	

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarPerfisCount(String nome) {
		return getPerfilDAO().pesquisarPerfisCount(nome, null);
	}
	
	
	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarPerfisCount(String nome, String descricao) {	
		return getPerfilDAO().pesquisarPerfisCount(nome, descricao);
	}	
	
	/**
	 * Busca o histórico do cadastro do perfil
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtroPerfilJnVO
	 * @return
	 */
	public List<PerfilJn> pesquisarHistoricoPorPerfil(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfilJnVO filtroPerfilJnVO) {
		if(orderProperty == null){
			orderProperty = PerfilJn.Fields.SEQ_JN.toString();
			asc = false;
		}
		return getPerfilJnDAO().pesquisarPorPerfil(firstResult, maxResult,
				orderProperty, asc, filtroPerfilJnVO);
	}

	/**
	 * Quantidade de registros no histórico do cadastro do perfil
	 * 
	 * @param filtroPerfilJnVO
	 * @return
	 */
	public Long pesquisarHistoricoPorPerfilCount(FiltroPerfilJnVO filtroPerfilJnVO) {
		return getPerfilJnDAO().pesquisarPorPerfilCount(filtroPerfilJnVO);
	}

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 */
	public Long pesquisarPerfilCountSuggestionBox(String nome) {
		return getPerfilDAO().pesquisarPerfisCount(nome, null);
	}	
	
	public Perfil obterPerfil(Integer id) throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		Perfil perfil = getPerfilDAO().obterPorChavePrimaria(id);
		if (perfil == null) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO);
		}
		return perfil;

	}

	public void excluirPerfil(Integer idPerfil) throws ApplicationBusinessException {
		if (idPerfil == null) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		Perfil perfil = obterPerfil(idPerfil);

		if (perfil == null) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO);
		}

		try {
			Set<PerfisPermissoes> perfisPermissoeses = perfil.getPerfisPermissoeses();
			for (PerfisPermissoes perfisPermissoes : perfisPermissoeses) {
				perfisPermissoesDAO.remover(perfisPermissoes);
				perfisPermissoesDAO.flush();
			}
			// cria a journal
			PerfilJn perfilJn = this.criarJournal(perfil, DominioOperacoesJournal.DEL);
						
			getPerfilDAO().remover(perfil);
			getPerfilDAO().flush();
						
			// persiste a journal
			PerfilJnDAO perfilJnDAO = getPerfilJnDAO();
			perfilJnDAO.persistir(perfilJn);
			perfilJnDAO.flush();
			
		} catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						PerfilONExceptionCode.CASCA_VIOLACAO_FK_PERFIL,
						cve.getConstraintName());
			}
		}
	}

	public Set<String> obterNomePerfisPorUsuario(String username) {
		return new HashSet<String>(getPerfilDAO().obterNomePerfisPorUsuario(username));
	}
	
	public Long pesquisarPerfisAtivosDoUsuarioCount(Usuario usuario) {
		return getPerfisUsuariosDAO().pesquisarPerfisAtivosDoUsuarioCount(usuario);
	}

	public void associarPermissoesPerfil(Integer idPerfil,
			List<Permissao> listaPermissoes) throws ApplicationBusinessException {

		if (idPerfil == null||listaPermissoes==null) {
			throw new ApplicationBusinessException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		PerfilDAO perfilDAO = this.getPerfilDAO();

		Perfil perfil = perfilDAO.obterPorChavePrimaria(idPerfil);
		removerPermissoesPerfil(perfil);
		associaPermissaoPerfil(perfil, listaPermissoes);
		perfilDAO.flush();
	}

	/**
	 * 
	 * @param usuario
	 */
	private void removerPermissoesPerfil(Perfil perfil) {
		PerfisPermissoesDAO perfisPermissoesDAO = getPerfisPermissoesDAO();
		List<PerfisPermissoes> listaPermissoesPerfilEncontrados = 
				perfisPermissoesDAO.pesquisarPerfisPermissoes(perfil);
		perfil.getPerfisPermissoeses().removeAll(
				listaPermissoesPerfilEncontrados);
		for (PerfisPermissoes perfilPermissao : listaPermissoesPerfilEncontrados) {
			perfisPermissoesDAO.remover(perfilPermissao);
			perfisPermissoesDAO.flush();
		}
	}
	
	/**
	 * Verifica se algum campo que deve gerar journal foi alterado
	 * 
	 * @param perfil
	 * @param perfilOriginal
	 * @return
	 */
	private boolean alterado(Perfil perfil, Perfil perfilOriginal){
		if(perfil != null && perfilOriginal != null){
			if(CoreUtil.modificados(perfil.getNome(), perfilOriginal.getNome()) ||
				CoreUtil.modificados(perfil.getDescricao(), perfilOriginal.getDescricao()) ||
				CoreUtil.modificados(perfil.getDescricaoResumida(), perfilOriginal.getDescricaoResumida()) ||
				CoreUtil.modificados(perfil.getSituacao(), perfilOriginal.getSituacao())){
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Cria uma entidade journal da Perfil
	 * 
	 * @param perfil
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private PerfilJn criarJournal(Perfil perfil, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		PerfilJn perfilJn = BaseJournalFactory.getBaseJournal(operacao, PerfilJn.class, getServidorLogadoFacade().obterServidorLogado().getUsuario());
		perfilJn.setId(perfil.getId());
		perfilJn.setNome(perfil.getNome());
		perfilJn.setDescricao(perfil.getDescricao());
		perfilJn.setDescricaoResumida(perfil.getDescricaoResumida());
		perfilJn.setSituacao(perfil.getSituacao());
		perfilJn.setDelegavel(perfil.isDelegavel());
		return perfilJn;
	}

	/**
	 * 
	 * @param usuario
	 * @param listaPerfis
	 */
	private void associaPermissaoPerfil(Perfil perfil,List<Permissao> listaPermissoes) {
		if (listaPermissoes != null && !listaPermissoes.isEmpty()) {
			for (Permissao p : listaPermissoes){
				PerfisPermissoes perfisPermissoes = new PerfisPermissoes(null, p, perfil);
				perfisPermissoesDAO.persistir(perfisPermissoes);
			}
		}
	}

	/**
	 * 
	 * @param idPerfil
	 * @param nome
	 * @return
	 */
	private boolean isPerfilExistente(Integer idPerfil, String nome) {
		Perfil perfil = this.getPerfilDAO().pesquisarPerfil(nome);
		// validacao para edicao
		if (perfil != null && idPerfil != null) {
			if (perfil.getId().equals(idPerfil)) {
				return false;
			}
			return true;
		}
		return perfil == null ? false : true;
	}

	
	public void associarPermissoesPerfil(Perfil perfil,	List<Permissao> listaPermissoes, List<Permissao> listaExcluidas) throws ApplicationBusinessException {
		if (perfil == null || listaPermissoes==null) {
			throw new ApplicationBusinessException(	PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		List<PerfisPermissoes> listaPermissoesOriginal = perfisPermissoesDAO.pesquisarPerfisPermissoes(perfil);
		List<Permissao> listaPermissoesInseridas = new ArrayList<>(listaPermissoes);

		for (PerfisPermissoes perfilPermissao : listaPermissoesOriginal) {
			if (listaExcluidas.contains(perfilPermissao.getPermissao())){
				perfisPermissoesDAO.remover(perfilPermissao);
			}else {
				listaPermissoesInseridas.remove(perfilPermissao.getPermissao());
			}
		}
		perfisPermissoesDAO.flush();
		associaPermissaoPerfil(perfil, listaPermissoesInseridas);
	}	
	
	protected PerfilDAO getPerfilDAO() {
		return perfilDAO;
	}
	
	protected PerfilJnDAO getPerfilJnDAO() {
		return perfilJnDAO;
	}
	
	protected PerfisPermissoesDAO getPerfisPermissoesDAO() {
		return perfisPermissoesDAO;
	}
	
	protected PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}


	
}
