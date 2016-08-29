package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;

public class ScoCondicaoPgtoLicitacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCondicaoPgtoLicitacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2756451572678889685L;

	public ScoCondicaoPgtoLicitacao buscarCondicaoPagamentoPK(Integer seqCondicaoPgto){
		ScoCondicaoPgtoLicitacao condicaoPagamento = null;
		
		if (seqCondicaoPgto != null){
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPgtoLicitacao.class);
			criteria.createAlias(ScoCondicaoPgtoLicitacao.Fields.FORMA_PAGAMENTO.toString(), "fpg", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq(ScoCondicaoPgtoLicitacao.Fields.SEQ.toString(),seqCondicaoPgto));
			
			condicaoPagamento = (ScoCondicaoPgtoLicitacao) executeCriteriaUniqueResult(criteria);
		}
		
		return condicaoPagamento;
	}	
	
	public List<ScoCondicaoPgtoLicitacao> obterCondicaoPgtoPac(
			Integer numeroLicitacao, Short numeroItem, Integer first,
			Integer max, String order, boolean asc) {
		DetachedCriteria criteria = getCondicaoPgtoPacCriteria(numeroLicitacao,
				numeroItem);
		
		criteria.addOrder(Order.asc(ScoCondicaoPgtoLicitacao.Fields.NUMERO.toString()));

		List<ScoCondicaoPgtoLicitacao> listaCondicaoPgto = executeCriteria(criteria, first,
				max, order, asc);
		return listaCondicaoPgto;
	}

	public Long obterCondicaoPgtoPacCount(Integer numeroLicitacao, Short numeroItem) {
		DetachedCriteria criteria = getCondicaoPgtoPacCriteria(numeroLicitacao,
				numeroItem);

		Long numCondicaoPgto = executeCriteriaCount(criteria);
		return numCondicaoPgto;
	}
	
	public Integer obterProxNumCondicaoPagamento(Integer numeroLicitacao, Short numeroItem){
		Integer proxNumero;
		
		DetachedCriteria criteria = getCondicaoPgtoPacCriteria(numeroLicitacao, numeroItem);
		
		criteria.setProjection(Projections.projectionList().add(Projections.max(ScoCondicaoPgtoLicitacao.Fields.NUMERO.toString())));
		
		proxNumero = (Integer) executeCriteriaUniqueResult(criteria);
		
		if (proxNumero == null){
			proxNumero = 1;
		} else {		
		    proxNumero = proxNumero + 1;
		}
		
		return proxNumero;
	}

	private DetachedCriteria getCondicaoPgtoPacCriteria(
			Integer numeroLicitacao, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPgtoLicitacao.class);
		criteria.createAlias(ScoCondicaoPgtoLicitacao.Fields.FORMA_PAGAMENTO.toString(), "fpg", JoinType.LEFT_OUTER_JOIN);

		if (numeroItem != null) {
			criteria.add(Restrictions.eq(ScoCondicaoPgtoLicitacao.Fields.ITL_LCT_NUMERO.toString(),numeroLicitacao));
			criteria.add(Restrictions.eq(ScoCondicaoPgtoLicitacao.Fields.ITL_NUMERO.toString(), numeroItem));			
		} else {
			criteria.add(Restrictions.eq(ScoCondicaoPgtoLicitacao.Fields.LCT_NUMERO.toString(),numeroLicitacao));
		}

		return criteria;
	}
	
	public List<ScoCondicaoPgtoLicitacao> pesquisarCondicaoPagamentoLicitacao(Integer numeroLicitacao, Short numeroItem, Boolean pesquisarLicitacao) {
		List<ScoCondicaoPgtoLicitacao> listaResultado = executeCriteria(this.getCondicaoPgtoPacCriteria(numeroLicitacao, numeroItem));
		
		if (pesquisarLicitacao) {
			if (listaResultado != null && listaResultado.size() == 0) {
				listaResultado = executeCriteria(this.getCondicaoPgtoPacCriteria(numeroLicitacao, null));
			}
		}
		return listaResultado;
	}
}
