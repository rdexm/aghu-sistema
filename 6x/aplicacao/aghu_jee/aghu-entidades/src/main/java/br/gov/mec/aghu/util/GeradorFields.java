package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("rawtypes")
public class GeradorFields {
	
	private static final Log LOG = LogFactory.getLog(GeradorFields.class);

	private static final String PACOTE_MODEL = "br.gov.mec.aghu.model";

	public static void main(String[] args) throws ClassNotFoundException, SecurityException, IOException {
		String dirPath = GeradorFields.class.getResource("").getPath();
		int index = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/util/");
		dirPath = dirPath.substring(0, index) + "src/main/java/br/gov/mec/aghu/model/";

		List<Class> lista = new ArrayList<Class>();

		for (Class<?> clazz : getClasses(PACOTE_MODEL)) {
			String className = clazz.getName();
			if (!className.endsWith("Id") && !className.endsWith("$Fields") && !className.endsWith("ExceptionCode")) {
				boolean possuiFields = false;

				Class[] innerClasses = clazz.getDeclaredClasses();
				if (innerClasses != null && innerClasses.length > 0) {
					for (Class innerClass : innerClasses) {
						String innerClassName = innerClass.getName();
						if (innerClassName.endsWith("$Fields")) {
							possuiFields = true;
							break;
						}
					}
				}

				if (!possuiFields) {
					lista.add(clazz);
				}
			}
		}

		for (Class clazz : lista) {
			criarFields(clazz, dirPath);
		}
	}

	private static void criarFields(Class clazz, String dirPath) throws IOException {
		String conteudoFields = obterConteudoFields(clazz);

		File file = new File(dirPath, clazz.getSimpleName() + ".java");
		if (file.exists() && existemFields(clazz)) {
			LOG.info("Criando fields para: " + clazz.getSimpleName());
			StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));

			int index = conteudo.lastIndexOf("}");

			conteudo.insert(index - 1, conteudoFields);

			FileUtils.writeStringToFile(file, conteudo.toString());
		}
	}

	private static boolean existemFields(Class clazz) {
		boolean aux = false;

		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);

			if (!field.getName().equals("serialVersionUID") && !field.getName().equals("version")) {
				aux = true;
				break;
			}
		}

		return aux;
	}

	private static String obterConteudoFields(Class clazz) {
		StringBuffer sb = new StringBuffer(240);
		sb.append("\n\tpublic enum Fields {\n\n\t\t");

		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);

			if (!field.getName().equals("serialVersionUID") && !field.getName().equals("version")) {
				sb.append(obterNomeField(field.getName()));
				sb.append("(\"");
				sb.append(field.getName());
				sb.append("\")");
				if (i != fields.length - 1) {
					sb.append(",\n\t\t");
				}
			}
		}

		sb.append(";\n\n\t\tprivate String fields;\n\n\t\tprivate Fields(String fields) {\n\t\t\tthis.fields = fields;\n\t\t}\n\n\t\t@Override\n\t\tpublic String toString() {\n\t\t\treturn this.fields;\n\t\t}\n\n\t}\n");

		return sb.toString();
	}

	private static String obterNomeField(String name) {
		StringBuffer aux = new StringBuffer(name);

		int count = 0;
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				aux = aux.insert(i + count++, "_");
			}
		}

		return aux.toString().toUpperCase();
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
			if (!file.isDirectory() && file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
