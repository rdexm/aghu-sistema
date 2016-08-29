package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedorIrregularFiscal;


public class ScoFornecedorIrregularFiscalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFornecedorIrregularFiscal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -827398428270951226L;
	
	public List<ScoFornecedorIrregularFiscal> pesquisarHistoricoEmailsPorFornecedor(Integer frnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedorIrregularFiscal.class, "FIF");
		criteria.createAlias("FIF." + ScoFornecedorIrregularFiscal.Fields.RAP_SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		criteria.add(Restrictions.eq("FIF." + ScoFornecedorIrregularFiscal.Fields.FRN_NUMERO.toString(), frnNumero));
		criteria.addOrder(Order.desc("FIF." + ScoFornecedorIrregularFiscal.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Integer obterMaxNumeroScoFornecedorIrregularFiscal(Integer fornNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedorIrregularFiscal.class);
		criteria.setProjection(Projections.max(ScoFornecedorIrregularFiscal.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoFornecedorIrregularFiscal.Fields.FRN_NUMERO.toString(), fornNumero));
		
		Integer max = (Integer) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			max = Integer.valueOf((int) 0);
		}
		return ++max;
	}
	
}
