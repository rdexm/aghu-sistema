package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe para a verificação das enums "Fields" declaradas internamente nas
 * classes da package "br.gov.mec.aghu.model" do projeto AGHU.
 * 
 * Caso alguma enum Field esteja mapeando um atributo de modo inválido (atributo
 * não localizado), ao final da execução do método principal desta classe será
 * exibida uma lista no console de erros detalhando cada mapeamento inválido
 * encontrado.
 * 
 * @author mtocchetto
 */
@SuppressWarnings("rawtypes")
public class JPAFieldsAnalyzer {
	
	private static final Log LOG = LogFactory.getLog(JPAFieldsAnalyzer.class);

	public static void main(String[] args) throws SecurityException,
			IllegalArgumentException, ClassNotFoundException, IOException,
			IllegalAccessException {

		List<String> listaErros = new ArrayList<String>();

		for (Class<?> clazz : getClasses("br.gov.mec.aghu.model")) {
			String className = clazz.getName();
			if (className.endsWith("$Fields")) {
				// System.out.println(className);
				String[] classes = StringUtils.splitByWholeSeparator(className,
						"$Fields");
				Class<?> clazzPojo = Class.forName(classes[0]);
				// System.out.println(clazzPojo.getSimpleName());

				for (Field enumField : clazz.getFields()) {
					Object enumValue = enumField.get(clazz);
					// System.out.println("Field: " + enumField.getName() +
					// " -> " + enumValue);

					try {
						String[] strFields = StringUtils.splitByWholeSeparator(
								enumValue.toString(), ".");

						if (strFields.length > 0) {
							Method method = getGetterFromClass(clazzPojo,
									strFields[0]);

							if (strFields.length > 1) {
								Class<?> clazzType = method.getReturnType();

								for (int i = 1; i < strFields.length; i++) {
									if (Collection.class
											.isAssignableFrom(clazzType)) {
										clazzType = (Class<?>) ((ParameterizedType) method
												.getGenericReturnType())
												.getActualTypeArguments()[0];
									}

									String sField = strFields[i];
									method = getGetterFromClass(clazzType,
											sField);
									clazzType = method.getReturnType();
								}
							}
						}
					} catch (NoSuchMethodException e) {
						listaErros.add(className + "." + enumField.getName()
								+ "(" + enumValue + ")#" + e.getMessage());
					}
				}
			}
		}

		LOG.error(listaErros.size() + " erro(s) encontrado(s).\n");
		int i = 0;
		for (String mensagem : listaErros) {
			LOG.error(++i + " - " + mensagem);
		}

	}

	// private static Field getFieldFromClass(Class<?> clazz, String fieldName)
	// throws NoSuchFieldException {
	// Field field = null;
	// Class<?> currentClass = clazz;
	//
	// while (field == null && !currentClass.equals(Object.class)) {
	// try {
	// field = currentClass.getDeclaredField(fieldName);
	// } catch (Exception e) {
	// currentClass = currentClass.getSuperclass();
	// }
	// }
	//
	// if (field == null) {
	// throw new NoSuchFieldException("Não foi possível acessar " +
	// clazz.getName() + "." + fieldName);
	// }
	//
	// return field;
	// }

	private static Method getGetterFromClass(Class<?> clazz, String fieldName)
			throws NoSuchMethodException {
		Method getter = null;
		Class<?> currentClass = clazz;

		Character primeiraLetra = fieldName.charAt(0);
		fieldName = fieldName.substring(1);
		primeiraLetra = Character.toUpperCase(primeiraLetra);
		String inicio = "get" + primeiraLetra;
		String nomeGetter = inicio + fieldName;

		while (getter == null && !currentClass.equals(Object.class)) {
			try {
				getter = currentClass.getDeclaredMethod(nomeGetter);
			} catch (Exception e) {
				currentClass = currentClass.getSuperclass();
				LOG.error("Exceção capturada: ", e);
			}
		}

		if (getter == null) {
			throw new NoSuchMethodException("Não foi possível acessar "
					+ clazz.getName() + "." + nomeGetter);
		}

		return getter;
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
	private static Class[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
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
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

}