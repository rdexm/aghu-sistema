package br.gov.mec.aghu.transplante.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MtxColetaMaterialTmo;

public class MtxColetaMaterialTmoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxColetaMaterialTmo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8219899848590865493L;

	
	/**
	 * #41772 - C6
	 * @author marcelo.deus
	 */
	public MtxColetaMaterialTmo buscarColetaMaterialTmoPorCodTransplante(Integer codTransplante){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxColetaMaterialTmo.class, "TRP");
		criteria.add(Restrictions.eq("TRP." + MtxColetaMaterialTmo.Fields.TRANSPLANTE_SEQ.toString(), codTransplante));
		if(executeCriteria(criteria) != null && !executeCriteria(criteria).isEmpty()){
			return (MtxColetaMaterialTmo) executeCriteria(criteria).get(0);
		}
		return null;
	}
	

}
