package br.gov.mec.aghu.core.persistence.dao;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * Classe responsavel por construir/criar consultas complexas.<br>
 * Deve ser criada uma nova sub-classe para cada consulta.<br> 
 * 
 * @author rcorvalao
 *
 * @param <T>
 */
public abstract class QueryBuilder<T> implements Serializable{
	private static final long serialVersionUID = -7634482592431413055L;

	@Inject
	private DataAccessService dataAcess;
	
	private T product;
	private boolean builded = false;
	
	/**
	 * Responsabilidade da sub-classe<br>
	 * Deve instancia o Produto deste builder, conforme a sub-classe:<br>
	 * QueryBuilder, DetachedCriteria, etc...
	 * 
	 * <b>SEMPRE que este metodo não instanciar o Produto o metoto setProduct deve ser usado na sub-classe.</b><br>
	 * 
	 * @return
	 */
	protected abstract T createProduct();

	/**
	 * 
	 * @param aProduct
	 */
	protected abstract void doBuild(T aProduct);
	
	/**
	 * Retorna o Produto gerado.
	 * @return
	 */
	public final T getResult() {
		if (!this.isBuilded()) {
			throw new IllegalStateException("QueryBuilder esta em estado invalido. O metodo build nao foi executado.");
		}
		return getProduct();
	}
	
	/**
	 * Constroi o Produto e retorna o objeto construido.
	 * 
	 * @return
	 */
	public final T build() {
		if (this.getProduct() == null && !this.isBuilded()) {
			T newProduct = this.createProduct();
			setProduct(newProduct);
			this.doBuild(newProduct);
			this.setBuilded(true);
		}
		
		return this.getResult();
	}



	private T getProduct() {
		return product;
	}
	
	/**
	 * Seta o produto gerado.<br>
	 * <b>Responsabilidade da sub-classe de chamar este metodo
	 * SEMPRE que o metodo createProduct() retornar vazio.</b><br>
	 *  
	 * @param p
	 */
	protected final void setProduct(T p) {
		this.product = p;
	}

	protected final boolean isBuilded() {
		return builded;
	}

	protected final void setBuilded(boolean b) {
		this.builded = b;
	}
	
	/**
	 * 
	 * @param hql
	 * @return
	 */
	protected final Query createQuery(String hql) {
		if (hql == null || "".equals(hql.trim())) {
			throw new IllegalArgumentException("QueryBuilder.createQuery: String informada para criacao a Query nao eh valida!!!");
		}
		return this.dataAcess.createQuery(hql);		
	}
	
	
	 protected org.hibernate.Query createHibernateQuery(String query) {
		 return dataAcess.createHibernateQuery(query);
	 }
	 
	 protected <T extends BaseEntity> void refresh(T entity) {
		 dataAcess.refresh(entity);
	 }	
	
	/**
	 * Cria um DetachedCriteria para a classe clazz<br>
	 * com alias, se o parametro alias for uma string nao nula e nao vazia.<br>
	 * 
	 * @param clazz
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected final DetachedCriteria createDetachedCriteria(Class clazz, String alias) {
		if (clazz == null) {
			throw new IllegalArgumentException("QueryBuilder.createDetachedCriteria: Class informada para criacao a DetachedCriteria nao eh valida!!!");
		}
		DetachedCriteria criteria;
		if (alias != null && !"".equals(alias.trim())) {
			criteria = DetachedCriteria.forClass(clazz, alias);
		} else {
			criteria = DetachedCriteria.forClass(clazz); 			
		}
		return criteria;
	}
	
	
}
