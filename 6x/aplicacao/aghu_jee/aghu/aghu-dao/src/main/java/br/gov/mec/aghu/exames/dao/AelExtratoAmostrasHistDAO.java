package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostrasHist;



public class AelExtratoAmostrasHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoAmostrasHist> {
	
	private static final long serialVersionUID = 742920643642741951L;
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoAmostrasHist.class);
		return criteria;
    }
	
	public List<AelExtratoAmostrasHist> buscarAelExtratosAmostrasPorAmostra(Integer amoSoeSeq, Short amoSeqp) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), amoSeqp));
		
		dc.addOrder(Order.asc(AelExtratoAmostras.Fields.AMO_SEQP.toString()));
		
		return executeCriteria(dc);
	}

	
}
