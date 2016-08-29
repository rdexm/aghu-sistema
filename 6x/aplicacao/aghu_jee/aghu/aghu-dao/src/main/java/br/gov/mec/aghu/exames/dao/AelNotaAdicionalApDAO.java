package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;

public class AelNotaAdicionalApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNotaAdicionalAp> {

	private static final long serialVersionUID = -7743250391990445864L;

	public List<AelNotaAdicionalAp> obterListaNotasAdicionaisPeloExameApSeq(Long luxSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelNotaAdicionalAp.class);

		criteria.add(Restrictions.eq(AelNotaAdicionalAp.Fields.AEL_EXAME_APS.toString() + "." + AelExameAp.Fields.SEQ.toString(), luxSeq));
		criteria.addOrder(Order.desc(AelNotaAdicionalAp.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
    }

}
