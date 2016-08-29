package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MtxCriterioPriorizacaoTmoDAO extends BaseDao<MtxCriterioPriorizacaoTmo> {

       private static final long serialVersionUID = -1664311034465814291L;
       
       private static final String CPT_PONTO = "CPT.";

       public MtxCriterioPriorizacaoTmo verificarExistenciaRegistro(Integer cidSeq){
             DetachedCriteria criteria  = DetachedCriteria.forClass(MtxCriterioPriorizacaoTmo.class);
//             criteria.add(Restrictions.eq(MtxCriterioPriorizacaoTmo.Fields.CID_SEQ.toString(), cidSeq));
             return (MtxCriterioPriorizacaoTmo) executeCriteriaUniqueResult(criteria);
       }
       
   	public List<CriteriosPriorizacaoAtendVO> pesquisarCriteriosPriorizacaoAtendimento(CriteriosPriorizacaoAtendVO filtro, Integer firstResult, 
			Integer maxResults, String orderProperty, boolean asc){		
   		
		DetachedCriteria criteria = criteriaCriteriosPriorizacaoAtendimento(filtro);	
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString()));
		}
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	

	public Long pesquisarCriteriosPriorizacaoAtendimentoCount(CriteriosPriorizacaoAtendVO filtro){
		DetachedCriteria criteria = criteriaCriteriosPriorizacaoAtendimento(filtro);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criteriaCriteriosPriorizacaoAtendimento(CriteriosPriorizacaoAtendVO filtro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxCriterioPriorizacaoTmo.class, "CPT");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.SEQ.toString()),
				CriteriosPriorizacaoAtendVO.Fields.SEQ.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString()),
				CriteriosPriorizacaoAtendVO.Fields.GRAVIDADE.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.CRITICIDADE.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.IND_SITUACAO.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.SITUACAO.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.TIPO_TMO.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.TIPO_TMO.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.STATUS.toString());
		criteria.setProjection(projList);
		
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));			
		}
		
		if(filtro.getCriticidade() != null ){
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString(), filtro.getCriticidade()));
		}
		
		if(filtro.getGravidade() != null){
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString(), filtro.getGravidade()));
		}
		
		if(filtro.getStatus() != null){
			criteria.add(Restrictions.ilike(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString(), filtro.getStatus(),MatchMode.ANYWHERE));
		}
		
		if(filtro.getTipoTmo() != null){
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.TIPO_TMO.toString(), filtro.getTipoTmo()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(CriteriosPriorizacaoAtendVO.class));
		
		return criteria;
	}
	
	/**#46495 C6 - Obtem uma lista de status de doenças do paciente**/
	public List<MtxCriterioPriorizacaoTmo> obterStatusDoencaPaciente(DominioSituacaoTmo tipo){
		DetachedCriteria criteria = criteriaObterStatusDoencaPaciente(tipo);
		return executeCriteria(criteria);		
	}
	
	//Monta a consulta C6 da estória #46495
	private DetachedCriteria criteriaObterStatusDoencaPaciente(DominioSituacaoTmo tipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxCriterioPriorizacaoTmo.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MtxCriterioPriorizacaoTmo.Fields.SEQ.toString())
						.as(MtxCriterioPriorizacaoTmo.Fields.SEQ.toString()))
				.add(Projections.property(MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString())
						.as(MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString())));
		
		criteria.add(Restrictions.eq(MtxCriterioPriorizacaoTmo.Fields.TIPO_TMO.toString(), tipo));
		criteria.add(Restrictions.eq(MtxCriterioPriorizacaoTmo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MtxCriterioPriorizacaoTmo.class));
		
		return criteria;
	}
	/**#46495 C4 - Obtem a gravidade e a criticidade para realizar o calculo do coeficiente**/
	public MtxCriterioPriorizacaoTmo obterCoeficiente(Integer statusDoenca){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxCriterioPriorizacaoTmo.class);
		
		criteria.add(Restrictions.eq(MtxCriterioPriorizacaoTmo.Fields.SEQ.toString(), statusDoenca));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString())
						.as(MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString()))
				.add(Projections.property(MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString())
						.as(MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MtxCriterioPriorizacaoTmo.class));
		return (MtxCriterioPriorizacaoTmo)executeCriteriaUniqueResult(criteria);
	}

}

