package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaTrgSinalVital;

public class MpmAltaTrgSinalVitalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaTrgSinalVital> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1703793833688900053L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaTriagemSeqp
	 * 
	 * @return MpmAltaTrgSinalVital
	 */
	public List<MpmAltaTrgSinalVital> pesquisarAltaTrgSinalVitalPorMpmAltaSumarioIdEAltaTriagemSeqp(Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaTrgSinalVital.class);
		criteria.add(Restrictions.eq(
				MpmAltaTrgSinalVital.Fields.ALTA_SUMARIO_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgSinalVital.Fields.ALTA_SUMARIO_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgSinalVital.Fields.ALTA_SUMARIO_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(
				MpmAltaTrgSinalVital.Fields.ATG_SEQP.toString(), altaTriagemSeqp));

		return executeCriteria(criteria);
	}
}
