package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHist;

/**
 * @HIST AelItemSolicConsultadoDAO
 */
public class AelItemSolicConsultadoHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicConsultadoHist> {

	private static final long serialVersionUID = 1156839659957694928L;
		
	
	public List<AelItemSolicConsultadoHist> pesquisarAelItemSolicConsultadosResultadosExamesHist(Integer iseSoeSeq, Short iseSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicConsultadoHist.class);
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SEQP.toString(), iseSeqp));
		// Ordena a lista
		criteria.addOrder(Order.desc(AelItemSolicConsultado.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}
}
