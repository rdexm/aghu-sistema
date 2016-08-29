package br.gov.mec.aghu.casca.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApi;
import br.gov.mec.aghu.casca.model.TokensApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class PerfisUsuariosApiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PerfisUsuariosApi> {
	
	private static final long serialVersionUID = -8070122131529535934L;

	public Boolean verificarPerfilToken(String token, Set<String> rolesSet) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuariosApi.class, "PUA");
		criteria.createAlias("PUA."+ PerfisUsuariosApi.Fields.USUARIO.toString(), "USU", JoinType.INNER_JOIN);
		criteria.createAlias("PUA."+ PerfisUsuariosApi.Fields.PERFIL.toString(), "PERF", JoinType.INNER_JOIN);
		criteria.createAlias("USU."+ UsuarioApi.Fields.TOKENS.toString(), "TOK", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("TOK."+ TokensApi.Fields.TOKEN.toString(), token));
		criteria.add(Restrictions.gt("TOK."+TokensApi.Fields.DATA_EXPIRACAO.toString(), new Date()));
		criteria.add(Restrictions.eq("USU."+UsuarioApi.Fields.ATIVO.toString(), 1));
		criteria.add(Restrictions.eq("PERF."+PerfilApi.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in("PERF."+PerfilApi.Fields.NOME.toString(), rolesSet));
		
		criteria.setProjection(Projections.count(TokensApi.Fields.SEQ.toString()));
		return ((Long) executeCriteriaUniqueResult(criteria)) > 0L;
	}
	
	public List<PerfisUsuariosApi> pequisarPerfisUsuariosApi(UsuarioApi usuarioApi) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuariosApi.class, "PU");		
		criteria.createAlias("PU."+PerfisUsuariosApi.Fields.USUARIO.toString(), "U" , JoinType.INNER_JOIN);
		criteria.createAlias("PU."+PerfisUsuariosApi.Fields.PERFIL.toString(), "P" , JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("U."+UsuarioApi.Fields.ID.toString(),usuarioApi.getId()));		
		return executeCriteria(criteria);
	}
}