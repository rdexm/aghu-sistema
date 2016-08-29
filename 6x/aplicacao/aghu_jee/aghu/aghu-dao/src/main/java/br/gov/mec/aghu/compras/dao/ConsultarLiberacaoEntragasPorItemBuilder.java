package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Estoria #5561
 * @author fabrica-ctis
 *
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultarLiberacaoEntragasPorItemBuilder extends QueryBuilder<Query> {

	private Date dataEntrega;
	private Integer lctNumero;
	private Short nroComplemento;
	private Boolean c1 = Boolean.FALSE;
	private Boolean c2 = Boolean.FALSE; 
	private Boolean c3 = Boolean.FALSE;
	private Boolean c4 = Boolean.FALSE;;

	@Override
	protected Query createProduct() {
		final String hql = this.makeQuery();
		Query query = this.createHibernateQuery(hql);
		return query;
	}

	@Override
	protected void doBuild(Query query) {
		if(c1){
			query.setParameter("P_IND_SITUACAO", DominioSituacaoAutorizacaoFornecimento.EF);
		}
		query.setParameter("P_IND_ASSINATURA", Boolean.FALSE);
		if(c2){
//			query.setParameter("P_IND_CANCELA", Boolean.TRUE);
			query.setParameter("P_IND_PLANEJAMENTO", Boolean.FALSE);
		}

		if (dataEntrega != null) {
			query.setParameter("P_DT_ENTREGA", dataEntrega);
		}
		if (nroComplemento != null) {
			query.setParameter("P_NUM_COMPLEMENTO", nroComplemento);
		}
		if (lctNumero != null) {
			query.setParameter("P_LCT_NUMERO", lctNumero);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultarParcelasEntregaMateriaisVO.class));
	}
	
	public Query build(final Date dataEntrega, final Integer lctNumero, final Short nroComplemento){
		this.dataEntrega = dataEntrega;
		this.lctNumero = lctNumero;
		this.nroComplemento = nroComplemento;
		return super.build();
	}

	private String makeQuery() {

		final String aliasPEA  = "PEA";
		final String aliasIAF  = "IAF";
		final String aliasAFN  = "AFN";
		final String aliasFSC1 = "FSC1";
		final String aliasFSC2 = "FSC2";
		final String aliasSLC  = "SLC";
		final String aliasMAT  = "MAT";
		final String aliasFRN  = "FRN";
		final String aliasGMT  = "GMT";

		StringBuilder sql = new StringBuilder(400);

		sql.append("SELECT ")
		.append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.PFR_LCT_NUMERO.toString())

		.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.NRO_COMPLEMENTO.toString())

		.append(',').append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.ITL_NUMERO.toString())

		.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.SITUACAO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_SITUACAO.toString())

		.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.IND_ESTOCAVEL.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_ESTOCAVEL.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.PARCELA.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.QTDE.toString())

		.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.UMD_CODIGO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.DATA_PREV_ENTREGA.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_GERACAO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.DATA_GERACAO.toString())

		.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.QTDE_SOLICITADA.toString())

		.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.FATOR_CONVERSAO.toString())

		.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.VALOR_UNITARIO.toString())

		.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_CONTRATO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_MANUAL.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_RECALC_MANUAL.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_CANCELA.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_TRAMITE_INTERNO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_ENTREGA_IMEDIATA.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_URGENTE.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_ENTREGA_URGENTE.toString())

		.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.MAT_CODIGO.toString())

		.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.NOME.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.MATERIAL.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IND_PLANEJAMENTO.toString())

		.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.DATA_VENC_CONTRATO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IAF_AFN_NUMERO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.IAF_NUMERO.toString())

		.append(',').append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString())
		.append(" AS ").append(ConsultarParcelasEntregaMateriaisVO.Fields.VALOR_TOTAL.toString())

		.append("	FROM ")
		.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(' ').append(aliasPEA).append(',')
		.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasIAF).append(',')		
		.append(ScoAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasAFN).append(',')
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC1).append(',')
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC2).append(',')
		.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(' ').append(aliasSLC).append(',')		
		.append(ScoMaterial.class.getSimpleName()).append(' ').append(aliasMAT).append(',')
		.append(ScoFornecedor.class.getSimpleName()).append(' ').append(aliasFRN).append(',')
		.append(ScoGrupoMaterial.class.getSimpleName()).append(' ').append(aliasGMT)

		.append("	WHERE ")

		.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
		.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())

		.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
		.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())

		.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
		.append(" = ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString())

		.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
		.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())

		.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())

		.append(" AND ").append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
		.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())

		.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())

		.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()).append(" IS NOT NULL")

		.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
		.append(" = ").append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())

		.append(" AND ").append(aliasFRN).append('.').append(ScoFornecedor.Fields.NUMERO.toString())
		.append(" = ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString())

		.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
		.append(" = ").append(aliasGMT).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString())
		.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()).append(" = ").append(":P_IND_ASSINATURA");
		
		if(c1){
		
			sql.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()).append(" < ").append(":P_DT_ENTREGA")
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.SITUACAO.toString()).append(" <> ").append(":P_IND_SITUACAO");		
		
		}else if(c2){

//			sql.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()).append(" = ").append(":P_IND_CANCELA")
			sql.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()).append(" = ").append(":P_IND_PLANEJAMENTO")
			.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()).append(" < ").append(":P_DT_ENTREGA")
		
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(" = ").append(":P_LCT_NUMERO")
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(" = ").append(":P_NUM_COMPLEMENTO");

		}else if(c3){
			
			sql.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(" = ").append(":P_LCT_NUMERO")
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(" = ").append(":P_NUM_COMPLEMENTO");
			
		}else if(c4){
	
			sql.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()).append(" = ").append(Boolean.FALSE)
			.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()).append(" = ").append(Boolean.FALSE)
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(" = ").append(":P_LCT_NUMERO")
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(" = ").append(":P_NUM_COMPLEMENTO")
			.append(" AND ((").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()).append(" is not null ")
				.append(" AND ").append(aliasPEA).append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()).append(" > 0 )")
				.append(" OR (").append(aliasPEA).append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()).append(" IS NOT NULL ")
				.append(" AND ").append(aliasPEA).append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()).append(" < ")
				.append(aliasPEA).append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()).append(" ))");
			
		}
		
		sql.append(" ORDER BY ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(", ")
		.append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(", ")
		.append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()).append(", ")
