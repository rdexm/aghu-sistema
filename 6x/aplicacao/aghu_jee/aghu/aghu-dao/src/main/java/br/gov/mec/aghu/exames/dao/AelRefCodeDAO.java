package br.gov.mec.aghu.exames.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelRefCode;

public class AelRefCodeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRefCode> {

	private static final long serialVersionUID = -6747452286166741733L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelRefCode.class);
    }
	
	public List<AelRefCode> obterCodigosPorDominio(String dominio) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRefCode.Fields.RV_DOMAIN.toString(), dominio));
		return executeCriteria(criteria);
	}

	public Integer verificarValorValido(String valor, String dominio) {
		StringBuilder hql = new StringBuilder(50);
		
		hql.append(" select ");
		hql.append(" count (*) ");
		hql.append(" from ");
		hql.append(AelRefCode.class.getSimpleName()).append(" o ");

		Query query = getQueryValorValido(hql, valor, dominio);
		
		return Integer.valueOf(query.getSingleResult().toString());
	}

	private Query getQueryValorValido(StringBuilder hql, String valor, String dominio) {
		hql.append(" where ");
		hql.append(" ( ( ");
		hql.append(" o.").append(AelRefCode.Fields.RV_HIGH_VALUE.toString()).append(" is null ");
		hql.append(" and ");  
		hql.append(" o.").append(AelRefCode.Fields.RV_LOW_VALUE.toString()).append(" = :parametro ");
		hql.append(" ) ");
		hql.append(" or ");
		hql.append(" :parametro ").append(" between ").append(AelRefCode.Fields.RV_LOW_VALUE.toString()).append(" and ").append(AelRefCode.Fields.RV_HIGH_VALUE.toString());
		hql.append(" ) ");
		hql.append(" and ").append(AelRefCode.Fields.RV_DOMAIN.toString()).append(" = :dominio");
		
		Query query = createQuery(hql.toString());
		
		query.setParameter("parametro", valor);
		query.setParameter("dominio", dominio);
		
		return query;
	}

	public List<AelRefCode> obterCodigosPorDominioELowValue(String dominio, String lowValue) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelRefCode.Fields.RV_DOMAIN.toString(), dominio));
		criteria.add(Restrictions.eq(AelRefCode.Fields.RV_LOW_VALUE.toString(), lowValue));
		return executeCriteria(criteria);
	}
}