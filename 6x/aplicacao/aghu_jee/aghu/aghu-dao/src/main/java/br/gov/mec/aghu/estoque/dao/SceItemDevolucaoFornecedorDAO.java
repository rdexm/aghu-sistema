package br.gov.mec.aghu.estoque.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class SceItemDevolucaoFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemDevolucaoFornecedor> {


	private static final long serialVersionUID = -1350504043791568790L;

	public Double pesquisarValorDevolucaoFornItem(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimentoDevolucaoFornecedor.class,"nrd");
		
		criteria.setProjection(Projections.sum("idf."+SceItemDevolucaoFornecedor.Fields.VALOR.toString()));
		
		criteria.createAlias("nrd."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.ITEM_DEVOLUCAO_FORNECEDOR.toString(), "idf", Criteria.INNER_JOIN);
		criteria.createAlias("idf."+SceItemDevolucaoFornecedor.Fields.DEVOLUCAO_FORNECEDOR.toString(), "dfs", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("dfs."+SceDevolucaoFornecedor.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("nrd."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.AFN_NUMERO.toString(), scoItensAutorizacaoForn.getId().getAfnNumero()));
		criteria.add(Restrictions.eq("nrd."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.IAF_NUMERO.toString(), scoItensAutorizacaoForn.getId().getNumero()));
		
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Soma as quantidades de problema por item de devolucao do fornecedor
	 * @param dfsSeq
	 * @param nroItem
	 * @return Integer
	 */
	public Integer somarQuantidadePorItemDevolucaoFornecedor(Integer dfsSeq, Integer nroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemDevolucaoFornecedor.class,"IDF");
		criteria.setProjection(Projections.sum("IDF."+SceItemDevolucaoFornecedor.Fields.QUANTIDADE.toString()));
		criteria.add(Restrictions.eq("IDF."+SceItemDevolucaoFornecedor.Fields.DFS_SEQ.toString(), dfsSeq));
		criteria.add(Restrictions.eq("IDF."+SceItemDevolucaoFornecedor.Fields.NUMERO.toString(), nroItem));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
}
