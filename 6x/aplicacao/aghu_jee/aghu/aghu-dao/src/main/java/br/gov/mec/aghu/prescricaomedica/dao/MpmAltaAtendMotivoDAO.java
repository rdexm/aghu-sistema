package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaAtendMotivo;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class MpmAltaAtendMotivoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaAtendMotivo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7274538859987115678L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaAtendSeqp
	 * 
	 * @return MpmAltaAtendMotivo
	 */
	public List<MpmAltaAtendMotivo> pesquisarAltaAtendMotivoPorMpmAltaSumarioIdEAltaAtendSeqp(MpmAltaSumarioId altaSumarioId, Integer altaAtendSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaAtendMotivo.class);
		criteria.add(Restrictions.eq(MpmAltaAtendMotivo.Fields.ID_AAT_ASU_APA_ATD_SEQ.toString(), altaSumarioId.getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaAtendMotivo.Fields.ID_AAT_ASU_APA_SEQ.toString(), altaSumarioId.getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaAtendMotivo.Fields.ID_AAT_ASU_SEQP.toString(), altaSumarioId.getSeqp()));
		criteria.add(Restrictions.eq(MpmAltaAtendMotivo.Fields.ID_AAT_SEQP.toString(), altaAtendSeqp));
		criteria.addOrder(Order.asc(MpmAltaAtendMotivo.Fields.ID_SEQP.toString()));

		return executeCriteria(criteria);
	}
}
