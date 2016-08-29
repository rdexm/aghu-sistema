package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipHistoriaFamiliares;

public class AipHistoriaFamiliaresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipHistoriaFamiliares> {

	private static final long serialVersionUID = -6906044358462258305L;

	public List<AipHistoriaFamiliares> pesquisarAipHistoriaFamiliares(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipHistoriaFamiliares.class);
		criteria.add(Restrictions.eq(AipHistoriaFamiliares.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
}
