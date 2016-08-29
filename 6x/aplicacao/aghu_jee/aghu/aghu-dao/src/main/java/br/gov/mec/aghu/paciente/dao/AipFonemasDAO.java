package br.gov.mec.aghu.paciente.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipFonemas;

public class AipFonemasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipFonemas> {
	
	private static final long serialVersionUID = -6477896281822491573L;

	public AipFonemas obterAipFonemaPorFonema(String fonema){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemas.class);
		criteria.add(Restrictions.eq(AipFonemas.Fields.FONEMA.toString(), fonema));
		AipFonemas aipFonema = (AipFonemas) this.executeCriteriaUniqueResult(criteria);
		return aipFonema;
	}
	
}
