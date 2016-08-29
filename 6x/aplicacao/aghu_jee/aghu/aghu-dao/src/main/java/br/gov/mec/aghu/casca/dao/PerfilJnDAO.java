package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.PerfilJn;
import br.gov.mec.aghu.casca.vo.FiltroPerfilJnVO;


public class PerfilJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PerfilJn> {

	private static final long serialVersionUID = 5434812963972003762L;

	/**
	 * Cria criteria para pesquisa de PerfilJn.
	 * 
	 * @param filtroPerfilJnVO
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisaPerfil(
			FiltroPerfilJnVO filtroPerfilJnVO) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PerfilJn.class);

		if (filtroPerfilJnVO != null) {
			if (filtroPerfilJnVO.getIdPerfil() != null) {
				criteria.add(Restrictions.eq(PerfilJn.Fields.ID.toString(),
						filtroPerfilJnVO.getIdPerfil()));
			}
			if (!StringUtils.isBlank(filtroPerfilJnVO.getNome())) {
				criteria.add(Restrictions.ilike(
						PerfilJn.Fields.NOME.toString(),
						filtroPerfilJnVO.getNome(), MatchMode.ANYWHERE));
			}
			if (filtroPerfilJnVO.getOperacao() != null) {
				criteria.add(Restrictions.eq(
						PerfilJn.Fields.OPERACAO.toString(),
						filtroPerfilJnVO.getOperacao()));
			}
			if (filtroPerfilJnVO.getDataInicio() != null) {
				criteria.add(Restrictions.ge(
						PerfilJn.Fields.DATA_ALTERACAO.toString(),
						filtroPerfilJnVO.getDataInicio()));
			}
			if (filtroPerfilJnVO.getDataFim() != null) {
				criteria.add(Restrictions.le(
						PerfilJn.Fields.DATA_ALTERACAO.toString(),
						filtroPerfilJnVO.getDataFim()));
			}
			if (!StringUtils.isBlank(filtroPerfilJnVO.getAlteradoPor())) {
				criteria.add(Restrictions.ilike(
						PerfilJn.Fields.NOME_USUARIO.toString(),
						filtroPerfilJnVO.getAlteradoPor()));
			}
		}
		return criteria;
	}

	public List<PerfilJn> pesquisarPorPerfil(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfilJnVO filtroPerfilJnVO) {
		return executeCriteria(
				this.criarCriteriaPesquisaPerfil(filtroPerfilJnVO),
				firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarPorPerfilCount(FiltroPerfilJnVO filtroPerfilJnVO) {
		return executeCriteriaCount(this
				.criarCriteriaPesquisaPerfil(filtroPerfilJnVO));
	}
}
