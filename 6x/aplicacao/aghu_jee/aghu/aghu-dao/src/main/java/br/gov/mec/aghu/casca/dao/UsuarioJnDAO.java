package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.UsuarioJn;
import br.gov.mec.aghu.casca.vo.FiltroUsuarioJnVO;


public class UsuarioJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<UsuarioJn> {

	private static final long serialVersionUID = 6445612981447986038L;

	/**
	 * Cria criteria para pesquisa de UsuarioJn.
	 * 
	 * @param filtroUsuarioJnVO
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisaUsuario(
			FiltroUsuarioJnVO filtroUsuarioJnVO) {

		DetachedCriteria criteria = DetachedCriteria.forClass(UsuarioJn.class);

		if (filtroUsuarioJnVO != null) {
			if (filtroUsuarioJnVO.getIdUsuario() != null) {
				criteria.add(Restrictions.eq(UsuarioJn.Fields.ID.toString(),
						filtroUsuarioJnVO.getIdUsuario()));
			}
			if (!StringUtils.isBlank(filtroUsuarioJnVO.getLogin())) {
				criteria.add(Restrictions.ilike(
						UsuarioJn.Fields.LOGIN.toString(),
						filtroUsuarioJnVO.getLogin(), MatchMode.ANYWHERE));
			}
			if (filtroUsuarioJnVO.getOperacao() != null) {
				criteria.add(Restrictions.eq(
						UsuarioJn.Fields.OPERACAO.toString(),
						filtroUsuarioJnVO.getOperacao()));
			}
			if (filtroUsuarioJnVO.getDataInicio() != null) {
				criteria.add(Restrictions.ge(
						UsuarioJn.Fields.DATA_ALTERACAO.toString(),
						filtroUsuarioJnVO.getDataInicio()));
			}
			if (filtroUsuarioJnVO.getDataFim() != null) {
				criteria.add(Restrictions.le(
						UsuarioJn.Fields.DATA_ALTERACAO.toString(),
						filtroUsuarioJnVO.getDataFim()));
			}
			if (!StringUtils.isBlank(filtroUsuarioJnVO.getAlteradoPor())) {
				criteria.add(Restrictions.ilike(
						UsuarioJn.Fields.NOME_USUARIO.toString(),
						filtroUsuarioJnVO.getAlteradoPor()));
			}
		}
		return criteria;
	}

	public List<UsuarioJn> pesquisarPorUsuario(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroUsuarioJnVO filtroUsuarioJnVO) {
		return executeCriteria(
				this.criarCriteriaPesquisaUsuario(filtroUsuarioJnVO),
				firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarPorUsuarioCount(FiltroUsuarioJnVO filtroUsuarioJnVO) {
		return executeCriteriaCount(this
				.criarCriteriaPesquisaUsuario(filtroUsuarioJnVO));
	}

}
