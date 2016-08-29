package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultaItensSerAFProgramacaoEntregaQueryBuilder extends QueryBuilder<Query> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2178991831587787887L;
	private Integer numeroAF;
	private Boolean isIndExclusao;

	private String makeQuery() {
		
		final String aliasAFN  = "AFN";
		final String aliasIAF  = "IAF";
		final String aliasFSC1 = "FSC1";
		final String aliasFSC2 = "FSC2";
		final String aliasCCT  = "CCT";
		final String aliasSRV  = "SRV";
		final String aliasSLS  = "SLS";
		
		StringBuilder sql = new StringBuilder(300);

		sql.append("SELECT ")
			.append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
	    	.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.PFR_LCT_NUMERO.toString())
	    	
			.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.NRO_COMPLEMENTO.toString())
			
			.append(',').append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_ENTREGA_PROGRAMADA.toString())
				
			.append(',').append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.ITL_NUMERO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_SITUACAO.toString())
				
			.append(',').append(aliasSRV).append('.').append(ScoServico.Fields.CODIGO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.CODIGO.toString())
				
			.append(',').append(aliasSRV).append('.').append(ScoServico.Fields.NOME.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.NOME.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.QTDE_SOLICITADA.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.QTDE_SOLICITADA.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.FATOR_CONVERSAO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.VALOR_UNITARIO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_CONTRATO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_AUTO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_PROGR_ENTG_AUTO.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_ANALISE_PROGR_PLANEJ.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_ANALISE_PROGR_PLANEJ.toString())
				
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_BLOQ.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.IND_PROGR_ENTG_BLOQ.toString())
				
			.append(',').append(aliasCCT).append('.').append(FccCentroCustos.Fields.DESCRICAO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.DESCRICAO.toString())
			
			.append(',').append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AS ").append(ConsultaItensAFProgramacaoEntregaVO.Fields.NUMERO_DO_ITEM.toString())	
			
			.append("	FROM ")
				.append(ScoAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasAFN).append(',')
				.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(' ').append(aliasIAF).append(',')		
				.append(ScoServico.class.getSimpleName()).append(' ').append(aliasSRV).append(',')
				.append(ScoSolicitacaoServico.class.getSimpleName()).append(' ').append(aliasSLS).append(',')		
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC1).append(',')
				.append(ScoFaseSolicitacao.class.getSimpleName()).append(' ').append(aliasFSC2).append(',')
				.append(FccCentroCustos.class.getSimpleName()).append(' ').append(aliasCCT)
			
			.append("	WHERE 1 = 1")
				.append(" AND ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = ").append(":P_AFN_NUMERO")
		
			.append(" AND ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
				.append(" = ").append(aliasAFN).append('.').append(ScoAutorizacaoForn.Fields.NUMERO.toString())
			
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString())
		
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
				.append(" = ").append(aliasIAF).append('.').append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		
			.append(" AND ").append(aliasSLS).append('.').append(ScoSolicitacaoServico.Fields.NUMERO.toString())
				.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
			
			.append(" AND ").append(aliasSRV).append('.').append(ScoServico.Fields.CODIGO.toString())
				.append(" = ").append(aliasSLS).append('.').append(ScoSolicitacaoServico.Fields.SRV_CODIGO.toString())
			
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
				.append(" = ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
			
			.append(" AND ").append(aliasSLS).append('.').append(ScoSolicitacaoServico.Fields.CCT_CODIGO.toString())
				.append(" = ").append(aliasCCT).append('.').append(FccCentroCustos.Fields.CODIGO.toString())
			
			.append(" AND ").append(aliasFSC1).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = ").append(":P_IND_EXCLUSAO")
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = ").append(":P_IND_EXCLUSAO")
			.append(" AND ").append(aliasFSC2).append('.').append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()).append(" IS NOT NULL");		
			
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