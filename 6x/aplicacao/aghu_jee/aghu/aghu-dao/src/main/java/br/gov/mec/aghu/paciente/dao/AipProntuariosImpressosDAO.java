package br.gov.mec.aghu.paciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipProntuariosImpressos;

public class AipProntuariosImpressosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipProntuariosImpressos> {

	private static final long serialVersionUID = -8867986155421376022L;

	public List<AipProntuariosImpressos> obterProntImpressos(Date dtReferencia) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipProntuariosImpressos.class);

		criteria.add(Restrictions.eq(AipProntuariosImpressos.Fields.DT_REFERENCIA.toString(), DateUtils.truncate(
				dtReferencia, Calendar.DAY_OF_MONTH)));

		return executeCriteria(criteria);
	}

}
