package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcDescricaoTecnicas;

public class MbcDescricaoTecnicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDescricaoTecnicas> {

	private static final long serialVersionUID = -3302094085045074031L;

	/**
	 * Efetua busca de MbcDescricaoTecnicas
	 * Consulta C7 #18527
	 * @param dcgCrgSeq
	 * @param seqp
	 * @return
	 */ 
	public MbcDescricaoTecnicas buscarDescricaoTecnicas(Integer dcgCrgSeq, Short seqp){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoTecnicas.class, "dtc");
		criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		if(seqp != null){
			criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_SEQP.toString(), seqp));
		}
		criteria.addOrder(Order.asc("dtc."+MbcDescricaoTecnicas.Fields.DESCRICAO_TECNICA.toString()));
		
		return (MbcDescricaoTecnicas) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcDescricaoTecnicas> buscarMbcDescricaoTecnicas(Integer dcgCrgSeq, Short seqp){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoTecnicas.class, "dtc");
		criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_SEQP.toString(), seqp));
		criteria.addOrder(Order.asc("dtc."+MbcDescricaoTecnicas.Fields.DESCRICAO_TECNICA.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long buscarMbcDescricaoTecnicasCount(Integer dcgCrgSeq, Short seqp){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoTecnicas.class, "dtc");
		criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("dtc."+MbcDescricaoTecnicas.Fields.DCG_SEQP.toString(), seqp));
		
		return executeCriteriaCount(criteria);
	}
}
