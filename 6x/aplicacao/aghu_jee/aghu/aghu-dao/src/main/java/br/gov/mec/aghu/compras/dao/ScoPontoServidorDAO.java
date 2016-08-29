package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;

/**
 * 
 * @author andremachado
 * 
 * 
 */

public class ScoPontoServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoDireitoAutorizacaoTemp> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1567349880722849117L;

	public List<ScoPontoServidor> pesquisarScoPontoServidor(
			ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, 
			RapServidores    servidor,
      	    RapServidores    servidorAutorizado,
			Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {
		
			DetachedCriteria criteria = montarConsulta(scoPontoParadaSolicitacao,servidor,servidorAutorizado);
			criteria.createAlias("PSR."+ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PPS", JoinType.LEFT_OUTER_JOIN);
			
			criteria.createAlias("PSR."+ScoPontoServidor.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);
			
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarScoPontoServidorCount(
			ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, 
			RapServidores    servidor ,
      	    RapServidores    servidorAutorizado) {
		
  		    DetachedCriteria criteria = montarConsulta(scoPontoParadaSolicitacao,servidor,servidorAutorizado);
			return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarConsulta(
			ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, 
			RapServidores    servidor, 			
		    RapServidores    servidorAutorizado) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoServidor.class, "PSR" );
			
			if (scoPontoParadaSolicitacao  != null && scoPontoParadaSolicitacao.getCodigo()!= null) {
				criteria.add(Restrictions.eq(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), scoPontoParadaSolicitacao ));
			}
			
			if (servidor != null && servidor.getId() != null
					&& ((RapServidoresId) servidor.getId()).getVinCodigo() != null) {
				criteria.add(Restrictions.eq(ScoPontoServidor.Fields.SERVIDOR.toString(), servidor));
			}

			if (servidorAutorizado  != null && servidorAutorizado.getId() != null
					&& ((RapServidoresId) servidorAutorizado.getId()).getVinCodigo() != null) {
				
				final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoDireitoAutorizacaoTemp.class, "EAL");
				subQuerie.createAlias("EAL." + ScoDireitoAutorizacaoTemp.Fields.SCO_PONTOS_SERVIDORES.toString(), "PSR2", JoinType.INNER_JOIN);
				subQuerie.add(Restrictions.eq("EAL." + ScoDireitoAutorizacaoTemp.Fields.RAP_SERVIDORES.toString(), servidorAutorizado));
				subQuerie.setProjection(Projections.property("EAL." + ScoDireitoAutorizacaoTemp.Fields.NUMERO.toString()));

				subQuerie.add(Property.forName("PSR." + ScoPontoServidor.Fields.CODIGO_PP_SOLICITACAO.toString()).eqProperty("PSR2." + ScoPontoServidor.Fields.CODIGO_PP_SOLICITACAO.toString()));

				subQuerie.add(Property.forName("PSR." + ScoPontoServidor.Fields.MATRICULA.toString()).eqProperty("PSR2." + ScoPontoServidor.Fields.MATRICULA.toString()));
				subQuerie.add(Property.forName("PSR." + ScoPontoServidor.Fields.VIN_CODIGO.toString()).eqProperty("PSR2." + ScoPontoServidor.Fields.VIN_CODIGO.toString()));
				
				criteria.add(Subqueries.exists(subQuerie));
				
			}
			
			return criteria;
	}
	
	/**
	 * Pesquisa ponto servidor com PPS_CODIGO igual ao informado.
	 * @param codigo
	 * @author dilceia.alves
	 * @since 31/10/2012
	 */
	public Long pesquisarPontoServidorPorPpsCodigoCount(Short codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoServidor.class);

		criteria.add(Restrictions.eq(ScoPontoServidor.Fields.PPS_CODIGO.toString(), codigo));

		return this.executeCriteriaCount(criteria);
	}
}
	

