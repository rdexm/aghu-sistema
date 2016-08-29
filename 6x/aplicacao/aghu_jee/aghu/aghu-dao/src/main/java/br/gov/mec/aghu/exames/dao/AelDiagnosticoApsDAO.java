package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelDiagnosticoAps;

public class AelDiagnosticoApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDiagnosticoAps> {

	private static final long serialVersionUID = -6948502032061612301L;

	public AelDiagnosticoAps obterAelDiagnosticoApsPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelDiagnosticoAps.Fields.LUX_SEQ.toString(), luxSeq));
		}
		
		criteria.addOrder(Order.asc(AelDiagnosticoAps.Fields.CRIADO_EM.toString()));
		
		return (AelDiagnosticoAps) executeCriteriaUniqueResult(criteria);
	}

	public List<AelDiagnosticoAps>  listAelDiagnosticoApsPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelDiagnosticoAps.Fields.LUX_SEQ.toString(), luxSeq));
		}
		criteria.addOrder(Order.asc(AelDiagnosticoAps.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}

	public Long listarDiagnosticoAPSCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoAps.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelDiagnosticoAps.Fields.LUX_SEQ.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}

	public AelDiagnosticoAps listarDiagnosticoAPS(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoAps.class);
		criteria.add(Restrictions.eq(AelDiagnosticoAps.Fields.LUX_SEQ.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return (AelDiagnosticoAps) executeCriteriaUniqueResult(criteria);
	}
	
}