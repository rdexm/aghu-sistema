package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtAchado;

public class PdtAchadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtAchado> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2762190458864965508L;

	public List<PdtAchado> pesquisarPdtAchados(Integer dgrDptSeq, Short dgrSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtAchado.class);
		
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_DPT_SEQ.toString(), dgrDptSeq));
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_SEQP.toString(), dgrSeqp));
		
		criteria.addOrder(Order.asc(PdtAchado.Fields.ID_SEQP.toString()));
		criteria.addOrder(Order.asc(PdtAchado.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);	
	}

	public PdtAchado obterPdtAchadoPorDescricao(String descricao,  Integer dgrDptSeq, Short dgrSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtAchado.class);
		criteria.add(Restrictions.eq(PdtAchado.Fields.DESCRICAO.toString(), descricao));
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_DPT_SEQ.toString(), dgrDptSeq));
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_SEQP.toString(), dgrSeqp));
		return (PdtAchado)executeCriteriaUniqueResult(criteria);	
	}

	public Short nextSeqpPdtAchado(Integer dgrDptSeq, Short dgrSeqp) {
		Short maxSeqp = this.obterSeqpMax(dgrDptSeq, dgrSeqp );
		
		if (maxSeqp != null) {
			maxSeqp++;
		}else{
			maxSeqp = Short.valueOf("1");
		}
		return maxSeqp;
	}


	private Short obterSeqpMax(Integer dgrDptSeq, Short dgrSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtAchado.class);
		
		criteria.setProjection(Projections.max(PdtAchado.Fields.ID_SEQP.toString()));  
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_DPT_SEQ.toString(), dgrDptSeq));
		criteria.add(Restrictions.eq(PdtAchado.Fields.ID_DGR_SEQP.toString(), dgrSeqp));
		
		return (Short)this.executeCriteriaUniqueResult(criteria);
	}
	
}
