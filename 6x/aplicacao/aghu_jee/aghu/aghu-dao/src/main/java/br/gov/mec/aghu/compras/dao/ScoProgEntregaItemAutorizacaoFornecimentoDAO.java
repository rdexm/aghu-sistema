package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.compras.autfornecimento.vo.*;
import br.gov.mec.aghu.compras.vo.*;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.utils.DateUtil;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoProgEntregaItemAutorizacaoFornecimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoProgEntregaItemAutorizacaoFornecimento> {
	
	@Inject
	private ScoSolicitacaoServicoDAO aScoSolicitacaoServicoDAO;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO aScoSolicitacoesDeComprasDAO;
	
	@Inject @New(ConsultarLiberacaoEntragasPorItemBuilder.class) 
	private Instance<ConsultarLiberacaoEntragasPorItemBuilder> consultarLiberacaoEntregasPorItem;
	
//	@Inject @New(ParcelaAFCompraServicoQueryBuilder.class)
//	private Instance<ParcelaAFCompraServicoQueryBuilder> parcelaAFCompraServicoQueryBuilder;

	@Inject @New(ImprimirPreviaProgramacaoParcelaAFMaterialBuilder.class)
	private Instance<ImprimirPreviaProgramacaoParcelaAFMaterialBuilder> imprimirPreviaProgramacaoParcelaAFMaterialBuilder;
	
	@Inject @New(ImprimirPreviaProgramacaoParcelaAFServicoBuilder.class)
	private Instance<ImprimirPreviaProgramacaoParcelaAFServicoBuilder> imprimirPreviaProgramacaoParcelaAFServicoBuilder;
	
	@Inject @New(BuscaParcelasNaoAssinadaNaoCanceladaQueryBuilder.class)
	private Instance<BuscaParcelasNaoAssinadaNaoCanceladaQueryBuilder> buscaParcelasNaoAssinadaNaoCanceladaQueryBuilder;
	
	@Inject @New(ExclusaoParcelasNaoAssinadasItemAFQueryBuilder.class)
	private Instance<ExclusaoParcelasNaoAssinadasItemAFQueryBuilder> exclusaoParcelasNaoAssinadasItemAFQueryBuilder;
	
	@Inject @New(EntregaPorItemBuilder.class)
	private Instance<EntregaPorItemBuilder> entregaPorItemBuilder;
	
	private static final long serialVersionUID = -7041050946487696547L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		return criteria;
	}

	public List<ConsultaRecebimentoMaterialServicoVO> pesquisarProgEntregaItensComSaldoPositivo(Integer iafAfnNumero, DominioTipoFaseSolicitacao tipoSolicitacao) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S), Restrictions.isNotNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA.toString())));
		if (tipoSolicitacao.equals(DominioTipoFaseSolicitacao.C)) {
			criteria.add(Restrictions.or(Restrictions.isNull(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.ltProperty(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString())));
		} else {
			criteria.add(Restrictions.or(Restrictions.isNull(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()), Restrictions.gtProperty(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString(), criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString())));
		}
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.asc("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()));
		criteria.addOrder(Order.asc(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()), "itlNumero");
		projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()), "itemAFNNumero");	projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString()), "itemNumero");projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "parcela");
		projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seqParcela");projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()), "afp");projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), "dtPrevEntrega");
		projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), "qtde");	projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), "qtdeEntregue");projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()), "valorTotal");
		projection.add(Projections.property(criteria.getAlias() + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()), "valorEfetivado");
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaRecebimentoMaterialServicoVO.class));
		return executeCriteria(criteria, CacheMode.IGNORE);
	}
	public Integer calculaQtdSaldoAssinado(Integer iafAfnNumero, Integer iafNumero) {
		Integer qtdSaldoAssinado = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE), Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString())));
		criteria.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE), Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString())));
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("(sum(COALESCE(QTDE, 0)) - sum(COALESCE(QTDE_ENTREGUE, 0))) as qtdSaldoAssinado", new String[] { "qtdSaldoAssinado" }, new Type[] { IntegerType.INSTANCE }), "qtdSaldoAssinado");
		criteria.setProjection(projection);
		List<Object> saldoAssinado = executeCriteria(criteria);
		qtdSaldoAssinado = (Integer) saldoAssinado.get(0);
		return qtdSaldoAssinado;
	}
	public BigDecimal calculaValorSaldoAssinado(Integer iafAfnNumero, Integer iafNumero) {
		BigDecimal valorSaldoAssinado = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE), Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString())));
		criteria.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE), Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString())));
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("(sum(COALESCE(VALOR_TOTAL, 0)) - sum(COALESCE(VALOR_EFETIVADO, 0))) as valorSaldoAssinado", new String[] { "valorSaldoAssinado" }, new Type[] { BigDecimalType.INSTANCE }), "valorSaldoAssinado");
		criteria.setProjection(projection);
		valorSaldoAssinado = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return valorSaldoAssinado;
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAf(Integer iafAfnNum, Integer iafNumero, Boolean filtraQtde, Boolean filtraCanceladas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		//criteria.createCriteria("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITENS_RECEB_PROVISORIO_X_PROG_ENTREGA.toString(), "IRPEA", JoinType.LEFT_OUTER_JOIN);
		//criteria.createCriteria("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SOLICITACOES_PROG_ENTREGA.toString(), "SPE", JoinType.LEFT_OUTER_JOIN);
		//criteria.createCriteria("IRPEA." + SceItemRecbXProgrEntrega.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		//criteria.createCriteria("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		//criteria.createCriteria("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		//criteria.createCriteria("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
		if (filtraCanceladas) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		}
		if (filtraQtde) {
			criteria.add(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), 0));
			criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString()));
			criteria.addOrder(Order.desc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		} else {
			criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		}
		return executeCriteria(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAfAss(Integer iafAfnNum, Integer iafNumero, Boolean indProgEntregaBloq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		criteria.createCriteria("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));
		if(indProgEntregaBloq!=null){
			criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTRG_BLOQ.toString(),indProgEntregaBloq));
		}else{
			criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
			criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			criteria.addOrder(Order.desc("PEA."+ ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString()));
			criteria.addOrder(Order.asc("PEA."+ ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		}
		return executeCriteria(criteria);
	}
	public Integer obterNumeroAfp(final Integer numeroAF) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		getUtilProgEntregaAut(numeroAF, criteria, Boolean.TRUE);
		criteria.setProjection(Projections.max(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}	
	private void getUtilProgEntregaAut(final Integer numeroAF,final DetachedCriteria criteria, Boolean indAssinatura) {
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
	}
	public Double obterValorTotal(final Integer numeroAF) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		getUtilProgEntregaAut(numeroAF, criteria, Boolean.FALSE);
		criteria.setProjection(Projections.sum(ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()));
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	public Integer obterMaxNumeroParcela(Integer iafAfnNum, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.setProjection(Projections.max(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}	
	public Integer obterMaxNumeroParcela(Integer iafAfnNum, Integer iafNumero, Boolean indAssinatura) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		criteria.setProjection(Projections.max(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNum));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		if(indAssinatura!=null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), indAssinatura));
		}
		return (Integer) this.executeCriteriaUniqueResult(criteria);		
	}	
	public Integer obterMaxNumeroSeqParcelaItemAf(Integer iafAfnNum, Integer iafNumero, Integer parcela, Boolean filtroC9) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		criteria.setProjection(Projections.max(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
		if(parcela!=null && (filtroC9 == null || (filtroC9 != null && !filtroC9))){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		}else if(parcela!=null && (filtroC9 != null && filtroC9)){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));			criteria.add(Restrictions.gt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		}
		return (Integer) this.executeCriteriaUniqueResult(criteria);		
	}
	public List<PesquisaItemAFPVO> pesquisarItensAFPedido(final FiltroAFPVO filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF");criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);	criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()), PesquisaItemAFPVO.Fields.PEA_IAF_AFN_NUMERO.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString()), PesquisaItemAFPVO.Fields.PEA_IAF_NUMERO.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), PesquisaItemAFPVO.Fields.PEA_SEQ.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), PesquisaItemAFPVO.Fields.PEA_PARCELA.toString()).add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()), PesquisaItemAFPVO.Fields.IPF_ITL_NUMERO.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), PesquisaItemAFPVO.Fields.PEA_QTDE.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), PesquisaItemAFPVO.Fields.IAF_UMD_CODIGO.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString()), PesquisaItemAFPVO.Fields.IAF_IND_SITUACAO.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), PesquisaItemAFPVO.Fields.PEA_DT_PREV_ENTREGA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_GERACAO.toString()), PesquisaItemAFPVO.Fields.PEA_DT_GERACAO.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), PesquisaItemAFPVO.Fields.IAF_QTDE_SOLICITADA.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()), PesquisaItemAFPVO.Fields.IAF_VALOR_UNITARIO.toString()).add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), PesquisaItemAFPVO.Fields.AFN_DT_VENCTO_CONTRATO.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString()), PesquisaItemAFPVO.Fields.IAF_IND_CONTRATO.toString()).add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()), PesquisaItemAFPVO.Fields.MATERIAL.toString()).add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString()), PesquisaItemAFPVO.Fields.SERVICO.toString()));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
		if (filtro.getNumeroAf() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), filtro.getNumeroAf()));
		}
		if (filtro.getParcela() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), filtro.getParcela()));
		}
		if (filtro.getQtde() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), filtro.getQtde()));
		}
		if (filtro.getQtdeSolicitada() != null) {
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString(), filtro.getQtdeSolicitada()));
		}
		if (filtro.getDtPrevEntrega() != null) {
			criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), filtro.getDtPrevEntrega(), DateUtil.obterDataComHoraFinal(filtro.getDtPrevEntrega())));
		}
		if (filtro.getDtGeracao() != null) {
			criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_GERACAO.toString(), filtro.getDtGeracao(), DateUtil.obterDataComHoraFinal(filtro.getDtGeracao())));
		}
		if (filtro.getCodigoMaterial() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString(), filtro.getCodigoMaterial()));
		}
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaItemAFPVO.class));
		return executeCriteria(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItemAf(Integer iafAfnNum, Boolean indPlanejamento, Boolean indCancelada, Boolean indAssinatura, Integer afnNum, Integer maxParcelaAssinada){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));	
		if(afnNum!=null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), afnNum));	
		}
		if(indPlanejamento!=null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), indPlanejamento));	
		}
		if(indCancelada!=null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), indCancelada));	
		}
		if(indAssinatura!=null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), indAssinatura));	
		}
		if(maxParcelaAssinada!=null){
			criteria.add(Restrictions.gt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), maxParcelaAssinada));	
		}
		return executeCriteria(criteria);
	}
	public Long pesquisarSituacaoEmpenhoCount(Integer numeroAF, Integer numeroAFP, Boolean indPendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), numeroAFP));
		if (indPendente) {
			criteria.add(Restrictions.not(Restrictions.in(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), new Object[] { DominioAfEmpenhada.S, DominioAfEmpenhada.P })));
			criteria.add(Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString()));
		} else {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S)).add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.P)).add(Restrictions.isNotNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString())));
		}
		return this.executeCriteriaCount(criteria);
	}
	public List<ScoItemAFPVO> pesquisaProgEntregaItemAf(Integer afeNumero, Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "IAFP");
		criteria.createAlias("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAUT");
		criteria.createAlias("IAUT." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPROS");
		criteria.createAlias("IPROS." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ILIC." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projection = Projections.projectionList().add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()), ScoItemAFPVO.Fields.IAF_AFN_NUMERO.toString()).add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()), ScoItemAFPVO.Fields.IAF_NUMERO.toString()).add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()), ScoItemAFPVO.Fields.SCO_COMPRA.toString()).add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()), ScoItemAFPVO.Fields.SCO_SERVICO.toString()).add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ScoItemAFPVO.Fields.NUMERO_PARCELA.toString()).add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), ScoItemAFPVO.Fields.SEQ_ITEM_AFP.toString());
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), afeNumero));
		criteria.add(Restrictions.eq("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), afnNumero));
		criteria.addOrder(Order.desc("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA));
		criteria.addOrder(Order.asc("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA));
		criteria.addOrder(Order.asc("ILIC." + ScoItemLicitacao.Fields.NUMERO));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAFPVO.class));
		return executeCriteria(criteria);
	}
	public List<PriorizaEntregaVO> pesquisarSolicitacaoProgEntregaComRecebimentoProvisorio(Integer nrpSeq, Integer nroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITENS_RECEBIMENTO_PROVISORIO.toString(), "IRP", JoinType.INNER_JOIN);
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SOLICITACOES_PROG_ENTREGA.toString(), "SPE", JoinType.INNER_JOIN);
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITENS_RECEB_PROVISORIO_X_PROG_ENTREGA.toString(), "IPP", JoinType.INNER_JOIN);
		criteria.createAlias("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.SOLICITACAO_COMPRA.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()), "iafAfnNumero");
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString()), "iafNumero");
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq");
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "parcela");
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()), "afe_numero");
		projection.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), "prev_entrega");
		projection.add(Projections.property("IPP." + SceItemRecbXProgrEntrega.Fields.QTDE_ENTREGUE.toString()), "qtd_entregue");
		projection.add(Projections.sqlProjection("(spe2_.QTDE - spe2_.QTDE_ENTREGUE) + ipp3_.QTDE_ENTREGUE as saldoQtd", new String[] { "saldoQtd" }, new Type[] { IntegerType.INSTANCE }), "saldoQtd");
		projection.add(Projections.sqlProjection("(spe2_.VALOR - spe2_.VALOR_EFETIVADO) + ipp3_.VALOR_EFETIVADO as saldoVlr", new String[] { "saldoVlr" }, new Type[] { BigDecimalType.INSTANCE }), "saldoVlr");
		projection.add(Projections.property("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.IND_PRIORIDADE.toString()), "prioridade");
		projection.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), "slc_numero");
		projection.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()), "sls_numero");
		projection.add(Projections.property("IPP." + SceItemRecbXProgrEntrega.Fields.SEQ.toString()), "seq_receb");
		projection.add(Projections.property("IPP." + SceItemRecbXProgrEntrega.Fields.VALOR_EFETIVADO.toString()), "vlr_efetivado");
		projection.add(Projections.property("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.SEQ.toString()), "seqSpe");
		criteria.setProjection(projection);
		criteria.add(Restrictions.eqProperty("IPP." + SceItemRecbXProgrEntrega.Fields.SPE_SEQ.toString(), "SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq));
		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), nroItem));
		criteria.add(Restrictions.eq("IPP." + SceItemRecbXProgrEntrega.Fields.IND_ESTORNADO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.asc("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.IND_PRIORIDADE.toString()));
		List<Object[]> listaRetorno = executeCriteria(criteria);
		List<PriorizaEntregaVO> listaVO = new ArrayList<PriorizaEntregaVO>();
		for (Object[] obj : listaRetorno) {
			PriorizaEntregaVO objAdd = new PriorizaEntregaVO();
			objAdd.setRowId(RandomUtils.nextInt(0,10000));	objAdd.setNumeroAf(Integer.parseInt(obj[0].toString()));objAdd.setNumeroItemAf(Short.parseShort(obj[1].toString()));objAdd.setSeqProgEntrega(Integer.parseInt(obj[2].toString()));
			objAdd.setParcelaProgEntrega(Integer.parseInt(obj[3].toString()));
			objAdd.setSeqRecebimento(nrpSeq);
			objAdd.setItemRecebimento(nroItem);
			objAdd.setNumeroParcela(Integer.parseInt(obj[3].toString()));
			objAdd.setNumeroAfp(Integer.parseInt(obj[4].toString()));
			objAdd.setSeqSolicitacaoProgramacaoEntrega(Long.parseLong(obj[14].toString()));
			try {
				objAdd.setPrevisaoEntrega(DateUtils.parseDate(obj[5].toString(), new String[] { "dd/MM/yyyy" }));
			} catch (ParseException e) {
				objAdd.setPrevisaoEntrega(null);
			}
			objAdd.setPrioridade(Short.parseShort(obj[9].toString()));
			objAdd.setSeqItemRecbXProgrEntrega(Long.parseLong(obj[12].toString()));
			if (obj[10] != null) {
				objAdd.setSolicitacaoCompra(aScoSolicitacoesDeComprasDAO.obterPorChavePrimaria(Integer.parseInt(obj[10].toString())));
				objAdd.setSaldoSolicitacaoCompra(Integer.parseInt(obj[7].toString()));
				objAdd.setQtdeRecebidaSolicitacaoCompra(Integer.parseInt(obj[6].toString()));
			} else {
				objAdd.setSolicitacaoCompra(null);
				objAdd.setSaldoSolicitacaoCompra(null);
				objAdd.setQtdeRecebidaSolicitacaoCompra(null);
			}
			if (obj[11] != null) {
				objAdd.setSolicitacaoServico(aScoSolicitacaoServicoDAO.obterPorChavePrimaria(Integer.parseInt(obj[11].toString())));
				objAdd.setSaldoSolicitacaoServico(new BigDecimal(Double.parseDouble(obj[8].toString())));
				objAdd.setValorRecebidoSolicitacaoServico(new BigDecimal(Double.parseDouble(obj[13].toString())));
			} else {
				objAdd.setSolicitacaoServico(null);
				objAdd.setSaldoSolicitacaoServico(null);
				objAdd.setValorRecebidoSolicitacaoServico(null);
			}
			listaVO.add(objAdd);
		}
		return listaVO;
	}
	public List<ParcelasAutFornPedidoVO> pesquisarParcelasAfpPorFiltro(Integer numeroAF, Integer numeroAFP, DominioTipoFaseSolicitacao tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_AF_PEDIDO.toString(), "AFP", JoinType.INNER_JOIN);
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC1");
		if (tipo.equals(DominioTipoFaseSolicitacao.C)) { // Compra
			criteria.createAlias("FSC1." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC2", JoinType.INNER_JOIN);
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		} else { // Serviço
			criteria.createAlias("FSC1." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.FASES_SOLICITACAO.toString(), "FSC2", JoinType.INNER_JOIN);
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.INNER_JOIN);
			criteria.createAlias("SRV." + ScoServico.Fields.GRUPO_SERVICO.toString(), "GSR", JoinType.INNER_JOIN);
		}
		ProjectionList projection = Projections.projectionList().add(Projections.property("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ParcelasAutFornPedidoVO.Fields.NUMERO_IAF.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ParcelasAutFornPedidoVO.Fields.PARCELA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), ParcelasAutFornPedidoVO.Fields.QTDE.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), ParcelasAutFornPedidoVO.Fields.QTDE_ENTREGUE.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), ParcelasAutFornPedidoVO.Fields.DT_PREV_ENTREGA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()), ParcelasAutFornPedidoVO.Fields.VALOR_TOTAL.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()), ParcelasAutFornPedidoVO.Fields.IND_PLANEJAMENTO.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()), ParcelasAutFornPedidoVO.Fields.IND_ASSINATURA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString()), ParcelasAutFornPedidoVO.Fields.IND_EMPENHADA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_IMPRESSA.toString()), ParcelasAutFornPedidoVO.Fields.IND_IMPRESSA.toString()).add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString()), ParcelasAutFornPedidoVO.Fields.IND_ENVIO_FORNECEDOR.toString()).add(Projections.property("AFP." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()), ParcelasAutFornPedidoVO.Fields.DT_ENVIO_FORNECEDOR.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_UNID_MED_IAF.toString()).add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), ParcelasAutFornPedidoVO.Fields.FATOR_CONVERSAO_IAF.toString());
		if (tipo.equals(DominioTipoFaseSolicitacao.C)){  // Compra
			projection.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_GRP_MATERIAL.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_MATERIAL.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ParcelasAutFornPedidoVO.Fields.NOME_MATERIAL.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ParcelasAutFornPedidoVO.Fields.DESCR_MATERIAL.toString()).add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_UNID_MED_MATERIAL.toString()).add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), ParcelasAutFornPedidoVO.Fields.NOME_GRP_MATERIAL.toString());
		}else{ // Serviço
			projection.add(Projections.property("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_GRP_SERVICO.toString()).add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ParcelasAutFornPedidoVO.Fields.COD_SERVICO.toString()).add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), ParcelasAutFornPedidoVO.Fields.NOME_SERVICO.toString()).add(Projections.property("SRV." + ScoServico.Fields.DESCRICAO.toString()), ParcelasAutFornPedidoVO.Fields.DESCR_SERVICO.toString()).add(Projections.property("GSR." + ScoGrupoServico.Fields.DESCRICAO.toString()), ParcelasAutFornPedidoVO.Fields.NOME_GRP_SERVICO.toString());
		}
		criteria.setProjection(projection);
		criteria.add(Restrictions.isNotNull("FSC2." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
		criteria.add(Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO, Boolean.FALSE));
		if (numeroAF != null){ 
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), numeroAF));
		}
		if (numeroAFP != null){ 
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), numeroAFP));
		}
		criteria.addOrder(Order.asc("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		criteria.addOrder(Order.desc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ParcelasAutFornPedidoVO.class));
		return executeCriteria(criteria);
	}
	public Integer calcularTotalEntreguePorItemAf(Integer iafAfnNumero, Integer iafNumero, Integer parcela) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.setProjection(Projections.sqlProjection("sum(coalesce({alias}.QTDE_ENTREGUE,0)) as total_entregue", new String[] { "total_entregue" }, new Type[] { IntegerType.INSTANCE }));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaPorPedidoAf(Integer afnNumero, Integer afpNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createCriteria("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_AF_PEDIDO.toString(), "AFP", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), afpNumero));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		return executeCriteria(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarParcelasPendentes(Integer scId) {
		final String PEA = "PEA", SLC = "SLC", FSL = "FSL";
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, PEA);
		getUtilProgEntrag(criteria);
		DetachedCriteria parcelaScCriteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, SLC);
		parcelaScCriteria.setProjection(Projections.id());
		parcelaScCriteria.createAlias(SLC + '.' + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), FSL);
		parcelaScCriteria.add(Restrictions.eq(SLC + '.' + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), scId));
		parcelaScCriteria.add(Restrictions.eqProperty(FSL + '.' + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), PEA + '.' + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString()));
		criteria.add(Subqueries.exists(parcelaScCriteria));
		return executeCriteria(criteria);
	}
	private void getUtilProgEntrag(DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
	}	
	private DetachedCriteria obterCriteriaProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createCriteria("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), iafAfnNum));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_NUMERO.toString(), iafNumero));
		getUtilProgEntrag(criteria);
		return criteria;
	}
	public Boolean verificarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero) {
		return executeCriteriaCount(this.obterCriteriaProgEntregaItemAfPlanejamento(iafAfnNum, iafNumero)) > 0;
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero) {
		return executeCriteria(this.obterCriteriaProgEntregaItemAfPlanejamento(iafAfnNum, iafNumero));
	}
	private DetachedCriteria verificaPEA(Integer numeroAfn, Integer numeroAfp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), numeroAfn));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), numeroAfp));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		return criteria;
	}
	public Long verificarEntregaNaoConfirmada(Integer numeroAfn, Integer numeroAfp) {
		DetachedCriteria criteria = verificaPEA(numeroAfn, numeroAfp);
		Criterion indFornecedor = (Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), Boolean.TRUE));
		Criterion indImpressa = (Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_IMPRESSA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.or(indFornecedor, indImpressa));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		Criterion criterion1 = (Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) < COALESCE(qtde,0) "));
		DetachedCriteria subquery = this.criarSubCriteriaNotaRecebimento();
		Criterion criterion2 = Subqueries.exists(subquery);
		criteria.add(Restrictions.or(criterion1, criterion2));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()), "iafNumero"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return executeCriteriaCount(criteria);
	}
	private DetachedCriteria criarSubCriteriaNotaRecebimento() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eqProperty("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), "IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PEA_PARCELA.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.add(Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PEA_SEQ.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), "nrpSeq"));
		criteria.setResultTransformer(Transformers.aliasToBean(SceItemRecebProvisorio.class));
		return criteria;
	}
	public Long verificarEntregaLiberadaEfetMenor(Integer afnNumero, Integer afeNumero) {
		DetachedCriteria criteria = verificaPEA(afnNumero, afeNumero);
		Criterion indEmpenhada = (Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S));
		Criterion jstCodigo = Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA.toString());
		criteria.add(Restrictions.or(indEmpenhada, jstCodigo));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		criteria.add(Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) > 0 "));
		criteria.add(Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) < qtde "));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return executeCriteriaCount(criteria);
	}
	public Long verificarParcelaVencida(Integer afnNumero, Integer afeNumero) {
		DetachedCriteria criteria = verificaPEA(afnNumero, afeNumero);
		Criterion indEmpenhada = (Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S));
		Criterion jstCodigo = Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA.toString());
		criteria.add(Restrictions.or(indEmpenhada, jstCodigo));
		criteria.add(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0));
		criteria.add(Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) = 0 "));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return executeCriteriaCount(criteria);
	}
	public Long verificarEntregaLiberada(Integer numeroAFN, Integer numeroAFP) {
		DetachedCriteria criteria = verificaPEA(numeroAFN, numeroAFP);
		Criterion indEmpenhada = (Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S));
		Criterion jstCodigo = Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA.toString());
		criteria.add(Restrictions.or(indEmpenhada, jstCodigo));
		criteria.add(Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) = 0 "));
		criteria.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return executeCriteriaCount(criteria);
	}
	private DetachedCriteria obterCriteriaConsultaProgramadaEntregaItem() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC1");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("FSC1." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROPFORN");
		criteria.createAlias("PROPFORN." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORN");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC2");
		criteria.add(Restrictions.isNotNull("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		criteria.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		return criteria;
	}
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoPendente(Integer numeroAf, Integer numeroComplemento) {
		DetachedCriteria criteria = obterCriteriaConsultaProgramadaEntregaItem();
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), numeroComplemento.shortValue()));
		criteria.add(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.or(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.and(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0))), Restrictions.and(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.and(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), Restrictions.ltProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString())))));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.PFR_LCT_NUMERO.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NUMERO_COMPLEMENTO.toString());
		p.add(Projections.property("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.ITL_NUMERO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.PARCELA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_PREV_ENTREGA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), ConsultaProgramacaoEntregaItemVO.Fields.QTDE.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), ConsultaProgramacaoEntregaItemVO.Fields.QTDE_ENTREGUE.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.UMD_CODIGO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.FATOR_CONVERSAO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.MAT_UMD_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.GMT_CODIGO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_CANCELADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_PLANEJADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_ASSINATURA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_EMPENHADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_ENVIO_FORNECEDOR.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_MANUAL.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_RECALCULO_MANUAL.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_AUTOMATICO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_RECALCULO_AUTOMATICO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_NECESSIDADE_HCPA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_NECESSIDADE_HCPA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_LIB_PLANEJAMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_LIB_PLANEJAMENTO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ASSINATURA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_ASSINATURA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.AFNE_AFN_NUMERO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ConsultaProgramacaoEntregaItemVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NUMERO_FORNECEDOR.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IAF_AFN_NUMERO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IAF_NUMERO.toString());
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaProgramacaoEntregaItemVO.class));
		return executeCriteria(criteria);
	}
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoGeral(Integer numeroAf, Integer numeroComplemento) {
		DetachedCriteria criteria = obterCriteriaConsultaProgramadaEntregaItem();
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), numeroComplemento.shortValue()));
		criteria.add(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.or(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.PFR_LCT_NUMERO.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NUMERO_COMPLEMENTO.toString());
		p.add(Projections.property("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.ITL_NUMERO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.PARCELA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_PREV_ENTREGA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), ConsultaProgramacaoEntregaItemVO.Fields.QTDE.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), ConsultaProgramacaoEntregaItemVO.Fields.QTDE_ENTREGUE.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.UMD_CODIGO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.FATOR_CONVERSAO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.MAT_UMD_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.GMT_CODIGO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_CANCELADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_PLANEJADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_ASSINATURA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_EMPENHADA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_ENVIO_FORNECEDOR.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_MANUAL.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_RECALCULO_MANUAL.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_AUTOMATICO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IND_RECALCULO_AUTOMATICO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_NECESSIDADE_HCPA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_NECESSIDADE_HCPA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_LIB_PLANEJAMENTO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_LIB_PLANEJAMENTO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ASSINATURA.toString()), ConsultaProgramacaoEntregaItemVO.Fields.DT_ASSINATURA.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.AFNE_AFN_NUMERO.toString());
		p.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ConsultaProgramacaoEntregaItemVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.NUMERO_FORNECEDOR.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IAF_AFN_NUMERO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ConsultaProgramacaoEntregaItemVO.Fields.IAF_NUMERO.toString());
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaProgramacaoEntregaItemVO.class));
		return executeCriteria(criteria);
	}
	public SceEstoqueGeral obterCurvaAbc(Integer numeroFornecedor, Date competencia, Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR");
		criteria.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.MAT_CODIGO.toString(), "EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), true));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), numeroFornecedor));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(competencia)));
		return (SceEstoqueGeral) executeCriteriaUniqueResult(criteria);
	}
	private DetachedCriteria verificarParcelas(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		return criteria;
	}
	public Boolean verificarParcelasSeremLiberadasAssinatura(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = verificarParcelas(afnNumero, parcela, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.FALSE));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasEmpenhadas(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = verificarParcelas(afnNumero, parcela, iafNumero);
		criteria.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.or(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S), Restrictions.and(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.P), Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString()))));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0)), Restrictions.and(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.ltProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()))));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasPrevisaoEntregaVencida(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = verificarParcelas(afnNumero, parcela, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasAssinadasNaoEmpenhada(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = montaCriteria24898(afnNumero, parcela, iafNumero, DominioAfEmpenhada.N);
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasEmpenhadasNaoEntregue(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = montaCriteria24898(afnNumero, parcela, iafNumero, DominioAfEmpenhada.P);
		criteria.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasNaoEmpenhadasNaoEntregue(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = montaCriteria24898(afnNumero, parcela, iafNumero, DominioAfEmpenhada.S);
		criteria.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		return executeCriteriaCount(criteria) > 0;
	}
	public Boolean verificarParcelasEmpenhadasQtdeEntregueMenor(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = montaCriteria24898(afnNumero, parcela, iafNumero, DominioAfEmpenhada.S);
		criteria.add(Restrictions.ltProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));
		criteria.add(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));
		return executeCriteriaCount(criteria) > 0;
	}

	private DetachedCriteria montaCriteria24898(Integer afnNumero, Integer parcela, Integer iafNumero, DominioAfEmpenhada afEmpenhada) {
		DetachedCriteria criteria = verificarParcelas(afnNumero, parcela, iafNumero);
		if (afEmpenhada != null){ 
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), afEmpenhada));
		}
		return criteria;
	}
	public Boolean verificarParcelasEmpenhadasQtdeEntregueMenorAtrasada(Integer afnNumero, Integer parcela, Integer iafNumero) {
		DetachedCriteria criteria = montaCriteria24898(afnNumero, parcela, iafNumero, null);
		criteria.add(Restrictions.or(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S), Restrictions.and(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.P), Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString()))));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.gt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0)), Restrictions.and(Restrictions.isNotNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.ltProperty(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()))));
		return executeCriteriaCount(criteria) > 0;
	}
	public boolean verificarAtraso(Integer afnNumero, Integer afeNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), afeNumero));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) < qtde "));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return executeCriteriaCount(criteria) > 0;
	}
	public List<ProgrGeralEntregaAFVO> listarParcelasAFCompra(FiltroProgrGeralEntregaAFVO filtro,Integer firstResult, Integer maxResult) {
		ParcelaAFCompraServicoQueryBuilder builder = new ParcelaAFCompraServicoQueryBuilder();
		builder.setFiltro(filtro);
		builder.setIsC1(Boolean.TRUE);
		DetachedCriteria criteria = builder.build();
		if (firstResult == null || maxResult == null){
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, null, true);
		}
	}
	public Long listarParcelasAFCompraCount(FiltroProgrGeralEntregaAFVO filtro) {
		ParcelaAFCompraServicoQueryBuilder builder = new ParcelaAFCompraServicoQueryBuilder();
		builder.setFiltro(filtro);
		builder.setIsC1(Boolean.TRUE);
		DetachedCriteria criteria = builder.build();
		return executeCriteriaCount(criteria);
	}		
	public Long obterQuantidadeItensProgramados(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		return executeCriteriaCountDistinct(criteria, ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), true);
	}
	public Long obterQuantidadeEntregasPendentesEmpenhadas(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		getUtilRestrictions(afnNumero, criteria);
		criteria.add(Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		criteria.add(Restrictions.ge(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		return executeCriteriaCount(criteria);
	}
	public Long obterQuantidadeEntregasLiberadasAssinatura(Integer afnNumero, Integer qtdDiasProgEntregaPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.between(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.adicionaDias(DateUtil.obterUltimoDiaDoMes(new Date()), qtdDiasProgEntregaPadrao), DateUtil.adicionaDias(DateUtil.obterUltimoDiaDoMes(DateUtil.adicionaMeses(new Date(), 1)), qtdDiasProgEntregaPadrao)));
		return executeCriteriaCount(criteria);
	}
	public Long obterQuantidadeEntregasMenor(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		getUtilRestrictions(afnNumero, criteria);
		criteria.add(Restrictions.isNotNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()));
		criteria.add(Restrictions.ltProperty(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));
		return executeCriteriaCount(criteria);
	}
	private void getUtilRestrictions(Integer afnNumero,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
	}		
	public Long obterQuantidadeRecalulada(Integer afnNumero, Integer qtdDiasProgEntregaPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.lt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.adicionaDias(DateUtil.obterUltimoDiaDoMes(new Date()), qtdDiasProgEntregaPadrao)));
		return executeCriteriaCount(criteria);
	}
	public Long obterQuantidadeEntregasVencidas(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		getUtilRestrictions(afnNumero, criteria);
		criteria.add(Restrictions.sqlRestriction("COALESCE(QTDE_ENTREGUE, 0) < QTDE"));
		criteria.add(Restrictions.lt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		return executeCriteriaCount(criteria);
	}
	public Long obterQuantidadeEntregueNaoConfirmada(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.or(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S), Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString())));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.ltProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString())), Restrictions.and(Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0))));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		DetachedCriteria criteriaNota = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		criteriaNota.createAlias("NRP." + SceNotaRecebProvisorio.Fields.ITENS_NOTA_RECEB_PROV.toString(), "IRP", JoinType.INNER_JOIN);
		criteriaNota.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), false));
		criteriaNota.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), false));
		criteriaNota.add(Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ID.toString()));
		criteriaNota.setProjection(Projections.projectionList().add(Projections.count("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString())));
		criteria.add(Subqueries.exists(criteriaNota));
		return executeCriteriaCount(criteria);
	}
	public void liberarEntregas(Integer afnNumero, Date dataPrevEntrega, RapServidores usuarioLogado) {
		String sql = "UPDATE " + ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName() + " SET IND_PLANEJAMENTO = 'S', DT_LIB_PLANEJAMENTO = :sysdate, SER_MATRICULA_LIB_PLANEJ = :matricula, SER_VIN_CODIGO_LIB_PLANEJ = :vinculo" + " WHERE IAF_AFN_NUMERO= :afnNumero AND IND_PLANEJAMENTO = 'N' AND IND_CANCELADA = 'N' AND DT_PREV_ENTREGA <= :dataPrevEntrega";
		Query query = createHibernateQuery(sql);
		query.setParameter("afnNumero", afnNumero);
		query.setParameter("sysdate", new Date());
		query.setParameter("matricula", usuarioLogado.getId().getMatricula());
		query.setParameter("vinculo", usuarioLogado.getId().getVinCodigo());
		query.setParameter("dataPrevEntrega", DateUtil.truncaData(dataPrevEntrega));
		query.executeUpdate();
	}
	public List<ParcelasAFVO> consultaParcelasAFMaterial(Integer numeroAutorizacao) {
		ImprimirPreviaProgramacaoParcelaAFMaterialBuilder builder = imprimirPreviaProgramacaoParcelaAFMaterialBuilder.get();
		return executeCriteria(builder.build(numeroAutorizacao));
	}
	public List<ParcelasAFVO> consultaParcelasAFServico(Integer numeroAutorizacao) {
		ImprimirPreviaProgramacaoParcelaAFServicoBuilder builder = imprimirPreviaProgramacaoParcelaAFServicoBuilder.get();
		return executeCriteria(builder.build(numeroAutorizacao));
	}
	public DominioTipoFaseSolicitacao consultaAFMaterialServico(Integer numeroAutorizacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "afn");
		criteria.createAlias("afn." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "iaf");
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "fsc");
		criteria.add(Restrictions.eq("afn." + ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAutorizacao));
		criteria.setProjection(Projections.distinct(Projections.property("fsc." + ScoFaseSolicitacao.Fields.TIPO.toString())));
		return (DominioTipoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	public List<EntregaPorItemVO> consultaEntregaPorItem(ParcelasAFVO parcelasAFVO) {
		List<EntregaPorItemVO> entregasPorItens = executeCriteria(entregaPorItemBuilder.get().build(parcelasAFVO.getAfnNumero(), parcelasAFVO.getIafNumero(), parcelasAFVO.getPeaSeq(), parcelasAFVO.getPeaParcela()));
		for (EntregaPorItemVO entregaPorItemVO : entregasPorItens) {
			entregaPorItemVO.setParcelaAFVO(parcelasAFVO);
			entregaPorItemVO.setDevolvido(consultaSituacaoDevolvido(entregaPorItemVO.getNrpSeq()));
		}
		return entregasPorItens;
	}
	public DominioBoletimOcorrencias consultaSituacaoDevolvido(Integer nrpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceBoletimOcorrencias.class, "bo");
		criteria.createAlias("bo." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "nr");
		criteria.add(Restrictions.or(Restrictions.eq("bo." + SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.C), Restrictions.eq("bo." + SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.E)));
		criteria.add(Restrictions.eq("nr." + SceNotaRecebimento.Fields.NRP_SEQ.toString(), nrpSeq));
		List<SceBoletimOcorrencias> resultados = executeCriteria(criteria);
		if (resultados != null && !resultados.isEmpty()) {
			return resultados.get(0).getSituacao();
		}
		return null;
	}
	public QuantidadeProgramadaRecebidaItemVO buscarQuantidadeProgramadaRecebidaItem(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = obterCriteriaConsultaGeralAF(iafAfnNumero, iafNumero);
		criteria.setProjection(Projections.projectionList().add(Projections.sqlProjection("sum(QTDE) as quantidadeTotal", new String[] { "quantidadeTotal" }, new Type[] { IntegerType.INSTANCE })).add(Projections.sqlProjection("sum(QTDE_ENTREGUE) as quantidadeEntregueTotal", new String[] { "quantidadeEntregueTotal" }, new Type[] { IntegerType.INSTANCE })));
		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadeProgramadaRecebidaItemVO.class));
		return (QuantidadeProgramadaRecebidaItemVO) executeCriteriaUniqueResult(criteria);
	}
	private DetachedCriteria obterCriteriaConsultaGeralAF(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		return criteria;
	}
	public ScoProgEntregaItemAutorizacaoFornecimento obterProgramacaoEntregaItemAFPorItemAfNumeroParcela(Integer iafAfnNumero, Integer iafNumero, Integer parcela) {
		DetachedCriteria criteria = obterCriteriaConsultaGeralAF(iafAfnNumero, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcela));
		List<ScoProgEntregaItemAutorizacaoFornecimento> lista = executeCriteria(criteria);
		ScoProgEntregaItemAutorizacaoFornecimento item = null;
		if (lista != null && !lista.isEmpty()){
			item = lista.get(0);
		}
		return item;
	}
	public Integer obterProxParcelaPorItemAF(Integer iafAfnNumero, Integer iafNumero) {
		Integer proxParcela = null;
		DetachedCriteria criteria = obterCriteriaGerarParcelasMaterialDireto(iafAfnNumero, iafNumero);
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("COALESCE(MAX(PARCELA),0) + 1 as proxParcela", new String[] { "proxParcela" }, new Type[] { IntegerType.INSTANCE }), "proxParcela");
		criteria.setProjection(projection);
		proxParcela = (Integer) executeCriteriaUniqueResult(criteria);
		return proxParcela;
	}
	public Integer obterProxSeqPorItemAF(Integer iafAfnNumero, Integer iafNumero) {
		Integer proxSeq = null;
		DetachedCriteria criteria = obterCriteriaGerarParcelasMaterialDireto(iafAfnNumero, iafNumero);
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("COALESCE(MAX(SEQ),0) + 1 as proxSeq", new String[] { "proxSeq" }, new Type[] { IntegerType.INSTANCE }), "proxSeq");
		criteria.setProjection(projection);
		proxSeq = (Integer) executeCriteriaUniqueResult(criteria);
		return proxSeq;
	}
	private DetachedCriteria obterCriteriaGerarParcelasMaterialDireto(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		return criteria;
	}
	public Integer obterSaldoPorItemAF(Integer iafAfnNumero, Integer iafNumero) {
		Integer saldoProgramado = null;
		DetachedCriteria criteria = obterCriteriaGerarParcelasMaterialDireto(iafAfnNumero, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("COALESCE(SUM(QTDE - COALESCE(QTDE_ENTREGUE,0)),0) as saldoProgramado", new String[] { "saldoProgramado" }, new Type[] { IntegerType.INSTANCE }), "saldoProgramado");
		criteria.setProjection(projection);
		saldoProgramado = (Integer) executeCriteriaUniqueResult(criteria);
		return saldoProgramado;
	}
	public Integer obterQtdPrimeiraParcelaPorItemAF(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = obterCriteriaGerarParcelasMaterialDireto(iafAfnNumero, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), 0));
		criteria.setProjection(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	public Boolean verificarParcelasNaoCanceladas(Integer afnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		if (iafNumero != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		}
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		return executeCriteriaCount(criteria) > 0;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultarParcelasEntregaMateriaisVO> consultarLiberacaoEntragasPorItem(Date dataEntrega) {
		ConsultarLiberacaoEntragasPorItemBuilder builder = consultarLiberacaoEntregasPorItem.get();
		builder.setC1(Boolean.TRUE);
		List<ConsultarParcelasEntregaMateriaisVO> listConsultarParcelasEntregaMateriaisVO = builder.build(dataEntrega, null, null).list();
		return listConsultarParcelasEntregaMateriaisVO;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultarParcelasEntregaMateriaisVO> consultarParcelasLiberar(Date dataEntrega, Integer lctNumero, Short nroComplemento) {
		ConsultarLiberacaoEntragasPorItemBuilder builder = consultarLiberacaoEntregasPorItem.get();
		builder.setC2(Boolean.TRUE);
		List<ConsultarParcelasEntregaMateriaisVO> listConsultarParcelasEntregaMateriaisVO = builder.build(dataEntrega, lctNumero, nroComplemento).list();
		return listConsultarParcelasEntregaMateriaisVO;
	}
	@SuppressWarnings("unchecked")
	public List<ConsultarParcelasEntregaMateriaisVO> consultarAlterarProgramacaoProgPendente(Integer lctNumero, Short nroComplemento, Boolean filtroWhere) {
		List<ConsultarParcelasEntregaMateriaisVO> listConsultarParcelasEntregaMateriaisVO = null;
		ConsultarLiberacaoEntragasPorItemBuilder builder = consultarLiberacaoEntregasPorItem.get();
		if (filtroWhere) {
			builder.setC3(Boolean.TRUE);
			listConsultarParcelasEntregaMateriaisVO = builder.build(null, lctNumero, nroComplemento).list();
		} else if (!filtroWhere) {
			builder.setC3(Boolean.TRUE);
			listConsultarParcelasEntregaMateriaisVO = builder.build(null, lctNumero, nroComplemento).list();
		}
		return listConsultarParcelasEntregaMateriaisVO;
	}	
	public Integer deletarParcelasCanceladasNaoAssinadasItemAF(Integer iafAfnNumero, Integer iafNumero) {
		String sql = " delete from " + ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName() + " where iaf_afn_numero= :iafAfnNumero and iaf_numero= :iafNumero and ( ind_cancelada  = 'S' or ind_assinatura = 'N' )";
		javax.persistence.Query query = createQuery(sql);
		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		return query.executeUpdate();
	}
	private DetachedCriteria createCriteriaPea(Integer afnNumero, Integer iafNumero) {
		DetachedCriteria criteria = obterCriteriaGerarParcelasMaterialDireto(afnNumero, iafNumero);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.gtProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString())), Restrictions.and(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0), Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()))));
		return criteria;
	}
	public QuantidadeProgramadaRecebidaItemVO obterSaldoProgramado(Integer afnNumero, Integer iafNumero) {
		DetachedCriteria criteria = createCriteriaPea(afnNumero, iafNumero);
		criteria.setProjection(Projections.projectionList().add(Projections.sqlProjection("sum(QTDE) as quantidadeTotal", new String[] { "quantidadeTotal" }, new Type[] { IntegerType.INSTANCE })).add(Projections.sqlProjection("sum(QTDE_ENTREGUE) as quantidadeEntregueTotal", new String[] { "quantidadeEntregueTotal" }, new Type[] { IntegerType.INSTANCE })));
		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadeProgramadaRecebidaItemVO.class));
		return (QuantidadeProgramadaRecebidaItemVO) executeCriteriaUniqueResult(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> obterParcelasAssociadas(Integer afnNumero, Integer iafNumero) {
		DetachedCriteria criteria = createCriteriaPea(afnNumero, iafNumero);
		criteria.addOrder(Order.desc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		return executeCriteria(criteria);
	}
	public Boolean verificaParcelaAssinadas(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		return executeCriteriaCount(criteria) > 0;
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> buscaParcelasNaoAssinadasNaoCanceladas(Integer afnNumero, Integer numeroItem) {
		return executeCriteria(buscaParcelasNaoAssinadaNaoCanceladaQueryBuilder.get().build(afnNumero, numeroItem));
	}
	public void exclusaoParcelasNaoAssinadasItemAF(final Integer iafAfnNumero, final Integer iafNumero, final Boolean isIndAssinatura, final Integer qtdeEntregue) {
		Query query = exclusaoParcelasNaoAssinadasItemAFQueryBuilder.get().build(iafAfnNumero, iafNumero, isIndAssinatura, qtdeEntregue);
		query.executeUpdate();
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> consultarListaParcelasPorParcela(Integer iafAfnNumero, Integer iafNumero, Integer maxParcelaAssinada, Boolean max) {
		ConsultarLiberacaoEntragasPorItemBuilder builder = consultarLiberacaoEntregasPorItem.get();
		DetachedCriteria criteria = builder.consultarListaParcelasPorParcela(iafAfnNumero, iafNumero, maxParcelaAssinada, max);
		return executeCriteria(criteria);
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> verificaParcelasAssinadasNaoEntregues(Integer iafAfnNumero, Integer iafNumero) {
		BuscaParcelasNaoAssinadaNaoCanceladaQueryBuilder builder = buscaParcelasNaoAssinadaNaoCanceladaQueryBuilder.get();
		return executeCriteria(builder.buildVerificaParcelas(iafAfnNumero, iafNumero));
	}
	public List<ScoProgEntregaItemAutorizacaoFornecimento> listarParcelasAssociadas(Integer iafAfnNumero, Integer iafNumero) {
		BuscaParcelasNaoAssinadaNaoCanceladaQueryBuilder builder = buscaParcelasNaoAssinadaNaoCanceladaQueryBuilder.get();
		return executeCriteria(builder.buildListarParcelasAssociadas(iafAfnNumero, iafNumero));
	}
	
	public List<ProgrGeralEntregaAFVO> listarParcelasAFServico(FiltroProgrGeralEntregaAFVO filtro, Integer firstResult, Integer maxResult) {
		DetachedCriteria criteria = getUtilProgEntrega(filtro);
		if (firstResult == null || maxResult == null){
			return executeCriteria(criteria);
		} else {		
			return executeCriteria(criteria, firstResult, maxResult, null, true);
		}
	}
	private DetachedCriteria getUtilProgEntrega(FiltroProgrGeralEntregaAFVO filtro) {
		ParcelaAFCompraServicoQueryBuilder builder = new ParcelaAFCompraServicoQueryBuilder();
		builder.setFiltro(filtro);
		builder.setIsC1(Boolean.FALSE);
		DetachedCriteria criteria = builder.build();
		return criteria;
	}	
	public Long listarParcelasAFServicoCount(FiltroProgrGeralEntregaAFVO filtro) {
		DetachedCriteria criteria = getUtilProgEntrega(filtro);
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoProgEntregaItemAutorizacaoFornecimento> listarParcelasNaoEnviadas(Integer afnNumero, Short prazoEntrega){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), false));
		criteria.add(Restrictions.sqlRestriction("to_date('"+ new SimpleDateFormat("dd/MM/YYYY").format(new Date()) + "','DD/MM/YYYY') > (this_.dt_prev_entrega - " + prazoEntrega.toString() + ")"));
		return executeCriteria(criteria);
		
	}
}