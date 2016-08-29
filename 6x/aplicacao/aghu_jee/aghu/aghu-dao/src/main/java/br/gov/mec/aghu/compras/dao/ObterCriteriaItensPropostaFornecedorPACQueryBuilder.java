package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel por motar a busca de sistemas por nome.<br>
 * Usando o valor de busca para filtar nos campos:<br>
 * <ol>
 * 	<li>SIGLA: MatchMode.EXACT</li>
 * 	<li>ou NOME: MatchMode.ANYWHERE</li>
 * </ol><br>
 * 
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para org.hibernate.criterion.DetachedCriteria.
 * Com passagem dos filtros no proprio metodo build.</p>
 * 
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
class ObterCriteriaItensPropostaFornecedorPACQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private List<DominioSituacaoParecer> listaSituacaoParecer;
	private Integer numeroPAC;

	@Override
	protected DetachedCriteria createProduct() {
         DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "ITL");
		
		criteria.createAlias("ITL." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP_FORNEC");
		criteria.createAlias("ITL." + ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString(), "MARCA_ITL", Criteria.LEFT_JOIN);
		criteria.createAlias("ITL." + ScoItemPropostaFornecedor.Fields.MODELO_COMERCIAL.toString(), "MODELO_ITL", Criteria.LEFT_JOIN);
		criteria.createAlias("PROP_FORNEC." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORNEC");	
		
		
		criteria.createAlias("ITL." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(),"ITL_LICITACAO");		
		criteria.createAlias("ITL_LICITACAO." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(),"FASES");
		criteria.createAlias("FASES." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(),"SOLICITACAO_COMPRAS");
		criteria.createAlias("SOLICITACAO_COMPRAS." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(),"MATERIAL");
		criteria.createAlias("MATERIAL." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString(),"UN_MED_MATERIAL");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria aProduct) {
		
		
		aProduct.add(Restrictions.eq("ITL_LICITACAO." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroPAC));
		aProduct.add(Restrictions.ne("ITL_LICITACAO." + ScoItemLicitacao.Fields.IND_EXCLUSAO, true));
		aProduct.add(Restrictions.ne("ITL_LICITACAO." + ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA, true));
		aProduct.add(Restrictions.ne("ITL." + ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO, true));
						
        if (!listaSituacaoParecer.contains(DominioSituacaoParecer.TD)){
        	
        	ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder builderParecerAvaliacaoItemProposta = new ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder();
        	ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder builderParecerOcorrenciaItemProposta = new ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder();
        	ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder builderParecerAvaliacaoItemPropostaMarca = new ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder();
        	ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder builderParecerOcorrenciaItemPropostaMarca = new ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder();
        	ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder builderParecerAvaliacaoItemPropostaSemLista = new ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder();
        	ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder builderParecerOcorrenciaItemPropostaSemLista = new ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder();
        	ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder builderParecerAvaliacaoItemPropostaSemListaMarca = new ObterCriteriaParecerAvaliacaoItemPropostaQueryBuilder();
        	ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder builderParecerOcorrenciaItemPropostaSemListaMarca = new ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder();
        	
        	
        	
    		DetachedCriteria subQueryMenorValor = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "ITL2");		
			subQueryMenorValor.setProjection(Projections.min("ITL2." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()));
			subQueryMenorValor.add(Restrictions.isNull("ITL2."+ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()));
			subQueryMenorValor.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_AUTORIZ_USR.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eqProperty("ITL2."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), 
					"ITL."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString()));			
			aProduct.add(Subqueries.propertyEq("ITL."+ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString(), subQueryMenorValor));
			
			if (listaSituacaoParecer.size() > 1){			
				
				DetachedCriteria subQueryExcluiAutorizado = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "ITL2");		
				subQueryExcluiAutorizado.setProjection(Projections.property("ITL2." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()));
				subQueryExcluiAutorizado.add(Restrictions.isNull("ITL2."+ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()));
				subQueryExcluiAutorizado.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQueryExcluiAutorizado.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.FALSE));
				subQueryExcluiAutorizado.add(Restrictions.eq("ITL2."+ScoItemPropostaFornecedor.Fields.IND_AUTORIZ_USR.toString(), Boolean.TRUE));
				subQueryExcluiAutorizado.add(Restrictions.eqProperty("ITL2."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), 
						"ITL."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString()));			
				aProduct.add(Subqueries.notExists(subQueryExcluiAutorizado));
						
			}
        	
			if (listaSituacaoParecer.contains(DominioSituacaoParecer.SP)){
				aProduct.add(Restrictions.or(Subqueries.exists(builderParecerOcorrenciaItemProposta.build(true,listaSituacaoParecer)),
					     Restrictions.or(Subqueries.exists(builderParecerOcorrenciaItemPropostaMarca.build(false,listaSituacaoParecer)),
					     Restrictions.or(Subqueries.exists(builderParecerAvaliacaoItemProposta.build(true,listaSituacaoParecer)),
					     Restrictions.or(Subqueries.exists(builderParecerAvaliacaoItemPropostaMarca.build(false,listaSituacaoParecer)),
					     Restrictions.and(Restrictions.and(Subqueries.notExists(builderParecerOcorrenciaItemPropostaSemLista.build(true,null)), 
					    		          Subqueries.notExists(builderParecerOcorrenciaItemPropostaSemListaMarca.build(false,null))),
					    Restrictions.and(Subqueries.notExists(builderParecerAvaliacaoItemPropostaSemLista.build(true,null)), 
					    		         Subqueries.notExists(builderParecerAvaliacaoItemPropostaSemListaMarca.build(false,null)))))))));
				
			}
			else {
				aProduct.add(Restrictions.or(Subqueries.exists(builderParecerOcorrenciaItemProposta.build(true,listaSituacaoParecer)),
					     Restrictions.or(Subqueries.exists(builderParecerOcorrenciaItemPropostaMarca.build(false,listaSituacaoParecer)),
					     Restrictions.or(Subqueries.exists(builderParecerAvaliacaoItemProposta.build(true,listaSituacaoParecer)),
					    		         Subqueries.exists(builderParecerAvaliacaoItemPropostaMarca.build(false,listaSituacaoParecer))))));
			}
        } 		
	}
	
	public DetachedCriteria build(Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer) {
		
		this.numeroPAC = numeroPAC;
		this.listaSituacaoParecer = listaSituacaoParecer;
		
		return super.build();
	}

}
