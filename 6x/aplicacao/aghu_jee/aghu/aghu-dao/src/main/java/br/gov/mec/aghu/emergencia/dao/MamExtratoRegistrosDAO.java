package br.gov.mec.aghu.emergencia.dao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamExtratoRegistrosDAO extends BaseDao<MamExtratoRegistro> {

	private static final long serialVersionUID = 824892726430400298L;
	
	public Short obterMaxSeqpPorRgtSeq(Long rgtSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoRegistro.class, "mer");
		criteria.add(Restrictions.eq("mer." + MamExtratoRegistro.Fields.ID_RGT_SEQ.toString(), rgtSeq));
		criteria.setProjection(Projections.max("mer." + MamExtratoRegistro.Fields.ID_SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
		
	}
}
