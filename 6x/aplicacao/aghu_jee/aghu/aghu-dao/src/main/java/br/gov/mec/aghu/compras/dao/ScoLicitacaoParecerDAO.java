package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioParecerOcorrencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
public class ScoLicitacaoParecerDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLicitacao> {

	private static final long serialVersionUID = -6089877052617954721L;

	
	private DetachedCriteria obterCriteriaPesquisa(Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class,"LCT");
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.EXCLUSAO.toString(), Boolean.FALSE));
		
		if (numeroPac != null) {
			criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.NUMERO.toString(),	numeroPac));	
			criteria.add(Restrictions.ne("LCT."+ScoLicitacao.Fields.IND_SITUACAO.toString(), DominioSituacaoLicitacao.EF));
			
			DetachedCriteria subQueryProposta = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
			subQueryProposta.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()));
			subQueryProposta.add(Restrictions.eq("IPF."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), numeroPac));
			
			criteria.add(Subqueries.exists(subQueryProposta));
			
		} else {
			if (descricaoPac != null && StringUtils.isNotBlank(descricaoPac)) {
				criteria.add(Restrictions.ilike(
						"LCT."+ScoLicitacao.Fields.DESCRICAO.toString(), descricaoPac, MatchMode.ANYWHERE));
			}
			if (modalidade != null) {
				criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), modalidade.getCodigo()));				
			}
			
			criteria.add(Restrictions.or(
							Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_SITUACAO.toString(), DominioSituacaoLicitacao.PT), 
							Restrictions.and(
									Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_SITUACAO.toString(), DominioSituacaoLicitacao.GR), 
									Restrictions.isNotNull("LCT."+ScoLicitacao.Fields.DT_ENVIO_PAREC_TEC.toString()))));
			
			DetachedCriteria subQueryItemLicitacao = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");
			subQueryItemLicitacao.setProjection(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
			subQueryItemLicitacao.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryItemLicitacao.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString(), Boolean.FALSE));
			subQueryItemLicitacao.add(Restrictions.isNull("ITL."+ScoItemLicitacao.Fields.MOTIVO_CANCEL.toString()));
			subQueryItemLicitacao.add(Restrictions.eqProperty("ITL."+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), 
					"LCT."+ScoLicitacao.Fields.NUMERO.toString()));
			
			DetachedCriteria subQueryProposta = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
			subQueryProposta.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()));
			subQueryProposta.add(Restrictions.eqProperty("IPF."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), 
					"LCT."+ScoLicitacao.Fields.NUMERO.toString()));
			subQueryProposta.add(Restrictions.isNull("IPF."+ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()));
			
			DetachedCriteria criteriaMenorValorParecerFavoravel = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF3");
			criteriaMenorValorParecerFavoravel.setProjection(Projections.property("IPF3." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()));
			criteriaMenorValorParecerFavoravel.createAlias("IPF3."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL2", Criteria.INNER_JOIN);
			criteriaMenorValorParecerFavoravel.createAlias("ITL2."+ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FS", Criteria.INNER_JOIN);			
			criteriaMenorValorParecerFavoravel.createAlias("FS."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);
			criteriaMenorValorParecerFavoravel.add(Restrictions.eqProperty("IPF3."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), 
					"LCT."+ScoLicitacao.Fields.NUMERO.toString()));
						
			criteriaMenorValorParecerFavoravel.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteriaMenorValorParecerFavoravel.add(Restrictions.isNotNull("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			
			DetachedCriteria subQueryMenorValor = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF2");		
			subQueryMenorValor.setProjection(Projections.min("IPF2." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()));
			subQueryMenorValor.add(Restrictions.isNull("IPF2."+ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()));
			subQueryMenorValor.add(Restrictions.eq("IPF2."+ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eq("IPF2."+ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eq("IPF2."+ScoItemPropostaFornecedor.Fields.IND_AUTORIZ_USR.toString(), Boolean.FALSE));
			subQueryMenorValor.add(Restrictions.eqProperty("IPF2."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), 
					"IPF3."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString()));
			criteriaMenorValorParecerFavoravel.add(Subqueries.propertyEq("IPF3."+ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString(), subQueryMenorValor));
					
			DetachedCriteria criteriaPoc = DetachedCriteria
					.forClass(ScoParecerOcorrencia.class, "POC");
			criteriaPoc.setProjection(Projections.property("POC." + ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString()));
			criteriaPoc.add(Restrictions.eq("POC."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
				
			criteriaPoc.createAlias("POC."+ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), "PMTC", Criteria.INNER_JOIN);
			criteriaPoc.add(Restrictions.eqProperty("PMTC."+ScoParecerMaterial.Fields.MATERIAL.toString(), 
					"SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
			
			criteriaPoc.add(
					Restrictions.or(Restrictions.eqProperty("PMTC."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), 
															"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()), 
									Restrictions.and(
											Restrictions.eqProperty("PMTC."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), 
																	"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString()), 
											Restrictions.isNull("IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()))));

			criteriaPoc.add(Restrictions.ne("POC." + ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString(), DominioParecerOcorrencia.PF));

			DetachedCriteria criteriaPav = DetachedCriteria
					.forClass(ScoParecerAvaliacao.class, "PAV");
			criteriaPav.setProjection(Projections.property("PAV." + ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString()));			
			criteriaPav.createAlias("PAV."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMTA", Criteria.INNER_JOIN);
					
			criteriaPav.add(Restrictions.eqProperty("PMTA."+ScoParecerMaterial.Fields.MATERIAL.toString(), 
													"SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
			
			criteriaPav.add(
					Restrictions.or(Restrictions.eqProperty("PMTA."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), 
															"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()), 
									Restrictions.and(
											Restrictions.eqProperty("PMTA."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), 
																	"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString()), 
											Restrictions.isNull("IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()))));
			
			criteriaPav.add(Restrictions.ne("PAV." + ScoParecerAvaliacao.Fields.PARECER_GERAL.toString(), DominioParecer.PF));
			
			DetachedCriteria subQueryDataPav = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV1");
			subQueryDataPav.setProjection(Projections.max("PAV1."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
			subQueryDataPav.add(Restrictions.eqProperty("PAV1."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), 
													 "PMTA."+ScoParecerMaterial.Fields.CODIGO.toString()));		
			criteriaPav.add(Subqueries.propertyEq("PAV."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryDataPav));	
			
			DetachedCriteria subQueryExisteParecer =  DetachedCriteria.forClass(ScoParecerMaterial.class, "PMTE");
			subQueryExisteParecer.setProjection(Projections.property("PMTE."+ScoParecerMaterial.Fields.MATERIAL_CODIGO.toString()));			
			subQueryExisteParecer.add(Restrictions.eqProperty("PMTE."+ScoParecerMaterial.Fields.MATERIAL.toString(), 
					"SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
			subQueryExisteParecer.add(Restrictions.eq("PMTE."+ScoParecerMaterial.Fields.SITUACAO.toString(), 
					DominioSituacao.A));
			subQueryExisteParecer.add(
					Restrictions.or(Restrictions.eqProperty("PMTE."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), 
															"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()), 
									Restrictions.and(
											Restrictions.eqProperty("PMTE."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), 
																	"IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString()), 
											Restrictions.isNull("IPF3."+ScoItemPropostaFornecedor.Fields.MARCA_MODELO.toString()))));
			
			criteriaMenorValorParecerFavoravel.add(Restrictions.or(Subqueries.notExists(subQueryExisteParecer), 
													Restrictions.or(Subqueries.exists(criteriaPoc), Subqueries.exists(criteriaPav))));
								
			criteria.add(Subqueries.exists(subQueryItemLicitacao));
			criteria.add(Subqueries.exists(subQueryProposta));			
			criteria.add(Subqueries.propertyIn("LCT."+ScoLicitacao.Fields.NUMERO.toString(), criteriaMenorValorParecerFavoravel));
			
			if (vencida) {
				DetachedCriteria subQueryItemProposta = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF4");
				subQueryItemProposta.setProjection(Projections.property("IPF4." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()));
				subQueryItemProposta.add(Restrictions.eqProperty("IPF4."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), 
						"LCT."+ScoLicitacao.Fields.NUMERO.toString()));
				subQueryItemProposta.add(Restrictions.lt("IPF4."+ScoItemPropostaFornecedor.Fields.DT_ENTREGA_AMOSTRA.toString(), new Date()));
				
				criteria.add(Subqueries.exists(subQueryItemProposta));
			}
		}

		return criteria;
	}

	public Long contarLicitacaoParecerTecnico(Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida) {
		DetachedCriteria criteria = this.obterCriteriaPesquisa(numeroPac, descricaoPac, modalidade, vencida);		
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoLicitacao> pesquisarLicitacaoParecerTecnico(Integer firstResult,
					Integer maxResult, String order, boolean asc, 
					Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida) {
		
		DetachedCriteria criteria = this.obterCriteriaPesquisa(numeroPac, descricaoPac, modalidade, vencida);		
		return executeCriteria(criteria.addOrder(Order.asc("LCT."+ScoLicitacao.Fields.NUMERO.toString())), firstResult, maxResult, order, asc);
	}
}