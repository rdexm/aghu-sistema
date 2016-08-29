package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AinTiposCaraterInternacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AinTiposCaraterInternacao> {

	private static final long serialVersionUID = 8560217453680127425L;

	public List<AinTiposCaraterInternacao> pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
			Object filtros) {
		return executeCriteria(obterCriteriaPesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
				filtros, false));
	}

	public Long pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(
			Object filtros) {
		return executeCriteriaCount(obterCriteriaPesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
				filtros, true));
	}

	private DetachedCriteria obterCriteriaPesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
			final Object filtros, final boolean isCount) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposCaraterInternacao.class);

		if (filtros != null && !filtros.toString().trim().isEmpty()) {
			if (CoreUtil.isNumeroInteger(filtros)) {
				criteria.add(Restrictions.or(Restrictions.eq(
						AinTiposCaraterInternacao.Fields.CODIGO.toString(),
						Integer.valueOf(filtros.toString())), Restrictions.eq(
						AinTiposCaraterInternacao.Fields.CODIGO_SUS.toString(),
						Integer.valueOf(filtros.toString()))));

			} else {

				if (DominioSimNao.S.toString().equalsIgnoreCase(
						filtros.toString())
						|| DominioSimNao.N.toString().equalsIgnoreCase(
								filtros.toString())) {
					criteria.add(Restrictions.or(Restrictions.ilike(
							AinTiposCaraterInternacao.Fields.DESCRICAO
									.toString(), filtros.toString(),
							MatchMode.ANYWHERE), Restrictions.eq(
							AinTiposCaraterInternacao.Fields.IND_CARATER
									.toString(), DominioSimNao.valueOf(filtros
									.toString()))));
				} else {
					criteria.add(Restrictions.ilike(
							AinTiposCaraterInternacao.Fields.DESCRICAO
									.toString(), filtros.toString(),
							MatchMode.ANYWHERE));
				}
			}
		}

		if (!isCount) {
			criteria.addOrder(Order
					.asc(AinTiposCaraterInternacao.Fields.DESCRICAO.toString()));
		}

		return criteria;
	}

	public List<AinTiposCaraterInternacao> pesquisaTiposCaraterInternacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao, Integer codigoSUS,
			DominioSimNao indCaraterCidSec) {
		DetachedCriteria criteria = createPesquisaTiposCaraterInternacaoCriteria(
				codigo, descricao, codigoSUS, indCaraterCidSec);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisaTiposCaraterInternacaoCount(Integer codigo,
			String descricao, Integer codigoSUS, DominioSimNao indCaraterCidSec) {
		return executeCriteriaCount(createPesquisaTiposCaraterInternacaoCriteria(
				codigo, descricao, codigoSUS, indCaraterCidSec));
	}

	private DetachedCriteria createPesquisaTiposCaraterInternacaoCriteria(
			Integer codigo, String descricao, Integer codigoSUS,
			DominioSimNao indCaraterCidSec) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposCaraterInternacao.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AinTiposCaraterInternacao.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposCaraterInternacao.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		if (codigoSUS != null) {
			criteria.add(Restrictions.eq(
					AinTiposCaraterInternacao.Fields.CODIGO_SUS.toString(),
					codigoSUS));
		}

		if (indCaraterCidSec != null) {
			criteria.add(Restrictions.eq(
					AinTiposCaraterInternacao.Fields.IND_CARATER.toString(),
					indCaraterCidSec));
		}

		return criteria;
	}

	public AinTiposCaraterInternacao obterTiposCaraterInternacao(
			Integer ainTiposCaraterInternacaoCodigo) {
		AinTiposCaraterInternacao retorno = this.obterPorChavePrimaria(ainTiposCaraterInternacaoCodigo);
		return retorno;
	}

}
