package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.AelSismamaHistoCad;

public class AelSismamaHistoCadDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaHistoCad> {
	
	private static final long serialVersionUID = 7252153552042261066L;

	public List<AelSismamaHistoCad> pesquisarSismamaHistoCad() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoCad.class);
		return executeCriteria(criteria);
	}
	
}
