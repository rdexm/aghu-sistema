package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;


public class ScoParecerAvalConsulDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerAvalConsul> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2847094673101584283L;
	
	public ScoParecerAvalConsul obterParecerAvalConsulPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){	
		if (scoParecerAvaliacao != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvalConsul.class, "SCOAVCONSUL");
			
			criteria.createAlias("SCOAVCONSUL." + ScoParecerAvalConsul.Fields.SERVIDOR_AVALIACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);		
			
			criteria.add(Restrictions.eq( "SCOAVCONSUL." + ScoParecerAvalConsul.Fields.PARECER_AVALIACAO.toString(), scoParecerAvaliacao));			
		
			return (ScoParecerAvalConsul) this.executeCriteriaUniqueResult(criteria);		
		}
		return null;		
	}	

	

	
	
	
}
