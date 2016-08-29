package br.gov.mec.aghu.compras.dao;


import java.util.List;

import br.gov.mec.aghu.core.commons.CoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;

public class ScoModalidadeLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoModalidadeLicitacao> {
	
	private static final long serialVersionUID = 3398788015512682617L;

	public List<ScoModalidadeLicitacao> listarModalidadeLicitacaoAtivas(Object pesquisa) {
		DetachedCriteria criteria =  createListarModalidadeLicitacaoAtivas(pesquisa);
		criteria.addOrder(Order.asc(ScoModalidadeLicitacao.Fields.CODIGO.toString()));
		return this.executeCriteria(criteria);
	}
	
	public Long listarModalidadeLicitacaoAtivasCount(Object pesquisa) {
		DetachedCriteria criteria =  createListarModalidadeLicitacaoAtivas(pesquisa);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria  createListarModalidadeLicitacaoAtivas(Object pesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		String strParametro = (String) pesquisa;
		
		if(StringUtils.isNotBlank(strParametro)){
			criteria.add(Restrictions.or(
					     Restrictions.ilike(ScoModalidadeLicitacao.Fields.CODIGO.toString(), strParametro, MatchMode.ANYWHERE),
					     Restrictions.ilike(ScoModalidadeLicitacao.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE)
					     ));
		}
		
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacao(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		String strParametro = (String) pesquisa;
		
		if(StringUtils.isNotBlank(strParametro)){
			criteria.add(Restrictions.or(
					     Restrictions.ilike(ScoModalidadeLicitacao.Fields.CODIGO.toString(), strParametro, MatchMode.ANYWHERE),
					     Restrictions.ilike(ScoModalidadeLicitacao.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE)
					     ));
		}
		criteria.addOrder(Order.asc(ScoModalidadeLicitacao.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Obtém todas as modalidades licitação aprovadas
	 * @return
	 * @author bruno.mourao
	 * @since 30/05/2011
	 */
	public List<ScoModalidadeLicitacao> obterModalidadesLicitacaoAprovadasPorCodigo(String codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(!StringUtils.isEmpty(codigo)){
			criteria.add(Restrictions.like(ScoModalidadeLicitacao.Fields.CODIGO.toString(), codigo, MatchMode.START).ignoreCase());
		}
		
		return this.executeCriteria(criteria);
	}
		
	/**
	 * Obtém a quantidade de modalidades licitação do tipo artigo
	 * @return
	 */
	public Long obterQuantidadeArtigosPorCodigo(String codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.IND_ARTIGO.toString(), true));
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.CODIGO.toString(), codigo));
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa utilizada por suggestion box
	 * @param parametro
	 * @return
	 */
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(final Object parametro, final DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		String strParametro = (String) parametro;
		
		if(strParametro != null) {
			criteria.add(Restrictions.or(Restrictions.ilike(ScoModalidadeLicitacao.Fields.CODIGO.toString(), strParametro, MatchMode.ANYWHERE),
				     	 Restrictions.ilike(ScoModalidadeLicitacao.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE)));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.SITUACAO.toString(), situacao));
		}
		return this.executeCriteria(criteria, 0, 100, ScoModalidadeLicitacao.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * Método de pesquisa para a página de lista. 
	 * @param codigo, descricao, valorLimite, codigoSicon, situacao, geraLicitacao, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 01/03/2013
	 */
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoModalidadeLicitacao scoModalidadePac) {

		DetachedCriteria criteria = montarClausulaWhere(scoModalidadePac);

		criteria.addOrder(Order.asc(ScoModalidadeLicitacao.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método count de pesquisa para a página de lista.
	 * @param codigo, descricao, valorLimite, codigoSicon, situacao, geraLicitacao
	 * @author dilceia.alves
	 * @since 01/03/2013
	 */
	public Long listarModalidadeLicitacaoCount(ScoModalidadeLicitacao scoModalidadePac) {

		DetachedCriteria criteria = montarClausulaWhere(scoModalidadePac);

		return this.executeCriteriaCount(criteria);
	}

	public Long listarModalidadeCount(Object filter) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);

		String descricao = (String) filter;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(descricao)){
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.CODIGO.toString(),
					codigo));
		}

		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoModalidadeLicitacao.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarClausulaWhere(ScoModalidadeLicitacao scoModalidadePac) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
		if (scoModalidadePac != null) {
			if (StringUtils.isNotBlank(scoModalidadePac.getCodigo())) {
				criteria.add(Restrictions.eq(
						ScoModalidadeLicitacao.Fields.CODIGO.toString(),
						scoModalidadePac.getCodigo()));
			}
		}

