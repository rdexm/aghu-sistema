package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptCaracteristicaJn;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptCaracteristicaJnDAO extends BaseDao<MptCaracteristicaJn>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 546484514L;

	/**C7 Recuperar alterações da caracteristica selecionada  #46468 **/
	public List<MptCaracteristicaJn> pesquisarAlteracoesCaracteristica(Short carSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaJn.class);
		criteria.add(Restrictions.eq(MptCaracteristicaJn.Fields.SEQ.toString(), carSeq));
		criteria.addOrder(Order.desc(MptCaracteristicaJn.Fields.JN_DATE_TIME.toString()));
		return executeCriteria(criteria);
	}

}
