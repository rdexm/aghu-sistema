package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.PalavraChaveMenu;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PalavraChaveMenuDAO extends BaseDao<PalavraChaveMenu> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2284444486323672284L;

	
	public List<PalavraChaveMenu> listarPalavrasChave(Integer menu) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PalavraChaveMenu.class);
		criteria.add(Restrictions.eq(PalavraChaveMenu.Fields.MENU.toString() + "." + Menu.Fields.ID.toString(), menu));
		
		criteria.addOrder(Order.asc(PalavraChaveMenu.Fields.PALAVRA.toString()));

		return executeCriteria(criteria);
	}

}
