package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("rawtypes")
public class ValidadorMapeamentosInvalidos {
	
	private static final Log LOG = LogFactory.getLog(ValidadorMapeamentosInvalidos.class);

	public static void main(String[] args) throws ClassNotFoundException, SecurityException, IOException {
		List<String> erros = new ArrayList<String>();
		
		for (Class<?> clazz : getClasses("br.gov.mec.aghu.model")) {
			String className = clazz.getName();
			if (!className.endsWith("$Fields") && !className.endsWith("Id")) {
				for (Method method : clazz.getMethods()) {
					for (Annotation annotation : method.getAnnotations()) {
						if (annotation instanceof OneToMany && method.getReturnType().getClass().isInstance(Collection.class)) {
							OneToMany oneToManyAnnotation = (OneToMany) annotation;
							String mappedBy = oneToManyAnnotation.mappedBy();
							
							ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
							Class otherClazz = (Class) parameterizedType.getActualTypeArguments()[0];
							
							String aux = method.getName();
							if (aux.startsWith("get")) {
								aux = StringUtils.uncapitalize(aux.substring(3));
							}
							
							if (StringUtils.isNotBlank(mappedBy)) {
								if (!existeAtributo(otherClazz, clazz, mappedBy)) {
									erros.add(otherClazz.getName() + "." + mappedBy + " in " + clazz.getName() + "." + aux);
								}
							} else {
								boolean existeJoinColumns = false;
								for (Annotation annotation2 : method.getAnnotations()) {
									if (annotation2 instanceof JoinColumn || annotation2 instanceof JoinColumns
											|| annotation2 instanceof JoinTable) {
										existeJoinColumns = true;
										break;
									}
								}
								
								if (!existeJoinColumns) {
									erros.add(otherClazz.getName() + "." + mappedBy + " in " + clazz.getName() + "." + aux);
								}
							}
							
							break;
						}
					}
				}
			}
		}
		
		if (!erros.isEmpty()) {
			Collections.sort(erros);
			
			int i = 0;
			for(String erro : erros) {
				LOG.info(++i + " - " + erro);
			}
		} else {
			LOG.info("Nenhum erro de mapeamento do tipo OneToMany encontrado.");
		}
	}

	private static boolean existeAtributo(Class clazz, Class classeRetorno, String mappedBy) {
		boolean existeAtributo = false;

		for (Method method : clazz.getMethods()) {
			String assinatura = "get" + StringUtils.capitalize(mappedBy);

			if (method.getName().equals(assinatura) && method.getReturnType().getClass().isInstance(classeRetorno)) {
				return true;
			}
		}

		return existeAtributo;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * 
	 * @see http://snippets.dzone.com/posts/show/4831
	 */
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
