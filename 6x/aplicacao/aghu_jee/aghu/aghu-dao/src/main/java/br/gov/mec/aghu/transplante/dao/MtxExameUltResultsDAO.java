package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.MtxExameUltResults;

public class MtxExameUltResultsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxExameUltResults>{

	private static final long serialVersionUID = -899149612772512765L;


	public MtxExameUltResults obterMtxExameUltResultsExameLaudo(String exameSigla, Integer laudoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExameUltResults.class);
		
		criteria.add(Restrictions.eq(MtxExameUltResults.Fields.CAMPO_LAUDO_SEQ.toString(), laudoSeq));
		criteria.add(Restrictions.eq(MtxExameUltResults.Fields.AEL_EXAMES_SIGLA.toString(), exameSigla));
		
		return (MtxExameUltResults) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MtxExameUltResults> pesquisarExamesLaudosCampo(String exameSigla, Integer laudoSeq, DominioSituacao situacao, 
			  												   Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		
		DetachedCriteria criteria = obterCriteria(exameSigla, laudoSeq, situacao);
		
		criteria.createAlias("EUR."+MtxExameUltResults.Fields.AEL_EXAMES.toString(), "EXA", JoinType.INNER_JOIN);
		criteria.createAlias("EUR."+MtxExameUltResults.Fields.CAMPO_LAUDO.toString(), "CAL", JoinType.INNER_JOIN);
		
		if(StringUtils.isBlank(orderProperty)){
			criteria.addOrder(Order.asc("EXA."+AelExames.Fields.DESCRICAO.toString()));
		}else if(orderProperty.equals("aelExames")){
			if(asc){
				criteria.addOrder(Order.asc("EXA."+AelExames.Fields.DESCRICAO.toString()));
			}else{
				criteria.addOrder(Order.desc("EXA."+AelExames.Fields.DESCRICAO.toString()));
			}
			orderProperty = null;
		} 
		 
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarExamesLaudosCampoCount(String exameSigla, Integer laudoSeq, DominioSituacao situacao){
		DetachedCriteria criteria = obterCriteria(exameSigla, laudoSeq, situacao);
		criteria.createAlias("EUR."+MtxExameUltResults.Fields.AEL_EXAMES.toString(), "EXA", JoinType.INNER_JOIN);
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria obterCriteria(String exameSigla, Integer laudoSeq, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExameUltResults.class, "EUR");
		
		if(StringUtils.isNotBlank(exameSigla)){
			criteria.add(Restrictions.ilike(MtxExameUltResults.Fields.AEL_EXAMES_SIGLA.toString(), exameSigla));
		}
		
		if(laudoSeq != null){
			criteria.add(Restrictions.eq(MtxExameUltResults.Fields.CAMPO_LAUDO_SEQ.toString(), laudoSeq));		
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(MtxExameUltResults.Fields.SITUACAO.toString(), situacao));
		}
		
		return criteria;
	}
	
	/**
	 * #47146 - C2
	 * Consulta para obter os tipos de exames
	 */
	public List<MtxExameUltResults> obterTiposExames(){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExameUltResults.class);
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(MtxExameUltResults.Fields.SIGLA.toString()));
		projectionList.add(Projections.property(MtxExameUltResults.Fields.CAMPO_LAUDO.toString()));
		
		criteria.add(Restrictions.eq(MtxExameUltResults.Fields.SITUACAO.toString(),DominioSituacao.A));
		return executeCriteria (criteria);
	}
}
