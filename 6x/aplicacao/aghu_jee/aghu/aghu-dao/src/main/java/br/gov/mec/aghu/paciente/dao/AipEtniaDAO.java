package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipEtniaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipEtnia> {

	private static final long serialVersionUID = 3691007258036387777L;

	public List<AipEtnia> obterTodasEtnias() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEtnia.class);
		
		return this.executeCriteria(criteria);
	}

	public List<AipEtnia> pesquisarEtniaPorIDouDescricao(Object etnia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEtnia.class);
		
		if(CoreUtil.isNumeroInteger(etnia)){
			criteria.add(Restrictions.eq(AipEtnia.Fields.ID.toString(), Integer.valueOf(etnia.toString())));
		} else {
			criteria.add(Restrictions.ilike(AipEtnia.Fields.DESCRICAO.toString(), etnia.toString(), MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(AipEtnia.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 * #36436 C3
	 * Método para obter descricao Etnia por Id.
	 * 
	 * @param id
	 * @return
	 */
	public String obterDescricaoEtniaPorId(Integer id){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEtnia.class);
		criteria.setProjection(Projections.property(AipEtnia.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(AipEtnia.Fields.ID.toString(), id));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
}
