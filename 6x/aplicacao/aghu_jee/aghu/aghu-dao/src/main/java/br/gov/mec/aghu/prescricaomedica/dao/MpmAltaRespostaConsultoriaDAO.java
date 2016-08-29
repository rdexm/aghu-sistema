package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaRespostaConsultoria;

public class MpmAltaRespostaConsultoriaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaRespostaConsultoria> {

		/**
	 * 
	 */
	private static final long serialVersionUID = -5530918920099325097L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param altaConsultoriaSeqp
	 * 
	 * @return MpmAltaConsultoria
	 */
	public List<MpmAltaRespostaConsultoria> pesquisarAltaRespostaConsultoriaPorMpmAltaSumarioIdEAltaConsultoriaSeqp(Integer apaAtdSeq, Integer apaSeq, Short seqp, Short altaConsultoriaSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaRespostaConsultoria.class);
		criteria.add(Restrictions.eq(MpmAltaRespostaConsultoria.Fields.ID_ACN_ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaRespostaConsultoria.Fields.ID_ACN_ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmAltaRespostaConsultoria.Fields.ID_ACN_ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MpmAltaRespostaConsultoria.Fields.ID_ACN_SEQP.toString(), altaConsultoriaSeqp));

		return executeCriteria(criteria);
	}
	
}
