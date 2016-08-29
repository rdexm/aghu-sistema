package br.gov.mec.casca.app.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.seam.dao.GenericDAO;

public class ComponenteDAO extends GenericDAO<Componente> {

	public List<Componente> pesquisarComponentePorNome(Object nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		String _nome = "" + nome;
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
		if (!StringUtils.isBlank(nomeComponente)) {
			criteria.add(Restrictions.like(Componente.Fields.NOME.toString(),
					nomeComponente, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(Menu.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Integer pesquisarMetodosComponenteCount(Integer idComponente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		if (idComponente != null) {
			criteria.add(Restrictions.eq(Metodo.Fields.COMPONENTE.toString(),
					idComponente));
		}
		return executeCriteriaCount(criteria);
	}

	public List<Componente> pesquisarMetodosComponente(Integer idComponente,
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

	public Integer pesquisarComponentesCount(String nomeComponente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		if (!StringUtils.isBlank(nomeComponente)) {
			criteria.add(Restrictions.like(Componente.Fields.NOME.toString(),
					nomeComponente, MatchMode.ANYWHERE));
		}
		return executeCriteriaCount(criteria);
	}

	public List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PermissoesComponentes.class);
		criteria.add(Restrictions.eq(
				PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
				idPermissao));
		List<PermissoesComponentes> listaRetorno = executeCriteria(criteria,
				firstResult, maxResults, orderProperty, asc);
		return listaRetorno;
	}

	public Integer pesquisarComponentesPermissaoCount(Integer idPermissao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PermissoesComponentes.class);
		criteria.add(Restrictions.eq(
				PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
				idPermissao));
		Integer retorno = executeCriteriaCount(criteria);
		return retorno;
	}

	public Metodo obterMetodo(Integer idMetodo) throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		criteria.add(Restrictions.eq(Metodo.Fields.ID.toString(), idMetodo));
		return (Metodo) executeCriteriaUniqueResult(criteria);
	}

	public void excluirMetodo(Metodo metodo) throws CascaException {
		this.getEntityManager().remove(metodo);
		this.flush();
	}

	public void salvarMetodo(Metodo metodo) {
		this.getEntityManager().persist(metodo);
		this.flush();
	}

	public void atualizarMetodo(Metodo metodo) {
		this.getEntityManager().merge(metodo);
		this.flush();
	}
}
