package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoTabAdequacaoPesoDAO extends BaseDao<McoTabAdequacaoPeso> {
	private static final long serialVersionUID = 875525466722641212L;

	/**
	 * #27482 - C6
	 * @param igSemanas
	 * @return
	 */
	public McoTabAdequacaoPeso obterMcoTabAdequacaoPesoPorSemanasGestacionais(Short igSemanas) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoTabAdequacaoPeso.class);
		criteria.add(Restrictions.eq(McoTabAdequacaoPeso.Fields.IG_SEMANAS.toString(), igSemanas));
		return (McoTabAdequacaoPeso) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #27490 - C7
	 * @param igSemanas
	 * @return
	 */
	public Short obterPercentil3(Short igSemanas) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoTabAdequacaoPeso.class);
		criteria.add(Restrictions.eq(McoTabAdequacaoPeso.Fields.IG_SEMANAS.toString(), igSemanas));
		criteria.setProjection(Projections.property(McoTabAdequacaoPeso.Fields.PERCENTIL3.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #27490 - C10
	 * @return
	 */
	public List<McoTabAdequacaoPeso> obterListaMcoTabAdequacaoPesoComPencentil3NotNull() {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoTabAdequacaoPeso.class);
		criteria.add(Restrictions.isNotNull(McoTabAdequacaoPeso.Fields.PERCENTIL3.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #27490 - C1
	 * @param igSemanas
	 * @return
	 */
	public List<McoTabAdequacaoPeso> pesquisarAdequacaoPesoIgSemanas(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short igSemanas) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoTabAdequacaoPeso.class);
		if(igSemanas != null){
			criteria.add(Restrictions.eq(McoTabAdequacaoPeso.Fields.IG_SEMANAS.toString(), igSemanas));
		}
		criteria.addOrder(Order.asc(McoTabAdequacaoPeso.Fields.IG_SEMANAS.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarCapacidadesAdequacaoPesoIgSemanasCount(Short igSemanas) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoTabAdequacaoPeso.class);
		if(igSemanas != null){
			criteria.add(Restrictions.eq(McoTabAdequacaoPeso.Fields.IG_SEMANAS.toString(), igSemanas));
		}
		return executeCriteriaCount(criteria);
	}
	
}
