package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;


public class ScoAcoesPontoParadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAcoesPontoParada> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7914095858270951226L;
	
	public List<ScoAcoesPontoParada> listarAcoesPontoParadaPac(Integer seqAndamento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAcoesPontoParada.class, "APP");
		criteria.createAlias(ScoAcoesPontoParada.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA, "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("APP."+ScoAcoesPontoParada.Fields.SEQ_ANDAMENTO_PAC.toString(), seqAndamento));
		criteria.addOrder(Order.desc("APP." + ScoAcoesPontoParada.Fields.DT_ACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificarAcoesPontoParadaSc(Integer slcNumero, Date data) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAcoesPontoParada.class, "APP");
		
		criteria.add(Restrictions.eq("APP."+ScoAcoesPontoParada.Fields.SLC_NUMERO.toString(), slcNumero));
		criteria.add(Restrictions.gt("APP."+ScoAcoesPontoParada.Fields.DT_ACAO.toString(), data));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean verificarAcoesPontoParadaSs(Integer slsNumero, Date data) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAcoesPontoParada.class, "APP");
		
		criteria.add(Restrictions.eq("APP."+ScoAcoesPontoParada.Fields.SLS_NUMERO.toString(), slsNumero));
		criteria.add(Restrictions.gt("APP."+ScoAcoesPontoParada.Fields.DT_ACAO.toString(), data));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean verificarAcoesPontoParadaPac(Integer seqAndamento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAcoesPontoParada.class, "APP");
		
		criteria.add(Restrictions.eq("APP."+ScoAcoesPontoParada.Fields.SEQ_ANDAMENTO_PAC.toString(), seqAndamento));
		
		return executeCriteriaCount(criteria) > 0;
	}
	


	public List<ScoAcoesPontoParada> listarAcoesPontoParada(Integer numero, Short codigoPontoParada, DominioTipoSolicitacao tipoSolicitacao) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(numero, codigoPontoParada, tipoSolicitacao);
	
		criteria.addOrder(Order.desc("SAPP." + ScoAcoesPontoParada.Fields.DT_ACAO.toString()));

		
		return executeCriteria(criteria);

	}	
	
	private DetachedCriteria obterCriteriaBasica(Integer numero, Short codigoPontoParada, DominioTipoSolicitacao tipoSolicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAcoesPontoParada.class, "SAPP");
		
		criteria.createAlias("SAPP."+ScoAcoesPontoParada.Fields.SERVIDOR.toString(), "RAP");
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS");
		if (numero != null) {
			if (tipoSolicitacao == DominioTipoSolicitacao.SC) {
				criteria.add(Restrictions.eq("SAPP." +
						ScoAcoesPontoParada.Fields.SLC_NUMERO.toString(),
						numero));			
			} else {
				criteria.add(Restrictions.eq("SAPP." +
						ScoAcoesPontoParada.Fields.SLS_NUMERO.toString(),
						numero));
			}
		}
		

		if (codigoPontoParada != null) {
			criteria.add(Restrictions.eq("SAPP." +
					ScoAcoesPontoParada.Fields.PPS_CODIGO.toString(),
					codigoPontoParada));			
		}		
		
		return criteria;
	}	
	
}
