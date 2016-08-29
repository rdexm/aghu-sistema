package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;

public class EpeSinCaractDefinidoraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeSinCaractDefinidora> {

	private static final long serialVersionUID = 5315521561039107319L;

	public Boolean possuiCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeSinCaractDefinidora.class);
		criteria.add(Restrictions.or(
				Restrictions.eq(EpeSinCaractDefinidora.Fields.CARACT_DEFINIDORA_BY_CDE_CODIGO.toString(),epeCaractDefinidora),
				Restrictions.eq(EpeSinCaractDefinidora.Fields.CARACT_DEFINIDORA_BY_CDE_CODIGO_POSSUI.toString(),epeCaractDefinidora)));
		return executeCriteriaExists(criteria);
	}
	
	public List<EpeSinCaractDefinidora> buscarSinonimoSinaisSintomas(Integer cdeCodigo, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		
		DetachedCriteria criteria = buscarDadosSinonimos(cdeCodigo);
		criteria.addOrder(Order.asc("SCD." + EpeSinCaractDefinidora.Fields.ID_CODIGO_POSSUI.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long buscarSinonimoSinaisSintomasCount(Integer cdeCodigo){
		
		DetachedCriteria criteria = buscarDadosSinonimos(cdeCodigo);
		criteria.setProjection(Projections.groupProperty("SCD." + EpeSinCaractDefinidora.Fields.ID_CODIGO_POSSUI.toString()));
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria buscarDadosSinonimos(Integer cdeCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeSinCaractDefinidora.class, "SCD");
		criteria.createAlias("SCD." + EpeSinCaractDefinidora.Fields.CARACT_DEFINIDORA_BY_CDE_CODIGO_POSSUI.toString(), "CDE");
		
		criteria.add(Restrictions.eqProperty("SCD." + EpeSinCaractDefinidora.Fields.ID_CODIGO_POSSUI.toString(), "CDE." + EpeCaractDefinidora.Fields.CODIGO.toString()));
		
		if(cdeCodigo != null){
			criteria.add(Restrictions.eq("SCD." + EpeSinCaractDefinidora.Fields.ID_CODIGO.toString(), cdeCodigo));
		}
		
		return criteria;
	}

}
