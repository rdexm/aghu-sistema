package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoScJn;

public class ScoScJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoScJn> {

	private static final long serialVersionUID = -2946362328475119435L;

	public List<ScoScJn> listarPesquisaFasesSolicitacaoCompra(Integer numero) {

		DetachedCriteria criteria = this.obterCriteriaBasica(numero,  null, null);
	
		criteria.addOrder(Order.desc("SLCJ." + ScoScJn.Fields.JN_DATE_TIME.toString()));

		return executeCriteria(criteria);

	}	

	public Long countPesquisaFasesSolicitacaoCompra(Integer numero) {

		DetachedCriteria criteria = this.obterCriteriaBasica( numero,  null, null);

		return executeCriteriaCount(criteria);
	}

	public ScoScJn obterFaseSolicitacaoCompra(Integer numero, Short codigoPontoParada, Integer seq) {

		DetachedCriteria criteria = this.obterCriteriaBasica(numero, codigoPontoParada, seq);		

		return (ScoScJn) executeCriteriaUniqueResult(criteria);

	}	
	
	private DetachedCriteria obterCriteriaBasica(Integer numero, Short codigoPontoParada, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoScJn.class, "SLCJ");
		
		criteria.createAlias(ScoScJn.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN );
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN );
		criteria.setFetchMode(ScoScJn.Fields.PONTO_PARADA_SOLICITACAO.toString(), FetchMode.JOIN);

		if (numero != null) {
			criteria.add(Restrictions.eq("SLCJ." +
					ScoScJn.Fields.NUMERO.toString(),
					numero));			
		}
		
		if (codigoPontoParada != null) {
			criteria.add(Restrictions.eq("SLCJ." +
					ScoScJn.Fields.PPS_CODIGO_LOC_PROXIMA.toString(),
					codigoPontoParada));			
		}
		
		if (seq != null) {
			criteria.add(Restrictions.eq("SLCJ." +
					ScoScJn.Fields.SEQ_JN.toString(),
					seq));			
		}
		
		return criteria;
	}
	
	public Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoScJn.class, "JN");
		
		criteria.add(Restrictions.eq("JN."+ScoScJn.Fields.NUMERO.toString(), numero));
		
		DetachedCriteria subQueryA = DetachedCriteria.forClass(ScoScJn.class, "JN2");
		subQueryA.setProjection(Projections.count("JN2."+ScoScJn.Fields.NUMERO.toString()));
		subQueryA.add(Restrictions.eqProperty("JN."+ScoScJn.Fields.NUMERO.toString(), "JN2."+ScoScJn.Fields.NUMERO.toString()));
		subQueryA.add(Restrictions.eq("JN2."+ScoScJn.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), Short.valueOf("2")));
		
		criteria.add(Subqueries.lt(1L, subQueryA));
		
		DetachedCriteria subQueryB = DetachedCriteria.forClass(ScoScJn.class, "JN3");
		subQueryB.setProjection(Projections.property("JN3."+ScoScJn.Fields.PPS_CODIGO_LOC_PROXIMA.toString()));
		subQueryB.add(Restrictions.eqProperty("JN."+ScoScJn.Fields.NUMERO.toString(), "JN3."+ScoScJn.Fields.NUMERO.toString()));
		
		
		DetachedCriteria subQueryC = DetachedCriteria.forClass(ScoScJn.class, "JN4");
		subQueryC.add(Restrictions.eqProperty("JN."+ScoScJn.Fields.NUMERO.toString(), "JN4."+ScoScJn.Fields.NUMERO.toString()));
		subQueryC.setProjection(Projections.max("JN4."+ScoScJn.Fields.JN_DATE_TIME.toString()));
		
		subQueryB.add(Subqueries.propertyEq("JN3."+ScoScJn.Fields.JN_DATE_TIME.toString(), subQueryC));
		
		criteria.add(Subqueries.eq(Short.valueOf("2"), subQueryB));
		
		return executeCriteriaCount(criteria) > 0L;
	}
}
