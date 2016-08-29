package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoPartos;

public class McoPartosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoPartos> {

	private static final long serialVersionUID = -4315410151942886540L;

	public List<McoPartos> listarPartosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPartos.class);

		criteria.add(Restrictions.eq(McoPartos.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

}
