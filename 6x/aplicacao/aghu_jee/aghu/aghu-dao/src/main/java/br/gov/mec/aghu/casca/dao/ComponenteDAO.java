package br.gov.mec.aghu.casca.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;


public class ComponenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Componente> {

	private static final long serialVersionUID = -1843878256779201209L;
	
	@Inject
	private DataAccessService dataAcess;

	public List<Componente> pesquisarComponentePorNome(Object nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		String _nome = nome.toString();
		if (StringUtils.isNotBlank(_nome)) {
			criteria.add(Restrictions.like(Componente.Fields.NOME.toString(),
					_nome, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(Componente.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}

	public List<Componente> pesquisarTodosComponentes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		return executeCriteria(criteria);
	}

	public List<Componente> pesquisarComponentes(String nomeComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		criteria.createAlias(Componente.Fields.APLICACAO.toString(), "aplicacao");
		if (!StringUtils.isBlank(nomeComponente)) {
			criteria.add(Restrictions.like(Componente.Fields.NOME.toString(),
					nomeComponente, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(Menu.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisarMetodosComponenteCount(Integer idComponente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		if (idComponente != null) {
			criteria.add(Restrictions.eq(Metodo.Fields.COMPONENTE.toString(),
					idComponente));
		}
		return executeCriteriaCount(criteria);
	}

	public List<Metodo> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		if (idComponente != null) {
			criteria.add(Restrictions.eq(Metodo.Fields.COMPONENTE.toString(),
					idComponente));
		}
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisarComponentesCount(String nomeComponente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		if (!StringUtils.isBlank(nomeComponente)) {
			criteria.add(Restrictions.like(Componente.Fields.NOME.toString(),
					nomeComponente, MatchMode.ANYWHERE));
		}
		return executeCriteriaCount(criteria);
	}

	public List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, String target, String action,
			Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaPesquisaComponentesPermissao(
				idPermissao, target, action);

		List<PermissoesComponentes> listaRetorno = executeCriteria(criteria,
				firstResult, maxResults, orderProperty, asc);
		return listaRetorno;
	}

	public Long pesquisarComponentesPermissaoCount(Integer idPermissao,
			String target, String action) {
		DetachedCriteria criteria = obterCriteriaPesquisaComponentesPermissao(
				idPermissao, target, action);

		Long retorno = executeCriteriaCount(criteria);
		return retorno;
	}

	private DetachedCriteria obterCriteriaPesquisaComponentesPermissao(Integer idPermissao, String target, String action) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);

		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(),	idPermissao));
		criteria.createAlias(PermissoesComponentes.Fields.COMPONENTE.toString(), "c");
		criteria.createAlias(PermissoesComponentes.Fields.METODO.toString(), "m");

		if (StringUtils.isNotBlank(target)) {
			criteria.add(Restrictions.ilike("c." + Componente.Fields.NOME.toString(), target, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(action)) {
			criteria.add(Restrictions.ilike("m." + Metodo.Fields.NOME.toString(), action, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	public Metodo obterMetodo(Integer idMetodo) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		criteria.add(Restrictions.eq(Metodo.Fields.ID.toString(), idMetodo));
		return (Metodo) executeCriteriaUniqueResult(criteria,true);
	}

	public void excluirMetodo(Metodo metodo) throws ApplicationBusinessException {
		this.dataAcess.remove(metodo);
		this.flush();
	}

	public void salvarMetodo(Metodo metodo) {
		this.dataAcess.persist(metodo);
		this.flush();
	}

	public void atualizarMetodo(Metodo metodo) {
		this.dataAcess.update(metodo);
		this.flush();
	}
	
	

	
}
