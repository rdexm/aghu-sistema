package br.gov.mec.aghu.casca.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseCategoriaPerfil;
import br.gov.mec.aghu.model.CseCategoriaProfissional;

public class PerfisUsuariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PerfisUsuarios> {
	
	private static final long serialVersionUID = -8070122131059525939L;

	public boolean existePerfilUsuario(final String usuario, final String perfil) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);

		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p");
		criteria.createAlias(PerfisUsuarios.Fields.USUARIO.toString(), "u");

		criteria.add(Restrictions.ilike("p." + Perfil.Fields.NOME.toString(), perfil, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("u." + Usuario.Fields.NOME.toString(), usuario));
		
		return executeCriteriaExists(criteria);
	}
	
	public PerfisUsuarios pesquisarPerfisUsuariosPorUsuarioPerfil(final String usuario, final String perfil) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);

		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PerfisUsuarios.Fields.USUARIO.toString(), "u", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ilike("p." + Perfil.Fields.NOME.toString(), perfil, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ilike("u." + Usuario.Fields.NOME.toString(), usuario, MatchMode.ANYWHERE));

		return (PerfisUsuarios) this.executeCriteriaUniqueResult(criteria,true);
	}
	
	public PerfisUsuarios obterPerfilUsuarioLogin(final String usuario, final String perfil) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);

		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PerfisUsuarios.Fields.USUARIO.toString(), "u", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ilike("p." + Perfil.Fields.NOME.toString(), perfil, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ilike("u." + Usuario.Fields.LOGIN.toString(), usuario));

		return (PerfisUsuarios) this.executeCriteriaUniqueResult(criteria,true);
	}
	
	public PerfisUsuarios obterPerfilUsuario(final String usuario, final String perfil) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);

		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PerfisUsuarios.Fields.USUARIO.toString(), "u", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ilike("p." + Perfil.Fields.NOME.toString(), perfil, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("u." + Usuario.Fields.NOME.toString(), usuario));

		return (PerfisUsuarios) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Realiza pesquisa na entidade de vínculo entre perfis e usuários trazendo apenas
	 * os vínculados até uma data determinada.
	 * 
	 * @param perfil Perfil do sistema que se deseja obter os vínculos até a data
	 * @param dataCorte Data limite para filtrar os vínculos considerando apenas os que forem menor ou igual a valor deste parâmetro
	 * @return List<PerfisUsuarios> Listagem com o resultado da pesquisa 
	 */
	public List<PerfisUsuarios> listarPerfisUsuarios(final String perfil, final Date dataCorte) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);

		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.ilike("p." + Perfil.Fields.NOME.toString(), perfil, MatchMode.ANYWHERE));
		criteria.add(Restrictions.lt(PerfisUsuarios.Fields.DATA_CRIACAO.toString(), dataCorte));

		return this.executeCriteria(criteria);
	}

	public Long pesquisarPerfisAtivosDoUsuarioCount(Usuario usuario) {
		if (usuario == null || !usuario.isAtivo()) {
			return 0L;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class)
			.add(Restrictions.or(
					Restrictions.isNull(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
					Restrictions.gt(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
			.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(), usuario));
		criteria.createCriteria(PerfisUsuarios.Fields.PERFIL.toString())
			.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public List<PerfisUsuarios> pequisarPerfisUsuarios(Usuario usuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);		
		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p" , JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(),usuario));
		return executeCriteria(criteria,true);
	}
	
	public List<PerfisUsuarios> pequisarPerfisUsuariosSemCache(Usuario usuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);		
		criteria.createAlias(PerfisUsuarios.Fields.PERFIL.toString(), "p" , JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(),usuario));
		return executeCriteria(criteria);
	}
	
	/**
	 *  #39000 - Serviço que retorna existe servidor categoria prof medicos
	 * @param login
	 * @param categoriaProfMedico
	 * @return
	 */
	public Boolean existeServidorCategoriaProfMedico(String login , Integer categoriaProfMedico){
		DetachedCriteria criteria = DetachedCriteria.forClass(CseCategoriaPerfil.class, "ccp");
		criteria.createAlias("ccp."+ CseCategoriaPerfil.Fields.CSE_CATEGORIA_PROFISSIONAL, "cag");
		criteria.add(Restrictions.eq("ccp."+ CseCategoriaPerfil.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cag."+ CseCategoriaProfissional.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cag."+ CseCategoriaProfissional.Fields.SEQ.toString(), categoriaProfMedico));
		
		DetachedCriteria subCriteriaCscPerfil = DetachedCriteria.forClass(PerfisUsuarios.class, "perfilUsuario");
		subCriteriaCscPerfil.createAlias("perfilUsuario."+ PerfisUsuarios.Fields.PERFIL.toString(), "perfil");
		subCriteriaCscPerfil.createAlias("perfilUsuario."+ PerfisUsuarios.Fields.USUARIO.toString(), "usuario");
		subCriteriaCscPerfil.add(Restrictions.eqProperty("ccp."+CseCategoriaPerfil.Fields.PERFIL.toString() , "perfil." + Perfil.Fields.NOME.toString()));
		subCriteriaCscPerfil.add(Restrictions.eq("perfil."+ Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteriaCscPerfil.add(Restrictions.eq("usuario."+ Usuario.Fields.LOGIN.toString(), login));

		subCriteriaCscPerfil.setProjection(Projections.property("perfilUsuario."+ PerfisUsuarios.Fields.ID.toString()));		
		criteria.add(Subqueries.exists(subCriteriaCscPerfil));

		return executeCriteriaCount(criteria)>0;
		
		
	}
	
	public PerfisUsuarios pesquisarPerfisUsuariosPorUsuarioPerfil(Usuario usuario, Perfil perfil){
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);		
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(),usuario));
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.PERFIL.toString(),perfil));
		
		return (PerfisUsuarios) executeCriteriaUniqueResult(criteria);
	}
}
