package br.gov.mec.aghu.core.persistence.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.business.moduleintegration.HospitalQualifier;
import br.gov.mec.aghu.core.dao.PreviousEntitySearcher;
import br.gov.mec.aghu.core.dao.Sequence;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * Classe generica responsavel pelo acesso a dados e marcadora da camada DAO.
 *  
 * @author rcorvalao
 *
 * @param <E>
 */
@SuppressWarnings({"PMD.AghuTooManyMethods"})
public abstract class BaseDao<E extends BaseEntity> implements Serializable {
	
	private static final long serialVersionUID = -8684776823331952310L;	
	private static final Log LOG = LogFactory.getLog(BaseDao.class);	
	public static final int SUGGESTION_MAX_RESULT=50;

	@Inject
	private DataAccessService dataAcess;
	
	private PreviousEntitySearcher previousEntitySearcher =  new PreviousEntitySearcher();
	
	/**
	 * Campo a ser inicializado sob demanda para permitir testes unitarios.
	 * 
	 * @author gandriotti
	 */
	private Class<E> clazz = null;
	
	private String fieldNameId;
	
	@Inject @HospitalQualifier 
	private Boolean isHCPA;

	
	public Boolean isHCPA() {
		return isHCPA;
	}

	
	public FullTextQuery createFullTextQuery(org.apache.lucene.search.Query query, Class<?> clazzName) {
		return dataAcess.createFullTextQuery(query, clazzName);
	}
	
