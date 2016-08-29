package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrpMicroLacuna;

public class AelGrpMicroLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpMicroLacuna>  {
	
	private static final long serialVersionUID = 4938535243090072088L;

	public Short obterProximaSequence(Short luvLuuSeq, Short luvSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpMicroLacuna.class);		
		
		criteria.add(Restrictions.eq(AelGrpMicroLacuna.Fields.LUV_LUU_SEQ.toString(), luvLuuSeq));
		criteria.add(Restrictions.eq(AelGrpMicroLacuna.Fields.LUV_SEQP.toString(), luvSeqp));
		
		criteria.setProjection(Projections.max(AelGrpMicroLacuna.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}

		return ++seq;
		
	}
	
	public List<AelGrpMicroLacuna> pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(final Short aelTextoPadraoMicroLuuSeq, final Short aelTextoPadraoMicroSeqp) {

		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelGrpMicroLacuna.class);

		if (aelTextoPadraoMicroLuuSeq != null) {
			criteria.add(Restrictions.eq(AelGrpMicroLacuna.Fields.AEL_TEXTO_PADRAO_MICROS_LUU_SEQ.toString(), aelTextoPadraoMicroLuuSeq));
		}
		
		if (aelTextoPadraoMicroSeqp != null) {
			criteria.add(Restrictions.eq(AelGrpMicroLacuna.Fields.AEL_TEXTO_PADRAO_MICROS_SEQP.toString(), aelTextoPadraoMicroSeqp));
		}
		
		criteria.addOrder(Order.asc(AelGrpMicroLacuna.Fields.LACUNA.toString()));
		
		return executeCriteria(criteria);
	}
}