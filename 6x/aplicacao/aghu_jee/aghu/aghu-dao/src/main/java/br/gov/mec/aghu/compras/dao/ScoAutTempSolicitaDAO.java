package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.model.ScoAutTempSolicitaId;




public class ScoAutTempSolicitaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAutTempSolicita>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2293184151025362924L;

	public List<ScoAutTempSolicita> pesquisarAutSolicitacaoTemp(
			Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio)
	{
		
		final DetachedCriteria criteria = this.obterCriteriaBasica(centroCusto, servidor, dtInicio);
		criteria.createAlias(ScoAutTempSolicita.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.INNER_JOIN );
		criteria.createAlias(ScoAutTempSolicita.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN );
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.INNER_JOIN );

		List<ScoAutTempSolicita> result = this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
		return result;
	}
	
	
	
	public Long pesquisarAutSolicitacaoTempCount(FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(centroCusto, servidor, dtInicio);

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaBasica(FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutTempSolicita.class);

		if (servidor!= null && servidor.getId()!=null
						&& servidor.getId().getMatricula()!=null) {
				criteria.add(Restrictions.eq(ScoAutTempSolicita.Fields.SERVIDOR.toString(), servidor));
			}

			if (centroCusto!=null  && centroCusto.getCodigo()!=null) {
				criteria.add(Restrictions.eq(ScoAutTempSolicita.Fields.CENTRO_CUSTO.toString(), centroCusto));
			}

			if (dtInicio!= null) {
				criteria.add(Restrictions.eq(ScoAutTempSolicita.Fields.DT_INICIO.toString(), dtInicio));
			}
			
		return criteria;
	}


	public ScoAutTempSolicita obterScoAutTempSolicitaFull(ScoAutTempSolicitaId id) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutTempSolicita.class);
		criteria.add(Restrictions.eq(ScoAutTempSolicita.Fields.ID.toString(), id));
		
		criteria.createAlias(ScoAutTempSolicita.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.INNER_JOIN);
		
		criteria.createAlias(ScoAutTempSolicita.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.INNER_JOIN);
		
		return (ScoAutTempSolicita) executeCriteriaUniqueResult(criteria);
	}
}
