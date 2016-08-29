package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;

public class ScoParcelasPagamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParcelasPagamento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4165537379204024564L;

	public List<ScoParcelasPagamento> obterParcelasPagamento(Integer seqCondicaoPgto){
		DetachedCriteria criteria = getParcelasPagamentoPacCriteria(seqCondicaoPgto);
		
		criteria.addOrder(Order.asc(ScoParcelasPagamento.Fields.PARCELA.toString()));

		List<ScoParcelasPagamento> parcelasPgto = executeCriteria(criteria);
		return parcelasPgto;
	}

	public Long obterParcelasPagamentoCount(Integer seqCondicaoPgto){
		DetachedCriteria criteria = getParcelasPagamentoPacCriteria(seqCondicaoPgto);
		
		Long numParcelasPgto = executeCriteriaCount(criteria);
		return numParcelasPgto;
	}
	
	public List<ScoParcelasPagamento> obterParcelasPgtoProposta(Integer numeroProposta) {
		DetachedCriteria criteria = getParcelasPagamentoProposCriteria(numeroProposta);
		criteria.addOrder(Order.asc(ScoParcelasPagamento.Fields.PARCELA.toString()));
		List<ScoParcelasPagamento> parcelasPgto = executeCriteria(criteria);
		return parcelasPgto;
	}	

	public Long obterParcelasPgtoPropostaCount(Integer numeroProposta){
		DetachedCriteria criteria = getParcelasPagamentoProposCriteria(numeroProposta);
		
		Long numParcelasPgto = executeCriteriaCount(criteria);
		return numParcelasPgto;
	}
	
	public Set<ScoParcelasPagamento> pesquisarParcelasPgtoPac(Integer seqCpl) {
		Set<ScoParcelasPagamento> setParcelas = new HashSet<ScoParcelasPagamento>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParcelasPagamento.class);
		criteria.add(Restrictions.eq(ScoParcelasPagamento.Fields.CPL_SEQ.toString(), seqCpl));
		
		List<ScoParcelasPagamento> listaParcelas =  executeCriteria(criteria);
		
		if (listaParcelas != null && !listaParcelas.isEmpty()) {
			for (ScoParcelasPagamento parcela : listaParcelas) {
				setParcelas.add(parcela);
			}
		}
		
		return setParcelas;
	}
	
	private DetachedCriteria getParcelasPagamentoPacCriteria(Integer seqCondicaoPgto){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParcelasPagamento.class);
		criteria.add(Restrictions.eq(ScoParcelasPagamento.Fields.CPL_SEQ.toString(), seqCondicaoPgto));
		
		return criteria;
	}

	private DetachedCriteria getParcelasPagamentoProposCriteria(Integer numeroProposta){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParcelasPagamento.class);
		criteria.add(Restrictions.eq(ScoParcelasPagamento.Fields.CPF_SEQ.toString(), numeroProposta));
		
		return criteria;
	}
	
	public Boolean verificarLicitacaoPossuiCondicaoPorValor(Integer numeroLicitacao, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParcelasPagamento.class, "PAR");
		criteria.add(Restrictions.gt(ScoParcelasPagamento.Fields.VALOR_PAGAMENTO.toString(), BigDecimal.ZERO));
		
		DetachedCriteria criteriaCondPag = DetachedCriteria.forClass(ScoCondicaoPgtoLicitacao.class, "CPL");		
		criteriaCondPag.add(Restrictions.or(Restrictions.eq("CPL."+ScoCondicaoPgtoLicitacao.Fields.LCT_NUMERO.toString(),numeroLicitacao), 
									Restrictions.and(Restrictions.eq("CPL."+ScoCondicaoPgtoLicitacao.Fields.ITL_LCT_NUMERO.toString(),numeroLicitacao), 
													Restrictions.eq("CPL."+ScoCondicaoPgtoLicitacao.Fields.ITL_NUMERO.toString(), numeroItem))));
		criteriaCondPag.setProjection(Projections.property("CPL." + ScoCondicaoPgtoLicitacao.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn(ScoParcelasPagamento.Fields.CPL_SEQ.toString(), criteriaCondPag));
		
		return executeCriteria(criteria).size() > 0;
	}

	public Boolean verificarPropostaPossuiCondicaoPorValor(Integer numeroFornecedor, Integer numeroPac, Short numeroItemProposta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParcelasPagamento.class, "PAR");
		criteria.add(Restrictions.gt(ScoParcelasPagamento.Fields.VALOR_PAGAMENTO.toString(), BigDecimal.ZERO));
		
		DetachedCriteria criteriaCondPag = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class, "CPP");		
		
		criteriaCondPag.add(
				Restrictions.or(Restrictions.and(
									Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString(), numeroPac), 
									Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(), numeroFornecedor)), 
								Restrictions.and(
									Restrictions.and(
										Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.IPF_PFR_LCT_NUMERO.toString(), numeroPac), 
										Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.IPF_PFR_FRN_NUMERO.toString(), numeroFornecedor)), 
									Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.IPF_NUMERO.toString(), numeroItemProposta))));
		
		criteriaCondPag.setProjection(Projections.property("CPP."+ScoCondicaoPagamentoPropos.Fields.NUMERO.toString()));
		
		criteria.add(Subqueries.propertyIn(ScoParcelasPagamento.Fields.CDP_NUMERO.toString(), criteriaCondPag));
		
		return executeCriteria(criteria).size() > 0;
	}
	
	public Long contarParcelasPagamentoProposta(
			ScoCondicaoPagamentoPropos cond) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParcelasPagamento.class);

		criteria.setProjection(Projections
				.count(ScoParcelasPagamento.Fields.SEQ.toString()));

		criteria.add(Restrictions.eq(
				ScoParcelasPagamento.Fields.CDP_NUMERO.toString(), cond));

		return (Long) executeCriteriaUniqueResult(criteria);
	}
}