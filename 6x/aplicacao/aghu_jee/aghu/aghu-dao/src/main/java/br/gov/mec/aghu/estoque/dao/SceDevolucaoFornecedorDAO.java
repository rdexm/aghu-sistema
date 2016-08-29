package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceTipoMovimento;

public class SceDevolucaoFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceDevolucaoFornecedor>{
	
	private static final long serialVersionUID = 721067808396654918L;

	/**
	 * Pesquisa a data de geração das devoluções de fornecedores através do seq do documento fiscal de entrada
	 * @param documentoFiscalEntrada
	 * @return
	 */
	public List<Date> pesquisarDataGeracaoDevolucaoFornecedorPorDocumentoFiscalEntrada(Integer seqDocumentoFiscalEntradada) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDevolucaoFornecedor.class);
		criteria.createAlias(SceDevolucaoFornecedor.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", Criteria.INNER_JOIN);
		
		criteria.setProjection(Projections.property(SceDevolucaoFornecedor.Fields.DATA_GERACAO.toString()));

		criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString(), seqDocumentoFiscalEntradada));
		criteria.add(Restrictions.eq(SceDevolucaoFornecedor.Fields.IND_ESTORNO.toString(), Boolean.TRUE));
		
		return  executeCriteria(criteria);
	}

	/**
	 * Obtem o tipo de operacao basica (tmv) de uma devolucao ao fornecedor
	 * @param seqDfs
	 * @return DominioIndOperacaoBasica
	 */
	public DominioIndOperacaoBasica obterOperacaoBasicaNumeroDevolucaoFornecedor(Integer seqDfs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDevolucaoFornecedor.class, "DFS");
		criteria.createAlias("DFS."+SceDevolucaoFornecedor.Fields.TIPO_MOVIMENTO.toString(), "TMV", Criteria.INNER_JOIN);
		
		criteria.setProjection(Projections.property("TMV."+SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()));
		
		criteria.add(Restrictions.eq("DFS."+SceDevolucaoFornecedor.Fields.SEQ.toString(), seqDfs));
		
		return (DominioIndOperacaoBasica) executeCriteriaUniqueResult(criteria);
	}
}
