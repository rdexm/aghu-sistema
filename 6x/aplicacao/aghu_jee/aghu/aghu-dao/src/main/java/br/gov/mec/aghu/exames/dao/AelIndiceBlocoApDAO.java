package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelIndiceBlocoAp;

public class AelIndiceBlocoApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelIndiceBlocoAp> {

	private static final long serialVersionUID = 3062945896166777055L;

	public List<AelIndiceBlocoAp> listarAelIndiceBlocoApPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelIndiceBlocoAp.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelIndiceBlocoAp.Fields.LUX_SEQ.toString(), luxSeq));
		}
		
		criteria.addOrder(Order.asc(AelIndiceBlocoAp.Fields.INDICE_DE_BLOCO.toString()));
		
		return executeCriteria(criteria);
	}
	
}