		if (StringUtils.isNotBlank(scoModalidadePac.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoModalidadeLicitacao.Fields.DESCRICAO.toString(),
					scoModalidadePac.getDescricao(), MatchMode.ANYWHERE));
		}

		if (scoModalidadePac.getValorPermitido() != null) {
			criteria.add(Restrictions.eq(
					ScoModalidadeLicitacao.Fields.VALOR_PERMITIDO.toString(),
					scoModalidadePac.getValorPermitido()));
		}
		
		if (scoModalidadePac.getCodigoSicon() != null) {
			criteria.add(Restrictions.eq(
					ScoModalidadeLicitacao.Fields.CODIGO_SICON.toString(),
					scoModalidadePac.getCodigoSicon()));
		}

		if (scoModalidadePac.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoModalidadeLicitacao.Fields.SITUACAO.toString(),
					scoModalidadePac.getSituacao()));
		}

		if (scoModalidadePac.getLicitacao() != null) {
			criteria.add(Restrictions.eq(
					ScoModalidadeLicitacao.Fields.IND_LICITACAO.toString(), 
					scoModalidadePac.getLicitacao()));
		}
		
		return criteria;
	}

	public List<ScoModalidadeLicitacao> pesquisarScoModalidadeLicitacaoPorDocLicitAno(final Boolean docLicitAno) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.IND_DOC_LICIT_ANO.toString(), docLicitAno));
		return executeCriteria(criteria);
	}

	public List<ScoModalidadeLicitacao> pesquisarScoModalidadeLicitacaoPorEditalAno(final Boolean editalAno) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.IND_EDITAL_ANO.toString(), editalAno));
		return executeCriteria(criteria);
	}

	public Integer obterMaxEditalScoModalidadeLicitacao() {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.IND_EDITAL.toString(), Boolean.TRUE));
		criteria.setProjection(Projections.max(ScoModalidadeLicitacao.Fields.NUM_EDITAL.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	/*
	 * 
	 *
		update  sco_modalidade_licitacoes  mlc
	       set  mlc.num_doc_licit  =  1
       		where  mlc.ind_doc_licit_ano  =  'S'
	 */
	@Deprecated
	public void updateNumDocModLicitacao(Integer numDocLicit) {

		javax.persistence.Query q = this.createQuery(
				"UPDATE " + ScoModalidadeLicitacao.class.getName() + " " + "SET " + ScoModalidadeLicitacao.Fields.NUM_DOC_LICIT.toString() + " = "
						+ numDocLicit + " WHERE  " + ScoModalidadeLicitacao.Fields.IND_DOC_LICIT_ANO.toString() + "= 'S'");
		q.executeUpdate();
		this.flush();
	}
	
	/*
	 * 
	 *
		update  sco_modalidade_licitacoes  mlc
	       set   mlc.num_edital    =  1
	    	where mlc.num_edital_ano  =  'S'
	 */
	@Deprecated
	public void updateEditalModLicitacao(Integer editalLicit) {

		javax.persistence.Query q = this.createQuery(
				"UPDATE " + ScoModalidadeLicitacao.class.getName() + " " + "SET " + ScoModalidadeLicitacao.Fields.NUM_EDITAL.toString() + " = " + editalLicit
						+ " WHERE  " + ScoModalidadeLicitacao.Fields.IND_EDITAL_ANO.toString() + "= 'S'");
		q.executeUpdate();
		this.flush();
	}
	
	/*
	 * 
	
	 Update sco_modalidade_licitacoes
	  Set num_edital = 1+ ( select max(num_edital)  
	                                         from sco_modalidade_licitacoes
	*                                        where IND_EDITAL = 'S')
	*/
	@Deprecated
	public void updateMaxEditalLicitacoes() {
		javax.persistence.Query q = this.createQuery(
				"UPDATE " + ScoModalidadeLicitacao.class.getName() + " SET " + ScoModalidadeLicitacao.Fields.NUM_EDITAL.toString() + " = ("
						+ " select max(num_edital)+1 from " + ScoModalidadeLicitacao.class.getName() + "  where IND_EDITAL = 'S') " + " WHERE  "
						+ ScoModalidadeLicitacao.Fields.IND_EDITAL.toString() + "= 'S'");
		q.executeUpdate();
		this.flush();

	}

	// C3 - #5460
		@SuppressWarnings("unchecked")
		public List<Object[]> obterEstatisticaPACsPendentesPorModalidade(){
			
			StringBuilder hql = new StringBuilder(400);
			hql.append("SELECT ")
			.append("MLC." ).append( ScoModalidadeLicitacao.Fields.DESCRICAO.toString() ).append( " as MODALIDADE ")
			
			.append(", ( SELECT COUNT(*) ")
			.append("FROM ")
			.append(ScoLicitacao.class.getSimpleName() ).append( " LCT ")
			.append("WHERE ")
			.append("LCT." ).append( ScoLicitacao.Fields.MLC_CODIGO.toString() ).append( " = MLC." ).append( ScoModalidadeLicitacao.Fields.CODIGO.toString())
			.append(" AND LCT." ).append( ScoLicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = 'N'")
			.append(" AND LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( 
												" NOT IN ( SELECT " ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( 
														" FROM " ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " FRN " ).append(
														" WHERE FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( " )) as SEM_AF")
			
			.append(", ( SELECT COUNT(*) ")
			.append("FROM ")
			.append(ScoLicitacao.class.getSimpleName() ).append( " LCT ")
			.append("WHERE ")
			.append("LCT." ).append( ScoLicitacao.Fields.MLC_CODIGO.toString() ).append( " = MLC." ).append( ScoModalidadeLicitacao.Fields.CODIGO.toString())
			.append(" AND LCT." ).append( ScoLicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = 'N'")
			.append(" AND LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( 
												" IN ( SELECT " ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( 
													" FROM " ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " FRN " ).append(
													" WHERE FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( 
													" AND FRN." ).append( ScoAutorizacaoForn.Fields.SITUACAO.toString() ).append( " IN ('" ).append( DominioSituacaoAutorizacaoFornecimento.AE.toString() ).append( "', '" ).append( DominioSituacaoAutorizacaoFornecimento.PA.toString() ).append( "'))) as COM_AF")
			
			.append(" FROM ")
			.append(ScoModalidadeLicitacao.class.getSimpleName() ).append( " MLC ")
			
			.append("ORDER BY ")
			.append("MLC." ).append( ScoModalidadeLicitacao.Fields.DESCRICAO.toString());
			
			org.hibernate.Query query = createHibernateQuery(hql.toString());
			return query.list();
		}
		
		// C1 - #5460
		public Object obterQtdPACsPendentesSemAF(){
			
			StringBuilder hql = new StringBuilder(150);
			hql.append("SELECT COUNT(*) ")
			
			.append("FROM ")
			.append(ScoLicitacao.class.getSimpleName() ).append( " LCT ")
			
			.append("WHERE LCT." ).append( ScoLicitacao.Fields.EXCLUSAO.toString() ).append( " = 'N' ")
			.append("AND LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( 
										" NOT IN (SELECT FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append(
										" FROM " ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " FRN" ).append(
										" WHERE FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( ')');
			
			org.hibernate.Query query = createHibernateQuery(hql.toString());
			return query.uniqueResult();
		}
		
		// C2 - #5460
		public Object obterQtdPACsPendentesComAF(){
			
			StringBuilder hql = new StringBuilder(150);
			hql.append("SELECT COUNT(*) ")
			.append("FROM ")
			.append(ScoLicitacao.class.getSimpleName() ).append( " LCT ")
			.append("WHERE LCT." ).append( ScoLicitacao.Fields.EXCLUSAO.toString() ).append( " = 'N'")
			.append("AND LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append(
										" IN (SELECT FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append(
										" FROM " ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " FRN" ).append(
										" WHERE FRN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( 
										" AND FRN." ).append( ScoAutorizacaoForn.Fields.SITUACAO.toString() ).append( " IN ( '" ).append( DominioSituacaoAutorizacaoFornecimento.AE.toString() ).append( 
																											"', '" ).append( DominioSituacaoAutorizacaoFornecimento.PA.toString() ).append( "') )");
			
			org.hibernate.Query query = createHibernateQuery(hql.toString());
			return query.uniqueResult();
		}
		//#5550 C5
		public List<ScoModalidadeLicitacao> pesquisarModalidadesProcessoAdministrativo(String parametro){
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoModalidadeLicitacao.class);
		
			if(parametro != null && StringUtils.isNotBlank(parametro)){
				criteria.add(Restrictions.ilike(ScoModalidadeLicitacao.Fields.CODIGO.toString(), parametro, MatchMode.ANYWHERE));
			}
			return executeCriteria(criteria, 0, 100, null, false);
		}

		public Long pesquisarModalidadesProcessoAdministrativoCount(String parametro) {
			return Long.valueOf((pesquisarModalidadesProcessoAdministrativo(parametro)).size());
		}
	
}