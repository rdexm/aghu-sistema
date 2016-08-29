package br.gov.mec.aghu.dao.analyzer;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("PMD")
public class DaoAnalyzer {
	
	//private static final Log LOG = LogFactory.getLog(DaoAnalyzer.class);
	
	private StringBuilder logMessage = new StringBuilder();
	
	private Class[] listClasses;
	private LoadClass packageLoaded;
	
	public DaoAnalyzer(LoadClass aPackageLoaded, Class[] aClasses) {
		listClasses = aClasses;
		packageLoaded = aPackageLoaded;
	}
	
	public String getLogMessage() {
		return this.logMessage.toString();
	}
	
	public void analyse() {
		for (Class clazz : this.listClasses) {
			if (!clazz.getName().contains("$")) {
				adjust(clazz);
			}
		}
	}
	
	private boolean adjust(Class clazz) {
		boolean doneAdjust = false;
		
		try {
			String dirPath = this.packageLoaded.getDirPathSource();
			String className = clazz.getSimpleName();
			logMessage.append(className).append(": ");

			File file = new File(dirPath, className + ".java");
			//File fileOut = new File("/opt/dao/", className + ".java");
			
			if (file.exists()) {
				String conteudo = FileUtils.readFileToString(file);
				
				if (className.contains("DAO") 
						|| className.contains("Dao")
						|| className.endsWith("QueryBuilder")) {
					logMessage.append("Ajuste: ");
					conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.dao.AGHUGenericDAO;", "");
					conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.util.StringUtil;", "import br.gov.mec.aghu.core.utils.StringUtil;");
					conteudo = conteudo.replaceFirst("import br.gov.mec.dominio.DominioDiaSemana;", "import br.gov.mec.aghu.core.dominio.DominioDiaSemana;");
					conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.business.AGHUBaseBusiness.AGHUBaseBusinessExceptionCode;"
							, "import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;");
					conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.util.AghuParametrosEnum;"
							, "import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;");
					
					conteudo = conteudo.replace("this.getEntityManager().flush();", "this.flush();");
					conteudo = conteudo.replace("getEntityManager().flush();", "flush();");
					conteudo = conteudo.replace("getSession().flush()", "flush()");
					
					conteudo = conteudo.replace("import org.jboss.seam.annotations.Transactional;", "");
					
					conteudo = conteudo.replaceFirst("import br.gov.mec.dominio.DominioMimeType;", "import br.gov.mec.aghu.core.dominio.DominioMimeType;");
					
					conteudo = conteudo.replace("getSession().doWork", "this.doWork");
					conteudo = conteudo.replace("getSession().refresh", "super.refresh");
					
					// 1
					conteudo = adjustPackage(conteudo);
					// 2
					conteudo = adjustSuperClass(conteudo);
					// 3, 4
					conteudo = adjustAppException(conteudo);
					// 5
					conteudo = adjustQueryBuilder(conteudo);
					// 6, 7, 8, 9
					conteudo = adjustAghuUtilMecUtil(conteudo);
					// 10
					conteudo = adjustDateUtil(conteudo);
					// 11
					conteudo = adjustDateFormatUtil(conteudo);
					// 12
					conteudo = adjustLucene(conteudo);
					// 13
					conteudo = adjustRestrict(conteudo);
					// 14
					conteudo = adjustFonetizador(conteudo);
					// 15
					conteudo = adjustMetodosSuper(conteudo);
					// 22
					conteudo = adjustAghWork(conteudo);
					// 16
					conteudo = adjustGetAndLock(conteudo);
					// 17
					conteudo = adjustHibernateTypes(conteudo);
					// 18, 19
					conteudo = adjustFullTextEntityManager(conteudo);
					// 20
					conteudo = adjustLuceneCount(conteudo);
					// 21
					conteudo = adjustLuceneQuerySuggestion(conteudo);
					// 23
					conteudo = adjustExceptionCode(conteudo);
					// 24
					conteudo = adjustTransational(conteudo);
					// 25
					conteudo = adjustCastLongInt(conteudo);
					// 26
					conteudo = adjustLuceneQuery(conteudo);
					// 27
					conteudo = adjustMethodGetCascaService(conteudo);
					//28
					conteudo = adjustAghuDaoFactory(conteudo);
					//29
					conteudo = adjustEntityManagerUtil(conteudo);
					//30
					conteudo = adjustObterDoContexto(conteudo);
					
									
					doneAdjust = true;
				} else {
					logMessage.append("Classe nao contem DAO no nome.");
				}
				
				if (doneAdjust) {
					FileUtils.writeStringToFile(file, conteudo);
					//FileUtils.writeStringToFile(fileOut, conteudo);
				}
			} else {
				logMessage.append("Arquivo nao existe.");
			}
			
		} catch (Throwable e) {//NOPMD
			logMessage.append("Erro nao geral nao tratado.");
			doneAdjust = false;
		} finally {
			logMessage.append('\n');
		}
		
		return doneAdjust;
	}
	
	private String adjustObterDoContexto(String conteudo) {
		if (conteudo.contains("obterDoContexto")) {
			int count = StringUtils.countMatches(conteudo, "obterDoContexto");
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = conteudo.indexOf("obterDoContexto", indexInit);
				conteudo = doAdjustObterDoContexto(conteudo, indexInit);
				indexInit = indexInit + 10;
			}			
		}
		
		return conteudo;
	}

	private String doAdjustObterDoContexto(String conteudo, int indexInit) {
		int indexClassType = conteudo.indexOf("(", indexInit);
		indexClassType = indexClassType + 1;
		int indexClassTypeEnd = conteudo.indexOf(".class", indexInit);
		
		String strClassType = conteudo.substring(indexClassType, indexClassTypeEnd);
		
		int indexEndLine = conteudo.indexOf(";", indexInit);
		String strReplacement = conteudo.substring(indexInit, indexEndLine);
		
		String strNewAttribute = "a"+strClassType;
		
		conteudo = conteudo.replace(strReplacement, strNewAttribute);
		
		conteudo = insertNewAttributeIfNecessary(conteudo, strClassType, strNewAttribute);
		return conteudo;
	}

	private String adjustEntityManagerUtil(String conteudo) {
		conteudo = conteudo.replace("getEntityManager().find(", "findByPK(");
		conteudo = conteudo.replace("getEntityManager().isOpen()", "entityManagerIsOpen()");
		conteudo = conteudo.replace("getEntityManager().clear()", "entityManagerClear()");
		conteudo = conteudo.replace("getEntityManager().contains(", "contains(");
		conteudo = conteudo.replace("getEntityManager().merge(", "merge(");
		conteudo = conteudo.replace("getEntityManager().persist(", "persistir(");
		conteudo = conteudo.replace("getEntityManager().remove(", "remover(");
		conteudo = conteudo.replace("getSession().createFilter", "createFilterHibernate"); 
		
		
		conteudo = conteudo.replace("getEntityManager().lock", "lockEntity");
		conteudo = conteudo.replace("getEntityManager().joinTransaction()", "joinTransaction()");

		
		logMessage.append("29; ok ");
		return conteudo;
	}

	private String adjustAghuDaoFactory(String conteudo) {
		if (conteudo.contains("AGHUDAOFactory.getDAO")) {
			int count = StringUtils.countMatches(conteudo, "AGHUDAOFactory.getDAO");
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = conteudo.indexOf("AGHUDAOFactory.getDAO", indexInit);
				conteudo = doAdjustAghuDaoFactory(conteudo, indexInit);
				indexInit = indexInit + 10;
			}
		}
		
		return conteudo;
	}

	private String doAdjustAghuDaoFactory(String conteudo, int indexInit) {
		int indexClassType = conteudo.indexOf("(", indexInit);
		indexClassType = indexClassType + 1;
		int indexClassTypeEnd = conteudo.indexOf(".class", indexInit);
		
		String strClassType = conteudo.substring(indexClassType, indexClassTypeEnd);
		
		int indexEndLine = conteudo.indexOf(";", indexInit);
		String strReplacement = conteudo.substring(indexInit, indexEndLine);
		
		String strNewAttribute = "a"+strClassType;
		
		conteudo = conteudo.replace(strReplacement, strNewAttribute);
		
		conteudo = insertNewAttributeIfNecessary(conteudo, strClassType, strNewAttribute);
		return conteudo;
	}
	
	private String insertNewAttributeIfNecessary(String conteudo, String classType, String attributeName) {
		StringBuilder strBuilderConteudo = new StringBuilder(conteudo);
		String strInsert = "private " + classType + " " + attributeName + ";";
		
		if (strBuilderConteudo.indexOf(strInsert) == -1) {
			int indexInitClass = strBuilderConteudo.indexOf("public class");
			indexInitClass = strBuilderConteudo.indexOf("{", indexInitClass);
			strBuilderConteudo.insert(indexInitClass + 1, "\n    @Inject\n    "+strInsert);
		}
		
		return strBuilderConteudo.toString();
	}


	private String adjustMethodGetCascaService(String conteudo) {
		if (conteudo.contains("protected CascaService getCascaService() {")) {
			int indexInit = conteudo.indexOf("protected CascaService getCascaService() {");
			int indexFinish = conteudo.indexOf("}", indexInit);
			indexFinish = indexFinish + 1;
			
			String method = conteudo.substring(indexInit, indexFinish);
			
			conteudo = conteudo.replaceFirst(Pattern.quote(method), "");
		}
		
		return conteudo;
	}

	private String adjustCastLongInt(String conteudo) {
		if (conteudo.contains("public Integer")) {
			int count = StringUtils.countMatches(conteudo, "public Integer");
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = conteudo.indexOf("public Integer", indexInit);
				conteudo = doAdjustCastLongInt(conteudo, indexInit);
				indexInit = indexInit + 10;
			}
		}
		
		//25
		return conteudo;
	}
	
	private String doAdjustCastLongInt(String conteudo, int indexInit) {
		int indexFinish = conteudo.indexOf("{", indexInit);
		indexFinish = indexFinish + 1;
		String methodDeclaration = conteudo.substring(indexInit, indexFinish);
		
		int indexMethodFinish = conteudo.indexOf("return", indexFinish);
		indexMethodFinish = conteudo.indexOf(";", indexMethodFinish);
		String contentMethod = conteudo.substring(indexFinish, indexMethodFinish);
		
		if (contentMethod.contains("Count")) {
			String methodDeclarationNew = methodDeclaration.replaceFirst("Integer", "Long");
			
			conteudo = conteudo.replace(methodDeclaration, methodDeclarationNew);				
		}
		return conteudo;
	}

	private String adjustTransational(String conteudo) {
		if (conteudo.contains("@Transactional")) {
			//24
			conteudo = conteudo.replace("@Transactional", "");
		}
		
		return conteudo;
	}

	private String adjustExceptionCode(String conteudo) {
		if (conteudo.contains("NegocioExceptionCode")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.seam.business.exception.NegocioExceptionCode;"
					, "import br.gov.mec.aghu.core.exception.BusinessExceptionCode;");
			
			conteudo = conteudo.replace("NegocioExceptionCode", "BusinessExceptionCode");
			logMessage.append("23; ok ");
			
		} else {
			logMessage.append("23;nok ");
		}
		
		return conteudo;
	}

	private String adjustAghWork(String conteudo) {
		if (conteudo.contains("AghWork")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.util.AghWork;", "import br.gov.mec.aghu.dao.AghWork;");
			
			conteudo = conteudo.replace("getSession().getSessionFactory().getCurrentSession().doWork(", "this.doWork(");
			
			logMessage.append("22; ok ");
		} else {
			logMessage.append("22;nok ");			
		}
		
		if (conteudo.contains("new Work()")) {
			
			conteudo = conteudo.replace("getSession().getSessionFactory().getCurrentSession().doWork(", "this.doWork(");
			
			logMessage.append("22; ok ");
		} else {
			logMessage.append("22;nok ");			
		}
		
		return conteudo;
	}

	private String adjustLuceneQuerySuggestion(String conteudo) {
		if (conteudo.contains("executeLuceneQueryParaSuggestionBox")) {
			StringBuilder strBuilderConteudo = new StringBuilder(conteudo);
			
			this.insertLuceneIfNecessary(strBuilderConteudo);
			
			conteudo = strBuilderConteudo.toString();
			conteudo = conteudo.replace("executeLuceneQueryParaSuggestionBox", "lucene.executeLuceneQueryParaSuggestionBox");
			
			logMessage.append("21; ok ");		
		} else {
			logMessage.append("21;nok ");
		}
		
		return conteudo;
	}
	
	private String adjustLuceneQuery(String conteudo) {
		if (conteudo.contains("executeLuceneQuery(")) {
			StringBuilder strBuilderConteudo = new StringBuilder(conteudo);
			
			this.insertLuceneIfNecessary(strBuilderConteudo);
			
			conteudo = strBuilderConteudo.toString();
			conteudo = conteudo.replace("executeLuceneQuery(", "lucene.executeLuceneQuery(");
			
			logMessage.append("26; ok ");		
		} else {
			logMessage.append("26;nok ");
		}
		
		return conteudo;
	}


	private String adjustLuceneCount(String conteudo) {
		if (conteudo.contains("executeLuceneCount")) {
			StringBuilder strBuilderConteudo = new StringBuilder(conteudo);
			
			this.insertLuceneIfNecessary(strBuilderConteudo);
			
			conteudo = strBuilderConteudo.toString();
			conteudo = conteudo.replace("executeLuceneCount", "lucene.executeLuceneCount");
			
			logMessage.append("20; ok ");		
		} else {
			logMessage.append("20;nok ");
		}
		
		return conteudo;
	}

	private String adjustFullTextEntityManager(String conteudo) {
		conteudo = conteudo.replace("((FullTextEntityManager) getEntityManager()).", "");
		
		return conteudo;
	}

	private String adjustHibernateTypes(String conteudo) {
		StringBuilder strBuilderConteudo; 
		int indexLastImport;
		
		if (conteudo.contains("Hibernate.STRING")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.StringType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.STRING", "StringType.INSTANCE");
			logMessage.append("18; ok ");		
		} else {
			logMessage.append("18;nok ");
		}
		
		if (conteudo.contains("Hibernate.TIMESTAMP")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.TimestampType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.TIMESTAMP", "TimestampType.INSTANCE");
			logMessage.append("19; ok ");		
		} else {
			logMessage.append("19;nok ");			
		}
		
		if (conteudo.contains("Hibernate.DATE")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.DateType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.DATE", "DateType.INSTANCE");
		}
		
		if (conteudo.contains("Hibernate.INTEGER")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.IntegerType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.INTEGER", "IntegerType.INSTANCE");
		}
		
		if (conteudo.contains("Hibernate.LONG")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.LongType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.LONG", "LongType.INSTANCE");
		}
		
		if (conteudo.contains("Hibernate.SHORT")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.ShortType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.SHORT", "ShortType.INSTANCE");
		}
		
		if (conteudo.contains("Hibernate.BIG_DECIMAL")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.BigDecimalType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.BIG_DECIMAL", "BigDecimalType.INSTANCE");
		}
		  
		if (conteudo.contains("Hibernate.BOOLEAN")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.BooleanType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.BOOLEAN", "BooleanType.INSTANCE");
		}
		   
		if (conteudo.contains("Hibernate.BYTE")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.ByteType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.BYTE", "ByteType.INSTANCE");
		}
		   
		if (conteudo.contains("Hibernate.DOUBLE")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.DoubleType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.DOUBLE", "DoubleType.INSTANCE");
		}
		
		if (conteudo.contains("Hibernate.BIG_INTEGER")) {
			strBuilderConteudo = new StringBuilder(conteudo); 
			indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.hibernate.type.BigIntegerType;\n");
			conteudo = strBuilderConteudo.toString(); 
			conteudo = conteudo.replace("Hibernate.BIG_INTEGER", "BigIntegerType.INSTANCE");
		}
		   
		
		return conteudo;
	}

	private String adjustGetAndLock(String conteudo) {
		if (conteudo.contains("getSession().get")) {
			conteudo = conteudo.replaceFirst("import org.hibernate.LockMode;", "import org.hibernate.LockOptions;");
			conteudo = conteudo.replace("getSession().get", "getAndLock");
			conteudo = conteudo.replace("LockMode", "LockOptions");
			
			conteudo = doAdjustGetAndLockRemoverClass(conteudo);
			
			logMessage.append("16; ok ");
		} else {
			logMessage.append("16;nok ");			
		}
		
		return conteudo;
	}
	
	private String doAdjustGetAndLockRemoverClass(String str) {
		int indexInit = str.indexOf("getAndLock");
		
		int indexParen = str.indexOf("(", indexInit);
		int indexComma = str.indexOf(",", indexInit);
		indexParen = indexParen + 1;
		indexComma = indexComma + 1;
		
		String clazz = str.substring(indexParen, indexComma);
		
		str = str.replace("getAndLock(" + clazz, "getAndLock(");
		return str;
	}
	
	private String adjustMetodosSuper(String conteudo) {
		conteudo = conteudo.replace("getEntityManager().createQuery", "createQuery");
		conteudo = conteudo.replace("getSession().createQuery", "createHibernateQuery");
		conteudo = conteudo.replace("getSession().createSQLQuery", "createSQLQuery");
		conteudo = conteudo.replace("getAghuDataBase().isOracle()", "isOracle()");
		conteudo = conteudo.replace("getSession().evict", "desatachar");
		conteudo = conteudo.replace("session.evict", "desatachar");
		conteudo = conteudo.replace("getEntityManager().createNativeQuery", "createNativeQuery");
		
		if (conteudo.contains("Session session = (Session) this.getEntityManager().getDelegate();")) {
			conteudo = conteudo.replace("Session session = (Session) this.getEntityManager().getDelegate();"
					, "//Session session = (Session) this.getEntityManager().getDelegate();");
		}
		
		logMessage.append("15; ok ");
		return conteudo;
	}

	private String adjustFonetizador(String conteudo) {
		if (conteudo.contains("import br.gov.mec.seam.lucene.Fonetizador;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.seam.lucene.Fonetizador;", "import br.gov.mec.aghu.core.lucene.Fonetizador;");			
			logMessage.append("14; ok ");
		} else {
			logMessage.append("14;nok ");
		}
		return conteudo;
	}

	private String adjustRestrict(String conteudo) {
		if (conteudo.contains("@Restrict")) {
			conteudo = conteudo.replaceFirst("import org.jboss.seam.annotations.security.Restrict;", "");			
			conteudo = conteudo.replace("@Restrict", "//@Restrict");			
			logMessage.append("13; ok ");
		} else {
			logMessage.append("13;nok ");
		}
		return conteudo;
	}

	private String adjustLucene(String conteudo) {
		String returnValue = conteudo;
		String strLucene = "new BrazilianAnalyzer()";
		
		if (conteudo.contains(strLucene)) {
			StringBuilder strBuilderConteudo = new StringBuilder(conteudo); 
			
			this.insertLuceneIfNecessary(strBuilderConteudo);
			
			int  indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport org.apache.lucene.util.Version;\n");
			
			int indexWildcard = strBuilderConteudo.indexOf("private static final String WILDCARD");
			strBuilderConteudo.insert(indexWildcard - 1, "//");

			returnValue = strBuilderConteudo.toString();
			returnValue = returnValue.replace("WILDCARD", "Lucene.WILDCARD");
			returnValue = returnValue.replace("criaquery", "lucene.createQuery");
			returnValue = returnValue.replace(strLucene, "new BrazilianAnalyzer(Version.LUCENE_34)");
			
			logMessage.append("12; ok ");
		} else {
			logMessage.append("12;nok ");			
		}
		
		return returnValue;
	}

	private void insertLuceneIfNecessary(StringBuilder strBuilderConteudo) {
		if (strBuilderConteudo.indexOf("private Lucene lucene;") == -1) {
			int indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport br.gov.mec.aghu.core.search.Lucene;\nimport javax.inject.Inject;\n");
			int indexInitClass = strBuilderConteudo.indexOf("public class");
			indexInitClass = strBuilderConteudo.indexOf("{", indexInitClass);
			strBuilderConteudo.insert(indexInitClass + 1, "\n    @Inject\n    private Lucene lucene;");
		}
	}

	private String adjustSuperClass(String conteudo) {
		if (conteudo.contains("AGHUGenericDAO")) {
			conteudo = conteudo.replaceFirst("AGHUGenericDAO", "br.gov.mec.aghu.core.persistence.dao.BaseDao");			
			logMessage.append("02; ok ");
		} else {
			logMessage.append("02;nok ");
		}
		return conteudo;		
	}
	
	private String adjustPackage(String conteudo) {
		String novoValor = "br.gov.mec.aghu." + this.packageLoaded.getModuleName()  + ".dao";
		
		if (conteudo.contains("br.gov.mec.aghu.dao")) {
			conteudo = conteudo.replaceFirst("br.gov.mec.aghu.dao", novoValor);			
			logMessage.append("01; ok ");
		} else {
			logMessage.append("01;nok ");
		}
		
		if (conteudo.contains("br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.dao")) {
			conteudo = conteudo.replaceFirst("br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.dao", novoValor);			
			logMessage.append("01; ok ");
		} else {
			logMessage.append("01;nok ");
		}
		
		if (conteudo.contains("br.gov.mec.dao")) {
			conteudo = conteudo.replaceFirst("br.gov.mec.dao", novoValor);			
			logMessage.append("01; ok ");
		} else {
			logMessage.append("01;nok ");
		}
		
		
		
		
		return conteudo;
	}
	
	private String adjustAppException(String conteudo) {
		boolean importAppEx = false;
		
		if (conteudo.contains("import br.gov.mec.aghu.business.exception.AGHUNegocioException;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.business.exception.AGHUNegocioException;"
					, "import br.gov.mec.aghu.core.exception.ApplicationBusinessException;");			
			logMessage.append("03; ok ");
			importAppEx = true;
		} else {
			logMessage.append("03;nok ");
		}
		
		if (conteudo.contains("AGHUNegocioException")) {
			conteudo = conteudo.replace("AGHUNegocioException"
					, "ApplicationBusinessException");			
			logMessage.append("04; ok ");
		} else {
			logMessage.append("04;nok ");
		}
		
		if (conteudo.contains("SemRollback")) {
			conteudo = conteudo.replace("SemRollback", "");			
		}
		
		if (conteudo.contains("import br.gov.mec.aghu.casca.CascaException;")) {
			if (importAppEx) {
				conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.casca.CascaException;", "");
			} else {
				conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.casca.CascaException;"
						, "import br.gov.mec.aghu.core.exception.ApplicationBusinessException;");
			}
		}
		
		if (conteudo.contains("CascaException")) {
			conteudo = conteudo.replace("CascaException", "ApplicationBusinessException");			
		}
		

		if (conteudo.contains("import br.gov.mec.seam.business.exception.MECBaseException;")) {
				conteudo = conteudo.replaceFirst("import br.gov.mec.seam.business.exception.MECBaseException;"
						, "import br.gov.mec.aghu.core.exception.BaseException;");
		}
		
		if (conteudo.contains("MECBaseException")) {
			conteudo = conteudo.replace("MECBaseException", "BaseException");			
		}
		
		return conteudo;
	}
	
	private String adjustQueryBuilder(String conteudo) {
		if (conteudo.contains("import br.gov.mec.seam.dao.QueryBuilder;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.seam.dao.QueryBuilder;"
					, "import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;");			
			logMessage.append("05; ok ");
		} else {
			logMessage.append("05;nok ");
		}
		return conteudo;
	}
	
	private String adjustAghuUtilMecUtil(String conteudo) {
		boolean importCoreUtil = false;
		if (conteudo.contains("import br.gov.mec.aghu.util.AghuUtil;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.util.AghuUtil;"
					, "import br.gov.mec.aghu.core.commons.CoreUtil;");			
			logMessage.append("06; ok ");
			importCoreUtil = true;
		} else {
			logMessage.append("06;nok ");
		}

		if (conteudo.contains("AghuUtil")) {
			conteudo = conteudo.replace("AghuUtil"
					, "CoreUtil");			
			logMessage.append("07; ok ");
		} else {
			logMessage.append("07;nok ");
		}
		
		
		if (conteudo.contains("import br.gov.mec.util.MecUtil;")) {
			String valor = "import br.gov.mec.aghu.core.commons.CoreUtil;";
			if (importCoreUtil) {
				valor = "";
			}
			conteudo = conteudo.replaceFirst("import br.gov.mec.util.MecUtil;"
					, valor);			
			logMessage.append("08; ok ");
		} else {
			logMessage.append("08;nok ");
		}
		
		if (conteudo.contains("MecUtil")) {
			conteudo = conteudo.replace("MecUtil"
					, "CoreUtil");			
			logMessage.append("09; ok ");
		} else {
			logMessage.append("09;nok ");
		}
		
		return conteudo;		
	}
	
	private String adjustDateUtil(String conteudo) {
		if (conteudo.contains("import br.gov.mec.util.DateUtil;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.util.DateUtil;"
					, "import br.gov.mec.aghu.core.utils.DateUtil;");			
			logMessage.append("10; ok ");
		} else {
			logMessage.append("10;nok ");
		}
		
		if (conteudo.contains("br.gov.mec.util.DateUtil")) {
			conteudo = conteudo.replace("br.gov.mec.util.DateUtil", "DateUtil");			
		}
		
		if (conteudo.contains("DateUtil.obterData(")) {
			StringBuilder strBuilderConteudo = new StringBuilder(conteudo);
			int indexLastImport = strBuilderConteudo.lastIndexOf("import");
			strBuilderConteudo.insert(indexLastImport - 1, "\nimport br.gov.mec.aghu.core.utils.DateMaker;\n");
			
			conteudo = strBuilderConteudo.toString();
			conteudo = conteudo.replace("DateUtil.obterData(", "DateMaker.obterData(");
		}
		
		return conteudo;
	}
	
	private String adjustDateFormatUtil(String conteudo) {
		if (conteudo.contains("import br.gov.mec.aghu.util.DateFormatUtil;")) {
			conteudo = conteudo.replaceFirst("import br.gov.mec.aghu.util.DateFormatUtil;"
					, "import br.gov.mec.aghu.core.utils.DateFormatUtil;");			
			logMessage.append("11; ok ");
		} else {
			logMessage.append("11;nok ");
		}
		return conteudo;
	}


	
}
