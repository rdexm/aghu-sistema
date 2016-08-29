package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;

public class ScoJustificativaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoJustificativa> {

	private static final long serialVersionUID = -8025056739057213598L;

	public List<ScoJustificativa> pesquisarJustificativas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			final ScoJustificativa comprasJustificativa) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(comprasJustificativa);

		criteria.addOrder(Order.asc(ScoJustificativa.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public ScoJustificativa pesquisarJustificativasPorDescricao(final Short codigo, final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoJustificativa.class, "SCOJUS");

		if (codigo != null) {
			criteria.add(Restrictions.ne(ScoJustificativa.Fields.CODIGO.toString(), codigo));
		}
		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoJustificativa.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		}

		return (ScoJustificativa) this.executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarJustificativaCount(final ScoJustificativa comprasJustificativa) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(comprasJustificativa);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(final ScoJustificativa comprasJustificativa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoJustificativa.class, "SCOJUS");

		if (comprasJustificativa != null) {
			if (comprasJustificativa.getCodigo() != null) {
				criteria.add(Restrictions.eq(ScoJustificativa.Fields.CODIGO.toString(), comprasJustificativa.getCodigo()));
			}

			if (StringUtils.isNotBlank(comprasJustificativa.getDescricao())) {
				criteria.add(Restrictions.ilike(ScoJustificativa.Fields.DESCRICAO.toString(), comprasJustificativa.getDescricao(),
						MatchMode.ANYWHERE));
			}

			if (comprasJustificativa.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(ScoJustificativa.Fields.IND_SITUACAO.toString(), comprasJustificativa.getIndSituacao()));
			}

			if (comprasJustificativa.getTipo() != null) {
				criteria.add(Restrictions.eq(ScoJustificativa.Fields.TIPO.toString(), comprasJustificativa.getTipo()));
			}

		}

		return criteria;
	}

	//#24878 - C9		
	public ScoJustificativa obterJustificativaEmpenhoPorParcela(Integer iafAfnNumero, Integer iafNumero, Integer parcela,
			Integer progEntregaSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoJustificativa.class, "JST");
		criteria.createAlias("JST." + ScoJustificativa.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(),
				iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(),
				iafNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString(), progEntregaSeq));
		return (ScoJustificativa) executeCriteriaUniqueResult(criteria);
	}

}
