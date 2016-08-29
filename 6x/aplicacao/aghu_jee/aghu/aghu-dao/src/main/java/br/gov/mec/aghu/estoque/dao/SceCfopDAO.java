package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class SceCfopDAO extends BaseDao<SceCfop> {

	private static final long serialVersionUID = 5919059795627484230L;
	
	 /**
	  * Restrições da listagem de CFOP.
	  * @param SceCfop
	  * @return
	  */
	 private DetachedCriteria pesquisarSceCfopCriteria(SceCfop sceCfop) {

	  DetachedCriteria criteria = DetachedCriteria.forClass(SceCfop.class);
	 
	  //PMD NPath Complexity
	  criteria = createCriteriaPt1(sceCfop, criteria);
	  criteria = createCriteriaPt2(sceCfop, criteria);

	  return criteria;
	 }
	 
	 /**
	  * @param sceCfop
	  * @param criteria
	  */
	 private DetachedCriteria createCriteriaPt1(SceCfop sceCfop, DetachedCriteria criteria) {
	  if (sceCfop.getCodigo() != null) {
	   criteria.add(Restrictions.eq(SceCfop.Fields.CODIGO.toString(), sceCfop.getCodigo()));
	  }
	  if (sceCfop.getDescricao() != null && !sceCfop.getDescricao().isEmpty()){
	   criteria.add(Restrictions.ilike(SceCfop.Fields.DESCRICAO.toString(), sceCfop.getDescricao(), MatchMode.ANYWHERE));
	  }
	  if (sceCfop.getAplicacao()!= null && !sceCfop.getAplicacao().isEmpty()){
	   criteria.add(Restrictions.ilike(SceCfop.Fields.APLICACAO.toString(), sceCfop.getAplicacao(), MatchMode.ANYWHERE));
	  }
	  if (sceCfop.getCodRelMaterial() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.COD_REL_MATERIAL.toString(), sceCfop.getCodRelMaterial()));
	  }
	  
	  return criteria;
	 }

	 /**
	  * @param sceCfop
	  * @param criteria
	  */
	 private DetachedCriteria createCriteriaPt2(SceCfop sceCfop, DetachedCriteria criteria) {
	  if (sceCfop.getCodRelImobil() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.COD_REL_IMOBIL.toString(), sceCfop.getCodRelImobil()));
	  }
	  if (sceCfop.getGrupo() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.GRUPO.toString(), sceCfop.getGrupo()));
	  }
	  if (sceCfop.getIndNr() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.IND_NR.toString(), sceCfop.getIndNr()));
	  }
	  if (sceCfop.getIndEsl() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.IND_ESL.toString(), sceCfop.getIndEsl()));
	  }
	  if (sceCfop.getIndOutros() != null){
	   criteria.add(Restrictions.eq(SceCfop.Fields.IND_OUTROS.toString(), sceCfop.getIndOutros()));
	  }
	  
	  return criteria;
	 }
	
	/**
	 * Contador CFOP.
	 * @param SceCfop
	 * @return Long
	 */
	public Long pesquisarSceCfopCount(SceCfop sceCfop) {
		DetachedCriteria criteria = pesquisarSceCfopCriteria(sceCfop);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Listagem de CFOP.
	 * @param SceCfop
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return 
	 */
	public List<SceCfop> pesquisarSceCfopList(SceCfop sceCfop, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = pesquisarSceCfopCriteria(sceCfop);
		List<SceCfop> results = this.executeCriteria(criteria, firstResult, maxResults, SceCfop.Fields.CODIGO.toString(), asc); 
		
		return results;
	}
	
	/**
	 * Pesquisar CFOP por chave primária.
	 * @param codigo
	 * @return SceCfop
	 */
	public SceCfop pesquisarCodigo(Short codigo) {
		return obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Remover CFOP.
	 * @param codigo
	 */
	public void excluir(Short codigo) throws BaseException {
		removerPorId(codigo);
	}
	
	/**
	 * Alterar CFOP.
	 * @param sceCfop
	 */
	public void alterar(SceCfop sceCfop) {
		 merge(sceCfop);
	}
	
	/**
	 * Inserir CFOP.
	 * @param sceCfop
	 */
	public void incluir(SceCfop sceCfop) {
		persistir(sceCfop);
	}

}
