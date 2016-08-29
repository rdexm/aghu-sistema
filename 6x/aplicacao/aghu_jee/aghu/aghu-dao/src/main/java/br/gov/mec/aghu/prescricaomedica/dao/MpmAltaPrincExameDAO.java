package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaPrincExame;

public class MpmAltaPrincExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaPrincExame> {

	private static final long serialVersionUID = -1738672906699056688L;

    public List<MpmAltaPrincExame> listarMpmAltaPrincExamePorIdSemSeqpIndImprime(Integer apaAtdSeq, Integer apaSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincExame.class);
		
		criteria.add(Restrictions.eq(MpmAltaPrincExame.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincExame.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincExame.Fields.ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MpmAltaPrincExame.Fields.IND_IMPRIME.toString(), Boolean.TRUE));
		
		return executeCriteria(criteria);
    }
}
