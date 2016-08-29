package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"rawtypes", "unchecked", "unused", "PMD"})
public class EntityAnalyzerMain {
	
	private static final Log LOG = LogFactory.getLog(EntityAnalyzerMain.class);
	
	private static final List<String> TABELAS_SEM_PK = getListaTabelasSemPk();
	
	private static final String DIR_PATH_SRC_MODEL = getDirPathSourceModel();
	
	private static final String BASE_JOURNAL_CLASS_NAME = "BaseJournal";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String PACOTE_MODEL = "br.gov.mec.aghu.model";
		
		// Pega todas as classes do pacote definido.
		Class[] listaClasses = getClasses(PACOTE_MODEL);
		if (listaClasses == null) {
			LOG.info("Nao encontrou classes.");
			return;
		}
		LOG.info("Total: " + listaClasses.length);
		
		// Filtra apenas as classe que representam entidades.
		List<Class> listaParaAnalise = new LinkedList<Class>();
		int classesAnalisadasCount = 0;
		for (Class<?> clazz : listaClasses) {
			String className = clazz.getName();
			//!className.endsWith("Id") && !isJournal(clazz) &&
			if (!className.endsWith("$Fields") && !isExceptionCode(clazz) && !clazz.isEnum()) {
				classesAnalisadasCount = classesAnalisadasCount + 1;
				listaParaAnalise.add(clazz);
			} //IF className
		} //FOR clazz
		LOG.info("classesAnalisadasCount   : " + classesAnalisadasCount);
		
