package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoAchadoExameFisicos;

public class McoAchadoExameFisicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoAchadoExameFisicos> {

	private static final long serialVersionUID = 7806168950654114063L;

	public List<McoAchadoExameFisicos> listarAchadosExamesFisicos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAchadoExameFisicos.class);

		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAchadoExameFisicos.class);

		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	
	public List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPacienteGsoSeqpSepq(
			Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAchadoExameFisicos.class);

		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoAchadoExameFisicos.Fields.EFR_RNA_SEQP.toString(), seqp));
		

		return executeCriteria(criteria);
	}

}
