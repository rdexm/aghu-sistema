package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelDocResultadoExamesHist;

public class AelDocResultadoExamesHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDocResultadoExamesHist> {

	private static final long serialVersionUID = -2075476814117766515L;

	public AelDocResultadoExamesHist obterDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDocResultadoExamesHist.class);
		criteria.add(Restrictions.eq(AelDocResultadoExamesHist.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelDocResultadoExamesHist.Fields.ISE_SEQP.toString(), iseSeqp));
		Criterion lhs = Restrictions.eq(AelDocResultadoExamesHist.Fields.IND_ANULACAO_DOC.toString(), Boolean.FALSE);
		Criterion rhs = Restrictions.isNull(AelDocResultadoExamesHist.Fields.DATA_HORA_ANULACAO_DOC.toString());
		Criterion or = Restrictions.or(lhs, rhs);
		criteria.add(or);
		return (AelDocResultadoExamesHist) this.executeCriteriaUniqueResult(criteria);
	}

	public List<AelDocResultadoExamesHist> obterDocumentoAnexadoPorItemSolicitacaoExameHist(
			Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDocResultadoExamesHist.class);
		criteria.add(Restrictions.eq(AelDocResultadoExamesHist.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelDocResultadoExamesHist.Fields.ISE_SEQP.toString(), iseSeqp));
		return executeCriteria(criteria);
	}
}
