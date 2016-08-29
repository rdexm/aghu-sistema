package br.gov.mec.aghu.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.persistence.BaseEntity;

public class ModelUtil {

	private static final Log LOG = LogFactory.getLog(ModelUtil.class);

	/**
	 * Verifica se o metodo get do <b>field</b> em questao possui
	 * <b>annotation</b>.<br>
	 * 
	 * @param field
	 * @param annotation
	 * @return
	 */
	public static <E extends BaseEntity> boolean hasMethodAnnotationPresent(
			Field field, Class<? extends Annotation> annotation, Class<E> clazz) {
		boolean hasAnnotationPresent = false;

		String fieldName = StringUtils.capitalize(field.getName());
		Method methodGet = null;
		try {
			methodGet = clazz.getMethod("get" + fieldName);
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
	public static boolean isModelField(Field field) {
		return (
		// field.getType().getName().toUpperCase().contains("br.gov.mec.aghu.model.".toUpperCase())
		// &&
		field.getType().isAnnotationPresent(Entity.class));
	}

	/**
	 * Indica se o Field deve ser usado na Projeção do HQL.<br>
	 * Tambem serve para verificacao no set do Pojo apos a execucao do HQL.
	 * 
	 * @param field
	 * @return
	 */
	public static <E extends BaseEntity> boolean isProjectedField(Field field,
			Class<E> clazz) {
		boolean projectedField = true;

		if (field.getName().equalsIgnoreCase("serialVersionUID")
				|| isCollectionField(field) || isTransientField(field)
				|| isStaticField(field)
				|| hasMethodAnnotationPresent(field, Transient.class, clazz)) {
			projectedField = false;
		}

		return projectedField;
	}

	/**
	 * 
	 * @param field
	 * @return true se for uma classe Collection.
	 */
	private static boolean isCollectionField(Field field) {
		return (field.getType().getName().toUpperCase()
				.contains("LIST".toUpperCase())
				|| field.getType().getName().toUpperCase()
						.contains("SET".toUpperCase()) || field.getType()
				.getName().toUpperCase().contains("COLLECTION".toUpperCase()));
	}

	private static boolean isTransientField(Field field) {
		return Modifier.isTransient(field.getModifiers());
	}

	private static boolean isStaticField(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}
	
	/**
	 * Retorna o método get para o version da entidade.
	 * 
	 * @param clazz
	 * @return
	 */
	public static <E extends BaseEntity> Method getVersionMethod(Class<E> clazz) {
		final Method[] declaredMethods = clazz.getDeclaredMethods();
		for (final Method method : declaredMethods) {
			if (method.isAnnotationPresent(Version.class)) {
				return method;
			}
		}
		return null;
	}

}
