package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelResuCodifLws;

public class AelResuCodifLwsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResuCodifLws> {

	private static final long serialVersionUID = 3461970288592838676L;

	public boolean existeDependenciaResultadoCodificado(Integer resultadoCodificado, Integer grupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResuCodifLws.class);
		criteria.add(Restrictions.eq(AelResuCodifLws.Fields.SEQ.toString(), resultadoCodificado));
		criteria.add(Restrictions.eq(AelResuCodifLws.Fields.GTC_SEQ.toString(), grupo));
		return (executeCriteriaCount(criteria) > 0);
	}
}