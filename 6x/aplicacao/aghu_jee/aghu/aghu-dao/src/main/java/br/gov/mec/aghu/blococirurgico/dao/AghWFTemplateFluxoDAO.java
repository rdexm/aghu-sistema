package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.AghWFTemplateFluxo;

public class AghWFTemplateFluxoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFTemplateFluxo> {	


	/**
	 * 
	 */
	private static final long serialVersionUID = -3943486097206396370L;

	public  DetachedCriteria obterCriteriaBasica() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateFluxo.class);		
		return criteria;
	}
	
}
