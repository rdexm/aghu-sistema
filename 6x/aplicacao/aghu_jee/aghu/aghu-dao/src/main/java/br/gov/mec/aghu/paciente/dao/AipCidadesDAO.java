package br.gov.mec.aghu.paciente.dao;

import java.text.Normalizer;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.search.Lucene;


public class AipCidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipCidades> {
    @Inject
    private Lucene lucene;

	private static final long serialVersionUID = 978121169067812450L;
	
	private static final Log LOG = LogFactory.getLog(AipCidadesDAO.class);

//	private static final String Lucene.WILDCARD = "*";
	
	/**
	 * Método para obter uma cidade através do seu código 
	 * @param Código da cidade a ser removida
	 * @param booleano indicando se os respectivos distritos sanitarios da cidade devem ser pesquisados também.
	 */
	public AipCidades obterCidadePorCodigo(Integer codigo, boolean obterDistritosSanitarios) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		cri.add(Restrictions.idEq(codigo));
		if(obterDistritosSanitarios){
			cri.setFetchMode(AipCidades.Fields.DISTRITOS_SANITARIOS.toString(), FetchMode.JOIN);
		}
		return (AipCidades) executeCriteriaUniqueResult(cri);
	}

	/**
	 * Método para criar criteria com restrições dos campos não nulos da tela de pesquisa de cidades
	 * 
	 * @return DetachedCriteria com restrições dos campos não nulos da tela de pesquisa de cidades
	 */
	private DetachedCriteria criarCriteriaAipCidades(Integer codigo, Integer  codIbge, String nome,  
								 DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf) {
		
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		
		if(codigo != null) {
			cri.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), codigo));
		}
		
		if(codIbge != null) {
			cri.add(Restrictions.eq(AipCidades.Fields.COD_IBGE.toString(), codIbge));
		}
		
		if (nome != null && !"".equalsIgnoreCase(nome.trim())) {
			cri.add(Restrictions.ilike(AipCidades.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if (situacao != null) {
			cri.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if (cep != null && cep > 0) {
			cri.add(Restrictions.eq(AipCidades.Fields.CEP.toString(), cep));
		}
		
		if (cepFinal != null && cepFinal > 0) {
			cri.add(Restrictions.eq(AipCidades.Fields.CEP_FINAL.toString(), cepFinal));
		}
		
		if (uf != null) {
			cri.add(Restrictions.eq(AipCidades.Fields.UF_SIGLA.toString(), uf.getSigla()));
		}
		
		return cri;
	}
	
	
	/**
	 * Método para obter o numero de cidades que contemplam as restrições informadas pelo usuário na tela de pesquisa de cidades.
	 * @return Número de cidades
	 */
	public Long obterCidadeCount(Integer codigo, Integer codIbge, String nome,  
            DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf) {
		return executeCriteriaCount(criarCriteriaAipCidades(codigo, codIbge, nome, situacao, cep, cepFinal, uf));
	}
	
	public Long pesquisarCountCidadePorCodigoNome(String strPesquisa){
		if (CoreUtil.isNumeroInteger(strPesquisa)){
			DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
			Integer integerPesquisa = Integer.valueOf(strPesquisa);
			cri.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), integerPesquisa));	
			return this.executeCriteriaCount(cri);
		}
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			FullTextQuery query = pesquisarCidadesAtivasLucene(strPesquisa);
			return Long.valueOf(query.getResultSize());
		} else {
			return obterCidadeCount(null, null, null, DominioSituacao.A, null, null, null);
		}
	}
	
	/**
	 * Método para pesquisar cidades que contemplam restrições informadas pelo usuário na tela de pesquisa de cidades.
	 *  
	 * @return Lista de cidades
	 */
	public List<AipCidades> pesquisarCidades(Integer codigo, Integer codIbge,
			String nome, DominioSituacao situacao, Integer cep,
			Integer cepFinal, AipUfs uf) {
		return pesquisarCidades(0, Integer.MAX_VALUE, codigo, codIbge, nome,
				situacao, cep, cepFinal, uf);
	}
	
	/**
	 * Método para pesquisar cidades que contemplam restrições informadas pelo usuário na tela de pesquisa de cidades.
	 *  
	 * @return Lista de cidades
	 */
	public List<AipCidades> pesquisarCidades(Integer firstResult,
			Integer maxResult, Integer codigo, Integer codIbge, String nome,
			DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf) {
		DetachedCriteria criteria = criarCriteriaAipCidades(codigo, codIbge,
				nome, situacao, cep, cepFinal, uf);
		criteria.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
		return executeCriteria(criteria, firstResult, maxResult, null, false);
	}
	
	/**
	 * Método para listar as Cidades em um combo de acordo com o parâmetro informado.
	 * O parâmetro pode ser tanto parte do nome da cidade quanto o código da mesma.
	 * 
	 * @param paramPesquisa
	 * @param ativas, booleando para determinar se devem ser pesquisadas apenas as cidades ativas
	 * @return li
	 */
	public List<AipCidades> pesquisarPorCodigoNome(Object paramPesquisa, boolean ativas) {
		
		List<AipCidades> retorno = null;
		
		String strPesquisa = (String)paramPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
			
			Integer integerPesquisa = Integer.valueOf(strPesquisa);
			cri.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), integerPesquisa));					
						
			if(ativas){
				cri.add(Restrictions.ne(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.I));
			}
			
			cri.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
			
			List<AipCidades> li;
			if (StringUtils.isEmpty(strPesquisa)){
				li = executeCriteria(cri, 0, 100, null);
			} else {
				li = executeCriteria(cri);
			}
			
			retorno = li;
		} else {
			if (StringUtils.isNotBlank(strPesquisa)) {
				FullTextQuery query = pesquisarCidadesAtivasLucene(strPesquisa);

				Sort sort = new Sort(new SortField(AipCidades.Fields.NOME.toString(), SortField.Type.STRING, false));

				query = query.setSort(sort);

				retorno = query.setMaxResults(100).getResultList();
			} else {
				DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
				criteria.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
				criteria.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
				retorno  = executeCriteria(criteria, 0, 100, null, false);
			}
		}
		
		if(retorno!= null){
			for (AipCidades aipCidades : retorno) {
				Hibernate.initialize(aipCidades.getAipCidadePertence());
			}
		}
		
		return retorno;
	}
	
	public List<AipCidades> pesquisarPorCodigoNomeAlfabetica(Object paramPesquisa, boolean ativas) {

		String strPesquisa = (String)paramPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			
			DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
			Integer integerPesquisa = Integer.valueOf(strPesquisa);
			cri.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), integerPesquisa));					

			if(ativas){
				cri.add(Restrictions.ne(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.I));
			}
			cri.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
			List<AipCidades> li = null;
			if (StringUtils.isEmpty(strPesquisa)){
//				cri.getExecutableCriteria(getSession()).setFirstResult(0);					
//				cri.getExecutableCriteria(getSession()).setMaxResults(100);
				li = executeCriteria(cri, 0, 100, null, false);
			} else {
				li = executeCriteria(cri);
			}
			return li;
		}else{
			if (StringUtils.isNotBlank(strPesquisa)) {

				DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
				criteria.add(Restrictions.or(Restrictions.ilike(AipCidades.Fields.NOME.toString(), strPesquisa , MatchMode.EXACT),Restrictions.ilike(AipCidades.Fields.NOME.toString(), strPesquisa , MatchMode.START)));
				criteria.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
				return executeCriteria(criteria);
			} else {

				DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
				criteria.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
				criteria.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
				return executeCriteria(criteria, 0, 100, null, false);
			}
		}
	}
	
	private FullTextQuery pesquisarCidadesAtivasLucene(String strPesquisa){
		BooleanQuery totalQuery = new BooleanQuery();
		String campoAnalisado = AipCidades.Fields.NOME.toString();
		
		String campoFonetico = AipCidades.Fields.NOME_FONETICO.toString();
		
		Query luceneQueryBrazilian = null;
		Query luceneQueryKeyword = null;
		try {
			if (Lucene.WILDCARD.equals(strPesquisa)) {
				String buscaBrazilianAnalyzer = campoAnalisado+":(*)";
				luceneQueryBrazilian = lucene.createQuery(buscaBrazilianAnalyzer, new BrazilianAnalyzer());
				
				totalQuery.add(luceneQueryBrazilian, Occur.SHOULD);
			} else {
				String buscaBrazilianAnalyzer = campoAnalisado+":("+strPesquisa.trim()+"*) OR "+campoAnalisado+":("+strPesquisa.trim()+")";
				luceneQueryBrazilian = lucene.createQuery(buscaBrazilianAnalyzer, new BrazilianAnalyzer());
				String buscaKeywordAnalyzer = campoFonetico+":("+Fonetizador.fonetizar(strPesquisa.trim()).toLowerCase()+")";
				luceneQueryKeyword = lucene.createQuery(buscaKeywordAnalyzer, new KeywordAnalyzer());
				
				TermQuery situacaoQuery = new TermQuery(new Term(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.I.toString().toLowerCase() ));
				
				
				totalQuery.add(luceneQueryBrazilian, Occur.SHOULD);
				totalQuery.add(luceneQueryKeyword, Occur.SHOULD);
				totalQuery.add(situacaoQuery, Occur.MUST_NOT);
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage(),e);
		}
		
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		
		FullTextQuery query = createFullTextQuery(totalQuery, AipCidades.class);
		return query;
	}
	
	public List<AipCidades> pesquisarCidades(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipCidades.class);

			_criteria.add(Restrictions.eq(
					AipCidades.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			_criteria.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
			List<AipCidades> list = executeCriteria(_criteria, 0, 100, null, false);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipCidades.class);
				
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipCidades.Fields.NOME.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(AipCidades.Fields.NOME
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	/**
	 * Pesquisa cidades de um determinado Estado (UF)
	 * @param uf
	 * @return
	 */
	public List<AipCidades> pesquisarCidadesPorUf(AipUfs uf){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
		criteria.add(Restrictions.eq(AipCidades.Fields.UF_SIGLA.toString(), uf.getSigla()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarCidadePorCodigoNomeCount(String strPesquisa) {
		return executeCriteriaCount(obterCriteriaPesquisarCidadePorCodigoNome(strPesquisa));
	}
	
	public List<AipCidades> pesquisarCidadePorCodigoNome(String strPesquisa) {
		final DetachedCriteria cri = obterCriteriaPesquisarCidadePorCodigoNome(strPesquisa);
		cri.createAlias(AipCidades.Fields.UF.toString(), "UF", JoinType.INNER_JOIN);
		
		return executeCriteria(cri, 0, 100, AipCidades.Fields.CODIGO.toString(), true);
	}

	private DetachedCriteria obterCriteriaPesquisarCidadePorCodigoNome(
			String strPesquisa) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);

		if(StringUtils.isNotBlank(strPesquisa)) {
			if(CoreUtil.isNumeroInteger(strPesquisa)) {
				cri.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(),Integer.parseInt(strPesquisa)));
				
			} else {
				cri.add(Restrictions.ilike(AipCidades.Fields.NOME.toString(), strPesquisa.toUpperCase(), MatchMode.ANYWHERE));
			} 
		}

		cri.add(Restrictions.ne(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.I));
		return cri;
	}
	
	public AipCidades obterCidadePorCodigo(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		cri.add(Restrictions.idEq( codigo));
		cri.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (AipCidades) executeCriteriaUniqueResult(cri);
	}


	
	/**
	 * Método para obter uma cidade através do seu código 
	 */
	public AipCidades obterCidadePorCodIbge(Integer codIbge) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		cri.add(Restrictions.eq("codIbge", codIbge));
		
		return (AipCidades) executeCriteriaUniqueResult(cri);
	}
	
	/**
	 * Método para obter uma cidade através do seu CEP (só deveria retornar uma)
	 *  
	 */
	public List<AipCidades> obterCidadePorCEP(Integer cep) throws ApplicationBusinessException  {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		cri.add(Restrictions.eq("cep", cep));
	
		List<AipCidades> listaCidades = executeCriteria(cri);
		
		return listaCidades;
	}
	
	public List<AipCidades> pesquisarCidadesParaLogradouro(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipCidades.class);

			_criteria.setFetchMode(AipCidades.Fields.UF.toString(), FetchMode.JOIN);
			
			_criteria.add(Restrictions.eq(
					AipCidades.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			_criteria.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
			List<AipCidades> list = executeCriteria(_criteria, 0, 100, null, false);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipCidades.class);
		
		criteria.setFetchMode(AipCidades.Fields.UF.toString(), FetchMode.JOIN);
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipCidades.Fields.NOME.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(AipCidades.Fields.IND_LOGRADOURO.toString(), Boolean.TRUE));
		
		criteria.addOrder(Order.asc(AipCidades.Fields.NOME
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	/**
	 * 
	 * Busca as Cidades pelo Nome
	 * 
	 * @return Lista de cidades
	 */	
	public List<AipCidades> pesquisarCidadePorNome(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);

		cri.add(Restrictions.like(AipCidades.Fields.NOME.toString(),
				((String) strPesquisa).toUpperCase(), MatchMode.ANYWHERE));

		// Pesquisar por cidades que nao estejam em situacao inativa.
		cri.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
	

		cri.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));

		List<AipCidades> li = executeCriteria(cri,0,99,null,false);

		return li;
	}

	public List<AipCidades> pesquisarCidadesPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);

		String param = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		criteria.add(Restrictions.sqlRestriction("TRANSLATE({alias}.nome,'ÀÁáàÉÈéèÍíÓóÒòÚú','AAaaEEeeIiOoOoUu') LIKE '" + param + "'"));

		return this.executeCriteria(criteria);
	}

	public List<AipCidades> pesquisarCidadesPorNomeSiglaUf(String nome, String siglaUf) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipCidades.class);
		cri.add(Restrictions.eq(AipCidades.Fields.NOME.toString(), nome.toUpperCase()));
		cri.add(Restrictions.eq(AipCidades.Fields.UF_SIGLA.toString(), siglaUf));
		return this.executeCriteria(cri);
	}
	
	/**
	 * #44799 C1
	 * @param filtro
	 * @return
	 */
	public List<AipCidades> obterAipCidadesPorNomeAtivo(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipCidadesPorNomeAtivo(filtro);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AipCidades.Fields.CODIGO.toString())
						.as(AipCidades.Fields.CODIGO.toString()))
				.add(Projections.property(AipCidades.Fields.NOME.toString())
						.as(AipCidades.Fields.NOME.toString())));

		criteria.setResultTransformer(Transformers.aliasToBean(AipCidades.class));
		criteria.addOrder(Order.asc(AipCidades.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long obterAipCidadesPorNomeAtivoCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipCidadesPorNomeAtivo(filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaAipCidadesPorNomeAtivo(Object filtro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
		criteria.add(Restrictions.eq(AipCidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		String filtroPesquisa = filtro.toString();
		
		if(StringUtils.isNotBlank(filtroPesquisa)){
			criteria.add(Restrictions.ilike(AipCidades.Fields.NOME.toString(), filtroPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * #52025  CONSULTA UTILIZADA EM FUNCTION P9 AIPC_PROCEDENCIA_PAC
	 * @param codigo
	 * @return
	 */
	public String obterNomeAipCidadesPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipCidades.class);
		criteria.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.property(AipCidades.Fields.NOME.toString()));
		return (String) executeCriteriaUniqueResult(criteria);

}
}
