package br.gov.mec.casca.app.business;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.dao.AplicacaoDAO;
import br.gov.mec.casca.app.menu.vo.AplicacaoVO;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

class AplicacaoON extends SeamContextsManager {

	public enum AplicacaoONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_APLICACAO_NAO_INFORMADA, CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA, 
		CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_VIOLACAO_FK_APLICACAO, 
		CASCA_MENSAGEM_APLICACAO_SEM_CONTEXTO, CASCA_MENSAGEM_APLICACAO_SEM_SERVIDOR, 
		CASCA_MENSAGEM_APLICACAO_SEM_PORTA,CASCA_MENSAGEM_APLICACAO_SEM_NOME; 
	}

	/**
	 * 
	 * @param nome
	 * @return
	 */
	public List<Aplicacao> pesquisarAplicacaoPorNome(Object nome) {
		AplicacaoDAO aplicacaoDAO = criaAplicacaoDAO();
		
		List<Aplicacao> aplicacoes = null;
		if (nome != null && !"".equals(nome)) {
			String paramPesquisa = (String) nome;
			if (StringUtils.isNumeric(paramPesquisa)) {
				Integer id = Integer.valueOf(paramPesquisa);
				Aplicacao app = aplicacaoDAO.obterPorChavePrimaria(id);
				aplicacoes = new LinkedList<Aplicacao>();
				if (app != null) {
					aplicacoes.add(app);
				}
			} else {
				aplicacoes = aplicacaoDAO.pesquisarAplicacaoPorNome(paramPesquisa); 
			}
		} else {
			aplicacoes = aplicacaoDAO.recuperarAplicacoes();
		}
		
		return aplicacoes;
	}

	/**
	 * 
	 * @param servidor
	 * @param porta
	 * @param contexto
	 * @param nome
	 * @param externo
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		AplicacaoDAO aplicacaoDAO = criaAplicacaoDAO();
		return aplicacaoDAO.pesquisarAplicacoes(servidor, porta, contexto,
				nome, externo, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * @author gandriotti
	 * 
	 * @return
	 */
	public List<Aplicacao> pesquisarAplicacoes() {
		
		List<Aplicacao> result = null;
		AplicacaoDAO dao = null;
		
		dao = this.criaAplicacaoDAO();
		result = dao.recuperarAplicacoes();
		
		return result;
	}

	/**
	 * 
	 * @param aplicacao
	 * @throws CascaException
	 */
	public void salvar(Aplicacao aplicacao) throws CascaException {

		if (aplicacao == null) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_NAO_INFORMADA);
		}

		validarAplicacao(aplicacao);
		AplicacaoDAO aplicacaoDAO = criaAplicacaoDAO();

		if (aplicacao.getId() == null) {
			List<Aplicacao> lista = null;
			if (aplicacao.getExterno()) {
				lista = aplicacaoDAO.pesquisarAplicacoes(
						aplicacao.getServidor(), aplicacao.getPorta(),
						aplicacao.getContexto(), null, null);
			} else {
				// aplicacoes internas nao tem servidor, porta ou contexto
				lista = aplicacaoDAO.pesquisarAplicacoes(null, null, null,
						aplicacao.getNome(), aplicacao.getExterno());
			}

			if (!lista.isEmpty()) {
				throw new CascaException(
						AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA);
			}
			aplicacaoDAO.inserir(aplicacao);
		} else {
			aplicacaoDAO.atualizar(aplicacao);
		}
	}

	private void validarAplicacao(Aplicacao aplicacao)
			throws CascaException {
		if (StringUtils.isEmpty(aplicacao.getNome())) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_NOME);
		}
		
		if (!aplicacao.getExterno()) {
			return;
		}

		if (StringUtils.isEmpty(aplicacao.getContexto())) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_CONTEXTO);
		}

		if (StringUtils.isEmpty(aplicacao.getServidor())) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_SERVIDOR);
		}

		if (aplicacao.getPorta() == null) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_PORTA);
		}

	}

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws CascaException
	 */
	public Aplicacao obterAplicacao(Integer idAplicacao)
			throws CascaException {
		if (idAplicacao == null) {
			throw new CascaException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		AplicacaoDAO aplicacaoDAO = criaAplicacaoDAO();
		return aplicacaoDAO.obterPorChavePrimaria(idAplicacao);
	}

	/**
	 * 
	 * @param servidor
	 * @param porta
	 * @param contexto
	 * @param nome
	 * @param externo
	 * @return
	 */
	public Integer pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, DominioSimNao externo) {

		return criaAplicacaoDAO().pesquisarAplicacoesCount(servidor, porta,
				contexto, nome, externo);
	}

	/**
	 * @return
	 */
	protected AplicacaoDAO criaAplicacaoDAO() {
		return new AplicacaoDAO();
	}

	
	/**
	 * 
	 * @param idAplicacao
	 * @throws CascaException
	 */

	public void excluirAplicacao(Integer idAplicacao)
			throws CascaException {

		AplicacaoDAO aplicacaoDAO = criaAplicacaoDAO();
		Aplicacao aplicacao = obterAplicacao(idAplicacao);
		try {
			aplicacaoDAO.remover(aplicacao);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new CascaException(
						AplicacaoONExceptionCode.CASCA_VIOLACAO_FK_APLICACAO,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * Recupera todas as aplicações que o usuário tem acesso.
	 * 
	 * @return Um List de objetos AplicacaoVO
	 */
	public List<AplicacaoVO> recuperarAplicacoes(String login) {
		AplicacaoBar aplicacaoBar = (AplicacaoBar) Component.getInstance(AplicacaoBar.class);
		List<Aplicacao> aplicacaoes = aplicacaoBar.carregarAplicacoes();
		List<AplicacaoVO> vos = new ArrayList<AplicacaoVO>();
		montarAplicacaoVOs(getIdentity(), vos, aplicacaoes, aplicacaoBar);

		return vos;
	}

	/**
	 * Monta os vos de aplicação levando em consideração se o usuário tem acesso a aplicação.
	 * 
	 * @param identity
	 * @param vos
	 * @param aplicacoes
	 */
	private void montarAplicacaoVOs(Identity identity, List<AplicacaoVO> vos, List<Aplicacao> aplicacoes, AplicacaoBar aplicacaoBar) {
		try {
			for (Aplicacao aplicacao : aplicacoes) {
				if (identity.hasPermission(montaTarget(aplicacao), "acessar")) {
					AplicacaoVO vo = criarAplicacaoVO(aplicacao);
					vos.add(vo);
					aplicacaoBar.getVos().put(vo.getId(), vo);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao inicializar a lista de aplicações", e);
		}
	}

	/**
	 * Monta a string com o nome aplicacao em "caixa baixa" uppercase : "caixa baixa" lowercase.
	 * Esse é o padrão para montar o target de validação de acesso da aplicação. 
	 * 
	 * @param aplicacao
	 * @return
	 */
	private String montaTarget(Aplicacao aplicacao) {
		return aplicacao.getNome().toUpperCase() + ":" + aplicacao.getNome().toLowerCase();
	}
	
	/**
	 * Cria o vo com os dados da aplicação que o usuário tem acesso.
	 * 
	 * @param aplicacao
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private AplicacaoVO criarAplicacaoVO(Aplicacao aplicacao) throws UnsupportedEncodingException {
		AplicacaoVO vo = new AplicacaoVO();
		vo.setId(aplicacao.getId());
		vo.setNome(aplicacao.getNome());
		vo.setDescricao(aplicacao.getDescricao());
		String urlInicial = aplicacao.getUrlInicial();
		if (urlInicial == null) {
			urlInicial = "/home.xhtml";
		}
		String urlTemp = URLConfig.montarURL(urlInicial, aplicacao);
		urlTemp += "?cascaSessionId=" + ServletContexts.instance().getRequest().getSession().getId();
		vo.setUrlAcesso(urlTemp);

		return vo;
	}
	
	/**
	 * Retorna a instância do identitystore da aplicação.
	 * 
	 * @return
	 */
	private Identity getIdentity() {
		return (Identity) Component.getInstance(Identity.class);
	}
}