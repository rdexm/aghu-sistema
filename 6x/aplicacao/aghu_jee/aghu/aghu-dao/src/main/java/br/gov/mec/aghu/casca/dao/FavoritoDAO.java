package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.exception.BaseException;

public class FavoritoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Favorito> {

	private static final String USUARIO = "usuario.";
	private static final String MENU = "menu.";
	private static final long serialVersionUID = 8837312038767361166L;
	
	/**
	 * @deprecated utilizar {@link #obterFavoritos(String)}
	 * @param login
	 * @return
	 * @throws MECBaseException
	 */
	public List<Favorito> recuperaFavoritos(String login) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);
//		criteria.createAlias(Favorito.Fields.MENU.toString(), "menu");
//		criteria.createAlias("menu." + Menu.Fields.APLICACAO.toString(), "aplicacao");
//		criteria.createAlias(Favorito.Fields.USUARIO.toString(), "usuario");
//		
//		criteria.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
//		criteria.add(Restrictions.eq("usuario." + Usuario.Fields.ATIVO.toString(), Boolean.TRUE));		
//		criteria.add(Restrictions.eq("menu." + Menu.Fields.ATIVO.toString(), true));
//		criteria.addOrder(Order.asc("menu." + Menu.Fields.ORDEM.toString()));
//
//		return executeCriteria(criteria);
		return null;
	}
	
	/**
	 * Retorna os favoritos do usuário com login fornecido em que possui
	 * permissão.
	 * 
	 * @param login
	 * @return
	 * @throws MECBaseException
	 */
	public List<Favorito> obterFavoritos(String login) throws BaseException {
		/*
		DetachedCriteria permissoes = DetachedCriteria.forClass(Componente.class,"comp");
		permissoes.setProjection(Projections.property("id"));
		permissoes.createAlias(Componente.Fields.PERMISSOES_COMPONENTES.toString(), "permComp");
		permissoes.createAlias("permComp." + PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao"); 
		permissoes.createAlias("permissao." + Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfPerm");		
		permissoes.createAlias("permissao." + Permissao.Fields.PERMISSOES_MODULOS, "perfModulo");
		permissoes.createAlias("perfModulo." + PermissaoModulo.Fields.MODULO, "modulo");
		permissoes.createAlias("perfPerm." + PerfisPermissoes.Fields.PERFIL.toString(), "perfil");
		permissoes.createAlias("perfil." + Perfil.Fields.PERFIS_USUARIOS.toString(), "perfUsuario");
		permissoes.createAlias("perfUsuario." + PerfisUsuarios.Fields.USUARIO.toString(), "usuario");
		
		permissoes.add(Restrictions.or(
						Restrictions.isNull("perfUsuario." + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt("perfUsuario." + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())));
		permissoes.add(Restrictions.eq("perfil." + Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		permissoes.add(Restrictions.eq("modulo." + Modulo.Fields.ATIVO.toString(), Boolean.TRUE));
		permissoes.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		// relacao do menu com o componente/url
		permissoes.add(Restrictions.eqProperty("comp." + Componente.Fields.NOME.toString(), "menu."+Menu.Fields.URL.toString()));
		*/
		// favoritos do usuario
		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);

		criteria.createAlias(Favorito.Fields.MENU.toString(), "menu");
		criteria.createAlias(MENU + Menu.Fields.APLICACAO.toString(), "aplicacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(Favorito.Fields.USUARIO.toString(), "usuario");
		
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.ATIVO.toString(), Boolean.TRUE));		
		criteria.add(Restrictions.eq(MENU + Menu.Fields.ATIVO.toString(), true));
		criteria.addOrder(Order.asc(Favorito.Fields.ORDEM.toString()));
		
		// favoritos/menus que o usuario tem permissao
		//criteria.add(Subqueries.exists(permissoes));

		return executeCriteria(criteria, true);

	}
	
	
	
	public List<Integer> obterIdMenuFavoritos(String login)  {

		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);

		criteria.createAlias(Favorito.Fields.MENU.toString(), "menu");
		criteria.createAlias(MENU + Menu.Fields.APLICACAO.toString(), "aplicacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(Favorito.Fields.USUARIO.toString(), "usuario");
		
		criteria.setProjection(Projections.property("menu.id"));
		
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.ATIVO.toString(), Boolean.TRUE));		
		criteria.add(Restrictions.eq(MENU + Menu.Fields.ATIVO.toString(), true));
		
		criteria.addOrder(Order.asc(Favorito.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);

	}	
	
	
	public Favorito obterFavoritoIdMenu(String login, Integer menuId)  {

		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);

		criteria.createAlias(Favorito.Fields.MENU.toString(), "menu");
		criteria.createAlias(Favorito.Fields.USUARIO.toString(), "usuario");
		
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		criteria.add(Restrictions.eq(USUARIO + Usuario.Fields.ATIVO.toString(), Boolean.TRUE));		
		criteria.add(Restrictions.eq(MENU + Menu.Fields.ID.toString(), menuId));
		
		return (Favorito) executeCriteriaUniqueResult(criteria);

	}		
	
}
