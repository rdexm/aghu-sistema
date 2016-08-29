package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcMotivoAtraso;

public class MbcMotivoAtrasoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMotivoAtraso> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6790219942549485253L;

	/**
	 * Monta criteria da pesquisa paginada dos motivos de atraso
	 * 
	 * @param elemento
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisarMotivoAtraso(MbcMotivoAtraso elemento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMotivoAtraso.class);

		criteria.createAlias(MbcMotivoAtraso.Fields.SERVIDOR.toString(), "RAP");
		
		if (elemento.getSeq() != null) {
			criteria.add(Restrictions.eq(MbcMotivoAtraso.Fields.SEQ.toString(), elemento.getSeq()));
		}

		if (!StringUtils.isEmpty(elemento.getDescricao())) {
			criteria.add(Restrictions.ilike(MbcMotivoAtraso.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
		}

		if (elemento.getSituacao() != null) {
			criteria.add(Restrictions.eq(MbcMotivoAtraso.Fields.SITUACAO.toString(), elemento.getSituacao()));
		}

		return criteria;
	}

	/**
	 * Pesquisa paginada dos motivos de atraso
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<MbcMotivoAtraso> pesquisarMotivoAtraso(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcMotivoAtraso elemento) {

		DetachedCriteria criteria = montarCriteriaPesquisarMotivoAtraso(elemento);

		// Ordena pela descricao
		criteria.addOrder(Order.asc(MbcMotivoAtraso.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Contabiliza resultados da pesquisa paginada dos motivos de atraso
	 * 
	 * @param elemento
	 * @return
	 */
	public Long pesquisarMotivoAtrasoCount(MbcMotivoAtraso elemento) {
		DetachedCriteria criteria = montarCriteriaPesquisarMotivoAtraso(elemento);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa motivo de atraso por descrição
	 * 
	 * @param descricao
	 * @return
	 */
	public MbcMotivoAtraso pesquisarMotivoAtrasoPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMotivoAtraso.class);
		criteria.add(Restrictions.ilike(MbcMotivoAtraso.Fields.DESCRICAO.toString(), descricao));
		return (MbcMotivoAtraso) executeCriteriaUniqueResult(criteria);
	}

}
