package br.gov.mec.casca.app.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;
import br.gov.mec.seam.dao.GenericDAO;

public class UsuarioDAO extends GenericDAO<Usuario> {
	
	public enum UsuarioDAOExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_USUARIO_INATIVO
	}	

	public Usuario recuperarUsuario(String login) throws CascaException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		criteria.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		Usuario u = (Usuario) executeCriteriaUniqueResult(criteria);
		if (u != null && !u.isAtivo()) {
			throw new CascaException(UsuarioDAOExceptionCode.CASCA_MENSAGEM_USUARIO_INATIVO, login);
		}
		return u;
	}

	public List<Usuario> pesquisarUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nomeOuLogin) throws CascaException {
		return executeCriteria(criarCriteriaPesquisaUsuarios(nomeOuLogin),
				firstResult, maxResult, orderProperty, asc);
	}

	public Integer pesquisarUsuariosCount(String nomeOuLogin) {
		return executeCriteriaCount(criarCriteriaPesquisaUsuarios(nomeOuLogin));
	}

	/**
	 * Realiza a pesquisa dos logins não cadastrados no sistema.
	 * 
	 * @param logins
	 *            Lista de logins utilizados como filtro
	 * @return Uma lista com os logins não cadastrados.
	 */
	public List<String> pesquisarLoginsNaoCadastrados(List<String> logins) {
		if (logins == null || logins.isEmpty()) {
			return new ArrayList<String>();
		}
		List<String> loginsEmMaiusculo=new ArrayList<String>();
		for (String string : logins) {
			loginsEmMaiusculo.add(string.toUpperCase());
		}
		Query query = getSession().createQuery("Select usuario from br.gov.mec.casca.model.Usuario as usuario " +
				"where upper(usuario.login)  in (:varList)")  
		.setParameterList("varList",loginsEmMaiusculo);  
		List<Usuario> list = query.list();

		for (Usuario usuario : list) {
			loginsEmMaiusculo.remove(usuario.getLogin().toUpperCase());
		}

		return loginsEmMaiusculo;
	}

	public List<PerfisUsuarios> pequisarPerfisUsuarios(Usuario usuario) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PerfisUsuarios.class);
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(),
				usuario));
		return executeCriteria(criteria);
	}

	public void removerPerfilUsuario(PerfisUsuarios perfisUsuarios) {
		getEntityManager().remove(perfisUsuarios);
		getEntityManager().flush();
	}

	public void salvarPerfilUsuario(PerfisUsuarios perfisUsuarios) {
		getEntityManager().persist(perfisUsuarios);
		getEntityManager().flush();
	}
	
	public List<String> pesquisarUsuariosComPermissao(String permissao) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class, "usuario");
		
		criteria.setProjection(Projections.property(Usuario.Fields.LOGIN.toString()));
		
		criteria.createCriteria(Usuario.Fields.PERFIL_USUARIO.toString())
				.createCriteria(PerfisUsuarios.Fields.PERFIL.toString(), "perfil")
				.createCriteria(Perfil.Fields.PERFIS_PERMISSOES.toString())
				.createCriteria(PerfisPermissoes.Fields.PERMISSAO.toString(), "permissao")
				.add(Restrictions.eq(Permissao.Fields.NOME.toString(), permissao))
				.add(Restrictions.eq("permissao."+Permissao.Fields.ATIVO.toString(), DominioSituacao.A))
				.add(Restrictions.eq("perfil."+Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
				.add(Restrictions.eq("usuario"+Usuario.Fields.ATIVO.toString(), Boolean.TRUE));
		
	
		return this.executeCriteria(criteria);
	}

	/**
	 * Cria criteria para pesquisa de usuarios.
	 * 
	 * @param nomeOuLogin
	 *            Nome ou login do usuario
	 * @return Um DetachedCriteria pronto pra pesquisa.
	 */
	private DetachedCriteria criarCriteriaPesquisaUsuarios(String nomeOuLogin) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);

		if (!StringUtils.isBlank(nomeOuLogin)) {
			Criterion restrictionNome = Restrictions.ilike(
					Usuario.Fields.NOME.toString(), nomeOuLogin,
					MatchMode.ANYWHERE);
			Criterion restrictionLogin = Restrictions.ilike(
					Usuario.Fields.LOGIN.toString(), nomeOuLogin,
					MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(restrictionNome, restrictionLogin));
		}

		return criteria;
	}

	public List<String> pesquisarUsuariosPorPerfil(String perfil) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);

		criteria.setProjection(Projections.property(Usuario.Fields.LOGIN
				.toString()));
		
		criteria.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), true));

		criteria.createCriteria(Usuario.Fields.PERFIL_USUARIO.toString())
				.createCriteria(PerfisUsuarios.Fields.PERFIL.toString())
				.add(Restrictions.eq(Perfil.Fields.NOME.toString(),
						perfil)).add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		
	
				
		return this.executeCriteria(criteria);
	}
}
