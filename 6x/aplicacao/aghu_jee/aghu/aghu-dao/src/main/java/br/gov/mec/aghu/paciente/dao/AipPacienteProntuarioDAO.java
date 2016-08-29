package br.gov.mec.aghu.paciente.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.model.AipPacienteProntuario;

public class AipPacienteProntuarioDAO extends BaseDao<AipPacienteProntuario>{

	private static final long serialVersionUID = -8491370035272168327L;
	
	public DetachedCriteria criteriaObterProntuario(Integer prontuario){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacienteProntuario.class);
		criteria.add(Restrictions.eq(AipPacienteProntuario.Fields.PRONTUARIO.toString(), prontuario));
		return criteria;
		
	}
	
	public Integer obterProntuario(Integer prontuario) {
		final DetachedCriteria criteria = criteriaObterProntuario(prontuario);
		criteria.setProjection(Projections.property(AipPacienteProntuario.Fields.PRONTUARIO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	

}
