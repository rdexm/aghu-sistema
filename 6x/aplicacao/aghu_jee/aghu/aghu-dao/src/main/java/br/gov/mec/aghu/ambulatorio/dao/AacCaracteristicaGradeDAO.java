package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacCaracteristicaGrade;

public class AacCaracteristicaGradeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacCaracteristicaGrade> {

	private static final long serialVersionUID = 7434795228698254698L;
	private static final String UNIMED_FUNCIONARIOS = "Unimed Funcionarios";

	public List<AacCaracteristicaGrade> listarCaracteristicaGrade(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCaracteristicaGrade.class);
		criteria.add(Restrictions.eq(AacCaracteristicaGrade.Fields.GRD_SEQ.toString(), grdSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param grdSeq
	 * @return
	 */
	public AacCaracteristicaGrade obterCaracteristicaGradeUnimed(Integer grdSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(AacCaracteristicaGrade.class);
		criteria.add(Restrictions.eq(AacCaracteristicaGrade.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.ilike(AacCaracteristicaGrade.Fields.CARACTERISTICA.toString(),UNIMED_FUNCIONARIOS, MatchMode.ANYWHERE));
		return (AacCaracteristicaGrade) executeCriteriaUniqueResult(criteria);
	}
}