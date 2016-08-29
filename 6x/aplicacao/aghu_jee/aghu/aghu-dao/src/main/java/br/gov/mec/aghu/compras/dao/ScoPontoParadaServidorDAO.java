
/**
 * 
 */
package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.VRapPessoaServidor;

/**
 * 
 */
public class ScoPontoParadaServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPontoServidor> {


	private static final long serialVersionUID = -3938939901697909868L;

	/**
	 * Retorna os pontos de parada servidor que possuam código e/ou matricula
	 * igual as informadas.
	 * 
	 * @param codigo
	 * @param matricula
	 * @return
	 */
	public List<ScoPontoServidor> pesquisarPontoParadaServidorCodigoMatriculaVinculo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigo, Integer matricula, Short vinculo) {
		DetachedCriteria criteria = criarCriteriaPesquisarPorCodigoMatriculaVinculo(
				codigo, matricula, vinculo);
		if(orderProperty == null)
		{
			criteria.addOrder(Order.asc(ScoPontoServidor.Fields.CODIGO_PP_SOLICITACAO.toString()));
			criteria.addOrder(Order.asc(ScoPontoServidor.Fields.MATRICULA.toString()));
		}
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	
		
	/**
	 * Retorna quantidade de registros
	 * 
	 * @param codigo
	 * @param matricula
	 * @return
	 */
	public Long pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(
			Short codigo, Integer matricula, Short vinculo) {
		DetachedCriteria criteria = criarCriteriaPesquisarPorCodigoMatriculaVinculo(
				codigo, matricula, vinculo);
		Long result = this.executeCriteriaCount(criteria);
		return result;
	}

	/**
	 * Monta critérios de pesquisa por codigo, matricula e vínculo
	 * @author clayton.bras
	 * @param codigo
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisarPorCodigoMatriculaVinculo(Short codigo, Integer matricula, Short vinculo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoServidor.class, "PS"); 
		DetachedCriteria criteriaExists = DetachedCriteria.forClass(VRapPessoaServidor.class, "VRPS");
		
		// ponto parada solicitacao
		criteria.createAlias("PS." + ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PPS", JoinType.INNER_JOIN);
		// rap servidores
		criteria.createAlias("PS." + ScoPontoServidor.Fields.SERVIDOR.toString(), "RAP", JoinType.INNER_JOIN);
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		
		ProjectionList pListCriteriaExists = Projections.projectionList();		
		if (codigo != null) {			
			criteria.add(Restrictions.eq(ScoPontoServidor.Fields.CODIGO_PP_SOLICITACAO.toString(), codigo));
		}
		if (matricula != null) {			
			criteria.add(Restrictions.eq(ScoPontoServidor.Fields.MATRICULA.toString(), matricula));
		}
		if (vinculo != null) {			
			criteria.add(Restrictions.eq(ScoPontoServidor.Fields.VIN_CODIGO.toString(), vinculo));
		}
		
		pListCriteriaExists.add(Projections.sqlProjection("null", new String[]{}, new Type[]{}));	
		criteriaExists.setProjection(pListCriteriaExists);
		criteriaExists.add(Restrictions.eqProperty("VRPS." + VRapPessoaServidor.Fields.MATRICULA.toString(), "PS." + ScoPontoServidor.Fields.MATRICULA.toString()));
		criteriaExists.add(Restrictions.eqProperty("VRPS." + VRapPessoaServidor.Fields.VINCODIGO.toString(), "PS." + ScoPontoServidor.Fields.VIN_CODIGO.toString()));
		criteria.add(Subqueries.exists(criteriaExists));		
		return criteria;
	}
	
	public Boolean verificarPontoParadaPermitido(ScoPontoParadaSolicitacao pontoParada, RapServidores servidor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoServidor.class);
		
		criteria.add(Restrictions.eq(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), pontoParada));
		criteria.add(Restrictions.eq(ScoPontoServidor.Fields.SERVIDOR.toString(), servidor));
		
		return ((executeCriteriaCount(criteria) > 0) ? true : false);		
	}

}