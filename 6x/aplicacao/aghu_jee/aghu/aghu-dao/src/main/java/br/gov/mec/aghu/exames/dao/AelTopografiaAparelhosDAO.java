package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTopografiaAparelhosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaAparelhos> {
	
	private static final long serialVersionUID = -9040823408945690385L;

	public List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final String filtro, final DominioSituacao situacao, final AelTopografiaSistemas aelTopografiaSistemas){
		final DetachedCriteria criteria = obterCriteria(filtro, situacao, aelTopografiaSistemas);
		criteria.addOrder(Order.asc(AelTopografiaAparelhos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true); 
	}
	
	public List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final AelTopografiaSistemas aelTopografiaSistemas){
		final DetachedCriteria criteria = obterCriteria(null, null, aelTopografiaSistemas);
		criteria.addOrder(Order.asc(AelTopografiaAparelhos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	public Long pesquisarAelTopografiaAparelhosCount(final String filtro, final DominioSituacao situacao, final AelTopografiaSistemas aelTopografiaSistemas){
		return executeCriteriaCount(obterCriteria(filtro, situacao, aelTopografiaSistemas));
	}
	
	private DetachedCriteria obterCriteria(final String filtro, final DominioSituacao situacao, final AelTopografiaSistemas aelTopografiaSistemas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaAparelhos.class);
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelTopografiaAparelhos.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(aelTopografiaSistemas != null){
			criteria.add(Restrictions.eq(AelTopografiaAparelhos.Fields.AEL_TOPOGRAFIA_SISTEMAS.toString(), aelTopografiaSistemas));	
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroShort(filtro)){
				criteria.add(Restrictions.eq(AelTopografiaAparelhos.Fields.SEQP.toString(), Short.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTopografiaAparelhos.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public Short obterMaxSeqPPorAelTopografiaSistemas(final AelTopografiaSistemas aelTopografiaSistemas) {
		final DetachedCriteria criteria = obterCriteria(null, null, aelTopografiaSistemas);
		criteria.setProjection(Projections.max(AelTopografiaAparelhos.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}