package br.gov.mec.casca.app.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.dao.PerfilDAO;
import br.gov.mec.casca.app.dao.PerfisPermissoesDAO;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

// FIXME Arrumar comentarios dos metodos
class PerfilON extends SeamContextsManager {

	protected enum PerfilONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_PERFIL_NAO_INFORMADO, CASCA_MENSAGEM_PERFIL_EXISTENTE, CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO, CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, CASCA_VIOLACAO_FK_PERFIL;
	}

	public void salvarPerfil(Perfil perfil) throws CascaException {
		if (perfil == null) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		if (perfil.getId() == null) { // inclusao
			if (isPerfilExistente(null, perfil.getNome())) {
				throw new CascaException(
						PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			}
			perfil.setDataCriacao(new Date());
			getPerfilDAO().inserir(perfil);
		} else { // alteracao
			if (isPerfilExistente(perfil.getId(), perfil.getNome())) {
				throw new CascaException(
						PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			}
			getPerfilDAO().atualizar(perfil);
		}
	}

	public List<Perfil> pesquisarPerfis(String nome)
			throws CascaException {
		return getPerfilDAO().pesquisarPerfis(nome);
	}

	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws CascaException {
		return getPerfilDAO().pesquisarPerfisSuggestionBox(nome);
	}
	
	
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws CascaException {
		if (perfis == null || perfis.isEmpty()) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		return getPerfilDAO().pesquisarPerfis(perfis);
	}

	public List<Perfil> pesquisarPerfis(String nome, Integer firstResult,
			Integer maxResult, boolean asc) throws CascaException {
		return getPerfilDAO().pesquisarPerfis(nome, firstResult, maxResult, asc);
	}

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws CascaException
	 */
	public Integer pesquisarPerfisCount(String nome) {
		return getPerfilDAO().pesquisarPerfisCount(nome);
	}

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws AGHUNegocioException
	 */
	public Integer pesquisarPerfilCountSuggestionBox(String nome) {
		return getPerfilDAO().pesquisarPerfisCount(nome);
	}	
	
	public Perfil obterPerfil(Integer id) throws CascaException {
		if (id == null) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		Perfil perfil = getPerfilDAO().obterPorChavePrimaria(id);
		if (perfil == null) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO);
		}
		return perfil;

	}

	public void excluirPerfil(Integer idPerfil) throws CascaException {
		if (idPerfil == null) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		Perfil perfil = obterPerfil(idPerfil);

		if (perfil == null) {
			throw new CascaException(
					PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO);
		}

		try {
			Set<PerfisPermissoes> perfisPermissoeses = perfil.getPerfisPermissoeses();
			PerfisPermissoesDAO perfisPermissoesDAO = new PerfisPermissoesDAO();
			for (PerfisPermissoes perfisPermissoes : perfisPermissoeses) {
				perfisPermissoesDAO.remover(perfisPermissoes);
			}
			getPerfilDAO().remover(perfil);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new CascaException(
						PerfilONExceptionCode.CASCA_VIOLACAO_FK_PERFIL,
						cve.getConstraintName());
			}
		}
	}

	public List<String> obterNomePerfisPorUsuario(String username) {
		return this.getPerfilDAO().obterNomePerfisPorUsuario(username);
	}

	public void associarPermissoesPerfil(Integer idPerfil,
			List<Permissao> listaPermissoes) throws CascaException {

		if (idPerfil == null||listaPermissoes==null) {
			throw new CascaException(
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
		List<PerfisPermissoes> listaPermissoesPerfilEncontrados = this
				.getPerfilDAO().pesquisarPerfisPermissoes(perfil);
		perfil.getPerfisPermissoeses().removeAll(
				listaPermissoesPerfilEncontrados);
		for (PerfisPermissoes perfilPermissao : listaPermissoesPerfilEncontrados) {
			getPerfilDAO().remover(perfilPermissao);
		}
	}

	/**
	 * 
	 * @param usuario
	 * @param listaPerfis
	 */
	private void associaPermissaoPerfil(Perfil perfil,
			List<Permissao> listaPermissoes) {
		if (listaPermissoes != null && !listaPermissoes.isEmpty()) {
			for (Iterator<Permissao> iterator = listaPermissoes.iterator(); iterator
					.hasNext();) {
				Permissao permissao = (Permissao) iterator.next();
				this.getPerfilDAO().salvarPerfisPermissoes(permissao, perfil);
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
		Perfil perfil = this.getPerfilDAO().pesquisrPerfil(nome);
		// validacao para edicao
		if (perfil != null && idPerfil != null) {
			if (perfil.getId().equals(idPerfil)) {
				return false;
			}
			return true;
		}
		return perfil == null ? false : true;
	}

	protected PerfilDAO getPerfilDAO() {
		return new PerfilDAO();
	}

}
