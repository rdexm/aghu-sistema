/**
 * 
 */
package br.gov.mec.aghu.parametrosistema.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghModuloParametroAghu;

/**
 * @author bruno.mourao
 *
 */
public class AghModuloAghuDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghModuloAghu> {
	

	private static final long serialVersionUID = 3494722425084688876L;

	/**
	 * Pesquisa e retorna todos os módulos do sistema
	 * @return List<AghModuloAghu> 
	 */
	public List<AghModuloAghu> pesquisarModulosParametroSistemas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AghModuloAghu.class, "MOD");
		
		criteria.addOrder(Order.asc("MOD." + AghModuloAghu.Fields.NOME.toString()));
		return executeCriteria(criteria);

	}
	
	/**
	 * Pesquisa os módulos por parâmetro
	 * @param seqParametro
	 * @return List<AghModuloAghu> 
	 */
	public List<AghModuloAghu> pesquisarModulosPorParametro(Integer seqParametro){
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AghModuloAghu.class, "MOD");
		DetachedCriteria criteriaExists = DetachedCriteria.forClass(
				AghModuloParametroAghu.class, "MODPARAM");
		ProjectionList proj = Projections.projectionList();
		
		proj.add(Projections.sqlProjection("null", new String[]{}, new Type[]{}));		
		criteriaExists.setProjection(proj);
		criteriaExists.add(Restrictions.eq("MODPARAM." + AghModuloParametroAghu.Fields.CODIGO_PARAMETRO.toString(), 
				seqParametro));
		criteriaExists.add(Restrictions.eqProperty("MOD." + AghModuloAghu.Fields.SEQ, 
				"MODPARAM." + AghModuloParametroAghu.Fields.CODIGO_MODULO));
		
		criteria.add(Subqueries.exists(criteriaExists));
		criteria.addOrder(Order.asc("MOD." + AghModuloAghu.Fields.NOME));
		return executeCriteria(criteria);
	}

}
