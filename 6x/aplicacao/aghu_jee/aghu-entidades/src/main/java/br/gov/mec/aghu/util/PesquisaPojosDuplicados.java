package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

/**
 * 
 * @author evschneider
 *
 */
@SuppressWarnings("all")
public class PesquisaPojosDuplicados {

	/**
	 * Método principal para a execução da verificação de POJOs duplicados.
	 * 
	 * @param args
	 */
	public static void main (String[] args) {
		try {
			// Nome de innerclass Fields sempre concatena $Fields no nome real da classe
			final String enumName = "$Fields";
			final String packageName = "br.gov.mec.aghu.model";
			String name;
			List<String> annotations = new ArrayList<String>();
			
			Class[] classes = getClasses(packageName);
			for (Class clazz : classes) {
				if (clazz.getName().indexOf(enumName) < 0) {
					name = getAnnotationValue(clazz);
					if (!"".equals(name)) {
						annotations.add(name);
					}
				}
			}
			
			// Lista sem anotações duplicadas
			Set<String> uniqueAnnotations = new HashSet<String>();
			uniqueAnnotations.addAll(annotations);
			
			if (uniqueAnnotations.size() == annotations.size()) {
				System.out.println("Não existem POJOs duplicados");
			} else {
				System.out.println("ATENÇÃO: Existem POJOs duplicados!!!!");

				List<String> duplicatedPojos = new ArrayList<String>();
				
				// Percorre lista de elementos e verifica se existe mais de uma ocorrência na lista de anotações que permite duplicados 
				for (String annotationName : uniqueAnnotations) {
					int firstValue = annotations.indexOf(annotationName);
					int lastValue = annotations.lastIndexOf(annotationName);
					if (firstValue != lastValue) {
						duplicatedPojos.add(annotationName.toUpperCase());
					}
				}
				
				Collections.sort(duplicatedPojos);
				for (String duplicatedName : duplicatedPojos) {
					System.out.println(duplicatedName);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Classe para retornar o valor da anotação @Table da classe recebida por parâmetro
	 * 
	 * @param clazz
	 * @return
	 */
	private static String getAnnotationValue(Class clazz) {
		String annotationValue = "";
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Table) {
				String schema = "AGH";
				if (((Table) annotation).schema() != null && !"".equals(((Table) annotation).schema())) {
					schema = ((Table) annotation).schema();
				}
				//annotationValue = ((Table) annotation).schema() + "." + ((Table) annotation).name();
				annotationValue = schema + "." + ((Table) annotation).name();
			}
		}
		return annotationValue;
	}
	
	/**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
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
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
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
