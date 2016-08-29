package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.Dashboard;
import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class DashboardDAO extends BaseDao<Dashboard> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8937312038767377766L;

	public List<Dashboard> obterDashBoard(String login) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Dashboard.class);

		criteria.createAlias(Dashboard.Fields.FAVORITO.toString(), "favorito", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(Dashboard.Fields.USUARIO.toString(), "usuario");
		criteria.createAlias("favorito." + Favorito.Fields.MENU.toString(), "menu", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq("menu." + Menu.Fields.ATIVO.toString(), true), Restrictions.isNull("menu." + Menu.Fields.ATIVO.toString())));
		criteria.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), login).ignoreCase());

		criteria.addOrder(Order.asc(Dashboard.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(Dashboard.Fields.DATA_CRIACAO.toString()));
		
		return executeCriteria(criteria);
	}

	public Dashboard obterDashboardPeloMenuOuModal(String login, Integer menuId, String modal)  {

		DetachedCriteria criteria = DetachedCriteria.forClass(Dashboard.class);

		criteria.createAlias(Dashboard.Fields.FAVORITO.toString(), "favorito", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("favorito." + Favorito.Fields.MENU.toString(), "menu", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(Dashboard.Fields.USUARIO.toString(), "usuario");

		if(menuId != null) {
			criteria.add(Restrictions.eq("menu." + Menu.Fields.ID.toString(), menuId));
		} else if(!StringUtils.isEmpty(modal)) {
			criteria.add(Restrictions.like(Dashboard.Fields.MODAL.toString(), modal, MatchMode.START));	
		}
		criteria.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		
		return (Dashboard) executeCriteriaUniqueResult(criteria);

	}		

}
