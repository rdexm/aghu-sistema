package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamMvtoTriagem;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamExtratoTriagem
 * 
 * @author israel.haas
 * 
 */
public class MamMvtoTriagemDAO extends BaseDao<MamMvtoTriagem> {
	private static final long serialVersionUID = -3798652483347817019L;

	public Double obterMaxSeqPMvtoTriagem(Long seqTriagem) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamMvtoTriagem.class, "MamMvtoTriagem");
		criteria.add(Restrictions.eq("MamMvtoTriagem." + MamMvtoTriagem.Fields.TRG_SEQ.toString(), seqTriagem));
		
		criteria.setProjection(Projections.max("MamMvtoTriagem." + MamMvtoTriagem.Fields.SEQP.toString()));
		
		Double maxSeqP = (Double) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Double.valueOf(String.valueOf(maxSeqP + 1));
		}
		return (double) 1;
	}
}