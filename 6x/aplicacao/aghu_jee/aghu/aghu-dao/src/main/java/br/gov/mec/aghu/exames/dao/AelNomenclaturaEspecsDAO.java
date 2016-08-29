package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelNomenclaturaEspecsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNomenclaturaEspecs> {
	
	private static final long serialVersionUID = 2887432972084948429L;

	public List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecsPorAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics){
		final DetachedCriteria criteria = obterCriteriaAelNomenclaturaEspecsPorAelNomenclaturaGenerics(aelNomenclaturaGenerics);
		criteria.addOrder(Order.asc(AelNomenclaturaEspecs.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaAelNomenclaturaEspecsPorAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelNomenclaturaEspecs.class);
		
		if(aelNomenclaturaGenerics != null){
			criteria.add(Restrictions.eq(AelNomenclaturaEspecs.Fields.AEL_NOMENCLATURA_GENERICS.toString(), aelNomenclaturaGenerics));	
		}
		return criteria;
	}
	
	public List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecs(final String filtro, final DominioSituacao situacao, final AelNomenclaturaGenerics aelNomenclaturaGenerics){
		final DetachedCriteria criteria = obterCriteria(filtro, situacao, aelNomenclaturaGenerics);
		criteria.addOrder(Order.asc(AelNomenclaturaEspecs.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true); 
	}

	public Long pesquisarAelNomenclaturaEspecsCount(final String filtro, final DominioSituacao situacao, final AelNomenclaturaGenerics aelNomenclaturaGenerics){
		return executeCriteriaCount(obterCriteria(filtro, situacao, aelNomenclaturaGenerics));
	}
	
	private DetachedCriteria obterCriteria(final String filtro, final DominioSituacao situacao, final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelNomenclaturaEspecs.class);
		
		criteria.add(Restrictions.eq(AelNomenclaturaEspecs.Fields.IND_SITUACAO.toString(), situacao));
		if(aelNomenclaturaGenerics != null){
			criteria.add(Restrictions.eq(AelNomenclaturaEspecs.Fields.AEL_NOMENCLATURA_GENERICS.toString(), aelNomenclaturaGenerics));	
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroShort(filtro)){
				criteria.add(Restrictions.eq(AelNomenclaturaEspecs.Fields.SEQP.toString(), Short.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelNomenclaturaEspecs.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public Short obterMaxSeqPPorAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelNomenclaturaEspecs.class);
		criteria.add(Restrictions.eq(AelNomenclaturaEspecs.Fields.AEL_NOMENCLATURA_GENERICS.toString(), aelNomenclaturaGenerics));
		criteria.setProjection(Projections.max(AelNomenclaturaEspecs.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}