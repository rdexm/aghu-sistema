package br.gov.mec.aghu.parametrosistema.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import br.gov.mec.aghu.model.AghSistemas;

public class AghSistemasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghSistemas> {

	private static final long serialVersionUID = -3748592901572163710L;

	private DetachedCriteria createPesquisarSistemaPorNomeCriteria(String nomeSistema) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(AghSistemas.class);
//		if (StringUtils.isNotBlank(nomeSistema)) {
//			criteria.add(Restrictions.or(Restrictions.ilike(AghSistemas.Fields.SIGLA.toString(), nomeSistema, MatchMode.EXACT)
//					, Restrictions.ilike(AghSistemas.Fields.NOME.toString(), nomeSistema,MatchMode.ANYWHERE)));
//		}
//		return criteria;
		
		SistemaPorNomeQueryBuilder builder = new SistemaPorNomeQueryBuilder();
		
		return builder.build(nomeSistema);
	}
	
	public List<AghSistemas> pesquisarSistemaPorNome(String nomeSistema, Integer maxResults) {
		DetachedCriteria criteria = createPesquisarSistemaPorNomeCriteria(nomeSistema);
		if(maxResults != null){
			return executeCriteria(criteria, 0, maxResults, AghSistemas.Fields.NOME.toString(), true);	
		} else {
			criteria.addOrder(Order.asc(AghSistemas.Fields.NOME.toString()));
			return executeCriteria(criteria);
		}
		
	}
		
	@SuppressWarnings("unchecked")
	public List<AghSistemas> pesquisaParametroSistemaList(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			String sigla, String nome) {

		StringBuilder hql = new StringBuilder(50);
		hql.append(" select s ")
		.append(" from ")
		.append(" AghSistemas s ");

		Query query = this.getQuerySistema(hql, sigla, nome, true);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return query.getResultList();
	}
	
	public Long pesquisaParametroSistemaListCount(String sigla, String nome) {
		StringBuilder hql = new StringBuilder(50);

		hql.append(" select ")
		.append(" count (*) ")
		.append(" from ")
		.append(" AghSistemas s ");

		Query query = this.getQuerySistema(hql, sigla, nome, false);

		return Long.valueOf(query.getSingleResult().toString());
	}

	private Query getQuerySistema(StringBuilder hql, String sigla, String nome, boolean isOrder) {
		boolean buff = false;

		if (!StringUtils.isBlank(sigla) || !StringUtils.isBlank(nome)) {
			hql.append(" where ");

			if (!StringUtils.isBlank(sigla)) {
				hql.append(" s.sigla = :pSigla ");
				buff = true;
			}
			if (!StringUtils.isBlank(nome)) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" s.nome like :pNome ");
				buff = true;
			}
		}

		if (isOrder) {
			hql.append(" order by s.nome ");
		}

		Query query = this.createQuery(hql.toString());

		if (!StringUtils.isBlank(sigla)) {
			query.setParameter("pSigla", sigla.toUpperCase());
		}
		if (!StringUtils.isBlank(nome)) {
			query.setParameter("pNome", "%".concat(nome.toUpperCase()).concat("%"));
		}

		return query;
	}
}
