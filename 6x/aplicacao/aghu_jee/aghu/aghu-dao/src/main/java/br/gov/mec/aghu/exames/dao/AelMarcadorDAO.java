package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelMarcador;

/**
 * @author rafael.fonseca
 *
 */
public class AelMarcadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMarcador> {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 332650321694762908L;

	
	private DetachedCriteria obterCriteriaBasica(final AelMarcador aelMarcador) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMarcador.class);
		
		if(aelMarcador != null){
			
		    if(aelMarcador.getSeq() != null){	
				criteria.add(Restrictions.eq(AelMarcador.Fields.SEQ.toString(), aelMarcador.getSeq()));
			}
		    
		    if (aelMarcador.getFabricante() != null) {
		    	criteria.add(Restrictions.eq(AelMarcador.Fields.FABRICANTE.toString(), aelMarcador.getFabricante()));

		    }
		    
		    if (aelMarcador.getIndSituacao() != null) {
		    	criteria.add(Restrictions.eq(AelMarcador.Fields.IND_SITUACAO.toString(), aelMarcador.getIndSituacao()));
		    }
		    
		    if (StringUtils.isNotBlank(aelMarcador.getMarcadorPedido())) {
		    	criteria.add(Restrictions.like(AelMarcador.Fields.MARCADOR_PEDIDO.toString(), aelMarcador.getMarcadorPedido(), MatchMode.ANYWHERE));
		    }
		    

		}
		
		return criteria;
    }
	
	public List<AelMarcador> pesquisarAelMarcador(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, final AelMarcador aelMarcador) {
		final DetachedCriteria criteria = obterCriteriaBasica(aelMarcador);
		criteria.setFetchMode(AelMarcador.Fields.FABRICANTE.toString(), FetchMode.JOIN);
		
		if(orderProperty == null){
			criteria.addOrder(Order.asc(AelMarcador.Fields.IND_SITUACAO.toString()));
			criteria.addOrder(Order.asc(AelMarcador.Fields.MARCADOR_PEDIDO.toString()));
			asc = true;
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarAelMarcadorCount(AelMarcador aelMarcador) {
		final DetachedCriteria criteria = obterCriteriaBasica(aelMarcador);
		return executeCriteriaCount(criteria);
	}
	
	public AelMarcador obterAelMarcadorPorMarcadorPedido(String marcadorPedido) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMarcador.class);
		criteria.add(Restrictions.eq(AelMarcador.Fields.MARCADOR_PEDIDO.toString(), marcadorPedido));
		return (AelMarcador) executeCriteriaUniqueResult(criteria);
	}
}