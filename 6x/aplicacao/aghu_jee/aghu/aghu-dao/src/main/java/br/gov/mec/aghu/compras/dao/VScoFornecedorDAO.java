package br.gov.mec.aghu.compras.dao;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * @modulo compras
 * @author frutkowski
 *
 */

public class VScoFornecedorDAO extends BaseDao<VScoFornecedor> {

	private static final long serialVersionUID = 7972996021421662015L;


	public List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(final Object pesquisa){
		return pesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa, 100);
	}

	public List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(final Object pesquisa, final Integer limite) {
		return this.executeCriteria(criarCriteriaPesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa), 0, limite, null, false);
	}

	public Long contarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa) {
		return executeCriteriaCount(criarCriteriaPesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa));
	}
	
	public List<VScoFornecedor> pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(final Object pesquisa){
		return pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(pesquisa, 100);
	}
	
	public List<VScoFornecedor> pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(final Object pesquisa, final Integer limite) {
		return this.executeCriteria(criarCriteriaPesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(pesquisa), 0, limite, null, false);
	}
	
	public Long contarVFornecedorPorNumeroCgcCpfRazaoSocial(Object pesquisa) {
		return executeCriteriaCount(criarCriteriaPesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(pesquisa));
	}

	public Long contarVFornecedorPorCnpjRaiz(String cnpjRaiz) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VScoFornecedor.class);
		
		if (!isOracle()) {
			criteria.add(Restrictions.sqlRestriction("substr(cast(CGC_CPF as varchar),1,8) = '"+cnpjRaiz+"'"));
		} else {
			criteria.add(Restrictions.sqlRestriction("substr(to_char(CGC_CPF),1,8) = '"+cnpjRaiz+"'"));
		}
		
		return executeCriteriaCount(criteria);
	}
	

	private DetachedCriteria criarCriteriaPesquisarVFornecedorPorCgcCpfRazaoSocial(final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VScoFornecedor.class);

		String strParametro = pesquisa.toString();
		Long cpfOuCgc = null;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}else if(CoreUtil.isNumeroLong(strParametro)){
			cpfOuCgc = Long.valueOf(strParametro);
		}

		if (codigo != null || cpfOuCgc != null) {
			criteria.add(Restrictions.or(Restrictions.eq(VScoFornecedor.Fields.NRO_FORNECEDOR.toString(), codigo), Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc)));
//			criteria.add(Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc));
		} else {
			criteria.add(Restrictions.ilike(VScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strParametro, MatchMode.ANYWHERE));

		}
		return criteria;
	}

	public Long pesquisarVFornecedorPorCgcCpfRazaoSocialCount(final Object pesquisa){
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(VScoFornecedor.class);
		
		String strParametro = pesquisa.toString(); 
		Long cpfOuCgc = null;
		
		if(CoreUtil.isNumeroLong(strParametro)){
			cpfOuCgc = Long.valueOf(strParametro);
		}
		
		if (cpfOuCgc != null){
			criteria.add(Restrictions.eq(
					VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc));
		}
		else {
			criteria.add(Restrictions.ilike(
					VScoFornecedor.Fields.RAZAO_SOCIAL.toString(),
					strParametro, MatchMode.ANYWHERE));
		}
		
		return this.executeCriteriaCount(criteria);
		
	}
	
	
	private DetachedCriteria criarCriteriaPesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VScoFornecedor.class);

		String strParametro = pesquisa.toString();
		Long cpfOuCgc = null;
		Integer numero = null;
		

		if (CoreUtil.isNumeroLong(strParametro)) {
			cpfOuCgc = Long.valueOf(strParametro);
		}
		
		if (CoreUtil.isNumeroInteger(strParametro)) {
			numero = Integer.valueOf(strParametro);  
		}

		if (numero != null && cpfOuCgc != null){
			criteria.add(Restrictions.or(Restrictions.eq(VScoFornecedor.Fields.NRO_FORNECEDOR.toString(), numero), Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc)));
		} else if (cpfOuCgc != null) {
			criteria.add(Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc));
		} else {
			criteria.add(Restrictions.ilike(VScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strParametro, MatchMode.ANYWHERE));

		}
		return criteria;
	}
	
	public Long pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(final Object pesquisa){
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(VScoFornecedor.class);
		
		String strParametro = pesquisa.toString(); 
		Long cpfOuCgc = null;
		Integer numero = null;
		
		if(CoreUtil.isNumeroLong(strParametro)){
			cpfOuCgc = Long.valueOf(strParametro);
		}
		if (CoreUtil.isNumeroInteger(strParametro)) {
			numero = Integer.valueOf(strParametro);  
		}
		
		if (cpfOuCgc != null && numero != null){
			criteria.add(Restrictions.or(Restrictions.eq(VScoFornecedor.Fields.NRO_FORNECEDOR.toString(), numero), Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc)));
		}else if(cpfOuCgc != null){
			criteria.add(Restrictions.eq(VScoFornecedor.Fields.CGC_CPF.toString(), cpfOuCgc));
		}else {
			criteria.add(Restrictions.ilike(VScoFornecedor.Fields.RAZAO_SOCIAL.toString(),strParametro, MatchMode.ANYWHERE));
		}
		
		return this.executeCriteriaCount(criteria);
		
	}
	
}
