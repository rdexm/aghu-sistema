package br.gov.mec.aghu.bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>MultiplePropertiesResourceBundle</code> eh uma classe abstrata que tem a responsabilidade de 
 * combinar em um ResourceBundle multiplos arquivos de properties onde estes arquivos de properties devem estar 
 * listados em um arquivo central cujo o nome eh referenciado em - base-name - para agregar todos os properties destes arquivos
 * em um unico ResourceBundle.
 * <p>
 * Uma implementacao concreta desta deve fornecer um construtor default no qual
 * <code>super("base-name");</code> or <code>super("package.name","base-name");</code> deve ser 
 * chamado dependendo se o seu arquivo de properties inicial esta localizado no default ou em especifico package.
 * </p>
 * 
 * <pre>
 * public class MessagesResourceBundle extends MultiplePropertiesResourceBundle {
 * 	public ExampleResourceBundle() {
 * 		super(&quot;messages&quot;);
 * 	}
 * }
 * 
 * or 
 * 
 * public class MessagesResourceBundle extends MultiplePropertiesResourceBundle {
 * 	public ExampleResourceBundle() {
 * 		super(&quot;my.package&quot;, &quot;messages&quot;);
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * For each java.util.Locale that you need to support you also must provide java.util.Locale variants of your java
 * ResourceBundle class as shown below. Creating an empty subclass of the above class does the job -
 * the separate class is needed to let {@link ResourceBundle#getBundle(String, java.util.Locale)} find and
 * cache your bundle with the right java.util.Locale.
 * </p>
 * 
 * <pre>
 * public class MessagesResourceBundle_de extends MessagesResourceBundle {
 * }
 * </pre>
 * 
 * <h3>Informando lista de properties</h3>
 * <p>
 * O arquivo referenciado por base-name deve conter apenas properties que indiquem outros arquivos de properties.<br>
 * Exemplo:<br>
 * message-001=bundle.messages<br>
 * message-002=bundle.aghu.casca.messages_casca<br>
 * message-003=bundle.aghu.casca.messages_pendencia<br>
 * Neste exemplo bundle.messages, bundle.aghu.casca.messages_casca, bundle.aghu.casca.messages_pendencia sao os
 * arquivos de properties que o sistema necessita.
 * </p>
 * 
 * <p>
 * A orderm de load dos arquivos de properties eh a ordem alfabetica dos nomes dos arquivos definidos no 
 * arquivo base-name.
 * </p>
 * 
 * @author rcorvalao
 * 
 */
public abstract class MultiplePropertiesResourceBundle extends ResourceBundle {
	

	private static final Log LOG = LogFactory.getLog(MultiplePropertiesResourceBundle.class);

	/**
	 * The base name for the ResourceBundles to load in.
	 */
	private String baseName;

	/**
	 * The package name where the properties files should be.
	 */
	private String packageName;

	/**
	 * A Map containing the combined resources of all parts building this
	 * MultiplePropertiesResourceBundle.
	 */
	private Map<String, Object> combined;
	
	private Map<String, String> expressionMap = new  WeakHashMap<String, String>();

	/**
	 * Construct a <code>MultiplePropertiesResourceBundle</code> for the passed in base-name.
	 * 
	 * @param baseName
	 *          the base-name that must be part of the properties file names.
	 */
	protected MultiplePropertiesResourceBundle(String baseName) {
		this("", baseName);
	}

	/**
	 * Construct a <code>MultiplePropertiesResourceBundle</code> for the passed in base-name.
	 * 
	 * @param packageName
	 *          the package name where the properties files should be.
	 * @param baseName
	 *          the base-name that must be part of the properties file names.
	 */
	private MultiplePropertiesResourceBundle(String packageName, String baseName) {
		if (baseName == null || "".equals(baseName.trim())) {
			throw new IllegalArgumentException("Nome do ResourceBundle deve ser informado!!!");
		}
		this.packageName = packageName;
		this.baseName = baseName;
	}

	@Override
	public Object handleGetObject(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key não pode ser null");
		}
		loadBundlesOnce();
		return combined.get(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		loadBundlesOnce();
		ResourceBundle parent = this.parent;
		return new ResourceBundleEnumeration(combined.keySet(), (parent != null) ? parent.getKeys()	: null);
	}

	/**
	 * Load the resources once.
	 */
	private void loadBundlesOnce() {
		if (combined == null) {
			combined = new HashMap<String, Object>(128);
			
			String key = null;
			ResourceBundle bundle;
			List<String> bundleNames = findBaseNames();
			for (String bundleName : bundleNames) {
				try {
					bundle = ResourceBundle.getBundle(bundleName, getLocale());
					Enumeration<String> keys = bundle.getKeys();
					while (keys.hasMoreElements()) {
						key = keys.nextElement();
						
						String value = bundle.getString(key); //bundle.getObject(key)
						if (value.contains("#{")) {
							value = changeExpressionResult(value);
						}
						
						combined.put(key, value);
					}//WHILE
				} catch (Exception e) {
					LOG.info(e.getMessage());
				}
			}//FOR
		}//IF
	}
	
	private String changeExpressionResult(String sentence) {
		String expression = getExpressionValue(sentence);
		
		String value = expressionMap.get(expression);
		if (value==null) {
			FacesContext context = FacesContext.getCurrentInstance();
			value=(String) context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), expression, String.class).getValue(context.getELContext());
			expressionMap.put(expression, value);
		}
		return sentence.replace(expression, value);
	}
	
	private String getExpressionValue(String sentence) {
		return sentence.substring(sentence.indexOf("#{"), sentence.indexOf('}')+1);
	}
	
	/*
	public Locale getLocale() {
		Locale local = super.getLocale(); 
		if (local == null) {
			local = Locale.getDefault();
		}
		return local;
	}
	*/
	
	/**
	 * Return a Set with the real base-names of the multiple properties based resource bundles that
	 * contribute to the full set of resources.
	 * 
	 * @param baseName
	 *          the base-name that must be part of the properties file names.
	 * @return a List with the real base-names.
	 * /
	private List<String> findBaseNames(final String baseName) {
		final String METHOD = "findBaseNames";
		boolean isLoggable = LOG.isLoggable(Level.FINE);

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<String> bundleNames = new ArrayList<String>();
		try {
			String baseFileName = baseName + ".properties";
			String resourcePath = getResourcePath();
			String resourceName = resourcePath + baseFileName;
			if (isLoggable) {
				LOG.logp(Level.FINE, CLASS, METHOD, "Looking for files named '" + resourceName + "'");
			}
			
			Enumeration<URL> names = cl.getResources(resourceName);
			while (names.hasMoreElements()) {
				URL jarUrl = names.nextElement();
				if (isLoggable) {
					LOG.logp(Level.FINE, CLASS, METHOD, "inspecting: " + jarUrl);
				}
				if ("jar".equals(jarUrl.getProtocol())) {
					String path = jarUrl.getFile();
					String filename = path.substring(0, path.length() - resourceName.length() - 2);
					if (filename.startsWith("file:")) {
						filename = filename.substring(5);
					}
					JarFile jar = new JarFile(filename);
					for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						addMatchingNameOnce("", baseName, bundleNames, baseFileName, name);
					}
					jar.close();
				} else {
					File dir = new File(jarUrl.getFile());
					dir = dir.getParentFile();
					if (dir.isDirectory()) {
						for (String name : dir.list()) {
							addMatchingNameOnce(resourcePath, baseName, bundleNames, baseFileName, name);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(bundleNames, new Comparator<String>() {

			public int compare(String o1, String o2) {
				int rc = 0;
				if (baseName.equals(o1)) {
					rc = -1;
				} else if (baseName.equals(o2)) {
					rc = 1;
				} else {
					rc = o1.compareTo(o2);
				}
				return rc;
			}

		});
		if (isLoggable) {
			LOG.logp(Level.FINE, CLASS, METHOD, "Combine ResourceBundles named: " + bundleNames);
		}
		return bundleNames;
	}*/
	
	/**
	 * Return a Set with the real base-names of the multiple properties based resource bundles that
	 * contribute to the full set of resources.
	 * 
	 * @param baseName
	 *          the base-name that must be part of the properties file names.
	 * @return a List with the real base-names.
	 */
	private List<String> findBaseNames() {
		boolean isLoggable = LOG.isTraceEnabled();
		
		List<String> bundleNames = new ArrayList<String>();
		
		String resourceName = getMessageBaseNameFull();
		if (isLoggable) {
			LOG.trace("Looking for files named '" + resourceName + "'");
		}
		ResourceBundle bundle = ResourceBundle.getBundle(resourceName, getLocale());		
		Enumeration<String> keys = bundle.getKeys();
		String key = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			String msgPath = bundle.getString(key);
			if (msgPath != null && !"".equals(msgPath.trim())) {
				bundleNames.add(msgPath.trim());
			}
		}//WHILE
		
		Collections.sort(bundleNames, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int rc = 0;
				if (baseName.equals(o1)) {
					rc = -1;
				} else if (baseName.equals(o2)) {
					rc = 1;
				} else {
					rc = o1.compareTo(o2);
				}
				return rc;
			}
		});
		
		if (isLoggable) {
			LOG.trace("Combine ResourceBundles named: " + bundleNames);
		}
		return bundleNames;
	}

	private String getMessageBaseNameFull() {
		return this.packageName + getMessageBaseName();
	}
	
	private String getMessageBaseName() {
		return this.baseName;// + ".properties";
	}
	
	/*
	private void addMessagesFiles(File[] fileList, String baseFileName, List<String> bundleNames) throws IOException {
		if (fileList != null && fileList.length > 0) {
			for (File file : fileList) {
				if (file.isFile()) {
					if (file.getName().endsWith(baseFileName)) {
						bundleNames.add(file.getCanonicalPath());
					}
				} else {
					addMessagesFiles(file.listFiles(), baseFileName, bundleNames);
				}
			}//FOR
		}//IF
	}
	*/

	/*
	private String getResourcePath() {
		String result = "";
		if (packageName != null) {
			result = packageName.replaceAll("\\.", "/") + "/";
		}
		return result;
	}
	*/
	
	/*
	private void addMatchingNameOnce(String resourcePath, String baseName, List<String> bundleNames,
			String baseFileName, String name) {
		int prefixed = name.indexOf(baseName);
		if (prefixed > -1 && name.endsWith(baseFileName)) {
			String toAdd = resourcePath + name.substring(0, prefixed) + baseName;
			if (!bundleNames.contains(toAdd)) {
				bundleNames.add(toAdd);
			}
		}
	}
	*/

}
