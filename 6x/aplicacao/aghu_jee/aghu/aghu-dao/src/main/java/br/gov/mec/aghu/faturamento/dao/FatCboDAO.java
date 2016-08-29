package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class FatCboDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCbos> {
	private static final long serialVersionUID = 4073809993101594518L;

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por CBOs,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<FatCbos>
	 *  
	 */
	public List<FatCbos> listarCbos(Object objPesquisa) throws ApplicationBusinessException{
		final DetachedCriteria criteria = obterCriteriaListarCbos(objPesquisa);
		criteria.addOrder(Order.asc(FatCbos.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 100, FatCbos.Fields.CODIGO.toString(), true);
	}

	public List<FatCbos> listarCbosAtivos(Object objPesquisa) throws ApplicationBusinessException{
		final DetachedCriteria criteria = obterCriteriaListarCbos(objPesquisa);
		criteria.add(Restrictions.isNull(FatCbos.Fields.DT_FIM.toString()));
		criteria.addOrder(Order.asc(FatCbos.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 100, FatCbos.Fields.CODIGO.toString(), true);
	}
	
	
	private DetachedCriteria obterCriteriaListarCbos(Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCbos.class);
		
		//Foi implementado desta forma pois o codCbo eh uma string que pode ser composta por letras E numeros.
		if(objPesquisa != null && StringUtils.isNotEmpty(objPesquisa.toString())){
			criteria.add(Restrictions.or(
										  Restrictions.eq(FatCbos.Fields.CODIGO.toString(), objPesquisa.toString()),
										  Restrictions.ilike(FatCbos.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE)
										)
						);
		}
		return criteria;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por CBOs,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarCbosCount(Object objPesquisa){
		return executeCriteriaCount(obterCriteriaListarCbos(objPesquisa));
	}
	
	public Long listarCbosAtivosCount(Object objPesquisa){
		DetachedCriteria criteria = obterCriteriaListarCbos(objPesquisa);
		criteria.add(Restrictions.isNull(FatCbos.Fields.DT_FIM.toString()));
		return executeCriteriaCount(criteria);
	}

	
	private DetachedCriteria obterCriteriaFatCboPorCodigoVigente(final String codigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCbos.class);
		criteria.add(Restrictions.eq(FatCbos.Fields.CODIGO.toString(), codigo));
		//-- marina 23/08/2011
		criteria.add(Restrictions.isNull(FatCbos.Fields.DT_FIM.toString()));
		return criteria;
	}

	public FatCbos obterFatCboPorCodigoVigente(final String codigo) {

		List<FatCbos> retorno = executeCriteria(obterCriteriaFatCboPorCodigoVigente(codigo));
		if(retorno == null || retorno.isEmpty()){
			return null;
		}
		return retorno.get(0);
	}
	
	public List<FatCbos> buscarFatCboPorCodigoVigente(final String codigo) {
		return executeCriteria(obterCriteriaFatCboPorCodigoVigente(codigo));
	}
	
	public List<FatCbos> listarCbosSemDataFim() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCbos.class);
		criteria.add(Restrictions.isNull(FatCbos.Fields.DT_FIM.toString()));
		criteria.addOrder(Order.asc(FatCbos.Fields.CODIGO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Feito assim para melhorar desempenho, tabela nao possui triggers
	 * 
	 * @param seq
	 * @param dtFim
	 */
	public void atualizarFatCbo(Integer seq, Date dtFim) {
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();

		hql.append("update ")
		.append(FatCbos.class.getName())
		.append(" set ")
		.append(FatCbos.Fields.DT_FIM.toString())
		.append(" = :dtFim")
		.append(" where ")
		.append(FatCbos.Fields.SEQ.toString())
		.append(" = :seq")
		.append(" and ")
		.append(FatCbos.Fields.DT_FIM.toString())
		.append(" is null");

		query = createHibernateQuery(hql.toString());
		query.setParameter("dtFim", dtFim);
		query.setParameter("seq", seq);
		
		query.executeUpdate();
	}
	
	/**
	 * Método para obter o count na pesquisa dos CBOs.
	 * @param filtro
	 * @return
	 */
	public Long pesquisarCadastroBrasileiroOcupacoesCount(FatCbos filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCbos.class);
		
		montaCriteriaFatCbos(criteria, filtro);
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Método utilizado para obter os CBOs, filtrando por código e descrição.
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroFatCbo
	 * @return
	 */
	public List<FatCbos> pesquisarCadastroBrasileiroOcupacoes(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FatCbos filtroFatCbo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCbos.class);

		montaCriteriaFatCbos(criteria, filtroFatCbo);

		if(orderProperty == null){			
			criteria.addOrder(Order.desc(FatCbos.Fields.DT_INICIO.toString()));
			criteria.addOrder(Order.asc(FatCbos.Fields.CODIGO.toString()));
		}
		
		return this.executeCriteria(criteria, firstResult, maxResults.intValue(),
				orderProperty, asc);
	}

	/**
	 * Método que filtra código e descrição.
	 * @param criteria
	 * @param filtroFatCbos
	 */
	private void montaCriteriaFatCbos(DetachedCriteria criteria,
			final FatCbos filtroFatCbos) {

		if (StringUtils.isNotBlank(filtroFatCbos.getCodigo())) {
			criteria.add(Restrictions.eq(FatCbos.Fields.CODIGO.toString(),
					filtroFatCbos.getCodigo()));
		}

		if (StringUtils.isNotBlank(filtroFatCbos.getDescricao())) {
			criteria.add(Restrictions.ilike(
					FatCbos.Fields.DESCRICAO.toString(),
					replaceCaracterEspecial(filtroFatCbos.getDescricao()), 
					MatchMode.ANYWHERE));
		}
		
	}
	
	/**
	 * Formata as ocorrencias de "%" e "_" para que as mesmas sejam submetidas à pesquisa.
	 * 
	 * @param descricao {@link String}
	 * @return {@link String}
	 */
	private String replaceCaracterEspecial(String descricao) {
		
		return descricao.replace("_", "\\_").replace("%", "\\%");
	}
}
