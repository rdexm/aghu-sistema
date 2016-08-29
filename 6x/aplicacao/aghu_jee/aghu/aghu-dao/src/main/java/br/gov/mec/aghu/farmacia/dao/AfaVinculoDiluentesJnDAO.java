package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.model.AfaVinculoDiluentesJn;

public class AfaVinculoDiluentesJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaVinculoDiluentesJn>{

	private static final long serialVersionUID = 2600553619495215372L;

	public Long pesquisarVinculoDiluentesJnCount(Integer codMedicamento) {
		DetachedCriteria criteria = pesquisarVinculoDiluentesJnCountPorMedicamento(codMedicamento);
		
		return executeCriteriaCount(criteria);
	}

	public List<AfaVinculoDiluentesJn> pesquisarVinculoDiluentesJn(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigoMedicamento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentesJn.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentesJn.Fields.MAT_CODIGO.toString(), codigoMedicamento));
		criteria.addOrder(Order.desc(BaseJournal.Fields.DATA_ALTERACAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public DetachedCriteria pesquisarVinculoDiluentesJnCountPorMedicamento(Integer codMedicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentesJn.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentesJn.Fields.MAT_CODIGO.toString(), codMedicamento));
		
		return criteria;
	}
	
}
