package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPaSistPacientes;

public class AipPaSistPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPaSistPacientes> {

	private static final long serialVersionUID = -4966697989382198597L;

	public List<AipPaSistPacientes> listarPaSistPacientes(Integer codigoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPaSistPacientes.class);

		criteria.add(Restrictions.eq(AipPaSistPacientes.Fields.PAC_CODIGO.toString(), codigoOrigem));
		criteria.addOrder(Order.asc(AipPaSistPacientes.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}
	
	public Short buscaMaiorSeqp(Integer codigoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPaSistPacientes.class);
		
		criteria.add(Restrictions.eq(AipPaSistPacientes.Fields.PAC_CODIGO.toString(), codigoOrigem));
		criteria.setProjection(Projections.max(AipPaSistPacientes.Fields.SEQP.toString()));

		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
}
