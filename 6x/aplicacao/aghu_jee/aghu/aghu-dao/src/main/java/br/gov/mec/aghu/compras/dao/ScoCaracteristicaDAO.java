package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoCaracteristicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCaracteristica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2530319770929342438L;


	public List<ScoCaracteristica> pesquisarCaracteristicasPorCodigoOuDescricao(
			Object objPesquisa) {

		String strPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCaracteristica.class);

		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			codigo = Integer.valueOf(strPesquisa);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoCaracteristica.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(
					ScoCaracteristica.Fields.CARACTERISTICA.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(ScoCaracteristica.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);

	}
	
	
	public long pesquisarCaracteristicasPorCodigoOuDescricaoCount(
			Object objPesquisa) {

		String strPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCaracteristica.class);

		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			codigo = Integer.valueOf(strPesquisa);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoCaracteristica.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(
					ScoCaracteristica.Fields.CARACTERISTICA.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}
		
		return executeCriteriaCount(criteria);

	}
	
	public ScoCaracteristica obterCaracteristicaPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCaracteristica.class);
		
		criteria.add(Restrictions.eq(
				ScoCaracteristica.Fields.CARACTERISTICA.toString(),	nome));
		
		return (ScoCaracteristica) executeCriteriaUniqueResult(criteria);
	}
	
}