	/**
	 * Realiza a inicializacao sob demanda de forma que retorne um valor valido
	 * mesmo no caso de testes unitarios.<br>
	 * 
	 * Funciona para qualquer hierarquia de classes que esteja definindo o <b>Tipo</b><br>
	 * numa classe filha de uma classe abstrata ou de GenericDao.<br> 
	 * 
	 * Casos existentes:<br>
	 * 1. Hierarquia 2 niveis - genericdao e dao concreta.<br>
	 * 2. Hierarquia 3 niveis - com abstract no meio. <br>
	 * 3. Hierarquia 3 niveis - com 2 dao concretas.<br>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getClazz() {
		if (this.clazz == null) {
			Class<?> nivel = getClass();
			Class<?> proxNivel = nivel.getSuperclass();
			
			if (!Modifier.isAbstract(proxNivel.getModifiers())) {
				while ( !("BaseDao".equalsIgnoreCase(proxNivel.getSimpleName())) ) {
					nivel = proxNivel;
					proxNivel = proxNivel.getSuperclass();
				}
			}
			
			ParameterizedType paramType = (ParameterizedType) nivel.getGenericSuperclass();
			this.clazz = (Class<E>) paramType.getActualTypeArguments()[0];
		}

		return this.clazz;
	}
	
	public E carregarPorChavePrimaria(Object pk) {
		return dataAcess.load(pk, getClazz());
	}
	
	/**
	 * Lista todos elementos da entidade.<br>
	 * Ordenado pelo id. Quando este estiver marcado para <i>javax.persistence.Id</i>.<br>
	 * 
	 * @return
	 */
	public List<E> listarTodos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getClazz());
		
		if (getFieldNameId() != null) {
			criteria.addOrder(Order.asc(getFieldNameId()));
		}
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retrona o nome do atributo que eh o identificador da entidade.<br>
	 * 
	 * @return
	 */
	protected String getFieldNameId() {
		if (this.fieldNameId == null) {
			Field f = this.getFieldId();
			if (f != null) {
				this.fieldNameId = f.getName();
			}
		}
		
		return this.fieldNameId;
	}
	
	/**
	 * Busca entre os Fields da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * 
	 * @return
	 */
	private Field getFieldId() {
		return EntityIntrospector.getFieldId(getClazz());
	}
	
	/**
	 * Busca entre os Methods da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * @return
	 */
	private Method getMethodId() {
		return EntityIntrospector.getMethodId(getClazz());
	}
	
	/**
	 * Método genérico para inserir (sem flush) elementos na base de dados.
	 * 
	 * @param elemento
	 * @return
	 */
	public void persistir(E entity) {
		obterValorSequencialId(entity);
		
		dataAcess.persist(entity);
    }
    
 	/**
	 * Método usado para criar Detacheds criterias para o pojo relacionado ao
	 * DAO.
	 * 
	 * Changelog: 20110413 - gandriotti - obtem clazz sob demanda
	 * 
	 * @return
	 */
	protected DetachedCriteria criarDetachedCriteria() {
		return DetachedCriteria.forClass(getClazz());
	}
	
	 protected org.hibernate.Query createHibernateQuery(String query) {
		 return dataAcess.createHibernateQuery(query);
	 }
	 
	 protected org.hibernate.SQLQuery createSQLQuery(String query) {
		 return dataAcess.createSQLQuery(query);
	 }
	 
	 protected org.hibernate.Query createQueryStateless(String query) {
		 return dataAcess.createQueryStateless(query);
	 }
	 
	 protected Query createNativeQuery(String query){
		 return dataAcess.createNativeQuery(query);
	 }
	 
	public Query createNativeQuery(String sqlString, Class<E> resultClass) {
		return dataAcess.createNativeQuery(sqlString, resultClass);
	}
	 
	 protected Query createQuery(String query){
		 return dataAcess.createQuery(query);
	 }
	
	 public void evictQueryRegion(String region) {
		 this.dataAcess.evictQueryRegion(region);
	 }
	 
	 public <T extends BaseEntity> void evictEntityRegion(Class<T> type) {
		 this.dataAcess.evictEntityRegion(type);
	 }
	 
	/**
	 * Método que retorna a projeção COUNT para a criteria especificada
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @return O COUNT com o numero de registros retornado pela query
	 */
	protected Long executeCriteriaCount(DetachedCriteria criteria) {
		criteria.setProjection(Projections.rowCount());

		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método que retorna a contagem de registros de uma pesquisa com uma
	 * projecao de contagem em uma determinada coluna com a opção de usar
	 * distinct.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @param countProperty
	 *            Coluna a ser contada
	 * @param distinct
	 *            Diz se a contagem deve usar distinct ou nao.
	 * @return O COUNT com o numero de registros retornado pela query
	 */
	protected Long executeCriteriaCountDistinct(DetachedCriteria criteria,
			String countProperty, boolean distinct) {

		if (distinct) {
			criteria.setProjection(Projections.countDistinct(countProperty));
		} else {
			criteria.setProjection(Projections.count(countProperty));
		}

		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método usado para verificar se existe ao menos um registro na base que atende as restrições da criteria.
	 * @param criteria
	 * @return
	 */
	protected boolean executeCriteriaExists(DetachedCriteria criteria) {
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		executableCriteria.setMaxResults(1);
		Object object = executableCriteria.uniqueResult();		
		return object != null;		
	}
	
	
	
	/**
	 * Método preferencial para executar criterias.
	 * 
	 * O resultado dessa pesquisa nao fica no cache de segundo nível por padrão.
	 * Ou seja, sera executada toda vez contra o banco de dados.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @return Lista com o resultado da pesquisa
	 */
	protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
		return executeCriteria(criteria, false, null);
	}
	
	/**
	 * Método para criterias com informacao de CacheMode.
	 * 
	 * O resultado dessa pesquisa nao USA ou NAO a cache de segundo nível.
	 * Ou seja, sera executada contra o banco de dados dependendo doparametro cacheMode informado.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @param cacheMode
	 * @return Lista com o resultado da pesquisa
	 */
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, CacheMode cacheMode) {
		return executeCriteria(criteria, false, cacheMode);
	}
	
	/**
	 * Realiza a pesquisa passandiretivas de cache.
	 * 
	 * @param criteria
	 *            Criteria a ser utilizada;
	 * @param cacheble
	 *            Diz se o resultado desta pesquisa deve ser cacheado ou nao
	 * @return Lista com o resultado da pesquisa
	 */
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, boolean cacheble) {
		return executeCriteria(criteria, cacheble, null);
	}

	/**
	 * Realiza a pesquisa passando a region do cache. Util para invalidação quando necessário
	 * OBS: setCacheable sempre estará true neste método 
	 * @param criteria
	 *            Criteria a ser utilizada;
	 * @param region
	 *            Nome do region
	 * @return Lista com o resultado da pesquisa
	 */
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, String region) {
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);

		executableCriteria.setCacheable(true);
		if (region != null && !region.isEmpty()) {
			executableCriteria.setCacheRegion(region);
		}
		
		return executableCriteria.list();
	}
	
	/**
	 * Realiza a pesquisa passandiretivas de cache.
	 * 
	 * @param criteria
	 *            Criteria a ser utilizada;
	 * @param cacheble
	 *            Diz se o resultado desta pesquisa deve ser cacheado ou nao
	 * @param cacheMode
	 *            Informa o cacheMode da criteria
	 *            
	 * @return Lista com o resultado da pesquisa
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, boolean cacheble, CacheMode cacheMode) {
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);

		if (cacheble) {
			executableCriteria.setCacheable(cacheble);
		}
		
		if (cacheMode != null) {
			executableCriteria.setCacheMode(cacheMode);
		}

		return executableCriteria.list();
	}
	
	/**
	 * Método para executar criterias pagináveis.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @param firstResult
	 *            Indice do primeiro registro a ser retornada
	 * @param maxResults
	 *            Quantidade maxima de registros retornados
	 * @param orderProperty
	 *            Nome da propriedade a ser ordenada
	 * @param asc
	 *            Tipo de ordenação. True para ascendente e False para
	 *            descendente.
	 * @return Lista limitadacom resultado da pesquisa
	 */
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, int firstResult, int maxResults, String orderProperty, boolean asc) {
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc, null);
	}
	
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, int firstResult, int maxResults, String orderProperty, boolean asc, CacheMode cacheMode) {
		if (StringUtils.isNotEmpty(orderProperty)) {
			addOrder(criteria, orderProperty, asc);
		}
		
		Criteria executableCriteria = dataAcess.createExecutableCriteriaPaginator(criteria, firstResult, maxResults);

		if (cacheMode != null) {
			executableCriteria.setCacheMode(cacheMode);
		}
		
		return executableCriteria.list();
	}
	
	/**
	 * Método para executar criterias pagináveis. com múltiplos critérios de
	 * ordenação.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @param firstResult
	 *            Indice do primeiro registro a ser retornada
	 * @param maxResults
	 *            Quantidade maxima de registros retornados
	 * @param mapOrdenacao
	 *            Map contendo as propriedades e respectivas ordenações
	 * @return Lista limitadacom resultado da pesquisa
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> executeCriteria(DetachedCriteria criteria,
			int firstResult, int maxResults, Map<String, Boolean> mapOrdenacao) {
		
		if (mapOrdenacao != null) {
			Boolean asc;
			for (String orderProperty : mapOrdenacao.keySet()) {
				asc = mapOrdenacao.get(orderProperty);
				addOrder(criteria, orderProperty, asc);
			}
		}

		Criteria executableCriteria = criarExecutableCriteriaComPaginacao(
				criteria, firstResult, maxResults);

		return executableCriteria.list();
	}
	
	private Criteria criarExecutableCriteriaComPaginacao(DetachedCriteria criteria, int firstResult, int maxResults) {
		return dataAcess.createExecutableCriteriaPaginator(criteria, firstResult, maxResults);
	}
	
	/**
	 * Adicionar ordem a uma Detached Criteria.
	 * 
	 * @param criteria
	 * @param orderProperty
	 * @param asc
	 */
	private void addOrder(final DetachedCriteria criteria, String orderProperty, boolean asc) {
		if (orderProperty != null && StringUtils.isNotBlank(orderProperty)) {
			StringTokenizer tokenizer = new StringTokenizer(orderProperty, ".");
			
			String property = tokenizer.nextToken();
			while (tokenizer.hasMoreTokens()) {
				// Left join para nao excluir registros com relacionamentos nulos/nao existentes.
				criteria.createAlias(property, property, JoinType.LEFT_OUTER_JOIN);
				property = tokenizer.nextToken();
			}
			
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}
	}
	
	/**
	 * Método preferencial para executar criterias que retornam um único objeto.
	 * 
	 * O resultado dessa pesquisa nao fica no cache de segundo nível por padrão.
	 * Ou seja, sera executada toda vez contra o banco de dados.
	 * 
	 * @param criteria
	 *            Criteria com a query a ser usada
	 * @return Objeto retornado da pesquisa
	 */
	protected Object executeCriteriaUniqueResult(DetachedCriteria criteria) {
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		Object object = executableCriteria.uniqueResult();

		return object;
	}
	
	/**
	 *  Método preferencial para executar criterias que retornam um único objeto.
	 * 
	 * O resultado dessa pesquisa nao fica no cache de segundo nível por padrão.
	 * Ou seja, sera executada toda vez contra o banco de dados.
	 * 
	 * @param criteria
	 * 	 Criteria com a query a ser usada
	 * @param cacheable
	 *   Indicador se o resultado deve ser armazenado na cache de 2o nivel do hibernate.
	 * @return
	 */
	protected Object executeCriteriaUniqueResult(DetachedCriteria criteria, boolean cacheable) {
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		executableCriteria.setCacheable(cacheable);
		Object object = executableCriteria.uniqueResult();

		return object;
	}
	
	public void flush() {
		dataAcess.flush();
	}
	
	/*
	 * Metodos para refatorar.
	 * Pois estao com nomes em portugues apenas para agilizar a migracao. por diminuir a refatoração do codigo legado.
	 * 
	 */
	
	public E obterPorChavePrimaria(Object pk) {
		return dataAcess.find(pk,  getClazz());
	}
	
	public E obterPorChavePrimaria(Object pk, Enum... fetchArgs) {
		return (E)  obterPorChavePrimaria(pk, false, fetchArgs);
	}
	
	
	public E obterPorChavePrimaria(Object pk, boolean left, Enum... fetchInnerArgs) {
		DetachedCriteria c = DetachedCriteria.forClass(getClazz());
		c.add(Restrictions.eq(dataAcess.getIdProperty(getClazz()), pk));
		for (Enum e : fetchInnerArgs){
			if (left){
				c.createAlias(e.toString(), e.toString(), JoinType.LEFT_OUTER_JOIN);
			}else{
				c.createAlias(e.toString(), e.toString(), JoinType.INNER_JOIN);
			}	
		}	
		return (E) executeCriteriaUniqueResult(c);
	}	
	
	
	public E obterPorChavePrimaria(Object pk, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		DetachedCriteria c = DetachedCriteria.forClass(getClazz());
		c.add(Restrictions.eq(dataAcess.getIdProperty(getClazz()), pk));
		if (fetchArgsInnerJoin!=null){
			for (Enum e1 : fetchArgsInnerJoin){
				c.createAlias(e1.toString(), e1.toString(), JoinType.INNER_JOIN);
			}
		}	
		if (fetchArgsLeftJoin!=null){
			for (Enum e2 : fetchArgsLeftJoin){
				c.createAlias(e2.toString(), e2.toString(), JoinType.LEFT_OUTER_JOIN);
			}
		}	
		return (E) executeCriteriaUniqueResult(c);
	}	
	
	public E atualizar(E elemento) {
		dataAcess.update(elemento);
		return elemento;
	}
	
	public void remover(E elemento) {
		if (elemento == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		dataAcess.remove(elemento);
	}
	
	public void initialize(Object elemento) {
		dataAcess.initialize(elemento);
	}
	
	public void removerPorId(Object pk) {
		E elemento = obterPorChavePrimaria(pk);
		this.remover(elemento);
	}
	
	
	/**
	 * Pesquisa pelos valores anteriores da Entidade.<br>
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. E NAO deve ser usado nos metodos merge, persist, etc do entitymanager.<br>
	 * 
	 * A responsabilidade é obter os valores originais de uma entidade antes das mudanças efetuadas pelo sistema / usuário.<br>
	 * Basicamente usado para comparações de valores atuais com valores anterior de uma entidade.propriedade.<br>
	 * 
	 * 
	 * @param elementoModificado
	 * @return
	 */
	public E obterOriginal(E elemento) {
		return previousEntitySearcher.get(elemento, getClazz(), getFieldId(), getMethodId(), dataAcess);
	}
	
	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. E NAO deve ser usado nos metodos merge, persist, etc do entitymanager.<br>
	 * 
	 * A responsabilidade é obter os valores originais de uma entidade antes das mudanças efetuadas pelo sistema / usuário.<br>
	 * Basicamente usado para comparações de valores atuais com valores anterior de uma entidade.propriedade.<br>
	 * 
	 * @param id
	 * @return
	 */
	public E obterOriginal(EntityCompositeId id) {
		E entity = getEntityPK(id);
		
		return this.obterOriginal(entity);
	}
	
	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. E NAO deve ser usado nos metodos merge, persist, etc do entitymanager.<br>
	 * 
	 * A responsabilidade é obter os valores originais de uma entidade antes das mudanças efetuadas pelo sistema / usuário.<br>
	 * Basicamente usado para comparações de valores atuais com valores anterior de uma entidade.propriedade.<br>
	 * 
	 * @param idNumber
	 * @return
	 */
	public E obterOriginal(Number idNumber) {
		E entity = getEntityPK(idNumber);
		
		return this.obterOriginal(entity);		
	}
	
	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. E NAO deve ser usado nos metodos merge, persist, etc do entitymanager.<br>
	 * 
	 * A responsabilidade é obter os valores originais de uma entidade antes das mudanças efetuadas pelo sistema / usuário.<br>
	 * Basicamente usado para comparações de valores atuais com valores anterior de uma entidade.propriedade.<br>
	 * 
	 * @param idString
	 * @return
	 */
	public E obterOriginal(String idString) {
		E entity = getEntityPK(idString);
		
		return this.obterOriginal(entity);		
	}

	private E getEntityPK(Object pk) {
		E entity = null;
		
		try {
			entity = getClazz().newInstance();
			
			Field fieldId = this.getFieldId();
			fieldId.setAccessible(true);
			fieldId.set(entity, pk);
			
		} catch (InstantiationException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return entity;
	}


	/**
	 * Obtem da data atual do banco.
	 * 
	 * @return
	 */
	public Date getCurrentDate() {
		StringBuffer select = new StringBuffer(25);
		if (isOracle()) {
			select.append("select current_date from dual");
		} else if (isPostgreSQL()) {
			select.append("select current_date");
		} else if (isHSQL()) {
			select.append("call current_timestamp;");
		}
		
		if (isHSQL()) {
			return (Date) dataAcess.createNativeQuery(select.toString()).getResultList().get(0);
		}		
		return (Date) dataAcess.createNativeQuery(select.toString()).getSingleResult();
	}
	
	public boolean isOracle() {
		return dataAcess.isOracle();
	}
	
	protected boolean isPostgreSQL() {
		return dataAcess.isPostgreSQL();
	}
	
	protected boolean isHSQL() {
		return dataAcess.isHSQL();
	}
	
	/**
	 * Método responsável por obter o próximo valor de uma sequence
	 * 
	 * @param id
	 */
	public Integer getNextVal(Sequence id) {
		StringBuffer select = new StringBuffer();
		if (isOracle()) {
			select.append("select ").append(id.getSequenceName()).append(".NEXTVAL from dual");
		} else if (isPostgreSQL()) {
			select.append("select ").append(" nextval('").append(id.getSequenceName()).append("')");
		} else if (isHSQL()) {
			select.append("CALL NEXT VALUE FOR ").append(id.getSequenceName());
		}
		
		List<?> listNextVal = dataAcess.createNativeQuery(select.toString()).getResultList();
		
		Number nextVal = null;
		if (isOracle()) {
			nextVal = (BigDecimal) listNextVal.get(0);			
		} else if (isPostgreSQL()) {
			nextVal = (BigInteger) listNextVal.get(0);
		} else if (isHSQL()) {
			nextVal = (Integer) listNextVal.get(0);
		}
		
		return nextVal.intValue();
	}

	/**
	 * Método responsável por obter o valor atual de uma sequence
	 * 
	 * @param id
	 */
	public Integer getCurrVal(Sequence id) {
		StringBuffer select = new StringBuffer();
		if (isOracle()) {
			select.append("select ").append(id.getSequenceName()).append(".CURRVAL from dual");
		} else if (isPostgreSQL()) {
			select.append("select ").append(" currval('").append(id.getSequenceName()).append("')");
		} else if (isHSQL()) {
			select.append("CALL CURRENT VALUE FOR ").append(id.getSequenceName());
		}
		
		List<?> listCurrVal = dataAcess.createNativeQuery(select.toString()).getResultList();
		
		Number currVal = null;
		if (isOracle()) {
			currVal = (BigDecimal) listCurrVal.get(0);
		} else if (isPostgreSQL()) {
			currVal = (BigInteger) listCurrVal.get(0);
		} else if (isHSQL()) {
			currVal = (Integer) listCurrVal.get(0);
		}	
		
		return currVal.intValue();
	}
	
	
	/**
	 * Verifica se o objeto está no entity manager.
	 * 
	 */
	public boolean contains(E elemento) {
		return this.dataAcess.contains(elemento);
	}
	
	/**
	 * Desatacha o objeto da session
	 * 
	 * @param elemento
	 */
	public void desatachar(E elemento) {
		this.dataAcess.evict(elemento);
	}
	
	/**
	 * Responsabilidade da sub-classe<br>
	 * NAO usar este metodo para Id simples. Neste caso, Usar
	 * {@link javax.persistence.SequenceGenerator}<br>
	 * Conforme orientacoes em:<br>
	 * http://redmine.mec.gov.br/projects/aghu/wiki/Orienta%C3%A7%C3%
	 * B5es_constru%C3%A7%C3%A3o_Models<br>
	 * 
	 * Caso de id composto, sobrescrever este na sub-classe:<br>
	 * Exemplo:<br>
	 * 
	 * <i> protected void obterValorSequencialId(AelItemSolicitacaoExames
	 * elemento) {<br>
	 * if (elemento == null || elemento.getSolicitacaoExame() == null) {<br>
	 * throw new IllegalArgumentException("Parametro Invalido!!!");<br>
	 * }<br>
	 * AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();<br>
	 * id.setSoeSeq(elemento.getSolicitacaoExame().getSeq());<br>
	 * Integer seqp = 0;<br>
	 * Short maxSeqp =
	 * this.obterItemSolicitacaoExameSeqpMax(elemento.getSolicitacaoExame
	 * ().getSeq());<br>
	 * if (maxSeqp != null) {<br>
	 * seqp = maxSeqp.intValue();<br>
	 * }<br>
	 * seqp = seqp + 1;<br>
	 * id.setSeqp(seqp.shortValue());<br>
	 * elemento.setId(id);<br>
	 * }<br>
	 * </i><br>
	 * 
	 * rcorvalao 07/08/2013
	 * 
	 * @param parametro
	 */
	protected void obterValorSequencialId(E elemento) {
		// metodo de responsabilidade sub-classe. apenas para id composto.
	}
	
	/**
	 * Método genérico para reatachar elementos.
	 * 
	 * @param elemento
	 * @return
	 */
	public E merge(E elemento) {
		return this.dataAcess.merge(elemento);
	}
	
	/**
	 * Realiza um refresh do objeto.
	 * 
	 * @param elemento
	 */
	public void refresh(E elemento) {
		this.dataAcess.refresh(elemento);
	}
	
	/**
	 * Realiza um refresh e lock no registro do banco.
	 * 
	 * @param elemento
	 */
	public void refreshAndLock(E elemento) {
		this.dataAcess.refreshAndLock(elemento);
	}
	
	public E getAndLock(Serializable id, LockOptions lockOptions) {
		return this.dataAcess.getAndLock(this.getClazz(), id, lockOptions);
	}
	
	public E getAndLockForce(Serializable id) {
		return this.dataAcess.getAndLockForce(this.getClazz(), id);
	}
	
//	/**
//	 * Método que retorna o logger.
//	 * TODO Deve ser sobrescrito na sub-classe.
//	 * 
//	 * @return
//	 */
//	protected Log doGetLogger() {
//		return LogFactory.getLog(this.getClass());
//	}
	//###################################################################
	
	public void doWork(Work aghuWork) {
		this.dataAcess.doWork(aghuWork);
	}
	
	protected <T extends BaseEntity> T find(Class<T> type, Serializable id) {
		return this.dataAcess.find(id, type);
	}
	
	protected org.hibernate.Query createFilter(Object collection, String queryString) {
		return dataAcess.createFilter(collection, queryString);
	}
	
	protected E findByPK(Class<E> type, Object pk) {
		return dataAcess.find(pk, type);
	}
	
	protected boolean entityManagerIsOpen() {
		return dataAcess.entityManagerIsOpen();
	}
	
	public void entityManagerClear() {
		dataAcess.entityManagerClear();
	}
	
	protected org.hibernate.Query createFilterHibernate(Object collection, String queryString) {
		return dataAcess.createFilter(collection, queryString);
	}
	
	protected <T extends BaseEntity> void lockEntity(T entity, LockModeType lockMode) {
		dataAcess.lockEntity(entity, lockMode);
	}
	
	protected void joinTransaction() {
		dataAcess.joinTransaction();
	}
	
	protected void setReadOnly(E entity, boolean readOnly) { 
		dataAcess.setReadOnly(entity, readOnly);
	}

	protected ScrollableResults createScrollableResults(
			DetachedCriteria criteria, Integer fetchSize, ScrollMode scrollMode) {
		return dataAcess.createScrollableResults(criteria, fetchSize,
				scrollMode);
	}
	
	public Date obterDataHoraBanco(){
		return dataAcess.obterDataHora();		
	}
	
	public void indexar(Class clazz) throws InterruptedException{
		dataAcess.indexar(clazz);
	}
	
	
	public Object getUniqueStatelessResult(Class clazz, String idField, Object valueId, String field){
		org.hibernate.Query hql = dataAcess.createQueryStateless("select " + field + " from " + clazz.getSimpleName() + " where " + idField + "=:entityId");
		hql.setParameter("entityId", valueId);
		return hql.uniqueResult(); 
	}

}