//		.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString());
 		.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString());
		
		return sql.toString();
	}

	public Query getQueryDelete1(Integer iafAfnNumero, Integer iafNumero, Integer qtdParcela, Integer parcela) {
		StringBuilder ss = new StringBuilder(150);
		ss.append(" ( select max(seq) from " ).append(  ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName());
		ss.append("    where iaf_afn_numero = :iafAfnNumero ");
		ss.append("      and iaf_numero= :iafNumero )");
		StringBuilder sql = new StringBuilder(150);
		sql.append(" delete from ").append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName());
		sql.append("  where iaf_afn_numero = :iafAfnNumero ");
		sql.append("    and iaf_numero= :iafNumero ");
		sql.append("    and qtde = :qtde ");
		sql.append("    and parcela <> :parcela ");
		sql.append("    and seq in " ).append( ss.toString());
		
		final org.hibernate.Query query = createHibernateQuery(sql.toString());
		//Query query = session.createQuery(sql.toString());
		
		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		query.setParameter("qtde", qtdParcela);
		query.setParameter("parcela", parcela);
		return query;
	}

	public DetachedCriteria consultarListaParcelasPorParcela(Integer iafAfnNumero, Integer iafNumero, Integer maxParcelaAssinada, Boolean max) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
		if (max) {
			criteria.add(Restrictions.ge(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), maxParcelaAssinada));
		} else {
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), maxParcelaAssinada));
		}
		return criteria;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Boolean getC1() {
		return c1;
	}

	public void setC1(Boolean c1) {
		this.c1 = c1;
	}

	public Boolean getC2() {
		return c2;
	}

	public void setC2(Boolean c2) {
		this.c2 = c2;
	}

	public Boolean getC3() {
		return c3;
	}

	public void setC3(Boolean c3) {
		this.c3 = c3;
	}

	public Boolean getC4() {
		return c4;
	}

	public void setC4(Boolean c4) {
		this.c4 = c4;
	}

}
