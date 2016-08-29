package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFormaPagamento;

public class ScoCondicaoPagamentoProposDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCondicaoPagamentoPropos> {

	private static final long serialVersionUID = -4588568818411954354L;

	public List<ScoCondicaoPagamentoPropos> obterCondicaoPagamentoPropos(
			Integer nroFornecedor, Integer nroLicitacao, Short numeroItem,
			Integer first, Integer max, String order, boolean asc) {
		DetachedCriteria criteria = getCondicaoPgtoProposCriteria(nroFornecedor, nroLicitacao, numeroItem);
		criteria.addOrder(Order.asc(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString()));
		
		List<ScoCondicaoPagamentoPropos> listaCondicoesPgtoPropos = executeCriteria(criteria, first, max, order, asc);
		
		return listaCondicoesPgtoPropos;
	}
	
	public Long obterCondicaoPgtoProposCount(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem) {
		DetachedCriteria criteria = getCondicaoPgtoProposCriteria(nroFornecedor, nroLicitacao, numeroItem);

		Long numCondicaoPgtoPropos = executeCriteriaCount(criteria);
		return numCondicaoPgtoPropos;
	}
	
	public List<ScoCondicaoPagamentoPropos> obterCondicaoPagamentoProposta(
			Integer nroFornecedor, Integer nroLicitacao, Short numeroItem) {
		DetachedCriteria criteria = getCondicaoPgtoProposCriteria(nroFornecedor, nroLicitacao, numeroItem);
		criteria.addOrder(Order.asc(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString()));
		
		List<ScoCondicaoPagamentoPropos> listaCondicoesPgtoPropos = executeCriteria(criteria);
		
		return listaCondicoesPgtoPropos;
	}
	
	
	public ScoCondicaoPagamentoPropos obterCondicaoPagamentoPropostaPorNumero(Integer numeroCondicaoPagamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class);
		criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), numeroCondicaoPagamento));
		
		return (ScoCondicaoPagamentoPropos) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria getCondicaoPgtoProposCriteria(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class);
		
		if (numeroItem != null){
			criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.IPF_PFR_FRN_NUMERO.toString(),nroFornecedor));
			criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.IPF_PFR_LCT_NUMERO.toString(), nroLicitacao));
			criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.IPF_NUMERO.toString(), numeroItem));
		} else {
			criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(),nroFornecedor));
			criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString(), nroLicitacao));			
		}
		
		return criteria;		
	}

	/**
	 * Retorna uma lista com todas as condições de pagamento da proposta (proposta e item da proposta)
	 * @param numeroFornecedor
	 * @param numeroPac
	 * @param numeroItem
	 * @return
	 */
	public List<ScoCondicaoPagamentoPropos> pesquisarCondicaoPagamentoProposta(Integer numeroFornecedor, Integer numeroPac, Short numeroItem) {
		List<ScoCondicaoPagamentoPropos> listaCondicoes = new ArrayList<ScoCondicaoPagamentoPropos>();
		listaCondicoes.addAll(pesquisarCondicaoPagamentoProposta(numeroFornecedor, numeroPac, numeroItem, false));
		
		if (numeroItem != null) {
			listaCondicoes.addAll(pesquisarCondicaoPagamentoProposta(numeroFornecedor, numeroPac, numeroItem, true));
		}
		
		return listaCondicoes;
	}
	
	/**
	 * Retorna uma lista com as condições de pagamento da proposta ou do item da proposta
	 * @param numeroFornecedor
	 * @param numeroPac
	 * @param numeroItem
	 * @param retornaItem
	 * @return List
	 */
	private List<ScoCondicaoPagamentoPropos> pesquisarCondicaoPagamentoProposta(Integer numeroFornecedor, Integer numeroPac, Short numeroItem, Boolean retornaItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class, "PCD");

		criteria.createAlias("PCD." + ScoCondicaoPagamentoPropos.Fields.PARCELAS, "parcelas", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PCD."+ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(),numeroFornecedor));
		criteria.add(Restrictions.eq("PCD."+ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString(), numeroPac));
		
		
		if (numeroItem != null) {
			DetachedCriteria subQueryItem = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class, "ICD");
			if (!retornaItem) {
				subQueryItem.setProjection(Projections.property("ICD." + ScoCondicaoPagamentoPropos.Fields.NUMERO.toString()));
			}
			subQueryItem.add(Restrictions.eq("ICD."+ScoCondicaoPagamentoPropos.Fields.IPF_PFR_FRN_NUMERO.toString(),numeroFornecedor));
			subQueryItem.add(Restrictions.eq("ICD."+ScoCondicaoPagamentoPropos.Fields.IPF_PFR_LCT_NUMERO.toString(), numeroPac));
			subQueryItem.add(Restrictions.eq("ICD."+ScoCondicaoPagamentoPropos.Fields.IPF_NUMERO.toString(), numeroItem));
			
			criteria.add(Subqueries.notExists(subQueryItem));
			
			if (retornaItem) {
				return executeCriteria(subQueryItem);
			}
		} 
		
		return executeCriteria(criteria);
	}
	
	public String buscaCondicoesPagamento(Integer cdpNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class, "CPP");
		criteria.createAlias("CPP."+ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG");
		criteria.setProjection(Projections.property("FPG."+ScoFormaPagamento.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), cdpNumero));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoCondicaoPagamentoPropos obterCondicaoPagamentoPorPropostaFornecedor(Integer nroFornecedor, Integer nroLicitacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCondicaoPagamentoPropos.class, "CPP");
		criteria.add(Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(), nroFornecedor));
		criteria.add(Restrictions.eq("CPP."+ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString(), nroLicitacao));
		List<ScoCondicaoPagamentoPropos> scoCondicaoPagamentoProposList = executeCriteria(criteria);
		
		if(scoCondicaoPagamentoProposList != null && !scoCondicaoPagamentoProposList.isEmpty()){
			return (ScoCondicaoPagamentoPropos) scoCondicaoPagamentoProposList.get(0);
		}
		return null;
	}
	
}
