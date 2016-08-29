package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe responsavel por verificar os models que NAO tenham equals e hashcode e gera-los.
 * NAO regera os metodos equals e hashcode.
 * 
 * Faz algumas verificacoes, mas nada refinado. Para analise refinada usar EntityAnalyzerMain.
 * 
 * @author rcorvalao
 *
 */
@SuppressWarnings({"rawtypes", "unchecked", "PMD"})
public class GeradorEqualsHashCodeMain {
	
	private static final Log LOG = LogFactory.getLog(GeradorEqualsHashCodeMain.class);

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws SecurityException, ClassNotFoundException, IOException {
		String PACOTE_MODEL = "br.gov.mec.aghu.model";
		
		String dirPath = GeradorEqualsHashCodeMain.class.getResource("").getPath();
		int index = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/util/");
		dirPath = dirPath.substring(0, index) + "src/main/java/br/gov/mec/aghu/model/";

		List<Class> listaSemEquals = new ArrayList<Class>();
		List<Class> listaSemHashCode = new ArrayList<Class>();
		List<Class> listaSemEqualsHashCode = new ArrayList<Class>();
		List<Class> listaClassesJournals = new ArrayList<Class>();
		List<Class> listaComEqualsHashCode = new ArrayList<Class>();
		
		List<Class> listaClassSemId = new ArrayList<Class>();
		List<String> tabelas = getListaTabelasSemPk();
		
		Class[] listaClasses = getClasses(PACOTE_MODEL);
		LOG.info("Total: " + listaClasses.length);
		
		int classesAnalisadasCount = 0;
		for (Class<?> clazz : listaClasses) {
			String className = clazz.getName();
			
			//!className.endsWith("Id") &&
			if (!className.endsWith("$Fields") 
					&& !isJournal(clazz) && !isExceptionCode(clazz)
					&& !clazz.isEnum()
					) {
				
				classesAnalisadasCount = classesAnalisadasCount + 1;
				boolean possuiEquals = false;
				boolean possuiHashCode = false;
				
				Method[] listaMetodos = clazz.getDeclaredMethods();
				for (Method method : listaMetodos) {
					if (method.getName().startsWith("get")) {
						continue;
					} else {
						if (method.getName().equalsIgnoreCase("equals")) {
							possuiEquals = true;
						}
						if (method.getName().equalsIgnoreCase("hashCode")) {
							possuiHashCode = true;
						}
						if (possuiEquals && possuiHashCode) {
							break;
						}
					}
				}
				
				if (tabelas.contains(getTableName(clazz))) {
					listaClassSemId.add(clazz);
					continue;
				}
				
//				if (className.endsWith("Id")) {
//					classesAnalisadasCount = classesAnalisadasCount + 1;	
//					listaClassesIds.add(clazz);
//				}

				if (!possuiEquals && possuiHashCode) {
					listaSemEquals.add(clazz);
				}
				if (possuiEquals && !possuiHashCode) {
					listaSemHashCode.add(clazz);
				}
				if (!possuiEquals && !possuiHashCode) {
					listaSemEqualsHashCode.add(clazz);
				}
				if (possuiEquals && possuiHashCode) {
					listaComEqualsHashCode.add(clazz);
				}
			} //IF className
			else {
				if (isJournal(clazz) && (clazz.getSuperclass() == null || !"BaseJournal".equalsIgnoreCase(clazz.getSuperclass().getSimpleName()))) {
					classesAnalisadasCount = classesAnalisadasCount + 1;
					
					listaClassesJournals.add(clazz);
				}
			}
		} //FOR clazz

		LOG.info("classesAnalisadasCount   : " + classesAnalisadasCount);
		LOG.info("listaSemEquals           : " + listaSemEquals.size());
		showLista(listaSemEquals);
		LOG.info("listaSemHashCode         : " + listaSemHashCode.size());
		showLista(listaSemHashCode);
		LOG.info("listaClassesJournals     : " + listaClassesJournals.size());
		showLista(listaClassesJournals);
		LOG.info("listaClassSemId          : " + listaClassSemId.size());
		//showSqlListaSemId(listaClassSemId);
		//showLista(listaClassSemId);
		LOG.info("listaSemEqualsHashCode   : " + listaSemEqualsHashCode.size());
		showLista(listaSemEqualsHashCode);
		
		LOG.info("listaComEqualsHashCode   : " + listaComEqualsHashCode.size());
		
		LOG.info("\n\n ==================== \n\n");
		
		gerarEqualsHashcodeListas(listaSemEqualsHashCode);
		
		//apagarEqualsHashcodeListas(listaComEqualsHashCode);
		
	}// METODO MAIN
	
