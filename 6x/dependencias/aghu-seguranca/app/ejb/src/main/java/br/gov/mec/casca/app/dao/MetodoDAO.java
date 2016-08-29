/**
 * 
 */
package br.gov.mec.casca.app.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.seam.dao.GenericDAO;

/**
 * @author rafael
 *
 */
public class MetodoDAO extends GenericDAO<Metodo>{

	
	/**
	 * @param nome
	 * @return
	 */
	public List<Metodo> pesquisarMetodoPorNome(Object nome, Componente componente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		String _nome = "" + nome;
		if (StringUtils.isNotBlank(_nome)) {
			criteria.add(Restrictions.like(Metodo.Fields.NOME.toString(),
					_nome, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(Metodo.Fields.NOME.toString()));
		if (componente!=null) {
			criteria.add(Restrictions.eq(Metodo.Fields.COMPONENTE.toString(), componente.getId()));
		}

		return executeCriteria(criteria);
	}
}
