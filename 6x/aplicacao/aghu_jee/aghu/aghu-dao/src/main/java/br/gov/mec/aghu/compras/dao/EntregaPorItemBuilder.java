package br.gov.mec.aghu.compras.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.EntregaPorItemVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class EntregaPorItemBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer seq;
	private Integer parcela;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "nrp");
		criteria.createAlias("nrp." + SceNotaRecebProvisorio.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "irp");
		criteria.createAlias("nrp." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe", Criteria.LEFT_JOIN);
		criteria.createAlias("irp." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "pea");
		criteria.createAlias("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "iaf");
		
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.not(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), 0)));
		criteria.add(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("nrp." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()), EntregaPorItemVO.Fields.NRP_SEQ.toString());
		projectionList.add(Projections.property("nrp." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString()), EntregaPorItemVO.Fields.NRP_IND_ESTORNO.toString());
		projectionList.add(Projections.property("nrp." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString()), EntregaPorItemVO.Fields.NRP_IND_CONFIRMADO.toString());
		projectionList.add(Projections.property("nrp." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()), EntregaPorItemVO.Fields.DATA_ENTRADA.toString());
		projectionList.add(Projections.property("irp." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), EntregaPorItemVO.Fields.IRP_QUANTIDADE.toString());
		projectionList.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), EntregaPorItemVO.Fields.FATOR_CONVERSAO.toString());
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()), EntregaPorItemVO.Fields.PEA_VALOR_EFETIVADO.toString());
		projectionList.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), EntregaPorItemVO.Fields.DFE_NUMERO.toString());
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(EntregaPorItemVO.class));	
	}
	
	public DetachedCriteria build(Integer iafAfnNumero, Integer iafNumero, Integer seq, Integer parcela) {
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		this.seq = seq;
		this.parcela = parcela;
		return super.build();
	}
}
