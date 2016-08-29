package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;


/**
 * @see ValidadeAmostraDAO.java
 * 
 * @author fausto.trindade@squadra.com.br
 * 
 */
public class ValidadeAmostraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsValidAmostrasComponentes> {

	
	private static final long serialVersionUID = 4840458556767115357L;



	/**
	 * Obtem a lista de validade da amostra por codigo
	 * @param csaCodigo
	 */
	public Long obterListaValidadeAmostrasPorCodigoCount(String codComponente, Integer codigo, Integer idadeIni, Integer idadeFim ) {
		return executeCriteriaCount(criarCriteriaPesquisarValidadeAmostrasPorIdIdadeIniIdadeFim(codComponente, codigo, idadeIni, idadeFim));
	}
	
	/**
	 * Obtem a lista de validade da amostra por codigo
	 * @param csaCodigo
	 */
	public List<AbsValidAmostrasComponentes> obterListaValidadeAmostrasPorCodigo(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc,final String codComponente, Integer codigo, Integer idadeIni, Integer idadeFim ) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarValidadeAmostrasPorIdIdadeIniIdadeFim(codComponente, codigo, idadeIni, idadeFim);

		criteria.createAlias(AbsValidAmostrasComponentes.Fields.COMPONENTE_SANGUINEO.toString(), "CS", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.asc(AbsValidAmostrasComponentes.Fields.IDADE_MES_INICIAL.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	private DetachedCriteria criarCriteriaPesquisarValidadeAmostrasPorIdIdadeIniIdadeFim(String codComponente, Integer codigo, Integer idadeIni, Integer idadeFim ) {
		DetachedCriteria dc = DetachedCriteria.forClass(AbsValidAmostrasComponentes.class);
		
		dc.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.CSA_CODIGO.toString(),codComponente));
		
		if(codigo != null){
			dc.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.SEQP.toString(), codigo));	
		}
		
		if(idadeIni != null){
			dc.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.IDADE_MES_INICIAL.toString(), idadeIni));	
		}
		
		if(idadeFim != null){
			dc.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.IDADE_MES_FINAL.toString(), idadeFim));	
		}
		
		return dc;
	}
	
	/**
	 * Obtem a validade da amostra por codigo
	 */
	public AbsValidAmostrasComponentes obterValidadeAmostraPorCodigo(final String codComponente, Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsValidAmostrasComponentes.class);
		
		criteria.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.CSA_CODIGO.toString(),codComponente));
		criteria.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.SEQP.toString(), codigo));	
		criteria.createAlias(AbsValidAmostrasComponentes.Fields.COMPONENTE_SANGUINEO.toString(), "CS", JoinType.INNER_JOIN);
		
		return (AbsValidAmostrasComponentes)executeCriteriaUniqueResult(criteria);
	}
	
	
	
	/**
	 * Retorna o próximo código seqp
	 */
	public Long obterProximoCodigoSeqp(String codComponente) {
		
		final int SOMA_MAIS_UM_PROX_NUMERO = 1;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsValidAmostrasComponentes.class);
		ProjectionList proj = Projections.projectionList();
		criteria.setProjection(proj.add(Projections.max("id.seqp")));
		criteria.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.CSA_CODIGO.toString(), codComponente));	
		return executeCriteriaCount(criteria) + SOMA_MAIS_UM_PROX_NUMERO;
	}

	
}