		// Ordena a lista de classes para analise.
		Collections.sort(listaParaAnalise, new Comparator<Class>() {
			@Override
			public int compare(Class c1, Class c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
		
		//showLista(listaParaAnalise);
		
		// Separa as classes em Journal, Ids, outras.
		List<Class> listaJournals = new LinkedList<Class>();
		List<Class> listaIdCompostos = new LinkedList<Class>();
		List<Class> listaEntitiesSemId = new LinkedList<Class>();
		List<Class> listaEntities = new LinkedList<Class>();
		List<Class> listaSemNomeNaoEntidade = new LinkedList<Class>();
		
		for (Class clazz : listaParaAnalise) {
			String className = clazz.getName();
			
			
			if (!isClassId(clazz) && (
					clazz.getName().contains("$") 
					|| !clazz.isAnnotationPresent(Entity.class)
					|| !clazz.isAnnotationPresent(Table.class))) {
				listaSemNomeNaoEntidade.add(clazz);
			} else if (isTabelaSemPk(clazz)) {
				listaEntitiesSemId.add(clazz);
			} else if (isClassId(clazz)) {
				listaIdCompostos.add(clazz);
			} else if (isJournal(clazz)) {
				listaJournals.add(clazz);
			} else {
				listaEntities.add(clazz);
			}
		}//FOR listaParaAnalise
		
		int somatorioListas = listaIdCompostos.size() + listaJournals.size() 
			+ listaEntities.size() + listaEntitiesSemId.size()
			+ listaSemNomeNaoEntidade.size();
		LOG.info("somatorioListas: " + somatorioListas);
		
		
		LOG.info("listaSemNomeNaoEntidade  : " + listaSemNomeNaoEntidade.size());
		LOG.info("listaEntitiesSemId       : " + listaEntitiesSemId.size());
		LOG.info("listaIdCompostos         : " + listaIdCompostos.size());
		LOG.info("listaJournals            : " + listaJournals.size());
		LOG.info("listaEntities            : " + listaEntities.size());
		
		//showLista(listaSemNomeNaoEntidade, "listaSemNomeNaoEntidade");
		//showLista(listaEntitiesSemId, "listaEntitiesSemId");
		//showLista(listaIdCompostos, "listaIdCompostos");
		//showLista(listaJournals);
		//showLista(listaEntities);
		
		analizarIdComposto(listaIdCompostos, "listaIdCompostos", false);
		analizarEqualsHashcode(listaIdCompostos, "listaIdCompostos", false);
		analizarJournals(listaJournals, "listaJournals", false);
		analizarEqualsHashcode(listaEntities, "listaEntities", false);
		
	}// MAIN
	
	//###################################################
	
	

	protected static void analizarIdComposto(List<Class> lista, String titulo, boolean gerarCorrecoes) {
		int countIdCompostoSemImplements = 0;
		int gerados = 0;
		
		for (Class clazz : lista) {
			List<Class> listaInterfaces = Arrays.asList(clazz.getInterfaces());
			
			List<String> listaString = (List<String>)CollectionUtils.collect(listaInterfaces, new Transformer() {				
				@Override
				public Object transform(Object obj) {
					Class c1 = (Class) obj;
					return c1.getSimpleName();
				}
			});
			
			if (!listaString.contains("EntityCompositeId")) {
				countIdCompostoSemImplements = countIdCompostoSemImplements + 1;
				
				LOG.info(titulo + " nao implementa EntityCompositeId: ");
				showInfoClass(clazz);
				
				if (gerarCorrecoes) {
					int status = gerarIdComposto(clazz);
					gerados = gerados + status;
				}
			}//EntityCompositeId
			
		}//FOR
		
		LOG.info("analizarIdComposto " + titulo + " countIdCompostoSemImplements: " + countIdCompostoSemImplements);
		LOG.info("analizarIdComposto " + titulo + " gerados: " + gerados);
		LOG.info("analizarIdComposto " + titulo + " size: " + lista.size());
	}
	

	

	/**
	 * Journals que nao sao filhas de BaseJournal.
	 * 
	 */
	protected static void analizarJournals(List<Class> lista, String titulo, boolean gerarCorrecoes) {
		String dirPath = DIR_PATH_SRC_MODEL;
		
		int countJournalSemPai = 0;
		int countJournalSemId = 0;
		int geradosSemPai = 0;
		int geradosSemId = 0;		
		for (Class clazz : lista) {
			Class superClass = clazz.getSuperclass();
			if (!BASE_JOURNAL_CLASS_NAME.equalsIgnoreCase(superClass.getSimpleName())) {
				LOG.info(titulo + "SEM superclass: ");
				showInfoClass(clazz);
				countJournalSemPai = countJournalSemPai + 1;
				
				if (gerarCorrecoes) {
					int status = gerarJournal(clazz);
					geradosSemPai = geradosSemPai + status;
				}
				
			}//IF base journal
			
			Method metodoId = getMethodId(clazz);
			if (metodoId == null) {
				LOG.info(titulo + "SEM ID: ");
				showInfoClass(clazz);
				
				countJournalSemId = countJournalSemId + 1;

				if (gerarCorrecoes) {
					int status = gerarIdJournal(clazz);
					geradosSemId = geradosSemId + status;
				}
			}
			
		}//FOR
		
		LOG.info("analizarJournals " + titulo + " countJournalSemSuperClass: " + countJournalSemPai);
		LOG.info("analizarJournals " + titulo + " countJournalSemId: " + countJournalSemId);
		LOG.info("analizarJournals " + titulo + " geradosSemPai: " + geradosSemPai);
		LOG.info("analizarJournals " + titulo + " geradosSemId: " + geradosSemId);
		LOG.info("analizarJournals " + titulo + " size: " + lista.size());
	}

	

	private static int gerarJournal(Class clazz) {
		int returnCount = 0;
		String dirPath = DIR_PATH_SRC_MODEL;
		String className = clazz.getSimpleName();

		File file = new File(dirPath, className + ".java");
		//File fileOut = new File("/opt/model/", className + ".java");
		
		try {
			StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
			
			// Removendo fields.
			int indexfirst = conteudo.indexOf("private long seqJn;");
			if (indexfirst < 0) {
				indexfirst = conteudo.indexOf("private Long seqJn;");
			}
			if (indexfirst >= 0) {
				returnCount = 1;
				
				conteudo.insert(indexfirst-1, "/* ATUALIZADOR JOURNALS - PROPERTIES");
				int indexlast  = conteudo.indexOf("jnOperation;", indexfirst);
				indexlast  = conteudo.indexOf(";", indexlast);
				conteudo.insert(indexlast+1, "*/");
				
				int indexAnotacaoId = conteudo.indexOf("@Id");

				// Removendo as properties dos construtores.
				String comentarioAnalizador = "/* ATUALIZADOR JOURNALS - contrutor";
				String declaracaoConstrutor = "public " + className + "(";
				for (Constructor constructor : clazz.getDeclaredConstructors()) {
					String codigo = conteudo.substring(indexlast, indexlast+1200);
					int indexContrutor = conteudo.indexOf(declaracaoConstrutor, indexlast);
					int indexFimConstrutor = conteudo.indexOf("}", indexContrutor);
					indexlast = indexFimConstrutor;
					if (indexContrutor >= 0) {
						indexfirst = conteudo.indexOf("this.seqJn =", indexContrutor);
						if (indexfirst >= 0 && indexfirst < indexFimConstrutor && indexfirst < indexAnotacaoId) {
							conteudo.insert(indexfirst-1, comentarioAnalizador);
							indexlast  = conteudo.indexOf("jnOperation;", indexfirst);
							indexlast  = conteudo.indexOf(";", indexlast);
							conteudo.insert(indexlast+1, "*/");
							indexAnotacaoId = conteudo.indexOf("@Id");
						} else {
							indexfirst = conteudo.indexOf("this.jnUser =", indexContrutor);
							if (indexfirst >= 0 && indexfirst < indexFimConstrutor && indexfirst < indexAnotacaoId) {
								conteudo.insert(indexfirst-1, comentarioAnalizador);
								indexlast  = conteudo.indexOf("jnOperation;", indexfirst);
								indexlast  = conteudo.indexOf(";", indexlast);
								conteudo.insert(indexlast+1, "*/");
								indexAnotacaoId = conteudo.indexOf("@Id");
							}	
						}
					}
				}
				
				// Removendo getters e setters dos fields;
				indexfirst = conteudo.indexOf("@Id");
				if (indexfirst >= 0) {
					conteudo.insert(indexfirst-1, "/* ATUALIZADOR JOURNALS - Get / Set");
					indexlast  = conteudo.indexOf("public void setJnOperation", indexfirst);
					indexlast  = conteudo.indexOf("}", indexlast);
					conteudo.insert(indexlast+1, "*/");
				}
				
				// Removendo fields de queries;
				indexfirst = conteudo.indexOf("SEQ_JN(");
				if (indexfirst >= 0) {
					conteudo.insert(indexfirst-1, "/* ATUALIZADOR JOURNALS - Fields");
					indexlast  = conteudo.indexOf("JN_OPERATION(", indexfirst);
					indexlast  = conteudo.indexOf(",", indexlast);
					conteudo.insert(indexlast+1, "*/");
				}
				
				// Remover equals e hashcode.
				String anotacaoOverride = "@Override";
				String declaracaoHashCode = "public int hashCode()";
				indexfirst = conteudo.indexOf(declaracaoHashCode);
				indexfirst = indexfirst - anotacaoOverride.length() - 2;
				if (indexfirst >= 0) {
					LOG.info("apagando hashcode.");
					indexlast = conteudo.indexOf("return", indexfirst);
					indexlast = conteudo.indexOf("}", indexlast);
					conteudo.replace(indexfirst-1, indexlast+1, "");
				}
				
				String declaracaoEquals = "public boolean equals(";
				indexfirst = conteudo.indexOf(declaracaoEquals);
				indexfirst = indexfirst - anotacaoOverride.length() - 2;
				if (indexfirst >= 0) {
					LOG.info("apagando equals.");
					indexlast = conteudo.indexOf("return true;", indexfirst);
					indexlast = conteudo.indexOf("return true;", indexlast+1);
					indexlast = conteudo.indexOf("}", indexlast);
					conteudo.replace(indexfirst-1, indexlast+1, "");
				}
				
				//Ajustando heranca.
				String modificado = conteudo.toString().replaceFirst("implements java.io.Serializable", "extends BaseJournal");
				
				FileUtils.writeStringToFile(file, modificado);
			}
			
			
		} catch (IOException e) {
			LOG.error("Exceção capturada: ", e);
			returnCount = 0;
		}
		
		
		return returnCount;
	}
	
	private static int gerarEqualsHashCode(Class clazz) {
		int returnCount = 0;
		
		try {
			String dirPath = DIR_PATH_SRC_MODEL;
			String className = clazz.getSimpleName();

			File file = new File(dirPath, className + ".java");
			//File fileOut = new File("/opt/model/", className + ".java");
			
			String conteudoEqualsHashcode;
			String importStr = "// Nao precisa de import";
			if (isClassId(clazz)) {
				List<String> methodNameList = getMethodsComString(clazz);
				if (methodNameList != null && !methodNameList.isEmpty()) {
					conteudoEqualsHashcode = makeOverrideEqualsHashCode(methodNameList, clazz.getSimpleName());
					importStr =
						"\nimport org.apache.commons.lang3.builder.EqualsBuilder;\n" +
						"import org.apache.commons.lang3.builder.HashCodeBuilder;\n";
				} else {
					conteudoEqualsHashcode = "// Nao encontrou metodos para gerar equals e hashcode.";
				}
			} else {
				String methodName = getStringMethodId(clazz);
				if (methodName != null && !"".equals(methodName.trim())) {
					conteudoEqualsHashcode = makeOverrideEqualsHashCode(methodName, clazz.getSimpleName());
				} else {
					conteudoEqualsHashcode = "// Nao encontrou metodos para gerar equals e hashcode.";
				}
			}
	
			if (file.exists() && !conteudoEqualsHashcode.startsWith("// Nao")) {
				returnCount = 1;
				StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
				
				int indexLastImport = conteudo.lastIndexOf("import"); 
				conteudo.insert(indexLastImport - 1, importStr);
				
				int index = conteudo.lastIndexOf("}");
				conteudo.insert(index - 1, conteudoEqualsHashcode);
	
				FileUtils.writeStringToFile(file, conteudo.toString());
			}
			
		} catch (Throwable e) {//NOPMD
			returnCount = 0;
		}
		
		return returnCount;
	}
	
	private static int gerarIdComposto(Class clazz) {
		int returnCount = 0;
		
		try {
			String dirPath = DIR_PATH_SRC_MODEL;
			String className = clazz.getSimpleName();
			String strCompositeId = "implements EntityCompositeId";

			File file = new File(dirPath, className + ".java");
			//File fileOut = new File("/opt/model/", className + ".java");
			
			if (file.exists()) {
				StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
				String modificado;
				
				if (conteudo.indexOf("implements java.io.Serializable") > 0) {
					//Ajustando heranca.
					modificado = conteudo.toString().replaceFirst("implements java.io.Serializable", strCompositeId);
					returnCount = 1;
				} else if (conteudo.indexOf("implements Serializable") > 0) {
					//Ajustando heranca.
					modificado = conteudo.toString().replaceFirst("implements Serializable", strCompositeId);
					returnCount = 1;					
				} else {
					modificado = "nao encotrou padrao para substituicao";
					LOG.info("erro idcomposto: ");
					showInfoClass(clazz);
				}
				
				if (returnCount == 1) {
					FileUtils.writeStringToFile(file, modificado);					
				}
			}
		
		} catch (Throwable e) {//NOPMD
			returnCount = 0;
		}
		
		return returnCount;
	}
	
	private static int gerarIdJournal(Class clazz) {
		int returnCount = 0;
		String dirPath = DIR_PATH_SRC_MODEL;
		String className = clazz.getSimpleName();

		File file = new File(dirPath, className + ".java");
		//File fileOut = new File("/opt/model/", className + ".java");
		
		try {
			StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
			
			int indexfirst = conteudo.indexOf("/* ATUALIZADOR JOURNALS - Get / Set");
//			if (indexfirst < 0) {
//				indexfirst = conteudo.indexOf("private Long seqJn;");
//			}
			if (indexfirst >= 0) {
				String strMetodoId = makeOverrideIdJournal(clazz);
				
				if (StringUtils.isNotBlank(strMetodoId)) {
					returnCount = 1;
					
					conteudo.insert(indexfirst-1, strMetodoId);
					
					
					FileUtils.writeStringToFile(file, conteudo.toString());
				}
			}
			
			
		} catch (IOException e) {
			LOG.error("Exceção capturada: ", e);
			returnCount = 0;
		}
		
		return returnCount;
	}

	protected static void analizarEqualsHashcode(List<Class> lista, String titulo, boolean gerarCorrecoes) {
		int countEqualsHash = 0;
		
		int gerados = 0;
		for (Class entityClass : lista) {
			boolean hash = hasHashCode(entityClass);
			boolean equal = hasEquals(entityClass);
			
			if (!hash && !equal) {
				LOG.info(titulo + "equals hashcode ");
				showInfoClass(entityClass);
				countEqualsHash = countEqualsHash + 1;
				
				if (gerarCorrecoes) {
					int status = gerarEqualsHashCode(entityClass);
					gerados = gerados + status;
				}
				
			} else if (!hash && equal) {
				LOG.info(titulo + "equals ");
				showInfoClass(entityClass);
				countEqualsHash = countEqualsHash + 1;				
			} else if (hash && !equal) {
				LOG.info(titulo + "hashCode ");
				showInfoClass(entityClass);
				countEqualsHash = countEqualsHash + 1;
			}
			
		}//FOR listaEntities
		
		LOG.info("analizarEqualsHashcode " + titulo + " countEqualsHash: " + countEqualsHash);
		LOG.info("analizarEqualsHashcode " + titulo + " gerados: " + gerados);
		LOG.info("analizarEqualsHashcode " + titulo + " size: " + lista.size());		
	}
	
	
	//
	//###################################################
	


	private static void showLista(List<Class> lista, String titulo) {
		LOG.info("==> " + titulo + " - Lista Tamanho : " + lista.size());
		for (Class clazz : lista) {
			showInfoClass(clazz);
		}
	}
	
	private static void showInfoClass(Class clazz) {
		LOG.info(
				"     Classe: " + clazz.getSimpleName()
				+ " - TABLE: " + getTableName(clazz)
		);
	}
	
	/**
	 * Busca entre os Methods da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * @return
	 */
	private static Method getMethodId(Class clazz) {
		Method methodId = null;
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get")
					&& (method.isAnnotationPresent(javax.persistence.Id.class) || method
							.isAnnotationPresent(javax.persistence.EmbeddedId.class))) {
				methodId = method;
				break;
			}
		}

		return methodId;
	}
	
