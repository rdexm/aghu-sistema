package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoCumXProgrEntrega;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;

public class ScoCumXProgrEntregaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCumXProgrEntrega> {

	private static final long serialVersionUID = 7560943552026609163L;

	public Integer contarProgrEntregasPorAf(final Integer numAf) {
		/*
		 * SELECT COUNT(1) FROM SCO_CUM_X_PROGR_ENTREGAS CPE,
		 * SCO_PROGR_ENTREGA_ITENS_AF PEA WHERE PEA.IAF_AFN_NUMERO = [numero AF]
		 * AND CPE.PEA_IAF_AFN_NUMERO = PEA.IAF_AFN_NUMERO AND
		 * CPE.PEA_IAF_NUMERO = PEA.IAF_NUMERO AND CPE.PEA_PARCELA = PEA.PARCELA
		 */

		final Query q = createHibernateQuery(
						"select count(*) from ScoCumXProgrEntrega CPE, ScoProgEntregaItemAutorizacaoFornecimento PEA where CPE.scoProgEntregaItemAutorizacaoFornecimento.id.iafAfnNumero = PEA.id.iafAfnNumero and CPE.scoProgEntregaItemAutorizacaoFornecimento.id.iafNumero = PEA.id.iafNumero and CPE.scoProgEntregaItemAutorizacaoFornecimento.id.parcela = PEA.id.parcela and PEA.scoItensAutorizacaoForn.id.afnNumero = :numeroAF ");
		q.setParameter("numeroAF", numAf);

		return ((Long) q.uniqueResult()).intValue();
	}
	
	public Long contarProgrEntregasPorAfeAfeNum(Integer afNum, Integer afeNumero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCumXProgrEntrega.class, "CPEA");		
		criteria.createAlias("CPEA." + ScoCumXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO.toString(), "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString() , afNum));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO, afeNumero));
		
		return this.executeCriteriaCount(criteria);
	}
	
	
}
