package br.gov.mec.aghu.bancosangue.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsMotivosCancelaColetas;

public class AbsMotivosCancelaColetasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsMotivosCancelaColetas> {
		
	private static final long serialVersionUID = 4724371357451683028L;

	public AbsMotivosCancelaColetas obterAbsMotivosCancelaColetas(Short seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsMotivosCancelaColetas.class);
		criteria.add(Restrictions.eq(AbsMotivosCancelaColetas.Fields.SEQ.toString(), seq));
		
		return (AbsMotivosCancelaColetas) executeCriteriaUniqueResult(criteria);
		
	}

}