	private static List<String> getMethodsComString(Class clazz) {
		List<String> methodList = new LinkedList<String>();
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				methodList.add(method.getName());
			}
		}
		
		return methodList;
	}
	
	private static String getStringMethodId(Class clazz) {
		String nome = null;
		
		Method m = getMethodId(clazz);
		if (m != null) {
			nome = m.getName() + "()";
		}
		
		return nome;
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
	private static Class[] getClasses(String packageName) {
		try {
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
	
	//###################################################
	
	private static boolean isJournal(Class<?> clazz) {
		boolean journal = false;
		
		if (clazz.getSuperclass() != null 
				&& clazz.getSuperclass().getSimpleName().equalsIgnoreCase(BASE_JOURNAL_CLASS_NAME)) {
			journal = true;
		}
		
		if (clazz.getSimpleName().endsWith("Jn") || clazz.getSimpleName().endsWith("JN")) {
			journal = true;			
		}
		
		return journal;
	}
	
	private static boolean isExceptionCode(Class<?> clazz) {
		return clazz.getSimpleName().endsWith("ExceptionCode");
	}
	
	/**
	 * Verifica se existe apenas o field id na entidade.
	 * @param clazz
	 * @return
	 */
	private static boolean isOnlyFieldId(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> listFields = Arrays.asList(fields);
		
		if (listFields.size() < 3) {
			boolean serial = false;
			boolean id = false;
			for (Field field : listFields) {
				if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
					serial = true;
				}
				if ("id".equalsIgnoreCase(field.getName())) {
					id = true;
				}
			}
			return (serial && id);
		}
		
		return false;
	}
	
	private static boolean hasEquals(Class<?> clazz) {
		boolean possuiEquals = false;
		
		Method[] listaMetodos = clazz.getDeclaredMethods();
		for (Method method : listaMetodos) {
			if (method.getName().startsWith("get")) {
				continue;
			} else {
				if (method.getName().equalsIgnoreCase("equals")) {
					possuiEquals = true;
					break;
				}
			}
		}//FOR
		
		return (possuiEquals);
	}
	
	private static boolean hasHashCode(Class<?> clazz) {
		boolean possuiHashCode = false;
		
		Method[] listaMetodos = clazz.getDeclaredMethods();
		for (Method method : listaMetodos) {
			if (method.getName().startsWith("get")) {
				continue;
			} else {
				if (method.getName().equalsIgnoreCase("hashCode")) {
					possuiHashCode = true;
					break;
				}
			}
		}//FOR
		
		return (possuiHashCode);
	}
	
	private static boolean isTabelaSemPk(Class clazz) {
		return (
				//TABELAS_SEM_PK.contains(getTableName(clazz))
				//||
				isOnlyFieldId(clazz)
				);
	}
	
	private static boolean isClassId(Class clazz) {
		return clazz.getName().endsWith("Id") 
			|| clazz.getName().endsWith("ID") 
			;
	}

	//###################################################
	
	
	//###################################################
	
	private static String getTableName(Class clazz) {
		return (clazz.getAnnotation(Table.class) != null 
				? ((Table)clazz.getAnnotation(Table.class)).name().toLowerCase() 
						: "table not fount");
	}
	
	private static List<String> getListaTabelasSemPk() {
		TabelasSemPkEnum[] t = TabelasSemPkEnum.values();
		
		List<String> lista = new LinkedList<String>();
		
		for (TabelasSemPkEnum tabelasSemPkEnum : t) {
			lista.add(tabelasSemPkEnum.name().toLowerCase());
		}
		
		return lista;
	}
	
	private static String getDirPathSourceModel() {
		String dirPath = GeradorEqualsHashCodeMain.class.getResource("").getPath();
		int index = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/util/");
		dirPath = dirPath.substring(0, index) + "src/main/java/br/gov/mec/aghu/model/";
		return dirPath;
	}
	
	//###################################################
	
	private static String makeOverrideIdJournal(Class clazz) {
		StringBuilder strBuilder = new StringBuilder(); 
		
		strBuilder.append("\n\t// ATUALIZADOR JOURNALS - ID");
		
		strBuilder.append("\n\t@Id");
		strBuilder.append("\n\t@Column(name = \"SEQ_JN\", unique = true, nullable = false)");
		strBuilder.append("\n\t//@GeneratedValue(strategy = GenerationType.AUTO, generator = \"\")");
		strBuilder.append("\n\t@Override");
		strBuilder.append("\n\tpublic Integer getSeqJn() {");
		strBuilder.append("\n\t\treturn super.getSeqJn();");
		strBuilder.append("\n\t}");
		
		strBuilder.append("\n\t// ATUALIZADOR JOURNALS - ID");
		strBuilder.append("\n\t");
		
		return strBuilder.toString();
	}
	
	/**
	 * gera equals e hashcode para classes, nao pode ter equal e hashcode.
	 * 
	 * @return
	 */
	private static String makeOverrideEqualsHashCode(String fieldNameId, String className) {
		StringBuffer sb = new StringBuffer(680);
		sb.append("\n\n\t// ##### GeradorEqualsHashCodeMain #####");

		sb.append("\n\t");
		sb.append("@Override\n\tpublic int hashCode() {\n\t\tfinal int prime = 31;\n\t\tint result = 1;");
		sb.append("\n\t\tresult = prime * result + ((").append(fieldNameId)
				.append(" == null) ? 0 : ").append(fieldNameId)
				.append(".hashCode());\n\t\treturn result;");
		sb.append("\n\t}");

		sb.append("\n\t");

		sb.append("\n\t");
		sb.append("@Override\n\tpublic boolean equals(Object obj) {\n\t\tif (this == obj) {\n\t\t\treturn true;\n\t\t}");
		sb.append("\n\t\tif (obj == null) {\n\t\t\treturn false;\n\t\t}");
		sb.append("\n\t\tif (!(obj instanceof ").append(className)
				.append(")) {\n\t\t\treturn false;\n\t\t}");
		sb.append("\n\t\t").append(className).append(" other = (")
				.append(className).append(") obj;");
		sb.append("\n\t\tif (").append(fieldNameId)
				.append(" == null) {\n\t\t\tif (other.").append(fieldNameId)
				.append(" != null) {\n\t\t\t\treturn false;\n\t\t\t}");
		sb.append("\n\t\t} else if (!").append(fieldNameId)
				.append(".equals(other.").append(fieldNameId)
				.append(")) {\n\t\t\treturn false;\n\t\t}\n\t\treturn true;");
		sb.append("\n\t}");

		sb.append("\n\t// ##### GeradorEqualsHashCodeMain #####");
		sb.append('\n');

		return sb.toString();
	}
	
	private static String makeOverrideEqualsHashCode(List<String> listaFields, String className) {
		StringBuffer sb = new StringBuffer(680);
		sb.append("\n\n\t// ##### GeradorEqualsHashCodeMain #####");

		sb.append("\n\t");
		sb.append("@Override\n\tpublic int hashCode() {\n\t\tHashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();");
		for (String fieldName : listaFields) {
			sb.append("\n\t\tumHashCodeBuilder.append(this.").append(fieldName).append("());");			
		}
		sb.append("\n\t\treturn umHashCodeBuilder.toHashCode();");
		sb.append("\n\t}");

		sb.append("\n\t");

		sb.append("\n\t");
		sb.append("@Override\n\tpublic boolean equals(Object obj) {\n\t\tif (this == obj) {\n\t\t\treturn true;\n\t\t}");
		sb.append("\n\t\tif (obj == null) {\n\t\t\treturn false;\n\t\t}");
		sb.append("\n\t\tif (!(obj instanceof ").append(className)
				.append(")) {\n\t\t\treturn false;\n\t\t}");
		sb.append("\n\t\t").append(className).append(" other = (")
				.append(className).append(") obj;");
		
		
		sb.append("\n\t\tEqualsBuilder umEqualsBuilder = new EqualsBuilder();");
		for (String fieldName : listaFields) {
			sb.append("\n\t\tumEqualsBuilder.append(this.").append(fieldName).append("()");
			sb.append(", other.").append(fieldName).append("());");
		}
		sb.append("\n\t\treturn umEqualsBuilder.isEquals();");
		sb.append("\n\t}");

		sb.append("\n\t// ##### GeradorEqualsHashCodeMain #####");
		sb.append("\n\t");

		return sb.toString();
	}
	
	//###################################################
	
	
}
