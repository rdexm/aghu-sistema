package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFiguraDescricoes;

public class MbcFiguraDescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFiguraDescricoes> {

	private static final long serialVersionUID = 6427839578451845698L;

	public Integer obterMaxFDCSeqp(Integer crgSeq, Short seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFiguraDescricoes.class);
	
		criteria.setProjection(Projections.max(MbcFiguraDescricoes.Fields.SEQP.toString()));  
		
		criteria.add(Restrictions.eq(MbcFiguraDescricoes.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcFiguraDescricoes.Fields.DCG_SEQP.toString(), seqp));
		
		return (Integer)this.executeCriteriaUniqueResult(criteria);
		
		
	}
	
	/**
	 * Efetua busca de MbcFiguraDescricoes
	 * Consulta C10 #18527
	 * @param dcgCrgSeq
	 * @return
	 */
	public MbcFiguraDescricoes buscarFiguraDescricoes(Integer dcgCrgSeq, Short seqp){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFiguraDescricoes.class, "fdc");
		criteria.add(Restrictions.eq("fdc."+MbcFiguraDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		if(seqp != null){
			criteria.add(Restrictions.eq("fdc."+MbcFiguraDescricoes.Fields.DCG_SEQP.toString(), seqp));
		}	
		criteria.addOrder(Order.asc("fdc."+MbcFiguraDescricoes.Fields.ORDEM.toString()));
		
		List<MbcFiguraDescricoes> figurasDescs = executeCriteria(criteria);
		if(!figurasDescs.isEmpty()){
			return figurasDescs.get(0);
		}else{
			return null;
		}
	}

	public List<MbcFiguraDescricoes> buscarMbcFiguraDescricoes(Integer dcgCrgSeq, Short seqp){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFiguraDescricoes.class, "fdc");
		criteria.add(Restrictions.eq("fdc."+MbcFiguraDescricoes.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("fdc."+MbcFiguraDescricoes.Fields.DCG_SEQP.toString(), seqp));
		criteria.addOrder(Order.asc("fdc."+MbcFiguraDescricoes.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}

}
