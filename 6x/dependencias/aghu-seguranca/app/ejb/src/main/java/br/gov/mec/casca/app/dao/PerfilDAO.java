package br.gov.mec.casca.app.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.seam.dao.GenericDAO;

public class PerfilDAO extends GenericDAO<Perfil> {

	public List<Perfil> pesquisarPerfis(String nome)
			throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}

	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					Perfil.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (perfis != null && perfis.size() > 0) {
			criteria.add(Restrictions.in(Perfil.Fields.NOME.toString(), perfis));
		}
		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public List<Perfil> pesquisarPerfis(String nome, Integer firstResult,
			Integer maxResult, boolean asc) throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		String orderProperty = Perfil.Fields.NOME.toString();
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	public Integer pesquisarPerfisCount(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}
		return executeCriteriaCount(criteria);
	}
	public Integer pesquisarPerfilCountSuggestionBox(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					Perfil.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		return executeCriteriaCount(criteria);
	}

	public List<String> obterNomePerfisPorUsuario(String username) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		criteria.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString())
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), username).ignoreCase())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));
		criteria.setProjection(Projections.property(Perfil.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public List<PerfisPermissoes> pesquisarPerfisPermissoes(Perfil perfil) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PerfisPermissoes.class);
		criteria.add(Restrictions.eq("perfil", perfil));
		return executeCriteria(criteria);
	}

	public void remover(PerfisPermissoes elemento) {
		this.getEntityManager().remove(elemento);
		this.getEntityManager().flush();
	}

	public void salvarPerfisPermissoes(Permissao permissao, Perfil perfil) {
		PerfisPermissoes perfisPermissoes = new PerfisPermissoes(
				null, permissao, perfil);
		getEntityManager().persist(perfisPermissoes);
	}

	public Perfil pesquisrPerfil(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		criteria.add(Restrictions.eq(Perfil.Fields.NOME.toString(), nome));
		return (Perfil) executeCriteriaUniqueResult(criteria);
	}
}
