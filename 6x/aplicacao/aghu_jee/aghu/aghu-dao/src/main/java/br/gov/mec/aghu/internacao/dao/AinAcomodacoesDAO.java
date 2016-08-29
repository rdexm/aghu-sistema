package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AinAcomodacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinAcomodacoes>{
	
	
	private static final long serialVersionUID = 1702165474381256183L;

	/**
	 * @return
	 */
	private DetachedCriteria obterCriteriaAcomodacoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcomodacoes.class);
		return criteria;
	}
	
	private DetachedCriteria createPesquisaAcomodacoesCriteria(Integer codigo,
			String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinAcomodacoes.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AinAcomodacoes.Fields.SEQUENCIAL
					.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AinAcomodacoes.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Retorna uma lista de acomodações
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricao(Object parametro) {

		List<AinAcomodacoes> listaResultado;

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro);

		listaResultado = this.executeCriteria(criteria);

		return listaResultado;
	}

	/**
	 * Metódo de consulta de Acomodação por Codigo ou Descrição
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(Object parametro) {
		String descricao = null;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(parametro)) {
			if(parametro instanceof String) {
				codigo = Integer.valueOf((String)parametro);
			}
			else {
				codigo = (Integer)parametro;
			}
		}
		
		else if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		DetachedCriteria criteria = obterCriteriaAcomodacoes();

		if (descricao != null) {
			criteria.add(Restrictions.ilike(AinAcomodacoes.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(AinAcomodacoes.Fields.SEQUENCIAL
					.toString(), codigo));
		}
		
		criteria.addOrder(Order.asc(AinAcomodacoes.Fields.DESCRICAO.toString()));

		return criteria;

	}
	
	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacoes(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao) {

		DetachedCriteria criteria = createPesquisaAcomodacoesCriteria(codigo, descricao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Método responsável por obter Acomodações que possuam a mesma descrição.
	 * 
	 * @param acomodacao
	 */
	public List<AinAcomodacoes> getAcomodacaoComMesmaDescricao(AinAcomodacoes acomodacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcomodacoes.class);

		criteria.add(Restrictions.ilike(AinAcomodacoes.Fields.DESCRICAO
				.toString(), acomodacao.getDescricao(), MatchMode.EXACT));

		if (acomodacao.getSeq() != null) {
			criteria.add(Restrictions.ne(AinAcomodacoes.Fields.SEQUENCIAL
					.toString(), acomodacao.getSeq()));
		}

		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param codigo
	 * @param tipo
	 * @return
	 */
	public Long pesquisaAcomodacoesCount(Integer codigo, String tipo) {
		return executeCriteriaCount(createPesquisaAcomodacoesCriteria(codigo, tipo));
	}
	
	/**
	 * Retorna uma lista de acomodações ordenado por descrição
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(Object parametro) {

		List<AinAcomodacoes> listaResultado;

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro);

		criteria.addOrder(Order.asc(AinAcomodacoes.Fields.DESCRICAO.toString()));

		listaResultado = this.executeCriteria(criteria);

		return listaResultado;
	}




}
