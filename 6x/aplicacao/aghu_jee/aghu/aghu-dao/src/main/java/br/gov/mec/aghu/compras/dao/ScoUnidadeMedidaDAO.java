package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoUnidadeMedida;


/**
 * 
 * @modulo compras
 *
 */
public class ScoUnidadeMedidaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoUnidadeMedida>{

	
	private static final long serialVersionUID = 628092302286262667L;

	/**
	 *  Obtém uma unidade(s) de medida ativa(s) por código ou descrição
	 * @param param
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaAtivaPorCodigoDescricao(Object param) {
		return pesquisarUnidadeMedidaPorCodigoDescricao(param, true);		
	}
	
	
	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * @param param
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object param) {
		return pesquisarUnidadeMedidaPorCodigoDescricao(param, false);		
	}
	
	public Long pesquisarUnidadeMedidaPorCodigoDescricaoCount(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion criterionCodigo = Restrictions.ilike(ScoUnidadeMedida.Fields.CODIGO.toString(), 
					StringUtils.trim(strPesquisa), MatchMode.EXACT);
			Criterion criterionDescricao = Restrictions.ilike(ScoUnidadeMedida.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		return executeCriteriaCount(criteria);
	}

	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * @param param
	 * @param apenasAtivos
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object param, boolean apenasAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion criterionCodigo = Restrictions.ilike(ScoUnidadeMedida.Fields.CODIGO.toString(), 
					StringUtils.trim(strPesquisa), MatchMode.EXACT);
			Criterion criterionDescricao = Restrictions.ilike(ScoUnidadeMedida.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		
		if(apenasAtivos){
			criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.DESCRICAO.toString()));
		} else{
			criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.CODIGO.toString()));
		}

		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * @param param
	 * @param apenasAtivos
	 * @return
	 */
	public Long pesquisarUnidadeMedidaPorCodigoDescricaoCount(Object param, boolean apenasAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion criterionCodigo = Restrictions.ilike(ScoUnidadeMedida.Fields.CODIGO.toString(), 
					StringUtils.trim(strPesquisa), MatchMode.EXACT);
			Criterion criterionDescricao = Restrictions.ilike(ScoUnidadeMedida.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		
		if(apenasAtivos){
			criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.DESCRICAO.toString()));
		} else{
			criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.CODIGO.toString()));
		}

		return executeCriteriaCount(criteria);
	}

	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * @param param
	 * @param apenasAtivos
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorSigla(Object param, boolean apenasAtivos) {
		DetachedCriteria criteria = montaCriteriaUnidadeMedidaPorSigla(param,
				apenasAtivos);
		
		criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.CODIGO.toString()));
		
		List<ScoUnidadeMedida> unidades =  executeCriteria(criteria, 0, 100, null, true);
		
		if (unidades != null && !unidades.isEmpty()) {
			
			return unidades;
			
		}

		return pesquisarUnidadeMedidaPorCodigoDescricao(param, apenasAtivos);
		
	}
	
	
	/**
	 *  em {@code ScoUnidadeMedida}
	 * 
	 * @param unidadeMedida
	 * @return Se existe ou nao registro com o mesmo codigo
	 */
	public boolean verificarCodigo(ScoUnidadeMedida unidadeMedida) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoUnidadeMedida.class);

		if (unidadeMedida.getCodigo() != null) {
			criteria.add(Restrictions.eq(
					ScoUnidadeMedida.Fields.CODIGO.toString(),unidadeMedida.getCodigo()));
		}
		if (this.executeCriteriaCount(criteria)> 0 ){
			return false;
		}
		return true;
	}
	
	/**
	 *  em {@code ScoUnidadeMedida}
	 * 
	 * @param unidadeMedida
	 * @return Se existe ou nao registro com a mesma descrição
	 */
	public boolean verificarDescricao(ScoUnidadeMedida unidadeMedida) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);

		if(unidadeMedida.getCodigo()!=null){
			criteria.add(Restrictions.ne(
					ScoUnidadeMedida.Fields.CODIGO.toString(),unidadeMedida.getCodigo().toUpperCase()));
		}
		
		if (unidadeMedida.getDescricao() != null) {
			criteria.add(Restrictions.ilike(
					ScoUnidadeMedida.Fields.DESCRICAO.toString(),unidadeMedida.getDescricao().toUpperCase(),MatchMode.EXACT));
		}
		if (this.executeCriteriaCount(criteria)> 0 ){
			return false;
		}
		return true;
		
	}
	
	
	
	/**
	 * Pesquisar unidadeMedida
	 * @param ScoUnidadeMedida
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedida(Integer _firstResult,
			Integer _maxResult, String _orderProperty, boolean _asc,
			ScoUnidadeMedida unidadeMedida ) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoUnidadeMedida.class);

		if (unidadeMedida.getCodigo() != null && !"".equalsIgnoreCase(unidadeMedida.getCodigo())) {
			criteria.add(Restrictions.eq(
					ScoUnidadeMedida.Fields.CODIGO.toString(),unidadeMedida.getCodigo().toUpperCase()));
		}
		if (unidadeMedida.getDescricao() != null && !"".equalsIgnoreCase(unidadeMedida.getDescricao())) {

			criteria.add(Restrictions.ilike(
					ScoUnidadeMedida.Fields.DESCRICAO.toString(), unidadeMedida.getDescricao().toUpperCase(), MatchMode.ANYWHERE));
		}
		if (unidadeMedida.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoUnidadeMedida.Fields.IND_SITUACAO.toString(), unidadeMedida.getSituacao()));
		}
		
		criteria.addOrder(Order.asc(ScoUnidadeMedida.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, _firstResult, _maxResult,
				_orderProperty, _asc);
	}
	
	
	/**
	 * Recupera a contagem de registros encontrados em {@code ScoUnidadeMedida}
	 * 
	 * @param unidadeMedida
	 * @return Número indicando o total de registros na tabela
	 */
	public Long listarUnidadeMedidaCount(ScoUnidadeMedida unidadeMedida) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);

		if (unidadeMedida.getCodigo() != null && !"".equalsIgnoreCase(unidadeMedida.getCodigo())) {
			criteria.add(Restrictions.eq(
					ScoUnidadeMedida.Fields.CODIGO.toString(),unidadeMedida.getCodigo().toUpperCase()));
		}
		if (unidadeMedida.getDescricao() != null && !"".equalsIgnoreCase(unidadeMedida.getDescricao())) {
		
			criteria.add(Restrictions.ilike(
					ScoUnidadeMedida.Fields.DESCRICAO.toString(), unidadeMedida.getDescricao().toUpperCase(), MatchMode.ANYWHERE));
		}
		if (unidadeMedida.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoUnidadeMedida.Fields.IND_SITUACAO.toString(), unidadeMedida.getSituacao()));
		}

		return this.executeCriteriaCount(criteria);
	}
	
	public ScoUnidadeMedida obterPorCodigo(String codigo){		

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);
		criteria.add(Restrictions.eq(ScoUnidadeMedida.Fields.CODIGO.toString(), codigo));
		return (ScoUnidadeMedida)this.executeCriteriaUniqueResult(criteria);

	}

	public Long pesquisarUnidadeMedidaPorSiglaCount(Object param, boolean apenasAtivos) {
		DetachedCriteria criteria = montaCriteriaUnidadeMedidaPorSigla(param,
				apenasAtivos);
		
		Long countUnidades =  executeCriteriaCount(criteria);
		
		if (countUnidades != null && countUnidades > 0) {
			return countUnidades;
		}

		return pesquisarUnidadeMedidaPorCodigoDescricaoCount(param, apenasAtivos);
	}


	private DetachedCriteria montaCriteriaUnidadeMedidaPorSigla(Object param,
			boolean apenasAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoUnidadeMedida.class);
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(ScoUnidadeMedida.Fields.CODIGO.toString(), StringUtils.trim(strPesquisa), MatchMode.EXACT));
		}
		
		if (apenasAtivos){
			criteria.add(Restrictions.eq(ScoUnidadeMedida.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		return criteria;
	}
	
	
}
