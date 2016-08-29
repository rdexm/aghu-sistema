/**
 * 
 */
package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * @author rafael
 *
 */
public class MetodoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Metodo>{

	
	private static final long serialVersionUID = -5969129497556724428L;

	/**
	 * @param nome
	 * @return
	 */
	public List<Metodo> pesquisarMetodoPorNome(Object nome, Componente componente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		String _nome = nome.toString();
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
	
	
	public void excluirMetodo(Metodo metodo) throws ApplicationBusinessException {
		remover(metodo);
	}

	public void salvarMetodo(Metodo metodo) {
		this.persistir(metodo);
	}

	public void atualizarMetodo(Metodo metodo) {
		this.atualizar(metodo);
	}
}
