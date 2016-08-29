package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFRota;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;

public class AghWFRotaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFRota> {
	
	

	private static final long serialVersionUID = -636880293420565750L;

	public  DetachedCriteria obterCriteriaBasica() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFRota.class);		
		return criteria;
	}
	
	// #37052 C1
	public List<AghWFRota> obterRotasPorEtapa(AghWFEtapa etapa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFRota.class, "WRO"); //rota
		criteria.createAlias("WRO."+AghWFRota.Fields.WTE_SEQ_ORIGEM.toString(), "WTE_ORIGEM");//template		
		criteria.add(Subqueries.exists(subQueryEtapa(etapa)));
		return executeCriteria(criteria);
	}	
	
	private DetachedCriteria subQueryEtapa(AghWFEtapa etapa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFEtapa.class, "WET"); //etapa
		criteria.add(Restrictions.eqProperty("WET." + AghWFEtapa.Fields.TEMPLATE_ETAPA.toString(), "WTE_ORIGEM." + AghWFTemplateEtapa.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("WET."+AghWFEtapa.Fields.SEQ.toString(), etapa.getSeq()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("WET."+AghWFEtapa.Fields.TEMPLATE_ETAPA.toString())));
		return criteria;
	}
}
