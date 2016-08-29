package br.gov.mec.aghu.casca.business;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.MenuDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author kamila.nogueira
 * 
 */
@Stateless
public class MenucrudON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(MenucrudON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MenuDAO menuDAO;

	private static final long serialVersionUID = 620765035476200689L;

	/**
	 * Metodo responsavel por incluir ou alterar um menu.
	 * 
	 * @param menu
	 */

	protected enum MenucrudONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_MENU_NAO_INFORMADO, CASCA_MENSAGEM_DELETAR_FILHOS_DO_MENU, CASCA_MENSAGEM_URL_INVALIDA, CASCA_MENSAGEM_MENU_EXISTENTE, 
		CASCA_MENSAGEM_MENU_NAO_ENCONTRADO, CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, CASCA_MENU_VIOLACAO_UK1, CASCA_MENU_VIOLACAO_UK2;
	}

	public void salvarMenu(Menu menu) throws ApplicationBusinessException {

		if (menu == null) {
			throw new ApplicationBusinessException(
					MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_NAO_INFORMADO);
		}

		if (menu.getUrl() != null) {
			// Avalia se a URL é do tipo .xhtml|html (para o caso do Seam) ou 
			// se é do .fmx (Oracle Web Forms)
			Pattern patternInterno = Pattern
			        .compile("/[0-9a-zA-Z/\\-]*\\.(fmx|x?html)");
			Matcher matcherInterno = patternInterno.matcher(menu.getUrl());

			Pattern patternExterno = Pattern
					.compile("http://[0-9a-zA-Z\\-\\.]+(:[0-9]{2,5})?[0-9a-zA-Z/\\-\\.]*");
			Matcher matcherExterno = patternExterno.matcher(menu.getUrl());

			if (!matcherExterno.matches() && !matcherInterno.matches()) {
				throw new ApplicationBusinessException(MenucrudONExceptionCode.CASCA_MENSAGEM_URL_INVALIDA);
			}
		}

		if (validaCadastroMenu(menu.getNome(), menu.getAplicacao(), menu.getMenuPai(), menu.getUrl(), menu.getId())) {
			throw new ApplicationBusinessException(MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_EXISTENTE);
		}
		boolean inclusao = (menu.getId() == null);
		try{
			if (inclusao) { // inclusao
				menu.setDataCriacao(new Date());
				getMenuDAO().persistir(menu);
				getMenuDAO().flush();
			} else { // alteracao
				getMenuDAO().atualizar(menu);
				getMenuDAO().flush();
			}
		}catch(PersistenceException e){
			if(inclusao){
				menu.setId(null);
			}
			
			if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "CASCA.CSC_MENU_UK1")) {
				throw new ApplicationBusinessException(MenucrudONExceptionCode.CASCA_MENU_VIOLACAO_UK1);
			}
			
			if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "CASCA.CSC_MENU_UK2")) {
				throw new ApplicationBusinessException(MenucrudONExceptionCode.CASCA_MENU_VIOLACAO_UK2);
			}
		}
	}

	/**Método que retorna instancia de Events
	 * [para facilitar os testes unitários]
	 * @return
	 *
	public Events instanceEvents() {
		return Events.instance();
	}*/

	/**
	 * Verifica existencia de menu na hierarquia com mesmo nome ou mesma URL por aplicação.
	 * 
	 * @param nome
	 * @param aplicacao
	 * @param menuPai
	 * @param url
	 * @param id
	 * @return
	 */
	private boolean validaCadastroMenu(String nome, Aplicacao aplicacao, Menu menuPai, String url, Integer id) {
		List<Menu> pesquisarMenu = getMenuDAO().validaCadastroMenu(nome, aplicacao, menuPai, url, id);
		return !pesquisarMenu.isEmpty();
	}

	/**
	 * Metodo responsavel por pesquisar menus.
	 * 
	 * @param nomeMenu
	 * @return
	 */

	public List<Menu> pesquisarMenu(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc)  {

		return getMenuDAO().pesquisarMenu(nomeMenu, urlMenu, idAplicacao, idMenuPai,
				firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarMenucrudCount(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai) {
		return this.getMenuDAO().pesquisarMenucrudCount(nomeMenu, urlMenu,
				idAplicacao, idMenuPai);
	}

	public List<Menu> pesquisarMenuPorNomeEId(String strPesquisa) {
		
		if (StringUtils.isNotBlank(strPesquisa) && StringUtils.isNumeric(strPesquisa)) {
			Integer id = Integer.valueOf(strPesquisa);
			List<Menu> list = this.getMenuDAO().pesquisar(id);
			if (list.size() > 0) {
				return list;
			}
		}

		return this.getMenuDAO().pesquisar(strPesquisa);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */

	public Menu obterMenu(Integer id) throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(
					MenucrudONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		Menu menu = this.getMenuDAO().obterPorChavePrimaria(id, Menu.Fields.APLICACAO);
		if (menu == null) {
			throw new ApplicationBusinessException(
					MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_NAO_ENCONTRADO);
		}
		return menu;

	}

	/**
	 * Metodo responsavel por excluir um menu.
	 * 
	 * @param id
	 */

	public void deletarMenu(Integer id) throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(
					MenucrudONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		Menu menu = obterMenu(id);
		if (!menu.getMenus().isEmpty()) {
			throw new ApplicationBusinessException(
					MenucrudONExceptionCode.CASCA_MENSAGEM_DELETAR_FILHOS_DO_MENU);
		}
		this.getMenuDAO().remover(menu);
		this.getMenuDAO().flush();
	}

	protected MenuDAO getMenuDAO() {
		return menuDAO;
	}

}
