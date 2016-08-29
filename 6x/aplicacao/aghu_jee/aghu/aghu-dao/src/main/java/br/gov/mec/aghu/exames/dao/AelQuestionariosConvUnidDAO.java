/**
 * 
 */
package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelQuestionariosConvUnid;

/**
 * @author aghu
 *
 */
public class AelQuestionariosConvUnidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelQuestionariosConvUnid> {

	private static final long serialVersionUID = -4786749313981361210L;

	/**
	 * Retorna proximo sequencial da tabela AEL_QUESTIONARIOS_CONV_UNID
	 * @return
	 */
	public Integer obterProximoSequencial() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionariosConvUnid.class);
		criteria.setProjection(Projections.max(AelQuestionariosConvUnid.Fields.SEQP.toString()));
		Integer seqp = (Integer) executeCriteriaUniqueResult(criteria);		
		if(seqp != null) {
			seqp = seqp + 1;
		}else{
			seqp = 1;
		}
		return seqp;
	}

	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionariosConvUnid.class);
		
		criteria.add(Restrictions.eq(AelQuestionariosConvUnid.Fields.QTN_SEQ.toString(), seqQuestionario));
//		criteria.add(Restrictions.eq(AelQuestionariosConvUnid.Fields.UFE_UNF_SEQ.toString(), unfSeq));
//		if (nrApNull) {
//			criteria.add(Restrictions.isNull(AelItemSolicitacaoExames.Fields.NUMERO_AP.toString()));
//		}
		return executeCriteria(criteria);
	}
	
	public Long buscarAelQuestionariosConvUnidPorQuestionarioCount(Integer seqQuestionario) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionariosConvUnid.class);
		
		criteria.add(Restrictions.eq(AelQuestionariosConvUnid.Fields.QTN_SEQ.toString(), seqQuestionario));

		
		return executeCriteriaCount(criteria);
	}
	
	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario,
			Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionariosConvUnid.class);
		
		criteria.add(Restrictions.eq(AelQuestionariosConvUnid.Fields.QTN_SEQ.toString(), seqQuestionario));
		criteria.createAlias(AelQuestionariosConvUnid.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelQuestionariosConvUnid.Fields.CONVENIO_SAUDE.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
}
