package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoPrazoPagamento;

// TODO: Auto-generated Javadoc
/**
 * The Class ScoServicoDAO.
 * 
 *  @modulo compras
 *  
 */
public class ScoPrazoPagamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPrazoPagamento> {

/*	C11
	Fonte: cf_num_parcelasFormula
	   select count(PARCELA)
	     from sco_prazo_pagamentos
	    where cdp_numero = :cdp_numero; */
	
	private static final long serialVersionUID = -5307819059210391936L;

	public long pesquisarNumeroParcelasCount(Integer cdpNumero) {
		DetachedCriteria criteria = this.pesquisarCriteria(cdpNumero);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria pesquisarCriteria(Integer cdpNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoPrazoPagamento.class);
		criteria.add(Restrictions.eq(
				ScoPrazoPagamento.Fields.CDPNUMERO.toString(), cdpNumero));
		return criteria;
	}

/*  C12
    Fonte: cf_parcelasFormula
	   SELECT PARCELA
	     FROM SCO_PRAZO_PAGAMENTOS
	    WHERE CDP_NUMERO = :CDP_NUMERO
	    ORDER BY NUMERO; */
	
	public List<ScoPrazoPagamento> listarParcelas(Integer cdpNumero) {
		DetachedCriteria criteria = this.pesquisarCriteria(cdpNumero);
		criteria.addOrder(Order.asc(ScoPrazoPagamento.Fields.NUMERO.toString()));
		List<ScoPrazoPagamento> lista = executeCriteria(criteria);
		return lista;
	}
	
	public Short obterParcela(Integer cdpNumero) {
		List<ScoPrazoPagamento> lista = listarParcelas(cdpNumero);
		if(lista.isEmpty()){
			return 0;
		}
		return lista.get(lista.size()-1).getParcela();
	}
	
	public Short buscaParcela(Integer cdpNumero) {
				DetachedCriteria criteria = DetachedCriteria.forClass(ScoPrazoPagamento.class);
				criteria.setProjection(Projections.property(ScoPrazoPagamento.Fields.PARCELA.toString()));
				criteria.add(Restrictions.eq(ScoPrazoPagamento.Fields.CDPNUMERO.toString(), cdpNumero));
				return (Short) executeCriteriaUniqueResult(criteria);
			}
}