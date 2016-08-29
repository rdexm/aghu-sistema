package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMicroscopiaAps;

public class AelMicroscopiaApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMacroscopiaAps> {

	private static final long serialVersionUID = 8790774685550958253L;

	public AelMicroscopiaAps obterAelMicroscopiaApsPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMicroscopiaAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelMicroscopiaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		return (AelMicroscopiaAps) executeCriteriaUniqueResult(criteria);
	}
}