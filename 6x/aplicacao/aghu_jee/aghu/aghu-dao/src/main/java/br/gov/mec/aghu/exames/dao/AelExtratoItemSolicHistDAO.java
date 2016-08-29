package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExtratoItemSolicHist;

public class AelExtratoItemSolicHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoItemSolicHist> {

	private static final long serialVersionUID = -5676771775244410978L;

	public List<AelExtratoItemSolicHist> pesquisarAelExtratoItemSolicitacaoPorItemSolicitacao(final Integer iseSoeSeq, final Short iseSeqp) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicHist.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.addOrder(Order.asc(AelExtratoItemSolicHist.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	public AelExtratoItemSolicHist obterUltimoItemSolicitacaoSitCodigo(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		AelExtratoItemSolicHist result = null;
		List<AelExtratoItemSolicHist> partial = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteraiPorItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);

		criteria.addOrder(Order.desc(AelExtratoItemSolicHist.Fields.SEQP.toString()));

		partial = this.executeCriteria(criteria, 0, 1, null, false);
		if ((partial != null) && !partial.isEmpty()) {
			result = partial.get(0);
		}

		return result;
	}
	
	protected DetachedCriteria obterCriteraiPorItemSolicitacaoSitCodigo(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(AelExtratoItemSolicHist.class);
		result.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		result.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SEQP.toString(), seqp));
		if (sitCodigo != null) {
			result.createAlias(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
			result.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));
		}
		return result;
	}

	public AelExtratoItemSolicHist obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntradaHist(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		AelExtratoItemSolicHist result = null;
		List<AelExtratoItemSolicHist> partial = null;
		DetachedCriteria criteria = null;
	
		criteria = this.obterCriteraiPorItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);
		partial = this.executeCriteria(criteria, 0, 1, AelExtratoItemSolicHist.Fields.DATA_HORA_EVENTO.toString(), false);
		if ((partial != null) && !partial.isEmpty()) {
			result = partial.get(0);
		}
	
		return result;
	}
	
	public Date buscaMaiorDataRecebimento(final Integer itemSolicitacaoExameSoeSeq,	final Short itemSolicitacaoExameSeqp, final String situacaoItemSolicitacaoCodigo) {
		final StringBuilder hql = new StringBuilder(200);
		hql.append("select max(eis.");
		hql.append(AelExtratoItemSolicHist.Fields.DATA_HORA_EVENTO);
		hql.append(") ");
		hql.append("from ");
		hql.append(AelExtratoItemSolicHist.class.getSimpleName());
		hql.append(" eis ");
		hql.append(" where eis.");
		hql.append(AelExtratoItemSolicHist.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = :itemSolicitacaoExameSoeSeq ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicHist.Fields.ISE_SEQP.toString());
		hql.append("  = :itemSolicitacaoExameSeqp ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(" = :situacaoItemSolicitacaoCodigo ");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("itemSolicitacaoExameSoeSeq", itemSolicitacaoExameSoeSeq);
		query.setParameter("itemSolicitacaoExameSeqp", itemSolicitacaoExameSeqp);
		query.setParameter("situacaoItemSolicitacaoCodigo", situacaoItemSolicitacaoCodigo);
		return (Date) query.uniqueResult();
	}

}
