package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;



public class SceConversaoUnidadeConsumosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceConversaoUnidadeConsumos> {

	private static final long serialVersionUID = -5792205614687057068L;

	public List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumo(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), codigo));
		return executeCriteria(criteria);
	}
	
	public List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumoFiltro(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		criteria.createAlias(SceConversaoUnidadeConsumos.Fields.SCO_MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		
		if(material != null && material.getCodigo() != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), material.getCodigo()));
		}
		if(unidadeMedida != null && unidadeMedida.getCodigo() != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.UNIDADE_MEDIDA.toString(), unidadeMedida.getCodigo()));
		}
		if(fatorConversao != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.FATOR_CONVERSAO.toString(), fatorConversao));
		}
		
		criteria.addOrder(Order.asc(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString()));
		
		return executeCriteria(criteria,firstResult, maxResult, null, asc);
	}
	
	public Long listarConversaoUnidadeConsumoFiltroCount(ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		
		if(material != null && material.getCodigo() != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), material.getCodigo()));
		}
		if(unidadeMedida != null && unidadeMedida.getCodigo() != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.UNIDADE_MEDIDA.toString(), unidadeMedida.getCodigo()));
		}
		if(fatorConversao != null){
			criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.FATOR_CONVERSAO.toString(), fatorConversao));
		}
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtem conversao unidade através do material e unidade de medida
	 * @param material
	 * @param unidadeMedida
	 * @return
	 */
	public SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterialUnidadeMedida(ScoMaterial material, ScoUnidadeMedida unidadeMedida) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), material.getCodigo()));
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.UNIDADE_MEDIDA.toString(), unidadeMedida.getCodigo()));
		return (SceConversaoUnidadeConsumos) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem conversao unidade através do material
	 * @param material
	 * @return
	 */
	public SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterial(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), material.getCodigo()));
		return (SceConversaoUnidadeConsumos) executeCriteriaUniqueResult(criteria);
	}
	
	/**

	 * Pesquisa conversão para o material informado e filtra por código e descrição da unidade 

	 * @param matCodigo

	 * @param param

	 * @return

	 */

	public List<SceConversaoUnidadeConsumos> pesquisarConversaoPorMaterialCodigoUnidadeMaterialDescricao(Integer matCodigo, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class, "CUC");
		criteria.createAlias("CUC." + SceConversaoUnidadeConsumos.Fields.SCO_UNIDADE_MEDIDA.toString(), "UNI");
		String strPesquisa = (String) param;
		if (matCodigo != null) {
			criteria.add(Restrictions.eq("CUC." + SceConversaoUnidadeConsumos.Fields.MATERIAL, matCodigo));
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion criterionCodigo = Restrictions.ilike("UNI." + ScoUnidadeMedida.Fields.CODIGO.toString(), 
					StringUtils.trim(strPesquisa), MatchMode.EXACT);
			Criterion criterionDescricao = Restrictions.ilike("UNI." + ScoUnidadeMedida.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		return executeCriteria(criteria, 0, 100, null, true);
	}
}
