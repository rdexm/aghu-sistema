package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelDoadorRedome;



public class AelDoadorRedomeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDoadorRedome> {


    private static final long serialVersionUID = 9010531952529182167L;

    private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDoadorRedome.class, "ADR");
        criteria.createAlias("ADR." + AelDoadorRedome.Fields.AEL_CAMPANHAS.toString(), "ACA" , JoinType.LEFT_OUTER_JOIN);

		return criteria;
    }

	public AelDoadorRedome buscarAelDoadorRedomePorSeq(Integer seq) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelDoadorRedome.Fields.SEQ.toString(), seq));

		return (AelDoadorRedome) executeCriteriaUniqueResult(dc);
	}

	}
	
	
	

