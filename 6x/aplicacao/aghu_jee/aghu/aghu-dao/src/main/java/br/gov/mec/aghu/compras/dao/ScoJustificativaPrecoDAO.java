package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoJustificativaPrecoDAO extends BaseDao<ScoJustificativaPreco> {
	
	private static final long serialVersionUID = -8025056739057213598L;
	
	public List<ScoJustificativaPreco> pesquisarJustificativas(
			Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			final ScoJustificativaPreco justificativa)
	{
		DetachedCriteria criteria = this.obterCriteriaBasica(justificativa);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarJustificativasCount(final ScoJustificativaPreco justificativa) {
		DetachedCriteria criteria = this.obterCriteriaBasica(justificativa);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(ScoJustificativaPreco justificativa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoJustificativaPreco.class, "JP");

		if (justificativa != null) {
			if (justificativa.getCodigo() != null) {
				criteria.add(Restrictions.eq(ScoJustificativa.Fields.CODIGO.toString(), justificativa.getCodigo()));
			}

			if (StringUtils.isNotBlank(justificativa.getDescricao())) {
				criteria.add(Restrictions.ilike(ScoJustificativa.Fields.DESCRICAO.toString(), justificativa.getDescricao(), MatchMode.ANYWHERE));
			}

			if (justificativa.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(ScoJustificativa.Fields.IND_SITUACAO.toString(), justificativa.getIndSituacao()));
			}
		}
		return criteria;
	}
	

}
