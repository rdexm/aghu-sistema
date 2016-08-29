package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;

public class AelKitItemIndiceBlocoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelKitItemIndiceBloco> {
	
	private static final long serialVersionUID = 5167856750174523356L;

	public List<AelKitItemIndiceBloco> pesquisarAelKitItemIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco){
		final DetachedCriteria criteria = obterCriteria(aelKitIndiceBloco);
		criteria.addOrder(Order.asc(AelKitItemIndiceBloco.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	private DetachedCriteria obterCriteria(final AelKitIndiceBloco aelKitIndiceBloco) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelKitItemIndiceBloco.class);
		
		if(aelKitIndiceBloco != null){
			criteria.add(Restrictions.eq(AelKitItemIndiceBloco.Fields.AEL_KIT_INDICE_BLOCOS.toString(), aelKitIndiceBloco));	
		}
		
		return criteria;
	}

	public Short obterMaxSeqPPorAelKitIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco) {
		final DetachedCriteria criteria = obterCriteria(aelKitIndiceBloco);
		criteria.setProjection(Projections.max(AelKitItemIndiceBloco.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}