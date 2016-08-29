package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ImprimirPreviaProgramacaoParcelaBuilder extends ProgramacaoBuilder {

	private Integer numeroAutorizacao;
	
	
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = super.createProduct();
		criteria.createAlias("pfr." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "lct");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		ProjectionList projectionList = listagemBasicaParcelas();
		
		clausulasParcela(numeroAutorizacao, criteria);
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(ParcelasAFVO.class));
	}
	
	
	
	protected ProjectionList listagemBasicaParcelas() {
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), ParcelasAFVO.Fields.PEA_SEQ.toString());
		projectionList.add(Projections.property("afn." + ScoAutorizacaoForn.Fields.NUMERO.toString()), ParcelasAFVO.Fields.AFN_NUMERO.toString());
		projectionList.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ParcelasAFVO.Fields.IAF_NUMERO.toString());
		projectionList.add(Projections.property("itl." + ScoItemLicitacao.Fields.NUMERO.toString()), ParcelasAFVO.Fields.ITL_NUMERO.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ParcelasAFVO.Fields.PEA_PARCELA.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), ParcelasAFVO.Fields.PEA_DT_PREV_ENTREGA.toString());
		projectionList.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), ParcelasAFVO.Fields.IAF_UNID_CODIGO.toString());
		projectionList.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), ParcelasAFVO.Fields.IAF_FATOR_CONVERSAO.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), ParcelasAFVO.Fields.PEA_QTDE_ENTREGUE.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()), ParcelasAFVO.Fields.PEA_VALOR_TOTAL.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()), ParcelasAFVO.Fields.PEA_IND_PLANEJAMENTO.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()), ParcelasAFVO.Fields.PEA_IND_ASSINATURA.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString()), ParcelasAFVO.Fields.PEA_IND_ENTREGA_IMEDIATA.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString()), ParcelasAFVO.Fields.PEA_IND_TRAMITE_INTERNO.toString());
		return projectionList;
	}
	
	protected void clausulasParcela(Integer numeroAutorizacao, DetachedCriteria criteria) {
		//clausulas
		criteria.add(Restrictions.eq("fsc1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq("fsc2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.not(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), 0)));
		criteria.add(Restrictions.eq("lct." + ScoLicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.addOrder(Order.asc("itl." + ScoItemLicitacao.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.add(Restrictions.eq("afn." + ScoAutorizacaoForn.Fields.NUMERO, numeroAutorizacao));
	}
	
	public DetachedCriteria build(Integer numeroAutorizacao) {
		this.numeroAutorizacao = numeroAutorizacao;
		return super.build();
	}
}
