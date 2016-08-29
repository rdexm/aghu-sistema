package br.gov.mec.casca.app.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.security.vo.PermissaoVO;
import br.gov.mec.seam.dao.GenericDAO;

public class PermissaoDAO extends GenericDAO<Permissao> {

	public List<Permissao> pesquisarPermissoes(String nome, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.ilike(Permissao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public List<Permissao> pesquisarPermissoesSuggestionBox(String busca) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.or(Restrictions.ilike(Permissao.Fields.NOME.toString(), busca,
				MatchMode.ANYWHERE), Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(),
				busca, MatchMode.ANYWHERE)));
		return executeCriteria(criteria, 0, 50, Permissao.Fields.NOME.toString(), true);
	}

	public List<Permissao> pesquisarTodasPermissoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		return executeCriteria(criteria);
	}

	public Integer pesquisarPermissoesCount(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		Integer totalRegistros = executeCriteriaCount(criteria);
		return totalRegistros;
	}

	public Integer pesquisarPermissoesCountSuggestionBox(String consulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.or(Restrictions.ilike(Permissao.Fields.NOME.toString(), consulta,
				MatchMode.ANYWHERE), Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(),
				consulta, MatchMode.ANYWHERE)));
		Integer totalRegistros = executeCriteriaCount(criteria);
		return totalRegistros;
	}

	public PermissoesComponentes obterComponentePermissao(Integer idPermissaoComponente)
			throws CascaException {
		return getEntityManager().find(PermissoesComponentes.class, idPermissaoComponente);
	}

	public void excluirPermissoesComponentes(PermissoesComponentes object) {
		getEntityManager().remove(object);
		getEntityManager().flush();
	}

	public List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes,
			String targets, String actions) {

		// FIXME TRATAR SE parametros forem null
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A));

		// Target
		DetachedCriteria criteriaPermissoesComponentes = criteria
				.createCriteria(
						Permissao.Fields.PERMISSOES_COMPONENTES.toString(),
						"permissaoComponente");

		DetachedCriteria criteriaComponente = criteriaPermissoesComponentes
				.createCriteria(
						PermissoesComponentes.Fields.COMPONENTE.toString(),
						"componente");
		criteriaComponente.add(Restrictions.ilike(
				Componente.Fields.NOME.toString(), targets));

		// Aplicacao
		DetachedCriteria criteriaAplicacao = criteriaComponente.createCriteria(
				Componente.Fields.APLICACAO.toString(), "aplicacao");

		if (aplicacoes != null) {
			criteriaAplicacao.add(Restrictions.ilike(
					Aplicacao.Fields.NOME.toString(), aplicacoes));
		}

		// Action
		criteriaPermissoesComponentes.createCriteria(
				PermissoesComponentes.Fields.METODO.toString(), "metodo")
				.add(Restrictions.ilike(Metodo.Fields.NOME.toString(), actions))
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), DominioSimNao.S));

		// Usuario
		DetachedCriteria criteriaUsuario = criteriaPermissoesComponentes
				.createCriteria(
						PermissoesComponentes.Fields.PERMISSAO.toString(),
						"permissao")
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(),
						"perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString())
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(),
						"perfilUsuario")
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString(),
						"usuario")
						.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));

		criteria.setProjection(Projections.projectionList()
				.add(Property.forName("componente.nome"), "target")
				.add(Property.forName("metodo.nome"), "action")
				.add(Property.forName("aplicacao.nome"), "aplicacao")
				.add(Property.forName("usuario.login"), "login"));

		criteria.setResultTransformer(Transformers
				.aliasToBean(PermissaoVO.class));

		return executeCriteria(criteria, true);
	}

	public List<PermissaoVO> obterPermissoes(String aplicacao, String target,
			String action) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A));

		// Target
		DetachedCriteria criteriaPermissoesComponentes = criteria
				.createCriteria(
						Permissao.Fields.PERMISSOES_COMPONENTES.toString(),
						"permissaoComponente");

		DetachedCriteria criteriaComponente = criteriaPermissoesComponentes
				.createCriteria(
						PermissoesComponentes.Fields.COMPONENTE.toString(),
						"componente");
		criteriaComponente.add(Restrictions.eq(
				Componente.Fields.NOME.toString(), target));

		// Aplicacao
		DetachedCriteria criteriaAplicacao = criteriaComponente.createCriteria(
				Componente.Fields.APLICACAO.toString(), "aplicacao");

		if (aplicacao != null) {
			criteriaAplicacao.add(Restrictions.eq(
					Aplicacao.Fields.NOME.toString(), aplicacao));
		}

		// Action
		criteriaPermissoesComponentes.createCriteria(
				PermissoesComponentes.Fields.METODO.toString(), "metodo")
				.add(Restrictions.eq(Metodo.Fields.NOME.toString(), action))
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), DominioSimNao.S));

		// Usuario
		DetachedCriteria criteriaUsuario = criteriaPermissoesComponentes
				.createCriteria(
						PermissoesComponentes.Fields.PERMISSAO.toString(),
						"permissao")
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(),
						"perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString())
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(),
						"perfilUsuario")
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString(),
						"usuario")
						.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));

		criteria.setProjection(Projections.projectionList()
				.add(Property.forName("componente.nome"), "target")
				.add(Property.forName("metodo.nome"), "action")
				.add(Property.forName("aplicacao.nome"), "aplicacao")
				.add(Property.forName("usuario.login"), "login"));

		criteria.setResultTransformer(Transformers
				.aliasToBean(PermissaoVO.class));

		return executeCriteria(criteria);

	}

	public List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		criteria.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), DominioSimNao.S));

		criteria.createCriteria(Metodo.Fields.COMPONENTE_SEM_ID.toString()).add(
				Restrictions.eq(Componente.Fields.ID.toString(), idComponente));

		if (!contem) {
			List<Metodo> metodosQueContem = recuperaMetodosComponentePermissao(idPermissao,
					idComponente, true);
			if (!metodosQueContem.isEmpty()) {
				for (Metodo metodo : metodosQueContem) {
					criteria.add(Restrictions.not(Restrictions.eq(Metodo.Fields.ID.toString(),
							metodo.getId())));
				}
			}
		} else {
			criteria.createCriteria(Metodo.Fields.PERMISSOES_COMPONENTES.toString(),
					Criteria.INNER_JOIN).add(
					Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
							idPermissao));
		}

		return executeCriteria(criteria);
	}

	/**
	 * 
	 * @param permissao
	 * @param componente
	 */
	public void removePermissaoComponenteMetodos(Permissao permissao, Componente componente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
				permissao.getId()));
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.COMPONENTE_ID.toString(),
				componente.getId()));
		List<PermissoesComponentes> lista = executeCriteria(criteria);
		for (PermissoesComponentes permissoesComponentes : lista) {
			getEntityManager().remove(permissoesComponentes);
		}
	}

	/**
	 * 
	 * @param permissao
	 * @param componente
	 * @param listaDeMetodos
	 */
	public void associarPermissaoComponenteMetodos(Permissao permissao, Componente componente,
			Metodo metodo) {
		PermissoesComponentes permissoesComponentes;
		permissoesComponentes = new PermissoesComponentes();
		permissoesComponentes.setComponente(componente);
		permissoesComponentes.setPermissao(permissao);
		permissoesComponentes.setMetodo(metodo);
		getEntityManager().persist(permissoesComponentes);
	}

	public Permissao obterPermissao(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.NOME.toString(), nome));
		return (Permissao) executeCriteriaUniqueResult(criteria);
	}

	public Permissao obterPermissao(String nome, String login) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(Permissao.Fields.NOME.toString(), nome));
		criteria.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString())
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString())
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString())
				.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));
		return (Permissao) executeCriteriaUniqueResult(criteria);
	}


	public List<PermissoesComponentes> obterPermissao(String login, String componente, String metodo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);

		DetachedCriteria criteriaComponente = criteria
				.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString());
		criteriaComponente.add(Restrictions.eq(Componente.Fields.NOME.toString(), componente));

		criteria.createCriteria(PermissoesComponentes.Fields.METODO.toString())
			.add(Restrictions.eq(Metodo.Fields.NOME.toString(), metodo))
			.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), DominioSimNao.S));

		criteria.createCriteria(PermissoesComponentes.Fields.PERMISSAO.toString())
				.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A))
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString())
				.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(), "perfilUsuario")
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));

		return executeCriteria(criteria);
	}

}
