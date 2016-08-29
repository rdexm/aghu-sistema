package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaAtendRegistro;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class MpmAltaAtendRegistroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaAtendRegistro> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7274538859987115678L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaAtendSeqp
	 * 
	 * @return MpmAltaAtendRegistro
	 */
	public List<MpmAltaAtendRegistro> pesquisarAltaAtendRegistroPorMpmAltaSumarioIdEAltaAtendSeqp(MpmAltaSumarioId altaSumarioId, Integer altaAtendSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaAtendRegistro.class);
		criteria.add(Restrictions.eq(MpmAltaAtendRegistro.Fields.ID_AAT_ASU_APA_ATD_SEQ.toString(), altaSumarioId.getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaAtendRegistro.Fields.ID_AAT_ASU_APA_SEQ.toString(), altaSumarioId.getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaAtendRegistro.Fields.ID_AAT_ASU_SEQP.toString(), altaSumarioId.getSeqp()));
		criteria.add(Restrictions.eq(MpmAltaAtendRegistro.Fields.ID_AAT_SEQP.toString(), altaAtendSeqp));
		criteria.addOrder(Order.asc(MpmAltaAtendRegistro.Fields.ID_SEQP.toString()));

		return executeCriteria(criteria);
	}
}
