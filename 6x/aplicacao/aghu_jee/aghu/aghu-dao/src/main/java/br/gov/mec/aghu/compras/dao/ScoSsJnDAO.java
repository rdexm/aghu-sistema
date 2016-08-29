package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.core.model.BaseJournal;

public class ScoSsJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSsJn> {
	
	private static final long serialVersionUID = 8935301871011040777L;

	public List<ScoSsJn> listarPesquisaFasesSolicitacaoServico(Integer numero) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(numero,  null, null);
	
		criteria.addOrder(Order.desc("SLSJ." + BaseJournal.Fields.DATA_ALTERACAO.toString()));

		return executeCriteria(criteria);

	}	

	public Long countPesquisaFasesSolicitacaoServico(Integer numero) {

		final DetachedCriteria criteria = this.obterCriteriaBasica( numero,  null, null);

		return executeCriteriaCount(criteria);
	}

	public ScoSsJn obterFaseSolicitacaoServico(Integer numero, Short codigoPontoParada, Integer seq) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(numero, codigoPontoParada, seq);		

		return (ScoSsJn) executeCriteriaUniqueResult(criteria);
	}	
	
	private DetachedCriteria obterCriteriaBasica(Integer numero, Short codigoPontoParada, Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoSsJn.class, "SLSJ");
		

		if (numero != null) {
			criteria.add(Restrictions.eq("SLSJ." +
					ScoSsJn.Fields.NUMERO.toString(),
					numero));			
		}
		
		if (codigoPontoParada != null) {
			criteria.add(Restrictions.eq("SLSJ." +
					ScoSsJn.Fields.PPS_CODIGO.toString(),
					codigoPontoParada));			
		}
		
		if (seq != null) {
			criteria.add(Restrictions.eq("SLSJ." +
					ScoSsJn.Fields.SEQ_JN.toString(),
					seq));			
		}
		
		return criteria;
	}

	
	public Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSsJn.class, "JN");
		
		criteria.add(Restrictions.eq("JN."+ScoSsJn.Fields.NUMERO.toString(), numero));
		
		DetachedCriteria subQueryA = DetachedCriteria.forClass(ScoSsJn.class, "JN2");
		subQueryA.setProjection(Projections.count("JN2."+ScoSsJn.Fields.NUMERO.toString()));
		subQueryA.add(Restrictions.eqProperty("JN."+ScoSsJn.Fields.NUMERO.toString(), "JN2."+ScoSsJn.Fields.NUMERO.toString()));
		subQueryA.add(Restrictions.eq("JN2."+ScoSsJn.Fields.PPS_CODIGO.toString(), Short.valueOf("2")));
		
		criteria.add(Subqueries.lt(1L, subQueryA));
		
		DetachedCriteria subQueryB = DetachedCriteria.forClass(ScoSsJn.class, "JN3");
		subQueryB.setProjection(Projections.property("JN3."+ScoSsJn.Fields.PPS_CODIGO.toString()));
		subQueryB.add(Restrictions.eqProperty("JN."+ScoSsJn.Fields.NUMERO.toString(), "JN3."+ScoSsJn.Fields.NUMERO.toString()));
		
		
		DetachedCriteria subQueryC = DetachedCriteria.forClass(ScoSsJn.class, "JN4");
		subQueryC.add(Restrictions.eqProperty("JN."+ScoSsJn.Fields.NUMERO.toString(), "JN4."+ScoSsJn.Fields.NUMERO.toString()));
		subQueryC.setProjection(Projections.max("JN4."+ScoSsJn.Fields.SEQ_JN.toString()));
		
		subQueryB.add(Subqueries.propertyEq("JN3."+ScoSsJn.Fields.SEQ_JN.toString(), subQueryC));
		
		criteria.add(Subqueries.eq(Short.valueOf("2"), subQueryB));
		
		return executeCriteriaCount(criteria) > 0L;	
	}	
}
