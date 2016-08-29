package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitMatPatologia;

public class AelKitItemMatPatologiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelKitItemMatPatologia> {
	
	private static final long serialVersionUID = -4329927261863329052L;

	public List<AelKitItemMatPatologia> pesquisarAelKitItemMatPatologia(final AelKitMatPatologia aelKitMatPatologia){
		final DetachedCriteria criteria = obterCriteria(aelKitMatPatologia);
		criteria.addOrder(Order.asc(AelKitItemMatPatologia.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	private DetachedCriteria obterCriteria(final AelKitMatPatologia aelKitMatPatologia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelKitItemMatPatologia.class);
		
		if(aelKitMatPatologia != null){
			criteria.add(Restrictions.eq(AelKitItemMatPatologia.Fields.AEL_KIT_MAT_PATOLOGIAS.toString(), aelKitMatPatologia));	
		}
		
		return criteria;
	}

	public Short obterMaxSeqPPorAelKitMatPatologia(final AelKitMatPatologia aelKitMatPatologia) {
		final DetachedCriteria criteria = obterCriteria(aelKitMatPatologia);
		criteria.setProjection(Projections.max(AelKitItemMatPatologia.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}