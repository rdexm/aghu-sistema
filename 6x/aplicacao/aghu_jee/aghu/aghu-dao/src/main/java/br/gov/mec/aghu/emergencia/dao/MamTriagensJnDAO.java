package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTriagensJn;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTriagensJnDAO extends BaseDao<MamTriagensJn>{
	
	private static final long serialVersionUID = 1958277025069942218L;

	public List<Short> obterUnidadeTriagensJnPorTrgSeq(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagensJn.class);
		
		criteria.setProjection(Projections.property(MamTriagensJn.Fields.UNF_SEQ.toString()));
		
		criteria.add(Restrictions.eq(MamTriagensJn.Fields.SEQ.toString(), trgSeq));
		
		criteria.addOrder(Order.desc(MamTriagensJn.Fields.JN_DATE_TIME.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada para buscar o seq da unidade associada ao acolhimento na JOURNAL de MAM_TRIAGENS
	 * 
	 * C4 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public List<Short> obterUltimasTriagensJnPorTrgSeq(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagensJn.class);
		criteria.add(Restrictions.eq(MamTriagensJn.Fields.SEQ.toString(), trgSeq));
		criteria.addOrder(Order.desc(MamTriagensJn.Fields.JN_DATE_TIME.toString()));
		criteria.setProjection(Projections.property(MamTriagensJn.Fields.UNF_SEQ.toString()));
		return super.executeCriteria(criteria, 0, 2, null, false);		
	}
}
