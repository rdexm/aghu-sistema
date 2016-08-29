package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;

public class AelInformacaoClinicaApDAO extends	br.gov.mec.aghu.core.persistence.dao.BaseDao<AelInformacaoClinicaAP> {

	private static final long serialVersionUID = 5492832954945637804L;

	public AelInformacaoClinicaAP obterAelInformacaoClinicaApPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelInformacaoClinicaAP.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelInformacaoClinicaAP.Fields.AEL_EXAME_AP.toString()+"."+AelExameAp.Fields.SEQ.toString(), luxSeq));
		}
		
		return (AelInformacaoClinicaAP) executeCriteriaUniqueResult(criteria);
	}

}
