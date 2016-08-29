package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * @modulo orcamento
 * @author 
 *
 */
public class FsoConveniosFinanceiroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoConveniosFinanceiro> {

	private static final long serialVersionUID = 2495694203473216632L;


	public List<FsoConveniosFinanceiro> listarFsoConveniosAtiva(Object objPesquisa) {
		List<FsoConveniosFinanceiro> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa);
		
		criteria.addOrder(Order.asc(FsoConveniosFinanceiro.Fields.CODIGO.toString()));
		criteria.add(Restrictions.eq(FsoConveniosFinanceiro.Fields.SITUACAO.toString(), DominioSituacao.A));
		lista = executeCriteria(criteria, 0, 400, null, true);
		
		return lista;
	}
	
	public List<FsoConveniosFinanceiro> listarConvenios(Object objPesquisa) {
		List<FsoConveniosFinanceiro> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa);
		
		criteria.addOrder(Order.asc(FsoConveniosFinanceiro.Fields.DESC.toString()));
		criteria.add(Restrictions.eq(FsoConveniosFinanceiro.Fields.SITUACAO.toString(), DominioSituacao.A));
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}

	public Long listarConveniosCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa);
		criteria.add(Restrictions.eq(FsoConveniosFinanceiro.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}

	
	private DetachedCriteria montarCriteriaScoMateriais(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoConveniosFinanceiro.class);
		String strPesquisa = (String) objPesquisa;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(FsoConveniosFinanceiro.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					FsoConveniosFinanceiro.Fields.DESC.toString(), strPesquisa,MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
}