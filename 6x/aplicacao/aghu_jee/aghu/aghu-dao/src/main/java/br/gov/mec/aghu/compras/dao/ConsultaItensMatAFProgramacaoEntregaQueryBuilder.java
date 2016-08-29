package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class ConsultaItensMatAFProgramacaoEntregaQueryBuilder extends QueryBuilder<Query> {
	
	private static final String STR = " = ";
	private static final String AND = " AND ";
	private static final String AS = " AS ";
	private static final long serialVersionUID = -5252643031173028850L;
	private Integer numeroAF;
	private Boolean isIndExclusao;

	private String makeQuery() {
		
		final String aliasAFN  = "AFN";
		final String aliasIAF  = "IAF";
		final String aliasMAT  = "MAT";
		final String aliasSLC  = "SLC";
		final String aliasFSC1 = "FSC1";
		final String aliasFSC2 = "FSC2";
		final String aliasCCT  = "CCT";
		
		StringBuilder sql = new StringBuilder(287);

		sql.append("SELECT ")
	        .append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
	        	.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.PFR_LCT_NUMERO.toString())
			            	
			.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.NRO_COMPLEMENTO.toString())
				
			.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_ENTREGA_PROGRAMADA.toString())
				
			.append(',').append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.ITL_NUMERO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_SITUACAO.toString())
				
			.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.CODIGO.toString())
				
			.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.NOME.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.NOME.toString())
				
			.append(',').append(aliasMAT).append('.').append(ScoMaterial.Fields.IND_ESTOCAVEL.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_ESTOCAVEL.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.QTDE_SOLICITADA.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO_FORM.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.FATOR_CONVERSAO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.VALOR_UNITARIO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_CONTRATO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_AUTO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_PROGR_ENTG_AUTO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_ANALISE_PROGR_PLANEJ.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_ANALISE_PROGR_PLANEJ.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_BLOQ.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_PROGR_ENTG_BLOQ.toString())
				
			.append(',').append(aliasCCT).append('.').append(FccCentroCustos.Fields.DESCRICAO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.DESCRICAO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.append(AS).append(ConsultaItensAFProgramacaoEntregaVO.Fields.NUMERO_DO_ITEM.toString())	
			
			.append("	FROM ")
				.append(ScoAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasAFN).append(',')
				.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasIAF).append(',')
				.append(ScoMaterial.class.getSimpleName()).append(' ').append(aliasMAT).append(',')
				.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(' ').append(aliasSLC).append(',')
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC1).append(',')
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC2).append(',')
				.append(FccCentroCustos.class.getSimpleName()).append(' ').append(aliasCCT)
				
			.append("	WHERE ")
			 .append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(STR).append(":P_AFN_NUMERO")
			 
			.append(AND).append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
				.append(STR).append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString())
	
			.append(AND).append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
				.append(STR).append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
		
			.append(AND).append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
				.append(STR).append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
			
			.append(AND).append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
				.append(STR).append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
			
			.append(AND).append(aliasMAT).append('.').append(ScoMaterial.Fields.CODIGO.toString())
				.append(STR).append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
			
			.append(AND).append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
				.append(STR).append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
			
			.append(AND).append(aliasSLC).append('.').append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString())
				.append(STR).append(aliasCCT).append('.').append(FccCentroCustos.Fields.CODIGO.toString())
			
			.append(AND).append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(STR).append(":P_IND_EXCLUSAO")
			.append(AND).append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(STR).append(":P_IND_EXCLUSAO")
			.append(AND).append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()).append(" IS NOT NULL");
			
			return sql.toString();
	}

	@Override
	protected Query createProduct() {
		final String hql = this.makeQuery();
		Query query = this.createHibernateQuery(hql);
		return query;
	}

	@Override
	protected void doBuild(Query query) {
		query.setParameter("P_AFN_NUMERO", numeroAF);
		query.setParameter("P_IND_EXCLUSAO", isIndExclusao);
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensAFProgramacaoEntregaVO.class));
	}

	public Query build(final Integer numeroAF, final Boolean isIndExclusao){
		this.numeroAF = numeroAF;
		this.isIndExclusao = isIndExclusao;
		return super.build();
	}
}
