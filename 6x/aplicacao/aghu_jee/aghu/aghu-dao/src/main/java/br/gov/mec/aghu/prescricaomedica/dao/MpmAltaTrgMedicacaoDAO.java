package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaTrgMedicacao;

public class MpmAltaTrgMedicacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaTrgMedicacao> {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 5287966231639506837L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaTriagemSeqp
	 * 
	 * @return MpmAltaTrgMedicacao
	 */
	public List<MpmAltaTrgMedicacao> pesquisarAltaTrgMedicacaoPorMpmAltaSumarioIdEAltaTriagemSeqp(Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaTrgMedicacao.class);
		criteria.add(Restrictions.eq(
				MpmAltaTrgMedicacao.Fields.ALTA_SUMARIO_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgMedicacao.Fields.ALTA_SUMARIO_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaTrgMedicacao.Fields.ALTA_SUMARIO_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(
				MpmAltaTrgMedicacao.Fields.ATG_SEQP.toString(), altaTriagemSeqp));

		return executeCriteria(criteria);
	}
}
