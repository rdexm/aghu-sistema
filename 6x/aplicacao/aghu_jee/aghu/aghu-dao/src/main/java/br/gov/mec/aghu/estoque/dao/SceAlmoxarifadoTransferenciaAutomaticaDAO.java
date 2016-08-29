package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;

public class SceAlmoxarifadoTransferenciaAutomaticaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceAlmoxarifadoTransferenciaAutomatica>{
	
	private static final long serialVersionUID = -2876672094018695701L;

	/**
	 * Obtem SceAlmoxarifadoTransferenciaAutomatica através do seq do almoxarifado de origem e seq do almoxarifado de recebimento
	 * @param almSeq
	 * @param almSeqRecebe
	 * @return
	 */
	public SceAlmoxarifadoTransferenciaAutomatica obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(Short almSeq, Short almSeqRecebe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoTransferenciaAutomatica.class);
		criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ.toString(), almSeq));
		criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ_RECEBE.toString(), almSeqRecebe));
		return (SceAlmoxarifadoTransferenciaAutomatica) executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisarSceAlmoxTransfAutomaticasCount(Short almoxOrigem, Short almoxDestino, DominioSituacao situacao ){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoTransferenciaAutomatica.class);
		if(almoxOrigem != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ.toString(), almoxOrigem));
		}
		if(almoxDestino != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ_RECEBE.toString(), almoxDestino));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.SITUACAO.toString(), situacao));
		}
		return executeCriteriaCount(criteria);
	}
	
	public List<SceAlmoxarifadoTransferenciaAutomatica> pesquisarSceAlmoxTransfAutomaticas(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, Short almoxOrigem, Short almoxDestino, DominioSituacao situacao ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoTransferenciaAutomatica.class);
		
		// Necessário para a pesquisa de transferências
		criteria.createAlias(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALMOXARIFADO_ORIGEM.toString(), "ORG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALMOXARIFADO_DESTINO.toString(), "DEST", JoinType.LEFT_OUTER_JOIN);
		
		if(almoxOrigem != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ.toString(), almoxOrigem));
		}
		if(almoxDestino != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ_RECEBE.toString(), almoxDestino));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(SceAlmoxarifadoTransferenciaAutomatica.Fields.SITUACAO.toString(), situacao));
		}
		
		criteria.addOrder(Order.asc(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ.toString()));
		criteria.addOrder(Order.asc(SceAlmoxarifadoTransferenciaAutomatica.Fields.ALM_SEQ_RECEBE.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, null, asc);
	}
}