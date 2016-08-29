package br.gov.mec.aghu.core.search;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.Version;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextQuery;

import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;


/**
 * This is a high-performance, full-featured text search engine library written entirely in Java. 
 * It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.
 * 
 *
 */
public class Lucene {
	
	private static final Log LOG = LogFactory.getLog(Lucene.class);
	
	/**
	 * versao do lucene utilizada.
	 */
	public static final Version SEARCH_ENGINE_VERSION = Version.LUCENE_4_10_4;
	
	/**
	 * Campo utilizado pelo lucene para representar quaisquer caracter.
	 */
	public static final String WILDCARD = "*";
	
	
	@Inject
	private DataAccessService dataAcess;
	
	@Deprecated
	public void indexar(Class clazz) {
		/*
		// set the transaction timeout
		try {
			int transactionTimeoutSeconds = 1000000;
			LOG.info(
					"setting transaction timeout to "
							+ transactionTimeoutSeconds + " seconds");
			((javax.transaction.UserTransaction) org.jboss.seam.transaction.Transaction
					.instance())
					.setTransactionTimeout(transactionTimeoutSeconds);
		} catch (SystemException e1) {
			// failed to set the transaction timeout => just log some content
			LOG.error(e1.getMessage(), e1);
		}

		LOG.info("obtendo sessão hibernate");
		Session session = getSession();
		
		session.getSessionFactory().openSession();

		LOG.info("iniciando transação na sessão");
		session.beginTransaction();

		FullTextSession fullTextSession = Search.getFullTextSession(session);

		LOG.info("iniciando transação na sessão de texto");
		Transaction transaction = fullTextSession.beginTransaction();

		UserTransaction userTx = (UserTransaction) org.jboss.seam.Component
				.getInstance("org.jboss.seam.transaction.transaction");

		try {
			userTx.commit();
			if (!userTx.isActive()) {
				userTx.begin();
			}
			userTx.setTransactionTimeout(60 * 60 * 12); // 12 horas
			getEntityManager().joinTransaction();

			LOG.info("configurada transação seam e entity manager");
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (NotSupportedException e) {
			LOG.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			LOG.error(e.getMessage(), e);
		} catch (RollbackException e) {
			LOG.error(e.getMessage(), e);
		} catch (HeuristicMixedException e) {
			LOG.error(e.getMessage(), e);
		} catch (HeuristicRollbackException e) {
			LOG.error(e.getMessage(), e);
		}
		fullTextSession.setFlushMode(FlushMode.MANUAL);
		fullTextSession.setCacheMode(CacheMode.IGNORE);
		try {
			transaction = fullTextSession.beginTransaction();

			indexar(fullTextSession, clazz);

			transaction.commit();
			LOG.info("finalizada transação na sessão de texto");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			transaction.rollback();
		}
		*/
	}

	protected void indexar(FullTextSession fullTextSession, Class clazz) {
		int fetchSize = 10000;
		ScrollableResults results = fullTextSession.createCriteria(clazz)
				.setFetchSize(fetchSize).scroll(ScrollMode.SCROLL_INSENSITIVE);
		int index = 0;
		while (results.next()) {
			index++;
			fullTextSession.index(results.get(0)); // index each element
			if (index % fetchSize == 0 || results.isLast()) {
				fullTextSession.flushToIndexes(); // apply changes to indexes
				fullTextSession.clear(); // free memory since the queue is
											// processed
				LOG.info(clazz.getName() + " : " + index);
			}
		}
		results.close();
	}

	
	
	public FullTextQuery createFullTextQuery(String campoAnalisado,
			String campoFonetico, String valor,  Class<?> modelClazz) {
		BooleanQuery totalQuery = new BooleanQuery();
		Query luceneQueryBrazilian = null;
		Query luceneQueryKeyword = null;
		try {
			if (WILDCARD.equals(valor)) {
				String buscaBrazilianAnalyzer = campoAnalisado+":(*)";
				luceneQueryBrazilian = createQuery(buscaBrazilianAnalyzer, new BrazilianAnalyzer());
				
				totalQuery.add(luceneQueryBrazilian, Occur.SHOULD);
			} else {
				String buscaBrazilianAnalyzer = campoAnalisado+":("+valor.trim()+"*) OR "+campoAnalisado+":("+valor.trim()+")";
				luceneQueryBrazilian = createQuery(buscaBrazilianAnalyzer, new BrazilianAnalyzer());
				String buscaKeywordAnalyzer = campoFonetico+":("+Fonetizador.fonetizar(valor.trim()).toLowerCase()+")";
				luceneQueryKeyword = createQuery(buscaKeywordAnalyzer, new KeywordAnalyzer());
				
				totalQuery.add(luceneQueryBrazilian, Occur.SHOULD);
				totalQuery.add(luceneQueryKeyword, Occur.SHOULD);
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage(),e);
		}
		
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		
		FullTextQuery query = dataAcess.createFullTextQuery(totalQuery, modelClazz);
		//FullTextQuery query = ((FullTextEntityManager) getEntityManager()).createFullTextQuery(totalQuery, modelClazz);
		
		return query;
	}
	
	
	public Query createQuery(String queryLucene, Analyzer analyzer) throws ParseException {
		Query luceneQueryBrazilian;
		QueryParser queryParser = new QueryParser(queryLucene, analyzer);
		queryParser.setAllowLeadingWildcard( true );
		queryParser.setDefaultOperator(QueryParser.Operator.AND);
		luceneQueryBrazilian = queryParser.parse(queryLucene);
		return luceneQueryBrazilian;
	}
	
	public int executeLuceneCount(String campoAnalisado, String campoFonetico, String valor, Class<?> modelClazz, String... sortFields) {
		FullTextQuery query = createFullTextQuery(campoAnalisado, campoFonetico, valor, modelClazz);
		return query.getResultSize();
	}
	
	public <T> List<T> executeLuceneQuery(String campoAnalisado, String campoFonetico, String valor, Class<T> modelClazz, Integer quantidade, String[] sortFields) {
		FullTextQuery query = createFullTextQuery(campoAnalisado, campoFonetico,
				valor, modelClazz);
		
		if (sortFields!=null) {
			for (String sortField : sortFields) {
				Sort sort = new Sort(new SortField(sortField, Type.STRING, false));
				query = query.setSort(sort);
			}
		}
		List<T> retorno = new ArrayList<T>();
		if (quantidade!=null && quantidade>0) {
			retorno = query.setMaxResults(quantidade).getResultList();
		} else {
			retorno = query.getResultList();
		}
		
		return retorno;
	}
	
	public <T> List<T> executeLuceneQueryParaSuggestionBox(String campoAnalisado, String campoFonetico, String valor, Class<T> modelClazz, String... sortFields) {
		return executeLuceneQuery(campoAnalisado, campoFonetico, valor,  modelClazz,100, sortFields);
	}
	
	protected<T> List<T> executeLuceneQueryTodosResultados(String campoAnalisado, String campoFonetico, String valor, Class<T> modelClazz, String... sortFields) {
		return executeLuceneQuery(campoAnalisado, campoFonetico, valor,  modelClazz,null, sortFields);
	}


}
