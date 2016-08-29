package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;

public class MtxPeriodoRetornoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxPeriodoRetorno> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5553280007265871288L;

	
	/**
	 * @param mtxPeriodoRetorno
	 * @param repeticao
	 * @return
	 * 
	 */
	
	//#49923  - C1
		public List<MtxPeriodoRetorno> pesquisarPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno, DominioRepeticaoRetorno repeticao, MtxPeriodoRetorno selecionado){
			DetachedCriteria criteria = DetachedCriteria.forClass(MtxPeriodoRetorno.class,"PRE");
			
			criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.TIPO_RETORNO.toString(), "TRE", JoinType.INNER_JOIN);		
			criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.LISTA_ITEM_PERIODO_RETORNO.toString(), "IPR", JoinType.INNER_JOIN);
					
			
			if(selecionado != null){
				//criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.LISTA_ITEM_PERIODO_RETORNO.toString(), "IPR", JoinType.INNER_JOIN);
				criteria.add(Restrictions.eq("PRE." + MtxTipoRetorno.Fields.SEQ.toString(), selecionado.getSeq()));
				
			}else{
				criteria.add(Restrictions.eq("TRE." + MtxTipoRetorno.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
				
				if(mtxPeriodoRetorno.getTipoRetorno().getIndTipo() !=null){
					criteria.add(Restrictions.eq("TRE." + MtxTipoRetorno.Fields.IND_TIPO.toString(),mtxPeriodoRetorno.getTipoRetorno().getIndTipo()));
				}
				
				if(mtxPeriodoRetorno.getTipoRetorno().getDescricao() != null){
					criteria.add(Restrictions.eq("TRE." + MtxTipoRetorno.Fields.DESCRICAO.toString(),mtxPeriodoRetorno.getTipoRetorno().getDescricao()));
				}
					
				if(mtxPeriodoRetorno.getIndSituacao() !=null){
					criteria.add(Restrictions.eq("PRE." + MtxPeriodoRetorno.Fields.IND_SITUACAO.toString(),DominioSituacao.getInstance(mtxPeriodoRetorno.getIndSituacao().isAtivo())));
				}
				
				if(repeticao !=null){
					DetachedCriteria subCriteria = DetachedCriteria.forClass(MtxItemPeriodoRetorno.class,"IPR2");
					
					subCriteria.add(Restrictions.eq("IPR2." + MtxItemPeriodoRetorno.Fields.IND_REPETICAO ,repeticao));
					
					subCriteria.add(Restrictions.eqProperty("IPR2."+ MtxItemPeriodoRetorno.Fields.PERIODO_RETORNO.toString(), "PRE." + MtxPeriodoRetorno.Fields.SEQ.toString()));
				
					subCriteria.setProjection(Projections.property("IPR2."+ MtxItemPeriodoRetorno.Fields.PERIODO_RETORNO.toString()));
				
					criteria.add(Subqueries.exists(subCriteria));
				}
				
				
			}
			criteria.addOrder(Order.desc("TRE." + MtxTipoRetorno.Fields.IND_TIPO.toString()));
			criteria.addOrder(Order.asc("TRE." + MtxTipoRetorno.Fields.DESCRICAO.toString()));
			criteria.addOrder(Order.asc("PRE." + MtxPeriodoRetorno.Fields.SEQ.toString()));
			
			if(selecionado != null){
				criteria.addOrder(Order.asc("IPR." + MtxItemPeriodoRetorno.Fields.ORDEM.toString()));
			}
			
			
			return executeCriteria(criteria);
			
		}
		
		//C2
		
		public List<MtxTipoRetorno> pesquisarRegistroPeriodoRetorno(DominioTipoRetorno indTipo, String descricao) {
				DetachedCriteria criteria = DetachedCriteria.forClass(MtxTipoRetorno.class);
			
				criteria.setProjection(Projections.projectionList()
						.add(Projections.property(MtxTipoRetorno.Fields.SEQ.toString()),MtxTipoRetorno.Fields.SEQ.toString())
						.add(Projections.property(MtxTipoRetorno.Fields.DESCRICAO.toString()),MtxTipoRetorno.Fields.DESCRICAO.toString()));
			
				if(!descricao.isEmpty()){
					if (StringUtils.isNumeric(descricao)) {
						criteria.add(Restrictions.or(Restrictions.eq(MtxTipoRetorno.Fields.SEQ.toString(), Integer.valueOf(descricao)),     
								(Restrictions.or(Restrictions.ilike(MtxTipoRetorno.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE)))));  
					}else{
						criteria.add(Restrictions.ilike(MtxTipoRetorno.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE));
					}
				}

				criteria.add(Restrictions.eq(MtxTipoRetorno.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
				criteria.add(Restrictions.eq(MtxTipoRetorno.Fields.IND_TIPO.toString(), indTipo));
				criteria.addOrder(Order.asc(MtxTipoRetorno.Fields.DESCRICAO.toString()));
				criteria.setResultTransformer(Transformers.aliasToBean(MtxTipoRetorno.class));
			
			return executeCriteria(criteria, 0, 100, null);
		}
		
		public Long pesquisarRegistroPeriodoRetornoCount(DominioTipoRetorno indTipo, String param) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MtxTipoRetorno.class);
			
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(MtxTipoRetorno.Fields.SEQ.toString()),MtxTipoRetorno.Fields.SEQ.toString())
					.add(Projections.property(MtxTipoRetorno.Fields.DESCRICAO.toString()),MtxTipoRetorno.Fields.DESCRICAO.toString()));
		
			if(!param.isEmpty()){
				if (StringUtils.isNumeric(param)) {
					criteria.add(Restrictions.or(Restrictions.eq(MtxTipoRetorno.Fields.SEQ.toString(), Integer.valueOf(param)),     
								(Restrictions.or(Restrictions.ilike(MtxTipoRetorno.Fields.DESCRICAO.toString(),param, MatchMode.ANYWHERE)))));  
				}else{
					criteria.add(Restrictions.ilike(MtxTipoRetorno.Fields.DESCRICAO.toString(),param, MatchMode.ANYWHERE));
				}
			}

			criteria.add(Restrictions.eq(MtxTipoRetorno.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq(MtxTipoRetorno.Fields.IND_TIPO.toString(), indTipo));
			criteria.addOrder(Order.asc(MtxTipoRetorno.Fields.DESCRICAO.toString()));
			criteria.setResultTransformer(Transformers.aliasToBean(MtxTipoRetorno.class));
			
			return executeCriteriaCount(criteria);
		}
		
		
		
		public List<MtxItemPeriodoRetorno> pesquisarItensPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno){
			DetachedCriteria criteria = DetachedCriteria.forClass(MtxPeriodoRetorno.class,"PRE");
			criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.TIPO_RETORNO.toString(), "TRE", JoinType.INNER_JOIN);		
			criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.LISTA_ITEM_PERIODO_RETORNO.toString(), "IPR", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("PRE." + MtxTipoRetorno.Fields.SEQ.toString(), mtxPeriodoRetorno.getSeq()));
					
			criteria.setProjection(Projections.projectionList()
			.add(Projections.property("IPR." + MtxItemPeriodoRetorno.Fields.SEQ.toString()),MtxItemPeriodoRetorno.Fields.SEQ.toString())
			.add(Projections.property("IPR." + MtxItemPeriodoRetorno.Fields.PERIODO_RETORNO.toString()),MtxItemPeriodoRetorno.Fields.PERIODO_RETORNO.toString())
			.add(Projections.property("IPR." + MtxItemPeriodoRetorno.Fields.ORDEM.toString()),MtxItemPeriodoRetorno.Fields.ORDEM.toString())
			.add(Projections.property("IPR." + MtxItemPeriodoRetorno.Fields.IND_REPETICAO.toString()),MtxItemPeriodoRetorno.Fields.IND_REPETICAO.toString())
			.add(Projections.property("IPR." + MtxItemPeriodoRetorno.Fields.QUANTIDADE.toString()),MtxItemPeriodoRetorno.Fields.QUANTIDADE.toString()));
			
			criteria.addOrder(Order.desc("TRE." + MtxTipoRetorno.Fields.IND_TIPO.toString()));
			criteria.addOrder(Order.asc("TRE." + MtxTipoRetorno.Fields.DESCRICAO.toString()));
			criteria.addOrder(Order.asc("PRE." + MtxPeriodoRetorno.Fields.SEQ.toString()));
			criteria.addOrder(Order.asc("IPR." + MtxItemPeriodoRetorno.Fields.ORDEM.toString()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(MtxItemPeriodoRetorno.class));
			
			return executeCriteria(criteria);
		}
		
		


}
