package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ExcluirProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ProgramacaoItensAFQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer numeroAF;
	
	
	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())));
		subCriteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), "IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), "IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		subCriteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
		subCriteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		subCriteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), false));
		
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), numeroAF));
		criteria.addOrder(Order.asc("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Subqueries.exists(subCriteria));
		criteria = converterResultadoParaValueObject(criteria);
	}
	
	private DetachedCriteria converterResultadoParaValueObject(DetachedCriteria criteria) {
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.NUMERO_ITEM.toString());
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.QUANTIDADE_SOLICITADA.toString());
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.QUANTIDADE_RECEBIDA.toString());
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.SITUACAO.toString());
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.VALOR.toString());
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.CODIGO_MATERIAL.toString());
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.NOME_MATERIAL.toString());
		projectionList.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.CODIGO_SERVICO.toString());
		projectionList.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.NOME_SERVICO.toString());
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE.toString()), ExcluirProgramacaoEntregaItemAFVO.Fields.UNIDADE.toString());
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(ExcluirProgramacaoEntregaItemAFVO.class));
		return criteria;
	}
}
