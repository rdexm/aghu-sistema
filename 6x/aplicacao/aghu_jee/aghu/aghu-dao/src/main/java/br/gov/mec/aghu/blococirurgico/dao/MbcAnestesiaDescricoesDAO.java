package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoesId;
import br.gov.mec.aghu.model.MbcTipoAnestesias;

public class MbcAnestesiaDescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAnestesiaDescricoes> {
	
	private static final long serialVersionUID = -4758904890037050302L;

	/**
	 * Efetua busca de MbcAnestesiaDescricoes
	 * Consulta C6 #18527
	 * @param dcgCrgSeq
	 * @param seqp
	 * @return
	 */
	public MbcAnestesiaDescricoes buscarAnestesiaDescricoes(Integer dcgCrgSeq, Short seqp){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaDescricoes.class, "ane");
		criteria.createAlias("ane." + MbcAnestesiaDescricoes.Fields.TIPO_ANESTESIA.toString(), "tan", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("ane."+MbcAnestesiaDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("ane."+MbcAnestesiaDescricoes.Fields.DCG_SEQP.toString(), seqp));
			
		criteria.addOrder(Order.asc("tan."+MbcTipoAnestesias.Fields.DESCRICAO.toString()));
		
		return (MbcAnestesiaDescricoes) executeCriteriaUniqueResult(criteria);
	}	
	
	public List<MbcAnestesiaDescricoes> buscarAnestesiasDescricoes(Integer dcgCrgSeq, Short seqp){
	
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaDescricoes.class, "ane");
		criteria.add(Restrictions.eq("ane."+MbcAnestesiaDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("ane."+MbcAnestesiaDescricoes.Fields.DCG_SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}

	public Short obterProximoTanseq(MbcAnestesiaDescricoesId id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestesiaDescricoes.class, "POD");
		
		criteria.add(Restrictions.eq(MbcAnestesiaDescricoes.Fields.DCG_CRG_SEQ.toString(), id.getDcgCrgSeq()));
		criteria.add(Restrictions.eq(MbcAnestesiaDescricoes.Fields.DCG_SEQP.toString(), id.getDcgSeqp()));
		
		criteria.setProjection(Projections.max(MbcAnestesiaDescricoes.Fields.TAN_SEQ.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria, false);
	}	
}
