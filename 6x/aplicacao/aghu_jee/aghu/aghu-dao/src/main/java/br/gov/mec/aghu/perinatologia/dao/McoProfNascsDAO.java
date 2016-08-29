package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoProfNascs;

public class McoProfNascsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoProfNascs> {

	private static final long serialVersionUID = -3801541571307727240L;

	public List<McoProfNascs> listarProfNascs(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProfNascs.class);
		
		criteria.add(Restrictions.eq(McoProfNascs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoProfNascs.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoProfNascs> listarProfNascsPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProfNascs.class);
		
		criteria.add(Restrictions.eq(McoProfNascs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	public List<McoProfNascs> listarProfNascsPorNascimento(Integer pacCodigo, Short gsoSeqp, Integer nasSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProfNascs.class);

		criteria.add(Restrictions.eq(McoProfNascs.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoProfNascs.Fields.SEQUENCE.toString(), gsoSeqp));
		if (nasSeqp != null){
			criteria.add(Restrictions.eq(McoProfNascs.Fields.NAS_SEQP.toString(), nasSeqp));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Listar profissionais nascimento
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @param nasSeqp
	 * @return
	 */
	public List<McoProfNascs> listarProfNascs(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp){
		DetachedCriteria criteria = montarCriteriaProfNasc(gsoSeqp, gsoPacCodigo, nasSeqp);
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Existe profissionais nascimento
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @param nasSeqp
	 * @return
	 */
	public boolean existeProfNasc(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp){
		DetachedCriteria criteria = montarCriteriaProfNasc(gsoSeqp, gsoPacCodigo, nasSeqp);
		return executeCriteriaExists(criteria);
	}
	
	private DetachedCriteria montarCriteriaProfNasc(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProfNascs.class);
		criteria.add(Restrictions.eq(McoProfNascs.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoProfNascs.Fields.SEQUENCE.toString(), gsoSeqp));

		if (nasSeqp != null){
			criteria.add(Restrictions.eq(McoProfNascs.Fields.NAS_SEQP.toString(), nasSeqp));
		}
		
		return criteria;
	}
	
	
}
