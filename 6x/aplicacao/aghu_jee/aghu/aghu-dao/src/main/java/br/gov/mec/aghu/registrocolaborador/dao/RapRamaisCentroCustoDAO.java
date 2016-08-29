package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapRamaisCentroCusto;

public class RapRamaisCentroCustoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapRamaisCentroCusto>{
	private static final long serialVersionUID = 613657111833244467L;

	protected RapRamaisCentroCustoDAO() {
	}
	
	public List<RapRamaisCentroCusto> consultarRamalChefia2e3(Integer codigoCC, boolean restricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapRamaisCentroCusto.class);
		
		if (codigoCC != null) {
			criteria.createAlias(RapRamaisCentroCusto.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.INNER_JOIN);
			
			criteria.add(Restrictions.eq("cc."+ FccCentroCustos.Fields.CODIGO.toString(), codigoCC));
			
			if(restricao) {
				criteria.add(Restrictions.eq(RapRamaisCentroCusto.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));				
			}
			
			criteria.addOrder(Order.asc(RapRamaisCentroCusto.Fields.RAM_NRO_RAMAL.toString()));
		} 
		return executeCriteria(criteria, 0, 1, null);
	}	
	
}
