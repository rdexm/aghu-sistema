package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoSolicitacoesBull;

public class ScoSolicitacoesBullDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacoesBull> {

	private static final long serialVersionUID = 1554048519583996600L;

	/**
	 * #5521
	 * @author lucasbuzzo
	 * @param itlCodMat
	 * @return
	 */
public ScoSolicitacoesBull pesquisaScagh(Integer itlCodMat) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacoesBull.class);
		criteria.add(Restrictions.eq(ScoSolicitacoesBull.Fields.SCAGH.toString(), itlCodMat));

		return (ScoSolicitacoesBull)executeCriteriaUniqueResult(criteria);
	}

/**
 * #5521
 * @author lucasbuzzo
 * @param itlCodMat
 * @return
 */
public ScoSolicitacoesBull pesquisaSsagh(Integer itlCodMat) {
	
	DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacoesBull.class);
	criteria.add(Restrictions.eq(ScoSolicitacoesBull.Fields.SSAGH.toString(), itlCodMat));

	return (ScoSolicitacoesBull) executeCriteriaUniqueResult(criteria);
}
	
	
}
