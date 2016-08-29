package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;


@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultaItensAFProgramacaoManualQueryBuilder extends QueryBuilder<Query> {
	
	private Integer numeroAF;
	private Integer numeroItem;
	private Short numeroComplemento;
	private Integer numeroFornecedor;
	private Integer codigoMaterial;
	private Integer codigoGrupoMaterial;
	private Boolean isIndProgramado;

	public ConsultaItensAFProgramacaoManualQueryBuilder(final Integer numeroItem, final Integer numeroAF, final Short numeroComplemento, final Integer numeroFornecedor,
														final Integer codigoMaterial, final Integer codigoGrupoMaterial, final Boolean isIndProgramado){

		this.numeroItem = numeroItem;
		this.numeroAF = numeroAF;
		this.numeroComplemento = numeroComplemento;
		this.numeroFornecedor = numeroFornecedor;
		this.codigoMaterial = codigoMaterial;
		this.codigoGrupoMaterial = codigoGrupoMaterial;
		this.isIndProgramado = isIndProgramado;
	}

	public ConsultaItensAFProgramacaoManualQueryBuilder(){

	}

	private String makeQuery() {
		
		final String aliasAFN  = "AFN";
		final String aliasIAF  = "IAF";
		final String aliasMAT  = "MAT";
		final String aliasSLC  = "SLC";
		final String aliasFRN  = "FRN";
		final String aliasFSC1 = "FSC1";
		final String aliasFSC2 = "FSC2";
		final String aliasPEA  = "PEA";
		final String aliasGMT  = "GMT";
		
		StringBuilder sql = new StringBuilder(2000);

		sql.append("SELECT ")
	        .append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
	        	.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.PFR_LCT_NUMERO.toString())
			            	
			.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.NRO_COMPLEMENTO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.NUMERO_DO_ITEM.toString())

			.append(',').append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.ITL_NUMERO.toString())
			
			.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.CODIGO_MATERIAL.toString())

			.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.NOME.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.NOME_MATERIAL.toString())
				
			.append(',').append(aliasFRN).append('.').append(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.RAZAO_SOCIAL.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.QTDE_SOLICITADA.toString())
			
			.append(',').append(" (SELECT COALESCE(SUM(PEA.qtde), 0) FROM ")
				.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(' ').append(aliasPEA)
				.append(" WHERE ")
				.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
				.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString())
				.append(" = 'S')").append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.QTDE.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.FATOR_CONVERSAO.toString())
				
			.append(',').append(aliasFRN).append('.').append(ScoFornecedor.Fields.DIA_FAV_ENTG_MATERIAL.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.DIA_FAV_ENTG_MAT_FORN.toString())
				
			.append(',').append(aliasGMT).append('.').append(ScoGrupoMaterial.Fields.DIA_FAV_ENTG_MATERIAL.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.DIA_FAV_ENTG_MAT_GRUPO_MAT.toString())
				
			.append(',').append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.SLC_NUMERO.toString())	
			
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.VALOR_UNITARIO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoManualVO.Fields.AFN_NUMERO.toString())	
			
			.append(" FROM ")
				.append(ScoAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasAFN).append(',')
				.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasIAF).append(',')
				.append(ScoMaterial.class.getSimpleName()).append(' ').append(aliasMAT).append(',')
				.append(ScoGrupoMaterial.class.getSimpleName()).append(' ').append(aliasGMT).append(',')
				.append(ScoFornecedor.class.getSimpleName()).append(' ').append(aliasFRN).append(',')
				.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(' ').append(aliasSLC).append(',')
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC1).append(',')
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC2)
				
			.append(" WHERE ")
			.append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
				.append(" = ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString())
	
			.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
				.append(" = ").append(aliasGMT).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString())
				
			.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
				.append(" = ").append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
				
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.SITUACAO.toString())
				.append(" NOT IN ( 'EX', 'EF' ) ")
				
			.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
				.append(" > ( SELECT COALESCE(SUM(PEA.qtde), 0) FROM ")
				.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(' ').append(aliasPEA)
				.append(" WHERE ")
				.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
				.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString())
				.append(" > 0 ")
				.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString())
				.append(" = 'S') ")
		
			.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString())
				.append(" = ").append(aliasFRN).append('.').append(ScoFornecedor.Fields.NUMERO.toString())
				
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
				.append(" = ").append(" 'N' ")
				
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
				.append(" = ").append(" 'N' ")
			
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
				
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
				.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
				
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.LICITACAO_NRO.toString()).append(" IS NOT NULL")
			
			.append(" AND ").append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
				.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
			// Só possui numeroItem caso venha da estória #5555
			if (numeroItem != null) {
				sql.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
					.append(" = ").append(":P_NUMERO_AF")
				.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
					.append(" = ").append(":P_NUMERO_ITEM");
			// Caso não tenha numeroItem valida o preenchimento dos filtros da tela
			} else {
				if (numeroAF != null) {
					sql.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
						.append(" = ").append(":P_NUMERO_AF");
				}
				if (numeroComplemento != null) {
					sql.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
						.append(" = ").append(":P_NUMERO_COMPLEMENTO");
				}
				if (numeroFornecedor != null) {
					sql.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString())
						.append(" = ").append(":P_NUMERO_FORNECEDOR");
				}
				if (codigoMaterial != null) {
					sql.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
						.append(" = ").append(":P_COD_MATERIAL");
				}
				if (codigoGrupoMaterial != null) {
					sql.append(" AND ").append(aliasMAT).append('.').append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
						.append(" = ").append(":P_COD_GRUPO_MATERIAL");
				}
				if (isIndProgramado != null && isIndProgramado.equals(Boolean.FALSE)) {
					sql.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
						.append(" > ( SELECT COALESCE(SUM(PEA.qtde), 0) FROM ")
						.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(' ').append(aliasPEA)
						.append(" WHERE ")
						.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
						.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
						.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
						.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
						.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString())
						.append(" = 'N')");
				} else if (isIndProgramado != null && isIndProgramado.equals(Boolean.TRUE)) {
					sql.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
						.append(" <= ( SELECT COALESCE(SUM(PEA.qtde), 0) FROM ")
						.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(' ').append(aliasPEA)
						.append(" WHERE ")
						.append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())
						.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
						.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString())
						.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
						.append(" AND ").append(aliasPEA).append('.').append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString())
						.append(" = 'N')");
				}
			}
			return sql.toString();
	}

	@Override
	protected Query createProduct() {
		return this.createHibernateQuery(this.makeQuery());

	}

	@Override
	protected void doBuild(Query query) {
		if (numeroItem != null) {
			query.setParameter("P_NUMERO_ITEM", numeroItem);
		}
		if (numeroAF != null) {
			query.setParameter("P_NUMERO_AF", numeroAF);
		}
		if (numeroComplemento != null) {
			query.setParameter("P_NUMERO_COMPLEMENTO", numeroComplemento);
		}
		if (numeroFornecedor != null) {
			query.setParameter("P_NUMERO_FORNECEDOR", numeroFornecedor);
		}
		if (codigoMaterial != null) {
			query.setParameter("P_COD_MATERIAL", codigoMaterial);
		}
		if (codigoGrupoMaterial != null) {
			query.setParameter("P_COD_GRUPO_MATERIAL", codigoGrupoMaterial);
		}
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensAFProgramacaoManualVO.class));

	}

	public String getSql () {
		return this.makeQuery();
	}

	public Query build(final Integer numeroItem, final Integer numeroAF, final Short numeroComplemento, final Integer numeroFornecedor,
			final Integer codigoMaterial, final Integer codigoGrupoMaterial, final Boolean isIndProgramado){
		this.numeroItem = numeroItem;
		this.numeroAF = numeroAF;
		this.numeroComplemento = numeroComplemento;
		this.numeroFornecedor = numeroFornecedor;
		this.codigoMaterial = codigoMaterial;
		this.codigoGrupoMaterial = codigoGrupoMaterial;
		this.isIndProgramado = isIndProgramado;
		return super.build();
	}

}