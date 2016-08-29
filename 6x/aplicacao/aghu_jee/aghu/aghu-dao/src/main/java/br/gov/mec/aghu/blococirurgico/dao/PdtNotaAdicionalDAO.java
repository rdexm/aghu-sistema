package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.RapServidores;

public class PdtNotaAdicionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtNotaAdicional> {

	private static final long serialVersionUID = -1795347128404228629L;

	public List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtNotaAdicional.class);
		criteria.add(Restrictions.eq(PdtNotaAdicional.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.addOrder(Order.asc(PdtNotaAdicional.Fields.SEQP.toString())); 
		return executeCriteria(criteria);
	}
	
	public PdtNotaAdicional obterPdtNotaAdicionalPorServidorEPdtDescricao(RapServidores servidor, final Integer ddtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtNotaAdicional.class);
		criteria.add(Restrictions.eq(PdtNotaAdicional.Fields.RAP_SERVIDORES.toString(), servidor));
		criteria.add(Restrictions.eq(PdtNotaAdicional.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.addOrder(Order.desc(PdtNotaAdicional.Fields.SEQP.toString()));

		final List<PdtNotaAdicional> result = executeCriteria(criteria);
		
		if(!result.isEmpty()){
			return result.get(0);
			
		} else {
			return null;
		}
	}

	public List<PdtNotaAdicional> pesquisarNotaAdicionalPorDdtSeqESeqpDesc(Integer seqPdtDescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtNotaAdicional.class);
		criteria.add(Restrictions.eq(PdtNotaAdicional.Fields.DDT_SEQ.toString(), seqPdtDescricao));
		criteria.addOrder(Order.desc(PdtNotaAdicional.Fields.SEQP.toString())); 
		return executeCriteria(criteria);
	}
	

	public Short obterMaxSeqpPdtNotaAdicional(final Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtNotaAdicional.class);
	
		criteria.setProjection(Projections.max(PdtNotaAdicional.Fields.SEQP.toString()));  
		criteria.add(Restrictions.eq(PdtNotaAdicional.Fields.DDT_SEQ.toString(), ddtSeq));
		
		final Short maxSeq = (Short)this.executeCriteriaUniqueResult(criteria);
		
		return maxSeq;
	}
}
