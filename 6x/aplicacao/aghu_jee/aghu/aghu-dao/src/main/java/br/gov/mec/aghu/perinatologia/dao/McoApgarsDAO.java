package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoApgars;

public class McoApgarsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoApgars> {

	private static final long serialVersionUID = 2100542280803817540L;

	public List<McoApgars> listarApgarsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoApgars.class);

		criteria.add(Restrictions.eq(McoApgars.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<McoApgars> listarApgarsPorRecemNascido(Integer recemNascidoCodigoPaciente, Short recemNascidoSequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoApgars.class);

		criteria.add(Restrictions.eq(McoApgars.Fields.RECEM_NASCIDO_CODIGO_PACIENTE.toString(), recemNascidoCodigoPaciente));
		criteria.add(Restrictions.eq(McoApgars.Fields.RECEM_NASCIDO_SEQUENCE.toString(), recemNascidoSequence));

		return executeCriteria(criteria);
	}

	public List<McoApgars> listarApgarsPorRecemNascidoCodigoPaciente(Integer recemNascidoCodigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoApgars.class);

		criteria.add(Restrictions.eq(McoApgars.Fields.RECEM_NASCIDO_CODIGO_PACIENTE.toString(), recemNascidoCodigoPaciente));

		return executeCriteria(criteria);
	}
	
	public List<McoApgars> obterListaApgarPorCodigoPaciente(Integer codigoPaciente, Integer matricula, Short vinculo){
		DetachedCriteria criteria= DetachedCriteria.forClass(McoApgars.class);
		criteria.add(Restrictions.eq(McoApgars.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		if(matricula != null && vinculo != null) {
			criteria.add(Restrictions.eq(McoApgars.Fields.SER_MATRICULA.toString(), matricula));
			criteria.add(Restrictions.eq(McoApgars.Fields.SER_VIN_CODIGO.toString(), vinculo));
		}
		
		criteria.addOrder(Order.desc(McoApgars.Fields.ALTERADO_EM.toString()));
		criteria.addOrder(Order.desc(McoApgars.Fields.CRIADO_EM.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<McoApgars> obterListaApgarDoRecemNascido(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp){
		
		DetachedCriteria criteria= DetachedCriteria.forClass(McoApgars.class);
		
		criteria.add(Restrictions.eq(McoApgars.Fields.RNA_GSO_PAC_CODIGO.toString(), rnaGsoPacCodigo));
		criteria.add(Restrictions.eq(McoApgars.Fields.RNA_GSO_SEQP.toString(), rnaGsoSeqp));
		criteria.add(Restrictions.eq(McoApgars.Fields.RNA_SEQP.toString(), rnaSeqp));
		
		criteria.addOrder(Order.desc(McoApgars.Fields.ALTERADO_EM.toString()));
		criteria.addOrder(Order.desc(McoApgars.Fields.CRIADO_EM.toString()));

		return this.executeCriteria(criteria);
	}
	
}
