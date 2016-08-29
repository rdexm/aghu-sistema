package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoTrabPartos;

public class McoTrabPartosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoTrabPartos> {

	private static final long serialVersionUID = -3301309078254895727L;

	public List<McoTrabPartos> listarTrabPartos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoTrabPartos.class);

		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoTrabPartos> listarTrabPartosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoTrabPartos.class);

		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaMcoTrabPartos(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoTrabPartos.class);
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(McoTrabPartos.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(McoTrabPartos.Fields.GSO_SEQP.toString(), seqp));
		}
		return criteria;
	}
	
	public McoTrabPartos obterMcoTrabPartosPorId(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = obterCriteriaMcoTrabPartos(pacCodigo, seqp);
		return (McoTrabPartos) executeCriteriaUniqueResult(criteria);
	}

}
