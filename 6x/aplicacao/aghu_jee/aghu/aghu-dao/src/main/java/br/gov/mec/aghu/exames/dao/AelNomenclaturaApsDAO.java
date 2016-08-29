package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;

public class AelNomenclaturaApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNomenclaturaAps> {


	private static final long serialVersionUID = 8207011424635775114L;

	public List<AelNomenclaturaAps> listarNomenclaturaApPorLuxSeq(Long luxSeq) {
		
			final DetachedCriteria criteria = DetachedCriteria.forClass(AelNomenclaturaAps.class);

			criteria.createAlias(AelNomenclaturaAps.Fields.AEL_NOMENCLATURA_ESPECS.toString(), AelNomenclaturaAps.Fields.AEL_NOMENCLATURA_ESPECS.toString());
			
			if(luxSeq != null){
				criteria.add(Restrictions.eq(AelNomenclaturaAps.Fields.LUX_SEQ.toString(), luxSeq));
			}
			
			criteria.addOrder(Order.asc(AelNomenclaturaAps.Fields.AEL_NOMENCLATURA_ESPECS.toString() + "." + AelNomenclaturaEspecs.Fields.DESCRICAO.toString()));
			
			return executeCriteria(criteria);
	}
	
}
