package br.gov.mec.aghu.core.persistence.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.persistence.BaseEntity;

public class EntityIntrospector {
	
	
	private static final Log LOG = LogFactory.getLog(EntityIntrospector.class);
	
	
	/**
	 * Busca entre os Fields da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * 
	 * @return
	 */
	public static <E extends BaseEntity> Field getFieldId(Class<E> clazz) {
		Field returnValeu = null;
		
		Method methodId = getMethodId(clazz);
		
		String fieldName = methodId.getName().substring(3);
		//fieldName = fieldName.toLowerCase();
		
		StringBuffer first = new StringBuffer(fieldName.substring(0, 1).toLowerCase());
		fieldName = fieldName.substring(1);
		fieldName = first.append(fieldName).toString();
		
		try {
			returnValeu = clazz.getDeclaredField(fieldName);
		} catch (SecurityException e) {
			LOG.error("Problemas para encontrar Field: " + fieldName);
		} catch (NoSuchFieldException e) {
			LOG.error("Field NAO encontrado: " + fieldName);
		}
		
		return returnValeu;
	}
	
	
	/**
	 * Busca entre os Methods da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * @return
	 */
	public static <E extends BaseEntity> Method getMethodId(Class<E> clazz) {
		Method methodId = null;
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				if (method.isAnnotationPresent(javax.persistence.Id.class) 
						|| method.isAnnotationPresent(javax.persistence.EmbeddedId.class)) {
					methodId = method;
					break;
				}
			}
		}
		
		return methodId;
	}

}
