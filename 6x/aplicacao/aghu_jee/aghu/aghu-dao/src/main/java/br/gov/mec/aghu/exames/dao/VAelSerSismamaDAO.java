package br.gov.mec.aghu.exames.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAelSerSismama;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VAelSerSismamaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelSerSismama> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3692469377439605233L;

	public List<VAelSerSismama> pesquisarResponsavel(Object parametro) {
		DetachedCriteria criteria = obterCriteriaResponsavel(parametro);
		
		criteria.addOrder(Order.asc(VAelSerSismama.Fields.NOME.toString()));
		
		Map<String, Boolean> mapOrdenacao = new HashMap<String, Boolean>();
		
		return executeCriteria(criteria, 0, 100, mapOrdenacao);
	}
	
	public Long pesquisarResponsavelCount(Object parametro) {
		DetachedCriteria criteria = obterCriteriaResponsavel(parametro);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaResponsavel(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSerSismama.class);

		if (CoreUtil.isNumeroInteger(parametro)) {
			criteria.add(Restrictions.eq(VAelSerSismama.Fields.NRO_REG_CONSELHO.toString(), parametro.toString()));
		} else if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.or(
					Restrictions.ilike(VAelSerSismama.Fields.NOME.toString(), parametro.toString(), MatchMode.ANYWHERE), 
					Restrictions.ilike(VAelSerSismama.Fields.SIGLA.toString(), parametro.toString(), MatchMode.ANYWHERE)));
		}
		
		return criteria;
	}
	
	public VAelSerSismama pesquisarResponsavelCodigoMatricula(Short vinCodigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSerSismama.class);
		
		criteria.add(Restrictions.eq(VAelSerSismama.Fields.VIN_CODIGO.toString(), vinCodigo.intValue()));
		criteria.add(Restrictions.eq(VAelSerSismama.Fields.MATRICULA.toString(), matricula));
		return (VAelSerSismama) executeCriteriaUniqueResult(criteria);
	}
}
