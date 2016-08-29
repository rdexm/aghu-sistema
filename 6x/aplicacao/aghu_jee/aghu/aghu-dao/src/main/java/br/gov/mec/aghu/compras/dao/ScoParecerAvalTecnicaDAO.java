package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;

public class ScoParecerAvalTecnicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerAvalTecnica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8266874781345736679L;	
	
	
	public ScoParecerAvalTecnica obterParecerAvalTecnicaPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){	
		if (scoParecerAvaliacao != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvalTecnica.class, "SCOAVTEC");
			
			criteria.createAlias("SCOAVTEC." + ScoParecerAvalTecnica.Fields.SERVIDOR_AVALIACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.eq( "SCOAVTEC." + ScoParecerAvalTecnica.Fields.PARECER_AVALIACAO.toString(), scoParecerAvaliacao));			
		
			return (ScoParecerAvalTecnica) this.executeCriteriaUniqueResult(criteria);		
		}
		return null;		
	}	
	
	
}
