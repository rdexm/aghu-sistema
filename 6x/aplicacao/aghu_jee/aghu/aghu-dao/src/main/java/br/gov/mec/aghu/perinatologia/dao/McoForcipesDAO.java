package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoNascimentos;

public class McoForcipesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoForcipes> {

	private static final long serialVersionUID = -7355792400691876783L;


	public List<McoForcipes> listarForcipes(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoForcipes.class);

		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_SEQP.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoForcipes> listarForcipesPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoForcipes.class);

		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	

	/**
	 * Estoria #15838
	 * 
	 * @param codigoPaciente
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public McoForcipes obterForcipe(Integer codigoPaciente, Short gsoSeqp, Integer seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoForcipes.class);

		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoNascimentos.Fields.SEQP.toString(), seqp));

		return (McoForcipes) executeCriteriaUniqueResult(criteria);
	}
	
	protected DetachedCriteria obterCriteriaMcoForcipes(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoForcipes.class);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq(McoForcipes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq(McoForcipes.Fields.SEQUENCE.toString(), gsoSeqp));
		}
		if (pSeqp != null) {
			criteria.add(Restrictions.eq(McoForcipes.Fields.SEQP.toString(), pSeqp));
		}
		return criteria;
	}

	public McoForcipes obterMcoForcipes(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteriaUpdate = obterCriteriaMcoForcipes(gsoPacCodigo, gsoSeqp, pSeqp);
		return (McoForcipes) executeCriteriaUniqueResult(criteriaUpdate);
	}

	public Boolean verificaExisteForcipe(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoForcipes(gsoPacCodigo, gsoSeqp, pSeqp);
		return executeCriteriaExists(criteria);
	}

}
