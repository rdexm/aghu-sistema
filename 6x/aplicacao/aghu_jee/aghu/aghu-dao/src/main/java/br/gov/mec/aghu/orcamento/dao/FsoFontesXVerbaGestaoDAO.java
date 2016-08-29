package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoVerbaGestao;

public class FsoFontesXVerbaGestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoFontesXVerbaGestao>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -847393556146103916L;

	public List<FsoFontesXVerbaGestao> pesquisarFontesXVerba(FsoVerbaGestao verbaGestao){
		DetachedCriteria criteria = this.pesquisarCriteria(verbaGestao);
		
		criteria.addOrder(Order.asc(FsoFontesXVerbaGestao.Fields.IND_PRIORIDADE.toString()));
		
		List<FsoFontesXVerbaGestao> listaFontesXVerba = executeCriteria(criteria);
		
		return listaFontesXVerba;
	}
	
	private DetachedCriteria pesquisarCriteria(FsoVerbaGestao verbaGestao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FsoFontesXVerbaGestao.class);

		if (verbaGestao != null) {
			criteria.add(Restrictions.eq(
					FsoFontesXVerbaGestao.Fields.VERBA.toString(), verbaGestao));
		}

		return criteria;
	}
	

}
