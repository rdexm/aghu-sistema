package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class UsuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Usuario> {
	
	private static final long serialVersionUID = 2405592727712626072L;

	public enum UsuarioDAOExceptionCode implements BusinessExceptionCode {
		CASCA_ERRO_USUARIO_DUPLICADO;
	}
	
	
	public Usuario recuperarUsuario(String login) {
		return recuperarUsuario(login, true);
	}
	
	/**
	 * Recupera usuário da base conforme o login, considerando, ou não,
	 * se ele está em caixa alta ou baixo, conforme parâmetro.
	 * 
	 * @param login Username do usuário
	 * @param ignorarCaixa Caso true, ignora caixa alta ou baixa na busca
	 * por usuários, e false traz a usuário apenas se o login passado por 
	 * parâmetro coincidir exatamente com o da base de dados.
	 * @return Usuário recuperado da base conforme parâmetros informados.
	 */
	public Usuario recuperarUsuario(String login, boolean ignorarCaixa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		
		if (ignorarCaixa) {
			criteria.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		} else {
			criteria.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login));
		}
		
		return (Usuario) executeCriteriaUniqueResult(criteria, Boolean.TRUE);
	}


	public List<Usuario> pesquisarUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nomeOuLogin) throws ApplicationBusinessException {
		return executeCriteria(criarCriteriaPesquisaUsuarios(nomeOuLogin),
				firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarUsuariosCount(String nomeOuLogin) {
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
		Query query = createHibernateQuery("Select usuario from br.gov.mec.aghu.casca.model.Usuario as usuario " +
				"where upper(usuario.login)  in (:varList)")  
		.setParameterList("varList",loginsEmMaiusculo);  
		@SuppressWarnings("unchecked")
		List<Usuario> list = query.list();

		for (Usuario usuario : list) {
			loginsEmMaiusculo.remove(usuario.getLogin().toUpperCase());
		}

		return loginsEmMaiusculo;
	}
	
	public List<String> pesquisarUsuariosComPermissao(String permissao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		
		criteria.setProjection(Projections.property(Usuario.Fields.LOGIN.toString()));
		
		criteria.createCriteria(Usuario.Fields.PERFIL_USUARIO.toString())
				.createCriteria(PerfisUsuarios.Fields.PERFIL.toString())
				.createCriteria(Perfil.Fields.PERFIS_PERMISSOES.toString())
				.createCriteria(PerfisPermissoes.Fields.PERMISSAO.toString())
				.add(Restrictions.eq(Permissao.Fields.NOME.toString(), permissao));

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
				.add(Restrictions.or(
						Restrictions.isNull(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()), 
						Restrictions.gt(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
				.createCriteria(PerfisUsuarios.Fields.PERFIL.toString())
				.add(Restrictions.eq(Perfil.Fields.NOME.toString(),
						perfil)).add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Recupera os usuarios por nome ou login.
	 * 
	 * @param nomeOuLogin
	 * @return
	 */
	public List<Usuario> pesquisarUsuariosAssociaveis(String nomeOuLogin) {
		return criarCriteriaPesquisaUsuariosAssociaveis(nomeOuLogin);
	}
	
	/**
	 * Cria criteria para pesquisa de usuarios.
	 * 
	 * @param nomeOuLogin
	 *            Nome ou login do usuario
	 * @return Um DetachedCriteria pronto pra pesquisa.
	 */
	private List<Usuario> criarCriteriaPesquisaUsuariosAssociaveis(String nomeOuLogin) {
		
		DetachedCriteria subquery = DetachedCriteria.forClass(RapServidores.class, "serv");
		subquery.add(Restrictions.isNotNull("serv." + RapServidores.Fields.USUARIO.toString()));
		subquery.setProjection(Projections.distinct(Projections.property("serv."+ RapServidores.Fields.USUARIO.toString())));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class, "usr");
		if (!StringUtils.isBlank(nomeOuLogin)) {
			Criterion restrictionNome = Restrictions.ilike(
					Usuario.Fields.NOME.toString(), nomeOuLogin,
					MatchMode.ANYWHERE);
			Criterion restrictionLogin = Restrictions.ilike(
					Usuario.Fields.LOGIN.toString(), nomeOuLogin,
					MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(restrictionNome, restrictionLogin));
		}
		criteria.add(Subqueries.propertyNotIn(Usuario.Fields.LOGIN.toString(), subquery));
		criteria.addOrder(Order.asc(Usuario.Fields.LOGIN.toString()));		
		
		return this.executeCriteria(criteria);
	}	
	
	/**
	 * Pesquisa por usuários que possuam um certo nome e que não sejam de um certo id
	 * @param nome
	 * @param id
	 * @return
	 */
	public List<Usuario> pesquisarUsuarioMesmoNome(String nome, Integer id){
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		criteria.add(Restrictions.eq(Usuario.Fields.NOME.toString(), nome));
		if (id != null){
			criteria.add(Restrictions.ne(Usuario.Fields.ID.toString(), id));			
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * #44276 
	 * C7 - Busca usuário na tabela CSC_USUARIO
	 * @return
	 */
	public Usuario buscarUsuarioPorLogin(String loginUsuario){
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		
		//criteria.setProjection(Projections.property(Usuario.Fields.ID.toString()));
		
		criteria.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), loginUsuario));
		
		criteria.setResultTransformer(Transformers.aliasToBean(Usuario.class));
		
		return (Usuario)executeCriteriaUniqueResult(criteria); 
	}
	
}
