package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaJustificativa;

public class MbcAgendaJustificativaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaJustificativa> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7053450974761956830L;

	public Short buscarProximoSeqp(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaJustificativa.class, "agj");
		
		criteria.setProjection(Projections.max(MbcAgendaJustificativa.Fields.ID_SEQP.toString()));
		
		criteria.add(Restrictions.eq("agj.".concat(MbcAgendaJustificativa.Fields.ID_AGD_SEQ.toString()), agdSeq));
		
		Short seqp = (Short) executeCriteriaUniqueResult(criteria);
		if(seqp == null) {
			seqp = Short.valueOf("0");
		}
		return ++seqp; 
	}

	

}
