package br.gov.mec.casca.app.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Favorito;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.seam.business.exception.MECBaseException;
import br.gov.mec.seam.dao.GenericDAO;

public class MenuDAO extends GenericDAO<Menu> {

	public List<Favorito> recuperaFavoritos(String login)
			throws MECBaseException {
		DetachedCriteria criteria = criarCriteriaFavoritos(login);
		criteria.createCriteria(Favorito.Fields.MENU.toString()).add(
				Restrictions.eq(Menu.Fields.ATIVO.toString(), DominioSimNao.S));

		return executeCriteria(criteria);
	}

	/**
	 * Realiza uma query para a verificação da existencia de menu na hierarquia com mesmo nome ou mesma URL por aplicação.
	 * 
	 * @param nome
	 * @param aplicacao
	 * @param menuPai
	 * @param url
	 * @param id
	 * @return
	 */
	public List<Menu> validaCadastroMenu(String nome, Aplicacao aplicacao, Menu menuPai, String url, Integer id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

		if (id != null) {
			criteria.add(Restrictions.ne(Menu.Fields.ID.toString(), id));
		}

		Criterion and = Restrictions.eq(Menu.Fields.NOME.toString(), nome);

		if (menuPai == null) {
			and = Restrictions.and(and,
					Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			and = Restrictions.and(and,
					Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}

		if (url != null) {
			criteria.add(Restrictions.or(and,
					Restrictions.eq(Menu.Fields.URL.toString(), url)));
		} else {
			criteria.add(and);
		}

		criteria.add(Restrictions.eq(Menu.Fields.APLICACAO.toString(),
				aplicacao));

		return executeCriteria(criteria);
	}

	public List<Menu> pesquisarMenu(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) throws CascaException {

		return executeCriteria(criteriaCode(nomeMenu, urlMenu, idAplicacao),
				firstResult, maxResult, orderProperty, asc);
	}

	public Integer pesquisarMenucrudCount(String nomeMenu, String urlMenu,
			Integer idAplicacao) {
		return executeCriteriaCount(criteriaCode(nomeMenu, urlMenu, idAplicacao));
	}

	private DetachedCriteria criteriaCode(String nomeMenu, String urlMenu,
			Integer idAplicacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

		if (StringUtils.isNotBlank(nomeMenu)) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(),
					nomeMenu, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(urlMenu)) {
			criteria.add(Restrictions.ilike(Menu.Fields.URL.toString(),
					urlMenu, MatchMode.ANYWHERE));
		}

		if (idAplicacao != null) {
			criteria.createCriteria(Menu.Fields.APLICACAO.toString())
					.add(Restrictions.eq(Aplicacao.Fields.ID.toString(),
							idAplicacao));
		}

		return criteria;
	}

	public List<Menu> pesquisar() {
		DetachedCriteria _criteria = DetachedCriteria.forClass(Menu.class);

		// FIXME WTF! Que %@#*$ é essa? Comparação de Nome e ID com Situação Ativo????
		_criteria.add(Restrictions.eq(Menu.Fields.NOME.toString(),
				DominioSituacao.A));

		_criteria.add(Restrictions.eq(Menu.Fields.ID.toString(),
				DominioSituacao.A));

		return executeCriteria(_criteria);
	}

	public List<Menu> pesquisar(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(Menu.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna a criteria de recuperação de favoritos
	 * 
	 * @param login
	 *            O username do usuário
	 * @return o DetachedCriteria para ser usado em outros métodos
	 * @throws MECBaseException
	 */
	private DetachedCriteria criarCriteriaFavoritos(String login)
			throws MECBaseException {

		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);
		criteria.setFetchMode("menu", FetchMode.JOIN);
		criteria.createCriteria(Favorito.Fields.USUARIO.toString()).add(
				Restrictions.eq(Usuario.Fields.LOGIN.toString(), login)
						.ignoreCase());

		criteria.addOrder(Order.asc(Favorito.Fields.ORDEM.toString()));

		return criteria;
	}
	
	public List<Menu> recuperarMenus() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

		criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		criteria.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),
				DominioSimNao.S));

		criteria.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));

		List<Menu> menus = executeCriteria(criteria);
		List<Menu> listMenus = new ArrayList<Menu>(menus);
		initializeMenu(listMenus);

		return menus;
	}

	private void initializeMenu(List<Menu> menus) {
		for (Menu menu : menus) {
			Hibernate.initialize(menu);
			Hibernate.initialize(menu.getAplicacao());
			Hibernate.initialize(menu.getMenus());
			initializeMenu(menu.getMenus());
		}
	}
}
