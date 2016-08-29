package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAla;

public class AghAlaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghAla> {

	
	private static final long serialVersionUID = -7612610070638160166L;

	@SuppressWarnings("unchecked")
	public List<AghAla> pesquisaAlaList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String codigo, String descricao) {
		
		StringBuilder hql = new StringBuilder(50);
		hql.append(" select s ");
		hql.append(" from ");
		hql.append(" AghAla s ");

		Query query = this.getQuery(hql, codigo, descricao, true);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return query.getResultList();
	}

	public Long pesquisaAlaListCount(String codigo, String descricao) {
		StringBuilder hql = new StringBuilder(50);

		hql.append(" select ");
		hql.append(" count (*) ");
		hql.append(" from ");
		hql.append(" AghAla s ");

		Query query = this.getQuery(hql, codigo, descricao, false);

		return (Long) query.getSingleResult();
	}
	

	public List<AghAla> pesquisaAla(String codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAla.class);
		
		if (StringUtils.isNotEmpty(codigo)){
			criteria.add(Restrictions.eq(AghAla.Fields.CODIGO.toString(), codigo));
		}
		if (StringUtils.isNotEmpty(descricao)){
			criteria.add(Restrictions.ilike(AghAla.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		return executeCriteria(criteria);
	}
	
	
	
	private Query getQuery(StringBuilder hql, String sigla, String nome, boolean isOrder) {
		boolean buff = false;

		if (!StringUtils.isBlank(sigla) || !StringUtils.isBlank(nome)) {
			hql.append(" where ");

			if (!StringUtils.isBlank(sigla)) {
				hql.append(" ( s.codigo = :pSigla ");
				hql.append("   or s.codigo = :pSiglaUpper ");				
				hql.append(" )");
				buff = true;
			}
			if (!StringUtils.isBlank(nome)) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" ( s.descricao like :pNome ");
				hql.append("   or s.descricao like :pNomeUpper ");				
				hql.append(" )");
				buff = true;
			}
		}

		if (isOrder) {
			hql.append(" order by s.codigo ");
		}

		Query query = this.createQuery(hql.toString());

		if (!StringUtils.isBlank(sigla)) {
			query.setParameter("pSigla", sigla);
			query.setParameter("pSiglaUpper", sigla.toUpperCase());
		}
		if (!StringUtils.isBlank(nome)) {
			query.setParameter("pNome", "%".concat(nome).concat("%"));
			query.setParameter("pNomeUpper", "%".concat(nome.toUpperCase()).concat("%"));
		}

		return query;
	}
	

}
