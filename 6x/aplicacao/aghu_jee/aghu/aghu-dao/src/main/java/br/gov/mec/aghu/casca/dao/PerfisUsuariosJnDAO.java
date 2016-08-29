package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.PerfisUsuariosJn;
import br.gov.mec.aghu.casca.vo.FiltroPerfisUsuariosJnVO;


public class PerfisUsuariosJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PerfisUsuariosJn> {

	private static final long serialVersionUID = 186419881328411758L;

	/**
	 * Cria criteria para pesquisa de PerfisUsuariosJn.
	 * 
	 * @param filtroPerfisUsuariosJnVO
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisaPerfisUsuarios(
			FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuariosJn.class);

		if (filtroPerfisUsuariosJnVO != null) {
			if (filtroPerfisUsuariosJnVO.getIdUsuario() != null) {
				criteria.add(Restrictions.eq(PerfisUsuariosJn.Fields.ID_USUARIO.toString(),
						filtroPerfisUsuariosJnVO.getIdUsuario()));
			}
			if (filtroPerfisUsuariosJnVO.getIdPerfil() != null) {
				criteria.add(Restrictions.eq(PerfisUsuariosJn.Fields.ID_PERFIL.toString(),
						filtroPerfisUsuariosJnVO.getIdPerfil()));
			}
			if (!StringUtils.isBlank(filtroPerfisUsuariosJnVO.getLogin())) {
				criteria.add(Restrictions.ilike(
						PerfisUsuariosJn.Fields.LOGIN.toString(),
						filtroPerfisUsuariosJnVO.getLogin(), MatchMode.ANYWHERE));
			}
			if (!StringUtils.isBlank(filtroPerfisUsuariosJnVO.getNomePerfil())) {
				criteria.add(Restrictions.ilike(
						PerfisUsuariosJn.Fields.NOME_PERFIL.toString(),
						filtroPerfisUsuariosJnVO.getNomePerfil(), MatchMode.ANYWHERE));
			}
			if (filtroPerfisUsuariosJnVO.getOperacao() != null) {
				criteria.add(Restrictions.eq(
						PerfisUsuariosJn.Fields.OPERACAO.toString(),
						filtroPerfisUsuariosJnVO.getOperacao()));
			}
			if (filtroPerfisUsuariosJnVO.getDataInicio() != null) {
				criteria.add(Restrictions.ge(
						PerfisUsuariosJn.Fields.DATA_ALTERACAO.toString(),
						filtroPerfisUsuariosJnVO.getDataInicio()));
			}
			if (filtroPerfisUsuariosJnVO.getDataFim() != null) {
				criteria.add(Restrictions.le(
						PerfisUsuariosJn.Fields.DATA_ALTERACAO.toString(),
						filtroPerfisUsuariosJnVO.getDataFim()));
			}
			if (!StringUtils.isBlank(filtroPerfisUsuariosJnVO.getAlteradoPor())) {
				criteria.add(Restrictions.ilike(
						PerfisUsuariosJn.Fields.NOME_USUARIO.toString(),
						filtroPerfisUsuariosJnVO.getAlteradoPor()));
			}
		}
		return criteria;
	}

	public List<PerfisUsuariosJn> pesquisarPorPerfisUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		return executeCriteria(
				this.criarCriteriaPesquisaPerfisUsuarios(filtroPerfisUsuariosJnVO),
				firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarPorPerfisUsuariosCount(FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		return executeCriteriaCount(this
				.criarCriteriaPesquisaPerfisUsuarios(filtroPerfisUsuariosJnVO));
	}
}
