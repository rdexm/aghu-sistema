package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;

public class AbsOrientacaoComponentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsOrientacaoComponentes>{
	

	
	private static final long serialVersionUID = 2621251210285004144L;

	//Lista recuperada para paginaçao. 
	public List<AbsOrientacaoComponentes> pesquisarOrientacaoComponentes(Integer firstResult, Integer maxResult, String orderProperty, 
																	boolean asc, Short seq, String descricao, DominioSituacao situacao, String componenteSanguineo){
		
		DetachedCriteria criteria = montaCriteriaAbsOrientacaoComponentes(seq, descricao, situacao, componenteSanguineo);
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	//count da lista de paginaçao.
	public Long pesquisarOrientacaoComponentesCount(Short seq, String descricao, DominioSituacao situacao, String componenteSanguineo){

		DetachedCriteria criteria = montaCriteriaAbsOrientacaoComponentes(seq, descricao, situacao, componenteSanguineo);
		
		return this.executeCriteriaCount(criteria);
	}

	//Criteria para paginação. Segundo parametros.
	public DetachedCriteria montaCriteriaAbsOrientacaoComponentes(Short seq, String descricao, DominioSituacao situacao, String csaCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsOrientacaoComponentes.class);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AbsOrientacaoComponentes.Fields.SEQP.toString(), seq));
		}
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(AbsOrientacaoComponentes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE ));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AbsOrientacaoComponentes.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(StringUtils.isNotBlank(csaCodigo)){
			criteria.add(Restrictions.eq(AbsOrientacaoComponentes.Fields.CSA_CODIGO.toString(), csaCodigo));
		}
		return criteria;
	}

	public Short pesquisarMaxSeqp(String csaCodigo){

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsOrientacaoComponentes.class);
		criteria.setProjection(Projections.projectionList().add(Projections.max(AbsOrientacaoComponentes.Fields.SEQP.toString())));
		
		criteria.add(Restrictions.eq(AbsOrientacaoComponentes.Fields.CSA_CODIGO.toString(), csaCodigo));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}	
}