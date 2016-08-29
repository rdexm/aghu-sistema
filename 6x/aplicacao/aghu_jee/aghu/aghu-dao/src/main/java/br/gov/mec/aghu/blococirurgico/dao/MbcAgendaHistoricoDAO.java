package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaHistorico;

public class MbcAgendaHistoricoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaHistorico> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4349506236029021221L;

	public List<MbcAgendaHistorico> buscarAgendaHistorico(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaHistorico.class);
		criteria.add(Restrictions.eq(MbcAgendaHistorico.Fields.ID_AGD_SEQ.toString(),agdSeq));
		criteria.addOrder(Order.desc(MbcAgendaHistorico.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Double buscarProximoSeqp(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaHistorico.class, "ahi");
		
		criteria.setProjection(Projections.max(MbcAgendaHistorico.Fields.ID_SEQP.toString()));
		
		criteria.add(Restrictions.eq("ahi.".concat(MbcAgendaHistorico.Fields.ID_AGD_SEQ.toString()), agdSeq));
		
		Double seqp = (Double) executeCriteriaUniqueResult(criteria);
		if(seqp == null) {
			seqp = Double.valueOf("0");
		}
		return seqp; 
	}
	
}
