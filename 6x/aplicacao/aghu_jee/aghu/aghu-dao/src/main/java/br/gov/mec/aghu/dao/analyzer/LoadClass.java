package br.gov.mec.aghu.dao.analyzer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoadClass {
	
	private static final Log LOG = LogFactory.getLog(LoadClass.class);
	
	private String packageName;
	private String moduleName;
	
	public LoadClass(String aModuleName) {
		setModuleName(aModuleName);
		
		packageName = "br.gov.mec.aghu."+aModuleName+".dao";
	}
	
	private String getPathDir() {
		return packageName.replace('.', '/');
	}
	
	public String getDirPathSource() {
		String dirPath = LoadClass.class.getResource("").getPath();
		int index = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/dao/analyzer/");
		dirPath = dirPath.substring(0, index) + "src/main/java/" + getPathDir() + "/";
		return dirPath;
	}
	
	public Class[] execute() {
		return this.getClasses(packageName);
	}
	
	
	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * 
	 * @see http://snippets.dzone.com/posts/show/4831
	 */
	private Class[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = getPathDir();
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
		} catch (IOException e) {
			LOG.error("Exceção capturada: ", e);
		} catch (ClassNotFoundException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return null;
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
	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
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

	public String getModuleName() {
		return moduleName;
	}

	private void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
