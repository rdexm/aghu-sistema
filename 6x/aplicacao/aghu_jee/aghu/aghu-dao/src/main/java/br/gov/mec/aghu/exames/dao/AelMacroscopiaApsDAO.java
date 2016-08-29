package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelMacroscopiaAps;

public class AelMacroscopiaApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMacroscopiaAps> {

	private static final long serialVersionUID = -1319779792698039218L;

	public AelMacroscopiaAps obterAelMacroscopiaApsPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMacroscopiaAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelMacroscopiaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		return (AelMacroscopiaAps) executeCriteriaUniqueResult(criteria);
	}

	public Long listarMacroscopiasAPSCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMacroscopiaAps.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelMacroscopiaAps.Fields.LUX_SEQ.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}
	
}