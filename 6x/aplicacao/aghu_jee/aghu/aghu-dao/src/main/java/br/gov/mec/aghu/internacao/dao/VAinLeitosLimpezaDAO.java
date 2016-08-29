package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAinLeitosLimpeza;

public class VAinLeitosLimpezaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinLeitosLimpeza> {

	private static final long serialVersionUID = -4591469160729855512L;

	private DetachedCriteria obterCriteriaView(String leito, VAinLeitosLimpeza.Fields orderField) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinLeitosLimpeza.class);
		if (leito != null && StringUtils.isNotBlank(leito)) {
			criteria.add(Restrictions.eq(VAinLeitosLimpeza.Fields.LTO_ID.toString(), StringUtils.upperCase(leito)));
		}
		if (orderField != null) {
			criteria.addOrder(Order.asc(orderField.toString()));
		}
		return criteria;
	}

	public List<VAinLeitosLimpeza> pesquisarLeitosLimpeza(String leito, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = obterCriteriaView(leito, VAinLeitosLimpeza.Fields.LTO_ID);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<VAinLeitosLimpeza> pesquisarLeitosOrderByIdAndar() {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinLeitosLimpeza.class);
		criteria.addOrder(Order.asc(VAinLeitosLimpeza.Fields.LTO_ID.toString()));
		criteria.addOrder(Order.asc(VAinLeitosLimpeza.Fields.ANDAR_ALA_DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Método que obtém a lista de leitos limpeza.
	 * 
	 * @param leito
	 * @return
	 */
	public Long pesquisarLeitosLimpezaCount(String leito) {
		return executeCriteriaCount(obterCriteriaView(leito, null));
	}

}
