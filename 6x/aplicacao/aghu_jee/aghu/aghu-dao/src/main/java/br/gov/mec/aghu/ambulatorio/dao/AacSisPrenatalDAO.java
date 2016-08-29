package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacSisPrenatal;

public class AacSisPrenatalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacSisPrenatal> {

	private static final long serialVersionUID = -3170721068599947777L;

	public List<AacSisPrenatal> pesquisarAacSisPrenatal(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSisPrenatal.class);

		criteria.add(Restrictions.eq(AacSisPrenatal.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

}
