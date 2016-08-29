package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AelDescMaterialApsDAO extends BaseDao<AelDescMaterialAps> {

	private static final long serialVersionUID = -1319779792698039218L;

	public AelDescMaterialAps obterAelDescMaterialApsPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDescMaterialAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelDescMaterialAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		return (AelDescMaterialAps) executeCriteriaUniqueResult(criteria);
	}

	public Long listarDescMaterialCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDescMaterialAps.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelDescMaterialAps.Fields.LUX_SEQ.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}

}