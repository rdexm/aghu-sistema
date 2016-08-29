package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo compras
 *
 */
public class ScoMarcaModeloDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMarcaModelo>{
	
	private static final long serialVersionUID = 300197271404866667L;
	
	
	public DetachedCriteria montaCriteriaScoMarcaModelo(Integer codigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.MCM_CODIGO.toString(), codigo));
		}
		
		return criteria;
	}
	
	
	//Lista recuperada para paginaçao. 
	public List<ScoMarcaModelo> pesquisarMarcaModelo(Integer firstResult, Integer maxResult, String orderProperty, 
																	boolean asc, Integer codigo){
		
		DetachedCriteria criteria = montaCriteriaScoMarcaModelo(codigo);
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	//count da lista de paginaçao.
	public Long pesquisarMarcaModeloCount(Integer codigo){

		DetachedCriteria criteria = montaCriteriaScoMarcaModelo(codigo);
		
		return this.executeCriteriaCount(criteria);
	}
	
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorMarcaComercial(Integer id){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class);
		criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.MCM_CODIGO.toString(), id));
		
		return executeCriteria(criteria);
	}
	
	public ScoMarcaModelo buscarScoMarcaModeloPorId(Integer seqp, Integer codigo) {
		if(seqp == null){
			return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class);
		criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.MCM_CODIGO.toString(), codigo));
		return (ScoMarcaModelo) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método de pesquisa para a Suggestion Box. 
	 * @param param
	 * @return lista com registros encontrados
	 */
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		 return this.pesquisarMarcaModeloPorCodigoDescricaoSemLucene(param, marcaComercial, indAtivo);
	}
	
	/**
	 * Método de pesquisa para a Suggestion Box sem Lucene. 
	 * @param param
	 * @return lista com registros encontrados
	 */
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricaoSemLucene(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		DetachedCriteria criteria = obterCriteriaMarcaModeloPorCodigoDescricao(param, marcaComercial, indAtivo);
        return executeCriteria(criteria, 0, 100, ScoMarcaModelo.Fields.DESCRICAO.toString(), true);
		
	}
	
	/**
	 * Método de count para a Suggestion Box. 
	 * @param param
	 * @return qtde de registros encontrados
	 */
	public Long pesquisarMarcaModeloPorCodigoDescricaoCount(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		
		DetachedCriteria criteria = obterCriteriaMarcaModeloPorCodigoDescricao(param, marcaComercial, indAtivo);

		return this.executeCriteriaCount(criteria);
		
	}
	
	private DetachedCriteria obterCriteriaMarcaModeloPorCodigoDescricao(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {

		String strPesquisa = (String) param;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion restriction = Restrictions.ilike(
					ScoMarcaModelo.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE);
			
			if (CoreUtil.isNumeroInteger(param)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(
						ScoMarcaModelo.Fields.SEQP.toString(),
						Integer.valueOf(strPesquisa)));
			}
			
			criteria.add(restriction);
		}
		
		if (marcaComercial != null) {
			criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.MCM_CODIGO.toString(), marcaComercial.getCodigo()));
		}
		
		if (indAtivo != null) {
			if (indAtivo) {
				criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			}
			else {
				criteria.add(Restrictions.eq(ScoMarcaModelo.Fields.IND_SITUACAO.toString(), DominioSituacao.I));
			}
		}

		return criteria;
	}
	
	public List<ScoMarcaModelo> pesquisarMarcaModelo(Object marcaModelo){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class, "MOM");
		criteria.createAlias("MOM." + ScoMarcaModelo.Fields.MARCA_COMERCIAL.toString(), "MCM", Criteria.INNER_JOIN);
		if(!marcaModelo.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(marcaModelo)){
				criteria.add(Restrictions.eq("MCM." + ScoMarcaComercial.Fields.CODIGO.toString(), Integer.parseInt(marcaModelo.toString())));
			} else {
				criteria.add(Restrictions.ilike("MCM." + ScoMarcaComercial.Fields.DESCRICAO.toString(), marcaModelo.toString(), MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Verifica se existe outro modelo para mesma marca com a mesma descricao
	 * @param param
	 * @return lista com registros encontrados
	 */
	public Boolean verificarExisteModeloDescricao(ScoMarcaModelo marcaModelo , ScoMarcaComercial scoMarcaComercial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaModelo.class, "MOM");
		criteria.createAlias("MOM." + ScoMarcaModelo.Fields.MARCA_COMERCIAL.toString(), "MCM");
		
		criteria.add(Restrictions.eq("MCM." + ScoMarcaComercial.Fields.CODIGO.toString(), scoMarcaComercial.getCodigo()));
		criteria.add(Restrictions.eq("MOM." + ScoMarcaModelo.Fields.DESCRICAO.toString(), marcaModelo.getDescricao()));
		criteria.add(Restrictions.ne("MOM." + ScoMarcaModelo.Fields.SEQP.toString(),marcaModelo.getId().getSeqp()));
		
		return (this.executeCriteriaCount(criteria) > 0);
		 
	}

}