package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.RapServidores;

public class AelExtratoExameApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoExameAp> {
	
	private static final long serialVersionUID = 3170128610806494300L;

	/**
	 * ORADB: cursor c_lu5 (Function: AELC_BUSCA_DT_EXTR)
	 */
	public Date obterMaxCriadoEmPorLuxSeqEEtapasLaudo(final Long luxSeq, final DominioSituacaoExamePatologia etapasLaudo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoExameAp.class);
		criteria.setProjection(Projections.max(AelExtratoExameAp.Fields.CRIADO_EM.toString()));

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelExtratoExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		}
		
		if(etapasLaudo != null){
			criteria.add(Restrictions.eq(AelExameAp.Fields.ETAPAS_LAUDO.toString(), etapasLaudo));
		}
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public Short geraSeqp(final Long luxSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoExameAp.class);
		criteria.setProjection(Projections.max(AelExtratoExameAp.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelExtratoExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		
		Object obj = executeCriteriaUniqueResult(criteria);
		Short resultado = 0;
		if (obj != null) {
			resultado = (Short) obj;
		}
		return ++resultado;
	}
	
	public List<AelExtratoExameAp> listarAelExtratoExameApPorLuxSeq(final Long luxSeq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoExameAp.class);
		criteria.add(Restrictions.eq(AelExtratoExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		
		criteria.createAlias(AelExtratoExameAp.Fields.RAP_SERVIDORES.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.desc(AelExtratoExameAp.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}

	public Short obterMaxSeqAelExtratoExameAp(final Long luxSeq, final DominioSituacaoExamePatologia etapasLaudo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoExameAp.class);
		criteria.setProjection(Projections.max(AelExtratoExameAp.Fields.SEQP.toString()));

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelExtratoExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		}
		
		if(etapasLaudo != null){
			criteria.add(Restrictions.eq(AelExameAp.Fields.ETAPAS_LAUDO.toString(), etapasLaudo));
		}
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
}