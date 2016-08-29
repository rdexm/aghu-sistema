package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacEspecialidadePmpa;

/**
 * @author cicero.artifon
 * 
 */
public class AacEspecialidadePmpaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacEspecialidadePmpa> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7686426094029575265L;

	/**
	 * @param seq
	 * @param codigo
	 * @return
	 */
	public AacEspecialidadePmpa obterEspecialidade(Short seq, Long codigo) {
		DetachedCriteria criteria = createPesquisaPorCodigoCriteria(seq, codigo);

		return (AacEspecialidadePmpa) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir do codigo e seq.
	 * 
	 * @param seq
	 * @param codigo
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaPorCodigoCriteria(Short seq,
			Long codigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacEspecialidadePmpa.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AacEspecialidadePmpa.Fields.CODIGO.toString(), codigo));
		}

		if (seq != null) {
			criteria.add(Restrictions.eq(
					AacEspecialidadePmpa.Fields.SEQ.toString(), seq));
		}
		return criteria;
	}

	public List<AacEspecialidadePmpa> listarEspecialidadePmpaPaginado(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seqEspecialidadePmpa,
			Long codigoEspecialidadePmpa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacEspecialidadePmpa.class);
		
		criteria.createAlias(
				AacEspecialidadePmpa.Fields.AGH_ESPECIALIDADES.toString(),
				"esp", JoinType.LEFT_OUTER_JOIN);
		
		criteria = this.obterCriteriaPesquisaEspecialidadePmpaPaginado(
				seqEspecialidadePmpa, codigoEspecialidadePmpa, criteria);

		criteria.addOrder(Order.asc(AacEspecialidadePmpa.Fields.SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long countPesquisarEspecialidadePmpaPaginado(Short filtroSeq,
			Long filtrocodigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacEspecialidadePmpa.class);
		
		criteria = this.obterCriteriaPesquisaEspecialidadePmpaPaginado(
				filtroSeq, filtrocodigo, criteria);

		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisaEspecialidadePmpaPaginado(
			Short seqEspecialidadePmpa, Long codigoEspecialidadePmpa,
			DetachedCriteria criteria) {

		if (seqEspecialidadePmpa != null) {
			
			criteria.add(Restrictions.eq(
					AacEspecialidadePmpa.Fields.SEQ.toString(),
					seqEspecialidadePmpa));
		}
		
		if (codigoEspecialidadePmpa != null) {
			
			criteria.add(Restrictions.eq(
					AacEspecialidadePmpa.Fields.CODIGO.toString(),
					codigoEspecialidadePmpa));
		}
		return criteria;
	}
}