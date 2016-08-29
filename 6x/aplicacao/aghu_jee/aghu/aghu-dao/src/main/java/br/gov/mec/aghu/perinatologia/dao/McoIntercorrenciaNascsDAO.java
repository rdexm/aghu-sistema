package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoIntercorrenciaNascs;

/**
 * DAO da entidade McoIntercorrenciaNascs
 * 
 * @author luismoura
 * 
 */
public class McoIntercorrenciaNascsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIntercorrenciaNascs> {

	private static final long serialVersionUID = -4069447373030654038L;

	public List<McoIntercorrenciaNascs> listarIntercorrenciasNascs(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaNascs.class);

		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoIntercorrenciaNascs> listarIntercorrenciasNascsPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaNascs.class);

		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	public List<McoIntercorrenciaNascs> listarIntercorrenciaNascPorCodSequenceSeq(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaNascs.class);

		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_SEQP.toString(), seqp));

		return executeCriteria(criteria);
	}
	
	/**
	 * Monta uma criteria por nascimento
	 * 
	 * C1 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 * @return
	 */
	private DetachedCriteria montarCriteriaPorNascimento(final Integer nasGsoPacCodigo, final Short nasGsoSeqp, final Integer nasSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaNascs.class);
		if (nasGsoPacCodigo != null) {
			criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_GSO_PAC_CODIGO.toString(), nasGsoPacCodigo));
		}
		if (nasGsoSeqp != null) {
			criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_GSO_SEQP.toString(), nasGsoSeqp));
		}
		if (nasSeqp != null) {
			criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_SEQP.toString(), nasSeqp));
		}
		return criteria;
	}

	/**
	 * Pesquisa McoIntercorrenciaNascs por nascimento
	 * 
	 * C1 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 * @return
	 */
	public List<McoIntercorrenciaNascs> pesquisarPorNascimento(final Integer nasGsoPacCodigo, final Short nasGsoSeqp, final Integer nasSeqp) {
		DetachedCriteria criteria = this.montarCriteriaPorNascimento(nasGsoPacCodigo, nasGsoSeqp, nasSeqp);
		criteria.addOrder(Order.asc(McoIntercorrenciaNascs.Fields.DTHR_INTERCORRENCIA.toString()));
		return super.executeCriteria(criteria);
	}
	
	public Boolean verificaExisteIntercorrencia(final Integer nasGsoPacCodigo, final Short nasGsoSeqp, final Integer nasSeqp) {
		DetachedCriteria criteria = this.montarCriteriaPorNascimento(nasGsoPacCodigo, nasGsoSeqp, nasSeqp);
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * Consulta utilizada para obter o SEQP para inser��o em MCO_INTERCORRENCIA_NSCS
	 * 
	 * C4 de #37859
	 */
	@Override
	protected void obterValorSequencialId(McoIntercorrenciaNascs elemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaNascs.class);
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_GSO_PAC_CODIGO.toString(), elemento.getId().getNasGsoPacCodigo()));
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_GSO_SEQP.toString(), elemento.getId().getNasGsoSeqp()));
		criteria.add(Restrictions.eq(McoIntercorrenciaNascs.Fields.NAS_SEQP.toString(), elemento.getId().getNasSeqp()));
		criteria.setProjection(Projections.max(McoIntercorrenciaNascs.Fields.SEQP.toString()));
		Short seqp = (Short) super.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 0;
		}
		elemento.getId().setSeqp(++seqp);
	}
}
