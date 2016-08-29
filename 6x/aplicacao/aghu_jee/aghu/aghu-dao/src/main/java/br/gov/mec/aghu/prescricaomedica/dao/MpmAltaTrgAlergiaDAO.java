package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaTrgAlergia;

public class MpmAltaTrgAlergiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaTrgAlergia> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7429410180982764441L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaTriagemSeqp
	 * 
	 * @return MpmAltaTrgAlergia
	 */
	public List<MpmAltaTrgAlergia> pesquisarAltaTrgAlergiaPorMpmAltaSumarioIdEAltaTriagemSeqp(Integer apaAtdSeq,Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaTrgAlergia.class);
		criteria.add(Restrictions.eq(
				MpmAltaTrgAlergia.Fields.ALTA_SUMARIO_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgAlergia.Fields.ALTA_SUMARIO_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgAlergia.Fields.ALTA_SUMARIO_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(
				MpmAltaTrgAlergia.Fields.ATG_SEQP.toString(), altaTriagemSeqp));

		return executeCriteria(criteria);
	}
}
