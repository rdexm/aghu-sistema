package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class UsuarioApiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<UsuarioApi> {
	
	private static final long serialVersionUID = 2405542727612636022L;

	public UsuarioApi obterUsuarioApi(String authUsuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UsuarioApi.class);
		criteria.add(Restrictions.eq(UsuarioApi.Fields.AUTH_USUARIO.toString(), authUsuario));
		return (UsuarioApi)executeCriteriaUniqueResult(criteria);
	}
	
	public List<UsuarioApi> pesquisarUsuariosApi(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return executeCriteria(criarCriteriaPesquisaUsuariosApi(nome, email, loginHcpa, situacao),
				firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarUsuariosApiCount(String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return executeCriteriaCount(criarCriteriaPesquisaUsuariosApi(nome, email, loginHcpa, situacao));
	}

	private DetachedCriteria criarCriteriaPesquisaUsuariosApi(String nome, String email, String loginHcpa, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UsuarioApi.class);

		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.eq(UsuarioApi.Fields.NOME.toString(), nome));
		}
		
		if (!StringUtils.isBlank(email)) {
			criteria.add(Restrictions.eq(UsuarioApi.Fields.EMAIL.toString(), email));
		}

		if (!StringUtils.isBlank(loginHcpa)) {
			criteria.add(Restrictions.eq(UsuarioApi.Fields.LOGIN_HCPA.toString(), loginHcpa));
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(UsuarioApi.Fields.ATIVO.toString(), situacao.isAtivo() ? 1 : 0));
		}
		
		return criteria;
	}
}