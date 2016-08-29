package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoSiasgServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSiasgServico>{

	private static final long serialVersionUID = 4552847537477127759L;

	public List<ScoSiasgServico> pesquisarCatSer(Object objCatSer) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSiasgServico.class);
		
		if(!objCatSer.toString().isEmpty()){
			if (CoreUtil.isNumeroInteger(objCatSer)){
				criteria.add(Restrictions.eq(ScoSiasgServico.Fields.IT_CO_SERVICO.toString(), Integer.valueOf(objCatSer.toString())));
			} else {
				criteria.add(Restrictions.ilike(ScoSiasgServico.Fields.IT_NO_SERVICO.toString(), objCatSer.toString(), MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.isNull(ScoSiasgServico.Fields.IT_IN_STATUS.toString()));
		criteria.addOrder(Order.asc(ScoSiasgServico.Fields.IT_NO_SERVICO.toString()));
		
		return executeCriteria(criteria);
	}
}
