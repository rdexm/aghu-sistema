package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoTabBallardDAO extends BaseDao<McoTabBallard> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5608160471413668915L;
	
	public List<McoTabBallard> listarBallard(Short escore, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoTabBallard.class);
		
		if (escore != null) {
			criteria.add(Restrictions.eq(McoTabBallard.Fields.ESCORE_BALLARD.toString(), escore));
		}
		criteria.addOrder(Order.asc(McoTabBallard.Fields.IG_SEMANAS.toString()));
		
		return  executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}
	
	public Long listarBallardCount(Short escore) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoTabBallard.class);
		
		if (escore != null) {
			criteria.add(Restrictions.eq(McoTabBallard.Fields.ESCORE_BALLARD.toString(), escore));
		}
		//criteria.addOrder(Order.desc(McoSindrome.Fields.DESCRICAO.toString()));
		
		return  executeCriteriaCount(criteria);
	}

}
