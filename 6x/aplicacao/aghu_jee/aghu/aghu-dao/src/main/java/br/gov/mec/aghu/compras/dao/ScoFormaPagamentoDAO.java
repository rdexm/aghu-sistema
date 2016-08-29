package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoFormaPagamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFormaPagamento> {

	private static final long serialVersionUID = 2926925071386337819L;

	public List<ScoFormaPagamento> pesquisarFormasPagamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoFormaPagamento scoFormaPagamento) {

		DetachedCriteria criteria = this.obterCriteriaBasica(scoFormaPagamento);

		criteria.addOrder(Order.asc(ScoFormaPagamento.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long pesquisarFormasPagamentoCount(
			final ScoFormaPagamento scoFormaPagamento) {

		final DetachedCriteria criteria = this
				.obterCriteriaBasica(scoFormaPagamento);

		return this.executeCriteriaCount(criteria);
	}

	public ScoFormaPagamento pesquisarFormaPagamentoPorSiglaEDescricao(
			final Short codigo, final String sigla, final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoFormaPagamento.class, "SCOFPAG");

		if (codigo != null) {
			criteria.add(Restrictions.ne(
					ScoFormaPagamento.Fields.CODIGO.toString(), codigo));
		}
		if (sigla != null) {
			criteria.add(Restrictions.ilike(
					ScoFormaPagamento.Fields.SIGLA.toString(), sigla,
					MatchMode.EXACT));
		}
		if (descricao != null) {
			criteria.add(Restrictions.ilike(
					ScoFormaPagamento.Fields.DESCRICAO.toString(), descricao,
					MatchMode.EXACT));
		}

		return (ScoFormaPagamento) this.executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(
			final ScoFormaPagamento scoFormaPagamento) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoFormaPagamento.class);

		if (scoFormaPagamento != null) {
			if (scoFormaPagamento.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoFormaPagamento.Fields.CODIGO.toString(),
						scoFormaPagamento.getCodigo()));
			}
		}

		if (StringUtils.isNotBlank(scoFormaPagamento.getSigla())) {
			criteria.add(Restrictions.ilike(
					ScoFormaPagamento.Fields.SIGLA.toString(),
					scoFormaPagamento.getSigla(), MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(scoFormaPagamento.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoFormaPagamento.Fields.DESCRICAO.toString(),
					scoFormaPagamento.getDescricao(), MatchMode.ANYWHERE));
		}

		if (scoFormaPagamento.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoFormaPagamento.Fields.SITUACAO.toString(),
					scoFormaPagamento.getSituacao()));
		}

		return criteria;
	}
	
	public List<ScoFormaPagamento> listarFormasPagamento(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFormaPagamento.class);

		criteria.add(Restrictions.eq(
				ScoFormaPagamento.Fields.SITUACAO.toString(), DominioSituacao.A));		
		
		String strParametro = (String) pesquisa;
		Short codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Short.valueOf(strParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoFormaPagamento.Fields.CODIGO.toString(), codigo));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoFormaPagamento.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}

		return this.executeCriteria(criteria);

	}

	public String obterDescricao(Integer numeroCDP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class);
		criteria.createAlias(ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "fpg");
		criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), numeroCDP));
		criteria.setProjection(Projections.property("fpg." + ScoFormaPagamento.Fields.DESCRICAO.toString()));
		return (String) this.executeCriteriaUniqueResult(criteria);
	}
	
	
}
