package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;

public class AinCidsInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinCidsInternacao> {

	private static final long serialVersionUID = 5801655229067672590L;

	public List<AinCidsInternacao> pesquisarPorInternacao(AinInternacao internacao) {
		return pesquisarPorInternacao(internacao.getSeq());
	}

	public List<AinCidsInternacao> pesquisarPorInternacao(final Integer seqInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinCidsInternacao.class);
		criteria.add(Restrictions.eq(AinCidsInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.addOrder(Order.asc(AinCidsInternacao.Fields.CID_SEQ.toString()));
		return executeCriteria(criteria);
	}

}
