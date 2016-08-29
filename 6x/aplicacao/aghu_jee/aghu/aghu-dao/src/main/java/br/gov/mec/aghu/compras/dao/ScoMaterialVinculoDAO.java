package br.gov.mec.aghu.compras.dao;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialVinculo;

public class ScoMaterialVinculoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMaterialVinculo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6783600880448476592L;

	public List<ScoMaterialVinculo> obterMateriaisVinculados(
			ScoMaterial material) {
		List<ScoMaterialVinculo> materiaisVinculo = new LinkedList<ScoMaterialVinculo>();
		if (material != null) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(ScoMaterialVinculo.class);
			//criteria.setFetchMode(ScoMaterialVinculo.Fields.MATERIAL_VINCULO.toString(), FetchMode.JOIN);
			criteria.createAlias(ScoMaterialVinculo.Fields.MATERIAL_VINC.toString(), "MAT_VINC", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq(ScoMaterialVinculo.Fields.MATERIAL.toString(),
					material));
	
			materiaisVinculo = executeCriteria(criteria);
			return materiaisVinculo;
		}

		return null;
	}
	
}
