package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.search.Lucene;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
public class ScoMarcaComercialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMarcaComercial> {

	private static final long serialVersionUID = -5645400979583088844L;
	
	@Inject
	private Lucene lucene;


	//método para popular o SG de marcas comerciais da estória #5591
	public List<ScoMarcaComercial> obterMarcas(Object param) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMarcaComercial.class);

		String strPesquisa = (String) param;
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion restriction = Restrictions.ilike(
					ScoMarcaComercial.Fields.DESCRICAO.toString(), strPesquisa,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(
						ScoMarcaComercial.Fields.CODIGO.toString(),
						Integer.parseInt(strPesquisa)));
			}

			criteria.add(restriction);
		}
		
		criteria.add(Restrictions.eq(
				ScoMarcaComercial.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria, 0, 100, 
				ScoMarcaComercial.Fields.DESCRICAO.toString(), true);
	}
	
	public List<ScoMarcaComercial> listarScoMarcasAtiva(Object objPesquisa){
		List<ScoMarcaComercial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMarcasNomeAtiva(objPesquisa);
		
		criteria.addOrder(Order.asc(ScoMarcaComercial.Fields.CODIGO.toString()));
		
		//lista = executeCriteria(criteria);
		lista = executeCriteria(criteria, 0, 100, null, true);
				
		return lista;
	}
	
	private DetachedCriteria montarCriteriaScoMarcasNomeAtiva(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class);
		String strPesquisa = (String) objPesquisa;
		
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(ScoMarcaComercial.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public List<ScoMarcaComercial> listarScoMarcasAtiva(Object objPesquisa,
			Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc) {
		List<ScoMarcaComercial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMarcasNomeAtiva(objPesquisa);
		
		//#21881 
		if(orderProperty == null) {
			criteria.addOrder(Order.asc(ScoMarcaComercial.Fields.CODIGO.toString()));
		}
		
		lista = executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		
		return lista;
	}
	
	//Criteria para paginação. Segundo parametros.
	public DetachedCriteria montaCriteriaScoMarcaComercial(ScoMarcaComercial marcaComercial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class);
		
		if(marcaComercial.getCodigo()!=null){
			criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.CODIGO.toString(), marcaComercial.getCodigo()));
		}
		if(StringUtils.isNotBlank(marcaComercial.getDescricao())){
			criteria.add(Restrictions.ilike(ScoMarcaComercial.Fields.DESCRICAO.toString(), marcaComercial.getDescricao(), MatchMode.ANYWHERE));
		}
		if(marcaComercial.getIndSituacao()!=null){
			criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.SITUACAO.toString(), marcaComercial.getIndSituacao()));
		}
		
		return criteria;
	}
	
	
	//Lista recuperada para paginaçao. 
	public List<ScoMarcaComercial> pesquisarMarcaComercial(Integer firstResult, Integer maxResult, String orderProperty, 
																	boolean asc, ScoMarcaComercial marcaComercial){
		
		DetachedCriteria criteria = montaCriteriaScoMarcaComercial(marcaComercial);
		
		return this.executeCriteria(criteria, firstResult, maxResult,orderProperty, asc);
	}
	
	//count da lista de paginaçao.
	public Long pesquisarMarcaComercialCount(ScoMarcaComercial marcaComercial){

		DetachedCriteria criteria = montaCriteriaScoMarcaComercial(marcaComercial);
		
		return this.executeCriteriaCount(criteria);
	}


	/**
	 * Busca ScoMarcaComercial atraves do ID.
	 * @param codigo
	 * @return
	 */
	public ScoMarcaComercial buscarScoMarcaComercialPorId(Integer codigo) {
		if(codigo == null){
			return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class);
		criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.CODIGO.toString(), codigo));
		return (ScoMarcaComercial) executeCriteriaUniqueResult(criteria);
	}

	public Long listarScoMarcasAtivaCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaScoMarcasNomeAtiva(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	/*public ScoMarcaComercial obterOriginal(ScoMarcaComercial elementoModificado) {


	    Integer id = elementoModificado.getCodigo();
	   
	    StringBuilder hql = new StringBuilder();
	   
	    hql.append("select o.").append(ScoMarcaComercial.Fields.CODIGO.toString());
	    hql.append(", o.").append(ScoMarcaComercial.Fields.DESCRICAO.toString());
	    hql.append(", o.").append(ScoMarcaComercial.Fields.SITUACAO.toString());
	    
	    hql.append(" from ").append(ScoMarcaComercial.class.getSimpleName()).append(" o ");
	   
	    hql.append(" where o.").append(ScoMarcaComercial.Fields.CODIGO.toString()).append(" = :entityId ");
	   
	    Query query = this.createQuery(hql.toString());
	    query.setParameter("entityId", id);
	   
	    ScoMarcaComercial original = null;
	    @SuppressWarnings("unchecked")
	    List<Object[]> camposLst = (List<Object[]>) query.getResultList();
	       
	    if(camposLst != null && camposLst.size()>0) {
	           
	        Object[] campos = camposLst.get(0);
	       
	        original = new ScoMarcaComercial();
	       
	        original.setCodigo(id);
	        original.setDescricao((String)campos[1]);
	        original.setIndSituacao((DominioSituacao)campos[2]);
	   
	    }
	
	        return original;
	       
		
	}*/
	
	public ScoMarcaComercial obterMarcaComercialPorCodigo(Integer mcmCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class);
		criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.CODIGO.toString(), mcmCodigo));
		
		return (ScoMarcaComercial)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método de pesquisa para a Suggestion Box. 
	 * @param param
	 * @return lista com registros encontrados
	 */
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricao(Object param) {
		return pesquisarMarcaComercialPorCodigoDescricaoAtivo(param, null);		
	}
	
	
	/**
	 * Método de pesquisa para a Suggestion Box sem lucene. 
	 * @param param
	 * @return lista com registros encontrados
	 */
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoSemLucene(Object param) {
		return pesquisarMarcaComercialPorCodigoDescricaoAtivo(param, null,false);		
	}
	
	
	/**
	 * Método de count para a Suggestion Box. 
	 * @param param
	 * @return qtde de registros encontrados
	 */
	public Long pesquisarMarcaComercialPorCodigoDescricaoCount(Object param) {
		return pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(param, null);		
	}

	/**
	 * sco_mcm_uk1
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public Boolean verificarMarcaExistente(Integer codigo, String descricao) {
		
		List <ScoMarcaComercial> result = new ArrayList<ScoMarcaComercial>();
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class);
		
		if (codigo != null) {
			
			criteria.add(Restrictions.ne(ScoMarcaComercial.Fields.CODIGO.toString(), codigo));
			
		}
		
		criteria.add(Restrictions.eq(ScoMarcaComercial.Fields.DESCRICAO.toString(), descricao));
		result = executeCriteria(criteria);
		
		if (result.size() > 0) {
			
			return true;
			
		} else {
			
			return false;
			
		}	
		
	}
	
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoAtivo(Object param, DominioSituacao ativo, Boolean flagLucene) {
		String strPesquisa = (String) param;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class, "MCM");
		
		if (CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq("MCM."+ScoMarcaComercial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			 if (flagLucene){
				 return lucene.executeLuceneQueryParaSuggestionBox(ScoMarcaComercial.Fields.DESCRICAO.toString(), ScoMarcaComercial.Fields.DESCRICAO_FONETICO.toString(), strPesquisa, ScoMarcaComercial.class, ScoMarcaComercial.Fields.DESCRICAO.toString());	 
			 }
			 else {
				criteria.add(
						Restrictions.ilike("MCM."+ScoMarcaComercial.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE)
						);
			 }
			
		}
		
		if (ativo != null){			
		    criteria.add(Restrictions.eq("MCM." + ScoMarcaComercial.Fields.SITUACAO, ativo));			
		}
		criteria.addOrder(Order.asc("MCM."+ScoMarcaComercial.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria,0,100,null,false);
	}
	
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoAtivo(Object param, DominioSituacao ativo) {
		return this.pesquisarMarcaComercialPorCodigoDescricaoAtivo(param, ativo, true);
	}
	
	
	public Long pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(Object param, DominioSituacao ativo) {
		String strPesquisa = (String) param;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMarcaComercial.class, "MCM");
		
		if (CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq("MCM."+ScoMarcaComercial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
				criteria.add(
						Restrictions.ilike("MCM."+ScoMarcaComercial.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE)
						);
		}
		
		if (ativo != null){			
		    criteria.add(Restrictions.eq("MCM." + ScoMarcaComercial.Fields.SITUACAO, ativo));			
		}
				
		return this.executeCriteriaCount(criteria);
	}
	
	
	public List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MCM");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC1");
		
		Type[] typeString = new Type[] { StringType.INSTANCE };
		Type[] typeInteger = new Type[] { IntegerType.INSTANCE };
		String[] columnAliasNomeMat = new String[]{ItensRecebimentoAdiantamentoVO.Fields.NOME_MATERIAL.toString()};
		String[] columnAliasDescrMat = new String[]{ItensRecebimentoAdiantamentoVO.Fields.DESCRICAO_MATERIAL.toString()};
		String[] columnAliasDescrSc = new String[]{ItensRecebimentoAdiantamentoVO.Fields.DESCRICAO_SOLICITACAO.toString()};
		String[] columnAliasSaldoAF = new String[]{ItensRecebimentoAdiantamentoVO.Fields.SALDO_AF.toString()};
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.ITL_NUMERO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.COD_MATERIAL.toString())
				.add(Projections.sqlProjection(" SUBSTR(MAT4_.NOME,1,27) as nomeMaterial", columnAliasNomeMat, typeString))
				.add(Projections.sqlProjection(" SUBSTR(MAT4_.DESCRICAO,1,100) as descricaoMaterial", columnAliasDescrMat, typeString))
				.add(Projections.sqlProjection(" SUBSTR(SLC3_.DESCRICAO,1,100)  as descricaoSolicitacao", columnAliasDescrSc, typeString))
				.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.UNIDADE.toString())
				.add(Projections.sqlProjection(" IAF1_.QTDE_SOLICITADA - IAF1_.QTDE_RECEBIDA as saldoAF", columnAliasSaldoAF, typeInteger))
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.PERC_VAR_PRECO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.PERC_VAR_PRECO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.AFN_NUMERO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString())
						, ItensRecebimentoAdiantamentoVO.Fields.NUMERO.toString()));
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		criteria.add(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.sqlRestriction(" IAF1_.QTDE_SOLICITADA - IAF1_.QTDE_RECEBIDA > 0 "));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensRecebimentoAdiantamentoVO.class));
		
		return executeCriteria(criteria);
	}
	
}
