package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;


public class AelTxtMicroLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTxtMicroLacuna>  {
	
	private static final long serialVersionUID = -6698671980266789204L;

	public Short obterProximaSequence(Short lo3LufLubSeq, Short lo3LufSeqp, Short lo3Seqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTxtMicroLacuna.class);		
		
		criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_LUV_LUU_SEQ.toString(), lo3LufLubSeq));
		criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_LUV_SEQP.toString(), lo3LufSeqp));
		criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_SEQP.toString(), lo3Seqp));

		criteria.setProjection(Projections.max(AelTxtMicroLacuna.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}

		return ++seq;
		
	}
	
	public List<AelTxtMicroLacuna> pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) {
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTxtMicroLacuna.class);

		if (aelGrpMicroLacuna != null && aelGrpMicroLacuna.getId() != null) {
			criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_LUV_LUU_SEQ.toString(), aelGrpMicroLacuna.getId().getLuvLuuSeq()));
			criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_LUV_SEQP.toString(), aelGrpMicroLacuna.getId().getLuvSeqp()));
			criteria.add(Restrictions.eq(AelTxtMicroLacuna.Fields.LU9_SEQP.toString(), aelGrpMicroLacuna.getId().getSeqp()));
		}
		
		criteria.addOrder(Order.asc(AelTxtMicroLacuna.Fields.TEXTO_LACUNA.toString()));
		
		return executeCriteria(criteria);
	}
}
