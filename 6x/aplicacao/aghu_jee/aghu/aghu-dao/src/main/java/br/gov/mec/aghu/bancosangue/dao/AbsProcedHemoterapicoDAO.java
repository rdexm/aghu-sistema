package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;

public class AbsProcedHemoterapicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsProcedHemoterapico> {

	
	private static final long serialVersionUID = -4865866535701900570L;

	/**
	 * Obtem os Procedimentos Hemoterapicos que tem status Ativo
	 * 
	 * @return List<AbsProcedHemoterapico>
	 */
	public List<AbsProcedHemoterapico> obterProcedHemoterapicosAtivos() {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsProcedHemoterapico.class);

		criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

	/**
	 * Obtem os Procedimentos Hemoterapicos pelo código informado
	 * 
	 * @return AbsProcedHemoterapico
	 */
	
	public AbsProcedHemoterapico obterProcedHemoterapicoPorCodigo(String codigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsProcedHemoterapico.class);
		criteria.createAlias(AbsProcedHemoterapico.Fields.RAP_SERVIDORES.toString(), "SERV");
		criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.CODIGO
				.toString(), codigo));

		return (AbsProcedHemoterapico) executeCriteriaUniqueResult(criteria);
	}
	
	
	  /**
	 * Metodo que monta uma criteria para pesquisar AbsProcedHemoterapicos filtrando
	 * pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaProcedHemoterapicoPorCodigo(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsProcedHemoterapico.class);
		String strPesquisa = (String) objPesquisa;
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(AbsProcedHemoterapico.Fields.CODIGO.toString(), strPesquisa, MatchMode.EXACT));
		}
		
		return criteria;
	}
		

	  /**
	 * Metodo que monta uma criteria para pesquisar AbsProcedHemoterapicos filtrando
	 *  pela descricao.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaProcedHemoterapicoPorDescricao(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsProcedHemoterapico.class);
		String strPesquisa = (String) objPesquisa;
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(AbsProcedHemoterapico.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por AbsProcedHemoterapicos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<AbsProcedHemoterapico>
	 */
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(Object objPesquisa){
		List<AbsProcedHemoterapico> lista = null;
		DetachedCriteria criteria = montarCriteriaProcedHemoterapicoPorCodigo(objPesquisa);
		criteria.addOrder(Order.asc(AbsProcedHemoterapico.Fields.CODIGO.toString()));
		
		lista = executeCriteria(criteria);
		
		if(lista == null || lista.isEmpty()){
			criteria = montarCriteriaProcedHemoterapicoPorDescricao(objPesquisa);
			
			criteria.addOrder(Order.asc(AbsProcedHemoterapico.Fields.CODIGO.toString()));
			
			lista = executeCriteria(criteria, 0, 100, null, true);
		}
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por AbsProcedHemoterapicos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarAbsProcedHemoterapicoCount(Object objPesquisa){
		Long count = 0L;
		DetachedCriteria criteria = montarCriteriaProcedHemoterapicoPorCodigo(objPesquisa);
		count = executeCriteriaCount(criteria);
		if(count == null || count == 0){
			criteria = montarCriteriaProcedHemoterapicoPorDescricao(objPesquisa);
			count = executeCriteriaCount(criteria);
		}
		return count;
	}
	
	/**
	 * Obtém lista de Procedimentos Hemoterápicos utilizando vários filtros, se informados. Ordena por código e depois descrição, de forma ascendente.
	 * @param codigo
	 * @param descricao
	 * @param indAmostra
	 * @param indJustificativa
	 * @param indSituacao
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @author bruno.mourao
	 * @since 09/05/2012
	 */
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(String codigo,String descricao,Boolean indAmostra,Boolean indJustificativa,DominioSituacao indSituacao, int firstResult, int maxResults){
		
		DetachedCriteria criteria = montarCriteriaListarProcedHemoterapico(codigo, descricao, indAmostra, indJustificativa, indSituacao);
		
		criteria.addOrder(Order.asc(AbsProcedHemoterapico.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(AbsProcedHemoterapico.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResults, null, true);
		
	}

	private DetachedCriteria montarCriteriaListarProcedHemoterapico(
			String codigo, String descricao, Boolean indAmostra,
			Boolean indJustificativa, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsProcedHemoterapico.class);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.CODIGO.toString(), codigo));
		}
		if(descricao != null){
			criteria.add(Restrictions.like(AbsProcedHemoterapico.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indAmostra != null){
			criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.IND_AMOSTRA.toString(), indAmostra));
		}
		if(indJustificativa != null){
			criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.IND_JUSTIFICATIVA.toString(), indJustificativa));
		}
		if(indSituacao != null){
			criteria.add(Restrictions.eq(AbsProcedHemoterapico.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}
	
	public Long listarAbsProcedHemoterapicosCount(String codigo,String descricao,Boolean indAmostra,Boolean indJustificativa,DominioSituacao indSituacao){
		return this.executeCriteriaCount(montarCriteriaListarProcedHemoterapico(codigo, descricao, indAmostra, indJustificativa, indSituacao));
	}
	
	/**
	 * Pesquisa procedimentos por codigo ou descricao.
	 * @param parametro
	 * @return
	 * @author bruno.mourao
	 * @since 11/05/2012
	 */
	public List<AbsProcedHemoterapico> pesquisarProcedimentosHemoterapicosPorCodigoDescricao(Object parametro){
		String paramPesq = (String) parametro;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsProcedHemoterapico.class);
		
		SimpleExpression expCodigo = Restrictions.eq(AbsProcedHemoterapico.Fields.CODIGO.toString(), paramPesq);
		Criterion expDescricao = Restrictions.ilike(AbsProcedHemoterapico.Fields.DESCRICAO.toString(), paramPesq,MatchMode.ANYWHERE);
		
		criteria.add(Restrictions.or(expCodigo, expDescricao));
		
		return this.executeCriteria(criteria, 0, 100, AbsProcedHemoterapico.Fields.CODIGO.toString(), true);
	}

}
