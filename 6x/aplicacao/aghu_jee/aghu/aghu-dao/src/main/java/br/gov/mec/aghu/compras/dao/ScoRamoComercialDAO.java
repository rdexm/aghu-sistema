package br.gov.mec.aghu.compras.dao;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.compras.vo.ScoRamoComercialCriteriaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Classe DAO para Ramos Comerciais
 * 
 * @author mlcruz
 */
public class ScoRamoComercialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoRamoComercial> {
	private static final long serialVersionUID = 5062520402270291756L;

	
	/**
		 * SugestionBox Ramos Comerciais
		 * @param param
		 * @return
		 */
		public List<ScoRamoComercial> listarRamosComerciaisSugestion(Object param) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoRamoComercial.class);
			if (StringUtils.isNotBlank((String) param)) {
				if(CoreUtil.isNumeroInteger(param)) {
					criteria.add(Restrictions.eq(ScoRamoComercial.Fields.CODIGO.toString(),	Short.valueOf(param.toString())));
				} else {
					criteria.add(Restrictions.ilike(ScoRamoComercial.Fields.DESCRICAO.toString(),(String)param,MatchMode.ANYWHERE));
				}
			}
			criteria.add(Restrictions.eq(ScoRamoComercial.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
			//criteria.addOrder(Order.asc(ScoRamoComercial.Fields.DESCRICAO.toString()));
			return executeCriteria(criteria, 0, 100, ScoRamoComercial.Fields.DESCRICAO.toString(), true);
		}
	/**
	 * Busca ramos comerciais.
	 * 
	 * @param criteria Critério de busca.
	 * @param firstResult 
	 * @param maxResults 
	 * @param orderProperty 
	 * @param asc 
	 * @return Ramos comerciais.
	 */
	public List<ScoRamoComercial> pesquisarScoRamosComerciais(
			ScoRamoComercialCriteriaVO criteria, 
			int firstResult, int maxResults, 
			String orderProperty, boolean asc) {
		DetachedCriteria detachedCriteria = getCriteria(criteria);
		
		detachedCriteria.addOrder(Order.asc(
				ScoRamoComercial.Fields.CODIGO.toString()));
		
		return executeCriteria(detachedCriteria, firstResult, 
				maxResults, orderProperty, asc);
	}

	/**
	 * Conta ramos comerciais encontrados pela busca.
	 * 
	 * @param criteria Critério de busca.
	 * @return Número de ramos comerciais.
	 */
	public Long contarScoRamosComerciais(
			ScoRamoComercialCriteriaVO criteria) {
		return executeCriteriaCount(getCriteria(criteria));
	}
	
	/**
	 * Verifica se existe outro ramo comercial 
	 * cadastrado com a mesma descrição.
	 * 
	 * @param ramo Ramo Comercial.
	 * @return true, se existe; false, se não.
	 */
	public Boolean existeScoRamoComercial(ScoRamoComercial ramo) {
		DetachedCriteria criteria = DetachedCriteria
			.forClass(ScoRamoComercial.class);
		
		if (ramo.getCodigo() != null) {
			criteria.add(Restrictions.ne(
					ScoRamoComercial.Fields.CODIGO.toString(), 
					ramo.getCodigo()));
		}
		
		criteria.add(Restrictions.ilike(
				ScoRamoComercial.Fields.DESCRICAO.toString(), 
				ramo.getDescricao(), 
				MatchMode.EXACT));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	private DetachedCriteria getCriteria(ScoRamoComercialCriteriaVO base) {
		DetachedCriteria criteria = DetachedCriteria
			.forClass(ScoRamoComercial.class);

		if (base.getCodigo() != null) {
			criteria.add(Restrictions.eq(
					ScoRamoComercial.Fields.CODIGO.toString(), 
					base.getCodigo()));
		}
		
		if (isNotBlank(base.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoRamoComercial.Fields.DESCRICAO.toString(), 
					base.getDescricao(), 
					MatchMode.ANYWHERE));
		}
		
		if (base.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoRamoComercial.Fields.IND_SITUACAO.toString(), 
					base.getSituacao()));
		}
		
		return criteria;
	}
}
