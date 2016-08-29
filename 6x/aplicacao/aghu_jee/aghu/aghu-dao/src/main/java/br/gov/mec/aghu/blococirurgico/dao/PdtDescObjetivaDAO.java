package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtGrupo;

public class PdtDescObjetivaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDescObjetiva> {

	private static final long serialVersionUID = 3197003298939459957L;

	public List<PdtDescObjetiva> pesquisarDescricaoObjetivaPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescObjetiva.class);
		criteria.add(Restrictions.eq(PdtDescObjetiva.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.addOrder(Order.asc(PdtDescObjetiva.Fields.SEQP.toString())); 
		return executeCriteria(criteria);
	}
	
	public Long obterCountDescricaoObjetivaPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescObjetiva.class);
		
		criteria.add(Restrictions.eq(PdtDescObjetiva.Fields.DDT_SEQ.toString(), ddtSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<PdtDescObjetiva> pesquisarPdtDescObjetivaPorDdtSeq(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescObjetiva.class);		

		criteria.createAlias(PdtDescObjetiva.Fields.PDT_ACHADOS.toString(), "achados");
		criteria.createAlias("achados." + PdtAchado.Fields.PDT_GRUPOS.toString(), "grupos");		

		criteria.add(Restrictions.eq(PdtDescObjetiva.Fields.DDT_SEQ.toString(), seq));
		
		criteria.addOrder(Order.asc("grupos." + PdtGrupo.Fields.ID_SEQP.toString()));

		return executeCriteria(criteria);
	}
	
	public Short obterMaxSeqpPdtDescObjetiva(final Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescObjetiva.class);
	
		criteria.setProjection(Projections.max(PdtDescObjetiva.Fields.SEQP.toString()));  
		criteria.add(Restrictions.eq(PdtDescObjetiva.Fields.DDT_SEQ.toString(), ddtSeq));
		
		final Short maxSeq = (Short)this.executeCriteriaUniqueResult(criteria);
		
		return maxSeq;
	}
}
