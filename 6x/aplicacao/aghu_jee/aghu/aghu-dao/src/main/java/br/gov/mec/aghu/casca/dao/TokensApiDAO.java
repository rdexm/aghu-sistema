package br.gov.mec.aghu.casca.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.TokensApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;

public class TokensApiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<TokensApi> {
	
	private static final long serialVersionUID = 2405542725612626032L;
	
	public Long contarTokensAtivos(UsuarioApi usuarioApi) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TokensApi.class);
		criteria.add(Restrictions.eq(TokensApi.Fields.USUARIO_API.toString(), usuarioApi));
		criteria.add(Restrictions.gt(TokensApi.Fields.DATA_EXPIRACAO.toString(), new Date()));
		criteria.setProjection(Projections.count(TokensApi.Fields.SEQ.toString()));
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificarTokenAtivo(String token) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TokensApi.class);
		criteria.add(Restrictions.eq(TokensApi.Fields.TOKEN.toString(), token));
		criteria.add(Restrictions.gt(TokensApi.Fields.DATA_EXPIRACAO.toString(), new Date()));
		criteria.setProjection(Projections.count(TokensApi.Fields.SEQ.toString()));
		return ((Long) executeCriteriaUniqueResult(criteria)) > 0L;
	}
	
	public TokensApi obterTokenAcesso(String refreshToken) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TokensApi.class);
		criteria.createAlias(TokensApi.Fields.USUARIO_API.toString(), "USU", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(TokensApi.Fields.REFRESH_TOKEN.toString(), refreshToken));
		criteria.add(Restrictions.eq(TokensApi.Fields.IND_REFRESH.toString(), 0));
		
		return ((TokensApi) executeCriteriaUniqueResult(criteria));
	}
	
	
}