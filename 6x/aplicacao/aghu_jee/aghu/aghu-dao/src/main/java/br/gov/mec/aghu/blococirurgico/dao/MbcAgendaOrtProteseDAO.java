package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaOrtProtese;

public class MbcAgendaOrtProteseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaOrtProtese> {

	private static final long serialVersionUID = -2255865286345668755L;
	
	public List<MbcAgendaOrtProtese> buscarOrteseprotesePorAgenda(Integer agdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaOrtProtese.class);
		criteria.createCriteria(MbcAgendaOrtProtese.Fields.SCO_MATERIAL.toString(),MbcAgendaOrtProtese.Fields.SCO_MATERIAL.toString(),Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(MbcAgendaOrtProtese.Fields.ID_AGDSEQ.toString(), agdSeq));
		return executeCriteria(criteria);
		
	}
}
