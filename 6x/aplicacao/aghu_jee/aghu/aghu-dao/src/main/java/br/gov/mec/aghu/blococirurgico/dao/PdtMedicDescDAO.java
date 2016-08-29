package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtMedicDesc;

public class PdtMedicDescDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtMedicDesc> {

	private static final long serialVersionUID = -7854896542293911888L;

	public List<PdtMedicDesc> pesquisarMedicDescPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtMedicDesc.class);
		criteria.add(Restrictions.eq(PdtMedicDesc.Fields.ID_DDT_SEQ.toString(), seq));
		criteria.addOrder(Order.asc(PdtMedicDesc.Fields.PRE_TRANS.toString())); 
		criteria.addOrder(Order.asc(PdtMedicDesc.Fields.ID_SEQP.toString())); 
		return executeCriteria(criteria);
	}
	

	public List<PdtMedicDesc> pesquisarMedicDescPorDdtSeqOrdenadoPorDdtSeqESeqp(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtMedicDesc.class);
		criteria.createAlias(PdtMedicDesc.Fields.AFA_MEDICAMENTO.toString(), "MED");
		criteria.add(Restrictions.eq(PdtMedicDesc.Fields.ID_DDT_SEQ.toString(), seq));
		criteria.addOrder(Order.asc(PdtMedicDesc.Fields.ID_SEQP.toString())); 
		return executeCriteria(criteria);
	}


	public Integer obterMaxSeqpPorDdtSeq(Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtMedicDesc.class);
		
		criteria.setProjection(Projections.max(PdtMedicDesc.Fields.ID_SEQP.toString()));
		criteria.add(Restrictions.eq(PdtMedicDesc.Fields.ID_DDT_SEQ.toString(), ddtSeq));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}


}