	//########################################################
	
	protected static void showSqlListaSemId(List<Class> lista) {
		String sql = "select table_name, num_rows, sample_size from user_tables where table_name in ({0}) order by table_name";
		
		StringBuilder in = new StringBuilder();
		String table;
		for (Class clazz : lista) {
			table = getTableName(clazz);
			//log.info("TABLE: " + table);
			in.append('\'').append(table.toUpperCase()).append("', ");
		}
		
		String strIn = in.toString();
		if (strIn.length() > 0) {
			int index = in.lastIndexOf(", ");
			strIn = strIn.substring(0, index);
		}
		
		LOG.info("SQL: " + MessageFormat.format(sql, strIn));
	}

	public static String getEqualsHashcodeString(List<String> listaFields, String fieldNameId, String className) {
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
	
	/**
	 * 0 - getCodigo() 1 - AghAplicacoes
	 * 
	 * @return
	 */
	public static String getEqualsHashcodeString(String fieldNameId, String className) {
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
	
	
	//########################################################
	
	private static boolean isExceptionCode(Class<?> clazz) {
		return clazz.getSimpleName().endsWith("ExceptionCode");
	}

	private static boolean isJournal(Class<?> clazz) {
		boolean journal = false;
		
		if (clazz.getSuperclass() != null 
				&& clazz.getSuperclass().getSimpleName().equalsIgnoreCase("BaseJournal")) {
			journal = true;
		}
		
		if (clazz.getSimpleName().endsWith("Jn") || clazz.getSimpleName().endsWith("JN")) {
			journal = true;			
		}
		
		return journal;
	}

	private static void showLista(List<Class> lista) {
		for (Class clazz : lista) {
			String id = getFieldNameId(clazz);
			LOG.info(
					"     Classe: " + clazz.getSimpleName() 
					+ " id: " + (id != null ? id : ">>>> SEM ID <<<< " + getRazaoSemId(clazz))
					+ " - TABLE: " + getTableName(clazz)
			);
		}
	}
	
	
	
	protected static void apagarEqualsHashcodeListas(List<Class> lista) throws IOException {
		if (lista != null && !lista.isEmpty()) {
			List<Class> temp = lista;
			
			String dirPath = GeradorFields.class.getResource("").getPath();
			int i = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/util/");
			dirPath = dirPath.substring(0, i) + "src/main/java/br/gov/mec/aghu/model/";
			
			List<String> classesProblemas = Arrays.asList("CseAcoes", "CseUsuario", "EpeCuidadoDiagnosticoId", 
				"FatExclusaoCriticaBkpId", "FatGrupoProcedHospitalaJnId",
				"FatProcedTratamentoJnId", "FatResultadoExameSiscolosId", "McoExameFisicoRns", "MpmAltaTrgSinalVitaisId",
				"MpmPrescricaoMdtosHist", "MpmPrescricaoMedicasHist", "MpmSolicitacaoConsultHist", "MptProfCredAssinatLaudoId",
				"SceMovimentoMaterial", "ScoAcoesPontoParada", "VAelHrGradeDispTr", "VAelHrGradeDispTrId", "VFatAssociacaoProcedimentoId",
				"VMpaUsoOrdMdtosId", "VMpmOcorrenciaPrcr"
			);
			
			int count = 0;
			for (Class clazz : temp) {
				if (classesProblemas.contains(clazz.getSimpleName())) {
					if (apagarEqualsHashcode(clazz, dirPath)) {
						count++;
					}
				}
			}//FOR
			
			LOG.info("total de ids apagados: " + count);
		}//IF
	}
		
	protected static void gerarEqualsHashcodeListas(List<Class> lista) throws IOException {
		if (lista != null && !lista.isEmpty()) {
			List<Class> temp = lista;//.subList(0, 3);
			String message;
			
			String dirPath = GeradorFields.class.getResource("").getPath();
			int i = dirPath.lastIndexOf("target/classes/br/gov/mec/aghu/util/");
			dirPath = dirPath.substring(0, i) + "src/main/java/br/gov/mec/aghu/model/";
			
			for (Class clazz : temp) {
//				try {
					String id = getFieldNameId(clazz);
					
					if (clazz.getName().endsWith("Id")) {
						LOG.info("     ClasseId: " + clazz.getSimpleName());
						criarEqualsHashcodeParaClassesId(clazz, dirPath);
						
					} else {
						message = "     Classe  : " + clazz.getSimpleName() 
						+ " id: " + (id != null ? id : ">>>> SEM ID <<<< " + getRazaoSemId(clazz))
						+ " - TABLE: " + getTableName(clazz);
						
						LOG.info(message);
						criarEqualsHashcode(clazz, dirPath);
						
					}
//				} catch (IOException e) {
//					e.printStackTrace();
//					throw e;
//				} finally {
//					
//				}
			}//FOR
			
		}
	}
	
	protected static void criarEqualsHashcodeParaClassesId(Class clazz, String dirPath) throws IOException {
		List<String> methodNameList = getMethodsComString(clazz);
		
		if (methodNameList != null && !methodNameList.isEmpty()) {
			String conteudoFields = getEqualsHashcodeString(methodNameList, null, clazz.getSimpleName());
	
			File file = new File(dirPath, clazz.getSimpleName() + ".java");
			//File fileOut = new File("/opt/model/", clazz.getSimpleName() + ".java");
	
			if (file.exists()) {
				StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
				
				String importStr =
					"\nimport org.apache.commons.lang3.builder.EqualsBuilder;\n" +
					"import org.apache.commons.lang3.builder.HashCodeBuilder;\n";
				int indexLastImport = conteudo.lastIndexOf("import"); 
				conteudo.insert(indexLastImport - 1, importStr);
				
				int index = conteudo.lastIndexOf("}");
				conteudo.insert(index - 1, conteudoFields);
	
				FileUtils.writeStringToFile(file, conteudo.toString());
				//FileUtils.writeStringToFile(fileOut, conteudo.toString());
			}// file exist
		}// strMethodName

	}
	
	protected static boolean apagarEqualsHashcode(Class clazz, String dirPath) throws IOException {
		boolean returnValeu = false;
		File file = new File(dirPath, clazz.getSimpleName() + ".java");
		//File fileOut = new File("/opt/model/", clazz.getSimpleName() + ".java");

		if (file.exists()) {
			StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
			
			String padraoBusca = "// ##### GeradorEqualsHashCodeMain #####";
			
			int indexPadraoInicioEqualsHash = conteudo.indexOf(padraoBusca);
			int indexPadraoFinalEqualsHash = conteudo.lastIndexOf(padraoBusca);
			
			if (indexPadraoInicioEqualsHash > 0) {
				LOG.info(
						"     Classe: " + clazz.getSimpleName() 
						//+ " id: " + (id != null ? id : ">>>> SEM ID <<<< " + getRazaoSemId(clazz))
						+ " indexPadraoInicioEqualsHash: " + indexPadraoInicioEqualsHash 
						+ " - TABLE: " + getTableName(clazz)
				);
				
				indexPadraoInicioEqualsHash = indexPadraoInicioEqualsHash - 1;
				indexPadraoFinalEqualsHash = indexPadraoFinalEqualsHash + padraoBusca.length() + 1;
				conteudo.replace(indexPadraoInicioEqualsHash, indexPadraoFinalEqualsHash, " ");
				
				
				String importHashCodeBuilder = "import org.apache.commons.lang3.builder.HashCodeBuilder;";
				String importEqualsBuilder = "import org.apache.commons.lang3.builder.EqualsBuilder;";
				int init;
				int finish;
				init = conteudo.indexOf(importHashCodeBuilder);
				if (init > 0) {
					finish = init + importHashCodeBuilder.length() + 1;
					init = init - 1;
					conteudo.replace(init, finish, " ");
				}
				init = conteudo.indexOf(importEqualsBuilder);
				if (init > 0) {
					finish = init + importEqualsBuilder.length() + 1;
					init = init - 1;
					conteudo.replace(init, finish, " ");
				}				
				init = conteudo.indexOf(importHashCodeBuilder);
				if (init > 0) {
					finish = init + importHashCodeBuilder.length() + 1;
					init = init - 1;
					conteudo.replace(init, finish, " ");
				}
				init = conteudo.indexOf(importEqualsBuilder);
				if (init > 0) {
					finish = init + importEqualsBuilder.length() + 1;
					init = init - 1;
					conteudo.replace(init, finish, " ");
				}				
				
				returnValeu = true;
				FileUtils.writeStringToFile(file, conteudo.toString());
				//FileUtils.writeStringToFile(fileOut, conteudo.toString());
				
			}
			
		}// file exist
		
		return returnValeu;
	}
	
	protected static void criarEqualsHashcode(Class clazz, String dirPath) throws IOException {
		String strMethodName = getStringMethodId(clazz);
		
		if (strMethodName != null) {
			String conteudoFields = getEqualsHashcodeString(strMethodName, clazz.getSimpleName());
	
			File file = new File(dirPath, clazz.getSimpleName() + ".java");
			//File fileOut = new File("/opt/model/", clazz.getSimpleName() + ".java");
	
			if (file.exists()) {
				StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
	
				int index = conteudo.lastIndexOf("}");
	
				conteudo.insert(index - 1, conteudoFields);
	
				FileUtils.writeStringToFile(file, conteudo.toString());
				//FileUtils.writeStringToFile(fileOut, conteudo.toString());
			}// file exist
		}// strMethodName

	}
	
	private static String getTableName(Class clazz) {
		return (clazz.getAnnotation(Table.class) != null 
				? ((Table)clazz.getAnnotation(Table.class)).name().toLowerCase() 
						: "table not fount");
	}
	
	private static String getRazaoSemId(Class clazz) {
		String returnValue = "Nao encontrou razao para estar sem ID.";
		
		if (clazz.getSuperclass() != null) {
			returnValue = "super class: " + clazz.getSuperclass();
		}
		
		if (!clazz.isAnnotationPresent(Entity.class)) {
			returnValue = "Nao eh uma Entity.";			
		}
		
		return returnValue;
	}
	
	// ####################################################
	
	
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
	
	//###########################################################
	
	/**
	 * Retrona o nome do atributo que eh o identificador da entidade.<br>
	 * 
	 * @return
	 */
	private static String getFieldNameId(Class clazz) {
		String fieldNameId = null;
		
		Field f = getFieldId(clazz);
		if (f != null) {
			fieldNameId = f.getName();
		}
		
		return fieldNameId;
	}
	
	/**
	 * Busca entre os Fields da entidade aquele que possui a anotacao <i>javax.persistence.Id</i>.<br>
	 * 
	 * @return
	 */
	private static Field getFieldId(Class clazz) {
		Field returnValeu = null;
		
		Method methodId = getMethodId(clazz);
		
		if (methodId != null) {
			
			
			String fieldName = methodId.getName().substring(3);
			//fieldName = fieldName.toLowerCase();
			
			String first = fieldName.substring(0, 1);
			fieldName = fieldName.substring(1);
			
			StringBuffer sb = new StringBuffer();
			sb.append(first.toLowerCase());
			sb.append(fieldName);
			
			fieldName = sb.toString();
			
			try {
				returnValeu = clazz.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				LOG.info("Problemas para encontrar Field: " + fieldName + " na classe " + clazz.getSimpleName());
			} catch (NoSuchFieldException e) {
				LOG.info("Field NAO encontrado: " + fieldName + " na classe " + clazz.getSimpleName());
			}
		}
		
		return returnValeu;
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
	
	//##########################################################
	
	private static List<String> getListaTabelasSemPk() {
		TabelasSemPkEnum[] t = TabelasSemPkEnum.values();
		
		List<String> lista = new LinkedList<String>();
		
		for (TabelasSemPkEnum tabelasSemPkEnum : t) {
			lista.add(tabelasSemPkEnum.name().toLowerCase());
		}
		
		return lista;
	}

}
