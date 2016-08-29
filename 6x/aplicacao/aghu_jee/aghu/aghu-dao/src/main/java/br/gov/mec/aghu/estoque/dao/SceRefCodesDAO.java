package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatRefCodes;
import br.gov.mec.aghu.model.SceRefCodes;

public class SceRefCodesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceRefCodes> {

	private static final long serialVersionUID = 6108361842278399402L;

	private DetachedCriteria criarCriteriaSceRefCodesPorTipoOperConversao(final String valor, final String dominio) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceRefCodes.class);

		criteria.add(Restrictions.eq(SceRefCodes.Fields.RV_DOMAIN.toString(), dominio));

		final Criterion cri1 = Restrictions.and(Restrictions.isNull(SceRefCodes.Fields.RV_HIGH_VALUE.toString()),
				Restrictions.eq(FatRefCodes.Fields.RV_LOW_VALUE.toString(), valor));

		final Criterion cri2 = Restrictions.and(Restrictions.ge(SceRefCodes.Fields.RV_LOW_VALUE.toString(), valor),
				Restrictions.le(FatRefCodes.Fields.RV_HIGH_VALUE.toString(), valor));

		criteria.add(Restrictions.or(cri1, cri2));
		return criteria;
	}

	public List<SceRefCodes> buscarSceRefCodesPorTipoOperConversao(final String valor, final String dominio) {
		return executeCriteria(criarCriteriaSceRefCodesPorTipoOperConversao(valor,dominio));
	}

}