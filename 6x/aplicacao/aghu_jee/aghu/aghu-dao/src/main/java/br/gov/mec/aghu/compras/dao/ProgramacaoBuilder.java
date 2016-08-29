package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public abstract class ProgramacaoBuilder extends QueryBuilder<DetachedCriteria> {

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "afn");
		criteria.createAlias("afn." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "iaf");
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "fsc1");
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "pea");
		criteria.createAlias("afn." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "pfr");
		criteria.createAlias("pfr." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "for");
		
		return criteria;
	}

}
