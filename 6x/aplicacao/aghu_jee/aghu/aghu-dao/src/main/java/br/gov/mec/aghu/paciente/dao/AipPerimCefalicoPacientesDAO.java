package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPerimCefalicoPacientes;

public class AipPerimCefalicoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPerimCefalicoPacientes> {

	private static final long serialVersionUID = -3684961360051101429L;

	public List<AipPerimCefalicoPacientes> listarPerimCefalicosPacientes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPerimCefalicoPacientes.class);

		criteria.add(Restrictions.eq(AipPerimCefalicoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(AipPerimCefalicoPacientes.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	public Short buscaMaiorSeqp(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPerimCefalicoPacientes.class);

		criteria.add(Restrictions.eq(AipPerimCefalicoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.max(AipPerimCefalicoPacientes.Fields.SEQP.toString()));

		return (Short) executeCriteriaUniqueResult(criteria);
	}

}
