package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPaDiastPacientes;

public class AipPaDiastPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPaDiastPacientes> {
	
	private static final long serialVersionUID = -137282431447587244L;

	public List<AipPaDiastPacientes> listarPaDiastPacientes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPaDiastPacientes.class);
		
		criteria.add(Restrictions.eq(AipPaDiastPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(AipPaDiastPacientes.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}
	
	public Short buscaMaiorSeqp(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPaDiastPacientes.class);
		
		criteria.add(Restrictions.eq(AipPaDiastPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.max(AipPaDiastPacientes.Fields.SEQP.toString()));

		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
}
