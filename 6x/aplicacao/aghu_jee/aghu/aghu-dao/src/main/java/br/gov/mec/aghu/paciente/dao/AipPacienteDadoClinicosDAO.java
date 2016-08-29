package br.gov.mec.aghu.paciente.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;

public class AipPacienteDadoClinicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacienteDadoClinicos> {

	private static final long serialVersionUID = 8871699857621445919L;

	public AipPacienteDadoClinicos obterDadosAdicionaisPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacienteDadoClinicos.class);
		criteria.add(Restrictions.eq(AipPacienteDadoClinicos.Fields.PAC_CODIGO.toString(), paciente.getCodigo()));

		return (AipPacienteDadoClinicos) this.executeCriteriaUniqueResult(criteria);
	}

}
