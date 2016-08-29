package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.MbcNotaAdicionais;

public class MbcNotaAdicionaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcNotaAdicionais> {

	private static final long serialVersionUID = -7338245386203072764L;

	public Integer obterMaxNTASeqp(Integer crgSeq, Short seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class);
	
		criteria.setProjection(Projections.max(MbcNotaAdicionais.Fields.DCG_SEQP.toString()));  
		
		criteria.add(Restrictions.eq(MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcNotaAdicionais.Fields.DCG_SEQP.toString(), seqp));
		
		Short maxSeq = (Short)this.executeCriteriaUniqueResult(criteria);
		if(maxSeq != null){
			return Integer.valueOf(maxSeq.toString());
		}else{
			return null;
		}
		
	}

	public Integer obterNTASeqp(Integer crgSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class);
		
		criteria.setProjection(Projections.property(MbcNotaAdicionais.Fields.SEQP.toString()));  
		
		criteria.add(Restrictions.eq(MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcNotaAdicionais.Fields.DCG_SEQP.toString(), seqp));
		
		List<Integer> notas = executeCriteria(criteria);
		if(!notas.isEmpty()){
			return notas.get(0);
		}else{
			return null;
		}
	}
	
	
	/**
	 * Efetua busca em MbcNotaAdicionais
	 * Consulta C13 #18527
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @return
	 */
	public MbcNotaAdicionais buscarNotaAdicionais(Integer dcgCrgSeq, Short dcgSeqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class, "nta");
		criteria.createAlias(MbcNotaAdicionais.Fields.DESCRICAO_CIRURGICA.toString(), "DCRG");
		
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		if(dcgSeqp != null){
			criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString(), dcgSeqp));
		}	
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString()));
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString()));
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.SEQP.toString()));
		
		List<MbcNotaAdicionais> notas = executeCriteria(criteria);
		if(!notas.isEmpty()){
			return notas.get(0);
		}else{
			return null;
		}
	}
	
	public List<MbcNotaAdicionais> obterNotasAdicionais(Integer dcgCrgSeq, Short dcgSeqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class, "nta");
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString(), dcgSeqp));
		
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString()));
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString()));
		criteria.addOrder(Order.asc("nta."+MbcNotaAdicionais.Fields.SEQP.toString()));
		
		List<MbcNotaAdicionais> notas = executeCriteria(criteria);
		
		return notas;
	}
	
	/**
	 * Efetua busca em MbcNotaAdicionais
	 * Consulta C14 #18527
	 * @param dcgCrgSeq
	 * @param seqp2 
	 * @param dcgSeqp
	 * @param seqp
	 * @param integer 
	 * @return
	 */
	public MbcNotaAdicionais buscarNotaAdicionais1(Integer dcgCrgSeqC1, Short seqpC1, Integer dcgCrgSeq, Short dcgSeqp, Integer seqp){
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MbcNotaAdicionais.class, "nta1");
		subquery.setProjection(Projections.max("nta1."+MbcNotaAdicionais.Fields.CRIADO_EM.toString()));
		subquery.add(Restrictions.eq("nta1."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeqC1));
		if(seqpC1 != null){
			subquery.add(Restrictions.eq("nta1."+MbcNotaAdicionais.Fields.DCG_SEQP.toString(), seqpC1));
		}	

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class, "nta");
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.SEQP.toString(), seqp));
		criteria.add(Subqueries.propertyIn("nta."+MbcNotaAdicionais.Fields.CRIADO_EM.toString(), subquery));
		
		return (MbcNotaAdicionais) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Efetua busca em MBC_NOTA_ADICIONAIS
	 * Consulta C15 #18527
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @param seqp
	 * @return
	 */
	public MbcNotaAdicionais buscarNotaAdicionais2(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNotaAdicionais.class, "nta");
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_CRG_SEQ.toString(), dcgCrgSeq));
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.DCG_SEQP.toString(), dcgSeqp));
		criteria.add(Restrictions.eq("nta."+MbcNotaAdicionais.Fields.SEQP.toString(), seqp));
		
		return (MbcNotaAdicionais) executeCriteriaUniqueResult(criteria);
	}
}
