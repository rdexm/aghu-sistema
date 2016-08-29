package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;


public class ScoParecerAvalDesempDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerAvalDesemp> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1291423054622265078L;
	
	public ScoParecerAvalDesemp obterParecerAvalDesempPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){	
		if (scoParecerAvaliacao != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvalDesemp.class, "SCOAVDESEMP");
			
			criteria.createAlias("SCOAVDESEMP." + ScoParecerAvalDesemp.Fields.SERVIDOR_AVALIACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.eq( "SCOAVDESEMP." + ScoParecerAvalDesemp.Fields.PARECER_AVALIACAO.toString(), scoParecerAvaliacao));			
		
			return (ScoParecerAvalDesemp) this.executeCriteriaUniqueResult(criteria);		
		}
		return null;		
	}	

	
	
	
}
