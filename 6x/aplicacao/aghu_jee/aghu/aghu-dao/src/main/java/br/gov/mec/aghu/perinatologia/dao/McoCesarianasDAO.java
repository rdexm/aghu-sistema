package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoCesarianas;

public class McoCesarianasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoCesarianas> {

	private static final long serialVersionUID = -5509491500434666560L;

	public List<McoCesarianas> listarCesarianas(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoCesarianas.class);

		criteria.add(Restrictions.eq(McoCesarianas.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoCesarianas.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoCesarianas> listarCesarianasPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoCesarianas.class);

		criteria.add(Restrictions.eq(McoCesarianas.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	public McoCesarianas obterPorChavePrimaria(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoCesarianas.class);

		criteria.add(Restrictions.eq(McoCesarianas.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoCesarianas.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoCesarianas.Fields.SEQP.toString(), seqp));
		
		return (McoCesarianas) executeCriteriaUniqueResult(criteria);
	}
	
	protected DetachedCriteria obterCriteriaMcoCesarianas(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoCesarianas.class);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq(McoCesarianas.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq(McoCesarianas.Fields.SEQUENCE.toString(), gsoSeqp));
		}
		if (pSeqp != null) {
			criteria.add(Restrictions.eq(McoCesarianas.Fields.SEQP.toString(), pSeqp));
		}
		return criteria;
	}

	public McoCesarianas obterMcoCesarianas(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteriaUpdate = obterCriteriaMcoCesarianas(gsoPacCodigo, gsoSeqp, pSeqp);
		return (McoCesarianas) executeCriteriaUniqueResult(criteriaUpdate);
	}

	public Boolean verificaExisteCesariana(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoCesarianas(gsoPacCodigo, gsoSeqp, pSeqp);
		return executeCriteriaExists(criteria);
	}

}
