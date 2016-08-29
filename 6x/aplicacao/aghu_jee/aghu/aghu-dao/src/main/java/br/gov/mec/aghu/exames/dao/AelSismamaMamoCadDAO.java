package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelSismamaMamoCad;

public class AelSismamaMamoCadDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaMamoCad> {

	private static final long serialVersionUID = -828884666383993262L;
	
	
	public AelSismamaMamoCad obterAelSismamaMamoCad(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaMamoCad.class);
		
		criteria.add(Restrictions.eq(AelSismamaMamoCad.Fields.CODIGO.toString(), strPesquisa));
		
		return (AelSismamaMamoCad) this.executeCriteriaUniqueResult(criteria);
	}
	
}
