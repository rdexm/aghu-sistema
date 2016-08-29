package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAps;

public class AelTopografiaApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaAps> {

	
	private static final long serialVersionUID = 3846161295734091849L;

	public List<AelTopografiaAps> listarTopografiaApPorLuxSeq(Long luxSeq) {
		
			final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaAps.class);

			criteria.createAlias(AelTopografiaAps.Fields.AEL_TOPOGRAFIA_APARELHOS.toString(), "ata");
			
			if(luxSeq != null){
				criteria.add(Restrictions.eq(AelTopografiaAps.Fields.LUX_SEQ.toString(), luxSeq));
			}
			
			criteria.addOrder(Order.asc("ata." + AelTopografiaAparelhos.Fields.DESCRICAO.toString()));
			
			return executeCriteria(criteria);
	}
	
}
