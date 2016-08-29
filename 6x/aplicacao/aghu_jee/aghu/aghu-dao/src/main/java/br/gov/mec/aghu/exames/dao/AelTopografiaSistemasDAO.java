package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTopografiaSistemasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaSistemas> {
	
	private static final long serialVersionUID = 9067156687131891475L;

	public List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(null, filtro,situacao);
		criteria.addOrder(Order.asc(AelTopografiaSistemas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true); 
	}
	
	public List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final Integer seq, final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(seq, filtro,situacao);
		criteria.addOrder(Order.asc(AelTopografiaSistemas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	public Long pesquisarAelTopografiaSistemasCount(final Integer seq, final String filtro, final DominioSituacao situacao){
		return executeCriteriaCount(obterCriteria(seq, filtro, situacao));
	}
	
	private DetachedCriteria obterCriteria(final Integer seq, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaSistemas.class);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AelTopografiaSistemas.Fields.SEQ.toString(), seq));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(AelTopografiaSistemas.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelTopografiaSistemas.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTopografiaSistemas.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}