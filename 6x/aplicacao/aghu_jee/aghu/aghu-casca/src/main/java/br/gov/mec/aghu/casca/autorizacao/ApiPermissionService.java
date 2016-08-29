package br.gov.mec.aghu.casca.autorizacao;

import java.util.Date;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApi;
import br.gov.mec.aghu.casca.model.TokensApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;
import br.gov.mec.aghu.service.seguranca.IApiPermissionService;

@Stateless
@Remote (IApiPermissionService.class)
public class ApiPermissionService implements IApiPermissionService {


	/**
	 * 
	 */
	private static final long serialVersionUID = 146037184236684192L;
	
	
	@Inject
	private DataAccessService dataAcess;

	@Override
	public Boolean verificarTokenAtivo(String token) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TokensApi.class);
		criteria.add(Restrictions.eq(TokensApi.Fields.TOKEN.toString(), token));
		criteria.add(Restrictions.gt(TokensApi.Fields.DATA_EXPIRACAO.toString(), new Date()));
		criteria.setProjection(Projections.count(TokensApi.Fields.SEQ.toString()));
		
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		return ((Long) executableCriteria.uniqueResult()) > 0L;
	}

	@Override
	public Boolean verificarPerfilToken(String token, Set<String> rolesSet) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuariosApi.class, "PUA");
		criteria.createAlias("PUA."+ PerfisUsuariosApi.Fields.USUARIO.toString(), "USU", JoinType.INNER_JOIN);
		criteria.createAlias("PUA."+ PerfisUsuariosApi.Fields.PERFIL.toString(), "PERF", JoinType.INNER_JOIN);
		criteria.createAlias("USU."+ UsuarioApi.Fields.TOKENS.toString(), "TOK", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("TOK."+ TokensApi.Fields.TOKEN.toString(), token));
		criteria.add(Restrictions.gt("TOK."+TokensApi.Fields.DATA_EXPIRACAO.toString(), new Date()));
		criteria.add(Restrictions.eq("USU."+UsuarioApi.Fields.ATIVO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PERF."+PerfilApi.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in("PERF."+PerfilApi.Fields.NOME.toString(), rolesSet));
		
		criteria.setProjection(Projections.count(TokensApi.Fields.SEQ.toString()));
		
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		return ((Long) executableCriteria.uniqueResult()) > 0L;
	}
}