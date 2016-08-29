package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PerfilApiDAO extends BaseDao<PerfilApi> {

	private static final long serialVersionUID = 6615538062217457336L;

	public List<PerfilApi> pesquisarPerfilApi(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nome, String descricao, DominioSituacao situacao) {
		return executeCriteria(criarCriteriaPesquisaPerfilApi(nome, descricao, situacao),
				firstResult, maxResult, orderProperty, asc);
	}

	public List<PerfilApi> pesquisarPerfisApiSuggestionBox(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfilApi.class);
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					PerfilApi.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(PerfilApi.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		criteria.add(Restrictions.eq(PerfilApi.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(PerfilApi.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarPerfilApiCountSuggestionBox(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfilApi.class);
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					PerfilApi.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(PerfilApi.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		criteria.add(Restrictions.eq(PerfilApi.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarPerfilApiCount(String nome, String descricao, DominioSituacao situacao) {
		return executeCriteriaCount(criarCriteriaPesquisaPerfilApi(nome, descricao, situacao));
	}

	private DetachedCriteria criarCriteriaPesquisaPerfilApi(String nome, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfilApi.class);

		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.eq(PerfilApi.Fields.NOME.toString(), nome));
		}
		
		if (!StringUtils.isBlank(descricao)) {
			criteria.add(Restrictions.ilike(PerfilApi.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(UsuarioApi.Fields.ATIVO.toString(), situacao.isAtivo() ? 1 : 0));
		}
		
		return criteria;
	}
}