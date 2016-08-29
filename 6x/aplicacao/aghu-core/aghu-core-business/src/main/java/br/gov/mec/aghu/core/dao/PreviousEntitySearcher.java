package br.gov.mec.aghu.core.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.SQLGrammarException;

import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;




/**
 * Pesquisa de entidade com os valores originais do banco.<br>
 * Classe para recuperar a entidade do banco para comparacao de valores antigos.<br>
 * Objeto resultante nao atachado. E NAO deve ser usado nos metodos merge, persist, etc do entitymanager.<br>
 * 
 * A responsabilidade é obter os valores originais de uma entidade antes das mudanças efetuadas pelo sistema / usuário.<br>
 * Basicamente usado para comparações de valores atuais com valores anterior de uma entidade.propriedade.<br>
 * 
 * 
 * @author rcorvalao
 *
 */
public class PreviousEntitySearcher implements Serializable {
	private static final long serialVersionUID = -2345157302664540196L;
	
	private static final Log LOG = LogFactory.getLog(PreviousEntitySearcher.class);
	
	public PreviousEntitySearcher() {
		super();
	}
	
	
	/**
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
	public <E extends BaseEntity> E get(E elemento, Class<E> clazz, Field fieldId, Method mId, DataAccessService dataAcess) {
		E b = null;
		
		if (elemento != null) {
			String hql = null;
			try {
				fieldId.setAccessible(true);
				Object valueId = mId.invoke(elemento);
				
				if (valueId != null) {
					hql = this.buildQuery(clazz, fieldId);
		
					//Query query = this.dataAcess.createQuery(hql);
					org.hibernate.Query query = dataAcess.createQueryStateless(hql);
					query.setParameter("entityId", valueId);
					@SuppressWarnings("unchecked")
					List<Object[]> camposLst = (List<Object[]>) query.list();
		
					if (camposLst != null && camposLst.size() > 0) {
						b = clazz.newInstance();
						Field[] fields = clazz.getDeclaredFields();
						
						Integer countAux = 0;
						Object[] campos = camposLst.get(0);
						for (Field field : fields) {
							field.setAccessible(true);
							if (!this.isProjectedField(field, clazz)) {
								continue;
							}
							field.set(b, campos[countAux]);
							countAux++;
						}
					}
				}
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				LOG.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOG.error(e.getMessage(), e);
			} catch (SQLGrammarException e) {
				LOG.error("Erro ao executar o sequinte HQL: " + hql);
				LOG.error(e.getMessage(), e);
			}
		}
		
		return b;
	}


	public <E extends BaseEntity> String buildQuery(Class<E> clazz, Field fieldId) {
		StringBuilder hql = new StringBuilder(45);
		Field[] fields = clazz.getDeclaredFields();
		
		hql.append("select ");
		for (Field field : fields) {
			field.setAccessible(true);
			if (!this.isProjectedField(field, clazz)) {
				continue;
			}
			if (!this.isModelField(field)) {
				hql.append("o.");
			}
			hql.append(field.getName());
			hql.append(", ");
		}
		int index = hql.lastIndexOf(",");
		hql.replace(index, hql.length(), " ");
		
		hql.append("from ").append(clazz.getSimpleName()).append(" o ");
		for (Field field : fields) {
			field.setAccessible(true);
			if (this.isModelField(field)) {
				hql.append(" left outer join o.").append(field.getName()).append(' ');
				hql.append(field.getName()).append(' ');
			}
		}
		hql.append(" where o.").append(fieldId.getName()).append(" = :entityId ");
		
		return hql.toString();
	}
	

	/**
	 * Indica se o Field deve ser usado na Projeção do HQL.<br>
	 * Tambem serve para verificacao no set do Pojo apos a execucao do HQL.
	 * 
	 * @param field
	 * @return
	 */
	protected <E extends BaseEntity> boolean isProjectedField(Field field, Class<E> clazz) {
		boolean projectedField = true;
		
		if (field.getName().equalsIgnoreCase("serialVersionUID")
				|| isCollectionField(field)
				|| isTransientField(field)
				|| isStaticField(field)
				|| hasMethodAnnotationPresent(field, Transient.class, clazz)
				) {
			projectedField = false;
		}
		
		return projectedField;
	}
	
	/**
	 * 
	 * @param field
	 * @return  true se for uma classe Collection.
	 */
	private boolean isCollectionField(Field field) {
		return (
			field.getType().getName().toUpperCase().contains("LIST".toUpperCase())
			|| field.getType().getName().toUpperCase().contains("SET".toUpperCase())
			|| field.getType().getName().toUpperCase().contains("COLLECTION".toUpperCase())
		);		
	}
	
	private boolean isTransientField(Field field) {
		return Modifier.isTransient(field.getModifiers());
	}
	
	private boolean isStaticField(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}
	
	/**
	 * Verifica se o metodo get do <b>field</b> em questao possui <b>annotation</b>.<br>
	 * 
	 * @param field
	 * @param annotation
	 * @return
	 */
	private <E extends BaseEntity> boolean hasMethodAnnotationPresent(Field field, Class<? extends Annotation> annotation, Class<E> clazz) {
		boolean hasAnnotationPresent = false;
		
		String fieldName = StringUtils.capitalize(field.getName());
		Method methodGet = null;
		try {
			methodGet = clazz.getMethod("get"+fieldName);
		} catch (SecurityException e) {
			LOG.warn(e.getMessage());
		} catch (NoSuchMethodException e) {
			LOG.warn(e.getMessage());
		}
		
		if (methodGet != null) {
			hasAnnotationPresent = methodGet.isAnnotationPresent(annotation);
		}
		
		return hasAnnotationPresent;
	}
	
	/**
	 *  
	 * @param field
	 * @return true se for uma classe Model do Projeto.
	 */
	protected boolean isModelField(Field field) {
		return (
			//field.getType().getName().toUpperCase().contains("br.gov.mec.aghu.model.".toUpperCase()) && 
			field.getType().isAnnotationPresent(Entity.class)
		);
	}
	
}
