package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMensCalculoNpt;


public class AfaMensCalculoNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaMensCalculoNpt>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 935687L;

	private DetachedCriteria montarPesquisa(AfaMensCalculoNpt filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMensCalculoNpt.class);
		
		if(filtro.getSeq() != null){
			criteria.add(Restrictions.eq(AfaMensCalculoNpt.Fields.SEQ.toString(), filtro.getSeq()));
		}
		if(filtro.getDescricao() != null && !filtro.getDescricao().equals("")){
			criteria.add(Restrictions.ilike(AfaMensCalculoNpt.Fields.DESCRICAO.toString(), filtro.getDescricao(), MatchMode.ANYWHERE));
		}
		if(filtro.getIndSituacao() != null && !filtro.getIndSituacao().equals("")){
			criteria.add(Restrictions.ilike(AfaMensCalculoNpt.Fields.IND_SITUACAO.toString(), filtro.getIndSituacao(), MatchMode.EXACT));
		}
	
		return criteria;
	}
	
	public Long listarMensagensCalculoNptCount(AfaMensCalculoNpt filtro){
		DetachedCriteria criteria = montarPesquisa(filtro);
		return executeCriteriaCount(criteria);
	}
	
	public List<AfaMensCalculoNpt> listarMensagensCalculoNpt(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AfaMensCalculoNpt filtro){
		DetachedCriteria criteria = montarPesquisa(filtro);
		criteria.addOrder(Order.asc(AfaMensCalculoNpt.Fields.SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<AfaMensCalculoNpt> listarMensCalculoNptAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMensCalculoNpt.class);
		criteria.add(Restrictions.ilike(AfaMensCalculoNpt.Fields.IND_SITUACAO.toString(), "A", MatchMode.EXACT));
		return executeCriteria(criteria);
	}
	
}
