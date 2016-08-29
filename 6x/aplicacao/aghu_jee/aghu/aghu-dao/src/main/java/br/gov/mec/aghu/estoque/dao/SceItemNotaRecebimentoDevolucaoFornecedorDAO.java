package br.gov.mec.aghu.estoque.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SceItemNotaRecebimentoDevolucaoFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemNotaRecebimentoDevolucaoFornecedor> {

	private static final long serialVersionUID = -6842560172677526499L;

	public Double pesquisaValorEmDfPorItemAutorizacaoForn(
			SceItemNotaRecebimento itemNotaRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimentoDevolucaoFornecedor.class,"nrd");
		criteria.createAlias("nrd."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.ITEM_DEVOLUCAO_FORNECEDOR.toString(), "idf");
		criteria.createAlias("idf."+SceItemDevolucaoFornecedor.Fields.DEVOLUCAO_FORNECEDOR.toString(), "dfs");
		
		criteria.setProjection(Projections.sum("idf."+SceItemDevolucaoFornecedor.Fields.VALOR.toString()));
		
		criteria.add(Restrictions.eq("nrd."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.ITEM_NOTA_RECEBIMENTO, itemNotaRecebimento));
		criteria.add(Restrictions.eq("dfs."+SceDevolucaoFornecedor.Fields.IND_ESTORNO.toString(), DominioSimNao.N));
		
		return (Double) executeCriteriaUniqueResult(criteria);		
	}

	/**
	 * Calcula qtde total dos itens da NR de um item de devolucao
	 * @param dfsSeq
	 * @param itemDfs
	 * @return Integer
	 */
	public Integer calculaQtdeTotalNrdPorItemDevolucaoFornecedor(Integer dfsSeq, Short itemDfs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimentoDevolucaoFornecedor.class, "NRD");
		criteria.createAlias("NRD."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.ITEM_DEVOLUCAO_FORNECEDOR.toString(), "IDF");
		
		criteria.setProjection(Projections.sum("NRD."+SceItemNotaRecebimentoDevolucaoFornecedor.Fields.QUANTIDADE.toString()));
		
		criteria.add(Restrictions.eq("IDF."+SceItemDevolucaoFornecedor.Fields.DFS_SEQ.toString(), dfsSeq));
		criteria.add(Restrictions.eq("IDF."+SceItemDevolucaoFornecedor.Fields.NUMERO.toString(), Integer.valueOf(itemDfs)));
		
		return (Integer) CoreUtil.nvl(executeCriteriaUniqueResult(criteria), 0);
	}
	
}
