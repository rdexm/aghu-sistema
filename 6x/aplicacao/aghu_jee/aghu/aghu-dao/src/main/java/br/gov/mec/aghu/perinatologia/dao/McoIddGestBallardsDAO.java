package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoIddGestBallards;

public class McoIddGestBallardsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIddGestBallards> {

	private static final long serialVersionUID = 1623090184713527123L;

	public List<McoIddGestBallards> listarIddGestBallardsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIddGestBallards.class);

		criteria.add(Restrictions.eq(McoIddGestBallards.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

}
