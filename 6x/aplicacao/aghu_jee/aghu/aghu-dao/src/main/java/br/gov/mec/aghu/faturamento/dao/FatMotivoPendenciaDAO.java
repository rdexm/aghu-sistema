package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatMotivoPendencia;

public class FatMotivoPendenciaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoPendencia> {

	private static final long serialVersionUID = -2549677320746780555L;

	/**
	 * Lista os motivos de pendência por seq e/ou descricao
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	public List<FatMotivoPendencia> listarMotivosPendenciaPorSeqOuDescricao(
			final Object filtro) {

		return executeCriteria(getCriteriaSeqOuDescricao(filtro));
	}

	public Long listarMotivosPendenciaPorSeqOuDescricaoCount(final Object filtro) {

		return executeCriteriaCount(getCriteriaSeqOuDescricao(filtro));
	}

	/**
	 * Pesuisa MotivosPendencia
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param motivoPendencia
	 * @return
	 */
	public List<FatMotivoPendencia> pesquisarMotivosPendencia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatMotivoPendencia motivoPendencia) {
		
		DetachedCriteria criteria = obterCriteriaFiltro(motivoPendencia);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(FatMotivoPendencia.Fields.SEQ
					.toString()));
		}

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	private DetachedCriteria obterCriteriaFiltro(FatMotivoPendencia motivoPendencia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoPendencia.class);
		
		if (motivoPendencia.getSeq() != null){
			criteria.add(Restrictions.eq(FatMotivoPendencia.Fields.SEQ.toString(), motivoPendencia.getSeq()));
		}

		if (!StringUtils.isEmpty(motivoPendencia.getDescricao())){
			criteria.add(Restrictions.ilike(FatMotivoPendencia.Fields.DESCRICAO.toString(), motivoPendencia.getDescricao().trim(), MatchMode.ANYWHERE));
		}
		
		return criteria;
	}

	public Long pesquisarMotivosPendenciaCount(
			FatMotivoPendencia motivoPendencia) {
		DetachedCriteria criteria = obterCriteriaFiltro(motivoPendencia);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Criteria para listar os motivos de pendências por seq e descricao
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	private DetachedCriteria getCriteriaSeqOuDescricao(Object filtro) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatMotivoPendencia.class);

		if (filtro != null && !("").equals(filtro)) {

			if (StringUtils.isNumeric(filtro.toString())) {

				criteria.add(Restrictions.eq(
						FatMotivoPendencia.Fields.SEQ.toString(),
						Short.valueOf(filtro.toString())));
			} else {
				criteria.add(Restrictions.ilike(
						FatMotivoPendencia.Fields.DESCRICAO.toString(),
						filtro.toString(), MatchMode.ANYWHERE));
			}

		}

		return criteria;
	}
}
