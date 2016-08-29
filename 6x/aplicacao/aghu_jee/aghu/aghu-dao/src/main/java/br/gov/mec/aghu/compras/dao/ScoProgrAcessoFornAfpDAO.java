package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrAcessoFornAfp;

public class ScoProgrAcessoFornAfpDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoProgrAcessoFornAfp> {


	private static final long serialVersionUID = -4899508572629666542L;
	
	/**
	 * C6
	 * @param numeroAfe
	 * @param numeroAfeAfn
	 * @return
	 */
	public Date obterUltimoAcessoFornecedorPortal(Integer numeroAfe, Integer numeroAfeAfn){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrAcessoFornAfp.class);
		criteria.add(Restrictions.eq(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_NUMERO.toString(), numeroAfe));
		criteria.add(Restrictions.eq(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_AFN_NUMERO.toString(), numeroAfeAfn));
		criteria.setProjection(Projections.max(ScoProgrAcessoFornAfp.Fields.DT_ACESSO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificarAcessosFronAFP(Integer afnNumero, Integer afeNumero, ScoFornecedor forcenedor){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrAcessoFornAfp.class);
		criteria.add(Restrictions.eq(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_NUMERO.toString(), afeNumero));
		criteria.add(Restrictions.eq(ScoProgrAcessoFornAfp.Fields.SCO_FORNECEDOR.toString(), forcenedor));
		
		
		return this.executeCriteriaCount(criteria) > 0;
	}

}
