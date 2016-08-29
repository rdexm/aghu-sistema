package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoSiasgMaterialMestre;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

public class ScoSiasgMaterialMestreDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSiasgMaterialMestre> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4728276951102076855L;
	
	public List<ScoSiasgMaterialMestre> listarMateriaisCatMat(String material){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSiasgMaterialMestre.class);
		
		if(!material.isEmpty()){
			
			if(CoreUtil.isNumeroInteger(material)){
				criteria.add(Restrictions.eq(ScoSiasgMaterialMestre.Fields.CODIGO.toString(), "BR" + StringUtil.adicionaZerosAEsquerda(material, 7)));
			} else {
				criteria.add(Restrictions.ilike(ScoSiasgMaterialMestre.Fields.DESCRICAO.toString(), material, MatchMode.ANYWHERE));
			}
			
		}
		
		criteria.addOrder(Order.asc(ScoSiasgMaterialMestre.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 500, null, false);
	}

}
