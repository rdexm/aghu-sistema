package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaTrgExame;

public class MpmAltaTrgExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaTrgExame> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4384582294636509220L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaTriagemSeqp
	 * 
	 * @return MpmAltaTrgExame
	 */
	public List<MpmAltaTrgExame> pesquisarAltaTrgExamePorMpmAltaSumarioIdEAltaTriagemSeqp(Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaTrgExame.class);
		criteria.add(Restrictions.eq(
				MpmAltaTrgExame.Fields.ALTA_SUMARIO_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgExame.Fields.ALTA_SUMARIO_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgExame.Fields.ALTA_SUMARIO_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(
				MpmAltaTrgExame.Fields.ATG_SEQP.toString(), altaTriagemSeqp));

		return executeCriteria(criteria);
	}
}
