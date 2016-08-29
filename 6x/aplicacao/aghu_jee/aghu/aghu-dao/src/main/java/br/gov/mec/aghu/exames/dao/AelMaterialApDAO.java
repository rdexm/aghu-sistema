package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelMaterialApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMaterialAp> { 
 
	private static final long serialVersionUID = 3510017746209191348L;

	public List<AelMaterialAp> obterAelMaterialApPorAelExameAps(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMaterialAp.class);
		
		criteria.createAlias(AelMaterialAp.Fields.AEL_EXAME_APS.toString(), AelMaterialAp.Fields.AEL_EXAME_APS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelMaterialAp.Fields.RAP_SERVIDORES.toString(), AelMaterialAp.Fields.RAP_SERVIDORES.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelMaterialAp.Fields.ITEM_SOLICITACAO_EXAME.toString(), AelMaterialAp.Fields.ITEM_SOLICITACAO_EXAME.toString(), JoinType.LEFT_OUTER_JOIN);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelMaterialAp.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		criteria.addOrder(Order.asc(AelMaterialAp.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}

	public List<AelMaterialAp> listarAelMaterialApPorItemSolic(final Integer iseSoeSeq, Short iseSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMaterialAp.class);

		criteria.add(Restrictions.eq(AelMaterialAp.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelMaterialAp.Fields.ISE_SEQP.toString(), iseSeqp));

		return executeCriteria(criteria);
	}	
	
	public List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdem(final Long luxSeq, Short ordem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMaterialAp.class);

		criteria.add(Restrictions.eq(AelMaterialAp.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		
		criteria.add(Restrictions.eq(AelMaterialAp.Fields.ORDEM.toString(), ordem));
		
		return executeCriteria(criteria);
	}

	public List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdemMaior(final Long luxSeq, Short ordem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelMaterialAp.class);

		criteria.add(Restrictions.eq(AelMaterialAp.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		
		criteria.add(Restrictions.gt(AelMaterialAp.Fields.ORDEM.toString(), ordem));
		
		criteria.addOrder(Order.asc(AelMaterialAp.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaForAelMaterialAp(Long luxSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMaterialAp.class);
		criteria.add(Restrictions.eq(AelMaterialAp.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		return criteria;
	}
	
	private void avaliaParametroPesquisa(DetachedCriteria criteria, Object objPesquisa) {
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			criteria.add(Restrictions.eq(AelMaterialAp.Fields.ORDEM.toString(), Integer.parseInt(objPesquisa.toString())));
		} else {
			criteria.add(Restrictions.like(AelMaterialAp.Fields.MATERIAL.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
		}
	}

	public List<AelMaterialAp> pesquisaMateriaisCapsula(Object objPesquisa, Long luxSeq) {
		DetachedCriteria criteria =  getCriteriaForAelMaterialAp(luxSeq);
		if(objPesquisa != null && !"".equals(objPesquisa)){
			avaliaParametroPesquisa(criteria, objPesquisa);
		}
		criteria.addOrder(Order.asc(AelMaterialAp.Fields.ORDEM.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisaMateriaisCapsulaCount(Object objPesquisa, Long luxSeq) {
		DetachedCriteria criteria =  getCriteriaForAelMaterialAp(luxSeq);
		if(objPesquisa != null && !"".equals(objPesquisa)){
			avaliaParametroPesquisa(criteria, objPesquisa);
		}
		return executeCriteriaCount(criteria);
	}
	
}