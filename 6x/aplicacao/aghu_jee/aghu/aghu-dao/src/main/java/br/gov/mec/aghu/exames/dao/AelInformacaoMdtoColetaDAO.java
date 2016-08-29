package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;

public class AelInformacaoMdtoColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelInformacaoMdtoColeta> {

	private static final long serialVersionUID = 1002887176425624322L;

	public Long obterCountInformacaoMdtoColetaPorSoeSeqESeqp(final Integer soeSeq, final Short seqp) {
		return executeCriteriaCount(criarCriteriaAelInformacaoMdtoColetaByAelInformacaoColeta(seqp, soeSeq));
	}

	private DetachedCriteria criarCriteriaAelInformacaoMdtoColetaByAelInformacaoColeta(Short seqp, Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelInformacaoMdtoColeta.class);
		criteria.add(Restrictions.eq(AelInformacaoMdtoColeta.Fields.ICL_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelInformacaoMdtoColeta.Fields.ICL_SEQP.toString(), seqp));
		return criteria;
	}

	public List<AelInformacaoMdtoColeta> buscarAelInformacaoMdtoColetaByAelInformacaoColeta(Short seqp, Integer soeSeq) {
		final DetachedCriteria criteria = criarCriteriaAelInformacaoMdtoColetaByAelInformacaoColeta(seqp, soeSeq);
		criteria.addOrder(Order.asc(AelInformacaoMdtoColeta.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}

}
