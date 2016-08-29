package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;



public class MpmMotivoIngressoCtiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmMotivoIngressoCti> {
	
	private static final long serialVersionUID = 8302046997233198569L;

	/**
	 * Método que pesquisa Motivos de ingresso CTI por um atendimento
	 * @param atdSeq
	 * @return
	 */
	public List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtisPorAtendimento(AghAtendimentos atendimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoIngressoCti.class);
		criteria.add(Restrictions.eq(MpmMotivoIngressoCti.Fields.ATENDIMENTO.toString(), atendimento));
		return executeCriteria(criteria);
	}
	
	public List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtiPorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoIngressoCti.class);
		criteria.add(Restrictions.eq(MpmMotivoIngressoCti.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(MpmMotivoIngressoCti.Fields.DTHR_INGRESSO_UNID.toString()));
		
		return executeCriteria(criteria);
	}
}
