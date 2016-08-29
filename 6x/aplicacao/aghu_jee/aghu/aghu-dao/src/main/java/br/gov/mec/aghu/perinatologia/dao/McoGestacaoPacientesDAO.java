package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoGestacaoPacientes;

public class McoGestacaoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoGestacaoPacientes> {
	
	private static final long serialVersionUID = -6977092629368208544L;

	public List<McoGestacaoPacientes> listarGestacoesPacientePorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacaoPacientes.class);
		
		criteria.add(Restrictions.eq(McoGestacaoPacientes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
}
