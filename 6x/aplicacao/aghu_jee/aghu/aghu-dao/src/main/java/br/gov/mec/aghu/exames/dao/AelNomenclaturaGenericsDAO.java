package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelNomenclaturaGenericsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNomenclaturaGenerics> {
	
	private static final long serialVersionUID = -5251084317585522234L;

	public List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final Integer seq, final String descricao, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(seq, descricao,situacao);
		criteria.addOrder(Order.asc(AelNomenclaturaGenerics.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}
	
	public List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(null, filtro,situacao);
		criteria.addOrder(Order.asc(AelNomenclaturaGenerics.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true); 
	}

	public Long pesquisarAelNomenclaturaGenericsCount(final String filtro, final DominioSituacao situacao){
		return executeCriteriaCount(obterCriteria(null, filtro,situacao));
	}
	
	private DetachedCriteria obterCriteria(final Integer seq, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelNomenclaturaGenerics.class);
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelNomenclaturaGenerics.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(seq != null){
			criteria.add(Restrictions.eq(AelNomenclaturaGenerics.Fields.SEQ.toString(), seq));
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelNomenclaturaGenerics.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelNomenclaturaGenerics.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}