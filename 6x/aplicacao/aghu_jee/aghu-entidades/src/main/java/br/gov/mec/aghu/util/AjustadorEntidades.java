package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.io.FileUtils;




@SuppressWarnings({"PMD"})
public class AjustadorEntidades {

	private static final String PATH = "/home/geraldo/workspace-kepler/aghu-entidades2/src/main/java";

	private static final File BASE_PATH = new File(PATH);

	public static void main(String[] args) throws Exception {
		System.out.println("Iniciando processo de ajuste dos pojos do sistema"); //NOPMD
		processaArquivos(BASE_PATH);
		System.out.println("ajuste dos pojos concluido com sucesso"); //NOPMD
	}

	private static void processaArquivos(File baseDir) throws Exception {
		File[] files = baseDir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				processarArquivoClasse(file);
			}
		}
	}

	private static void processarArquivoClasse(File file) throws Exception {
		Class classe = obterClass(file);
		if (verificarEntidade(classe)) {

			System.out.println("ajustando pojo " + file.getName()); //NOPMD

			String nomeMembro = null;

			Member membroChavePrimaria = obterMetodoChavePrimaria(classe);

			if (membroChavePrimaria == null) {
				membroChavePrimaria = obterCampoChavePrimaria(classe);
				if (membroChavePrimaria == null) {
					System.out.println("Classe " + file.getName() + " Não possui @id!!!"); //NOPMD
					return;
				}
			}
			nomeMembro = membroChavePrimaria.getName();

			String nomeclasse = classe.getSimpleName();

			if (nomeMembro.contains("Id")) {
				ajustarHerancaId(nomeclasse, file, membroChavePrimaria);
			} else if (nomeMembro.contains("Seq")) {
				ajustarHerancaSeq(nomeclasse, file, membroChavePrimaria);
			} else if (nomeMembro.contains("Numero")) {
				ajustarHerancaNumero(nomeclasse, file, membroChavePrimaria);
			} else if (nomeMembro.contains("Codigo")) {
				ajustarHerancaCodigo(nomeclasse, file, membroChavePrimaria);
			}
		}

	}

	
	private static void ajustarHerancaId(String nomeClasse, File file,
			Member membroID) throws IOException {

		String nomeSuperClasse = "BaseEntityId";

		StringBuilder sbConteudo = new StringBuilder(
				FileUtils.readFileToString(file));

		String classDeclaration = "public class " + nomeClasse;

		int indexInsert = sbConteudo.indexOf(classDeclaration) + 13
				+ nomeClasse.length();

		String tipoAtributoId = null;

		String chamadaGet = null;
		String chamadaSet = null;
		if (membroID instanceof Method) {
			Method methodID = (Method) membroID;
			tipoAtributoId = methodID.getReturnType().getSimpleName();
			if (!methodID.getName().equals("getId")) {
				chamadaGet = methodID.getName() + "()";
				chamadaSet = methodID.getName().replaceFirst("get", "set")+"(id)";
			}
		} else {
			Field campoId = (Field) membroID;
			tipoAtributoId = campoId.getType().getSimpleName();
			if (!campoId.getName().equals("id")) {
				chamadaGet = campoId.getName();
				chamadaSet = chamadaGet;
			}
		}

		sbConteudo.insert(indexInsert, " extends " + nomeSuperClasse + "<"
				+ tipoAtributoId + ">");

		if (chamadaGet != null) {

			String metodoGetter = "@Transient public " + tipoAtributoId
					+ " getId(){ return this." + chamadaGet + ";}";

			String metodoSetter = "public void setId(" + tipoAtributoId
					+ " id){ this." + chamadaSet + ";}";

			int indexUltimaChave = sbConteudo.lastIndexOf("}");

			sbConteudo.insert(indexUltimaChave - 1, " \n " + metodoGetter + " \n "
					+ metodoSetter);

		}

		//File fileOut = new File("/home/geraldo/model/", nomeClasse + ".java");

		FileUtils.writeStringToFile(file, sbConteudo.toString());

	}
	

	private static void ajustarHerancaSeq(String nomeClasse, File file,
			Member membroID) throws IOException {

		String nomeSuperClasse = "BaseEntitySeq";

		StringBuilder sbConteudo = new StringBuilder(
				FileUtils.readFileToString(file));

		String classDeclaration = "public class " + nomeClasse;

		int indexInsert = sbConteudo.indexOf(classDeclaration) + 13
				+ nomeClasse.length();

		String tipoAtributoId = null;

		String chamadaGet = null;
		String chamadaSet = null;
		if (membroID instanceof Method) {
			Method methodID = (Method) membroID;
			tipoAtributoId = methodID.getReturnType().getSimpleName();
			if (!methodID.getName().equals("getSeq")) {
				chamadaGet = methodID.getName() + "()";
				chamadaSet = methodID.getName().replaceFirst("get", "set")+"(seq)";
			}
		} else {
			Field campoId = (Field) membroID;
			tipoAtributoId = campoId.getType().getSimpleName();
			if (!campoId.getName().equals("seq")) {
				chamadaGet = campoId.getName();
				chamadaSet = chamadaGet;
			}
		}

		sbConteudo.insert(indexInsert, " extends " + nomeSuperClasse + "<"
				+ tipoAtributoId + ">");

		if (chamadaGet != null) {

			String metodoGetter = "@Transient public " + tipoAtributoId
					+ " getSeq(){ return this." + chamadaGet + ";}";

			String metodoSetter = "public void setSeq(" + tipoAtributoId
					+ " seq){ this." + chamadaSet + ";}";

			int indexUltimaChave = sbConteudo.lastIndexOf("}");

			sbConteudo.insert(indexUltimaChave - 1, " \n " + metodoGetter + " \n "
					+ metodoSetter);

		}

		//File fileOut = new File("/home/geraldo/model/", nomeClasse + ".java");

		FileUtils.writeStringToFile(file, sbConteudo.toString());

	}
	
	
	private static void ajustarHerancaNumero(String nomeClasse, File file,
			Member membroID) throws IOException {

		String nomeSuperClasse = "BaseEntityNumero";

		StringBuilder sbConteudo = new StringBuilder(
				FileUtils.readFileToString(file));

		String classDeclaration = "public class " + nomeClasse;

		int indexInsert = sbConteudo.indexOf(classDeclaration) + 13
				+ nomeClasse.length();

		String tipoAtributoId = null;

		String chamadaGet = null;
		String chamadaSet = null;
		if (membroID instanceof Method) {
			Method methodID = (Method) membroID;
			tipoAtributoId = methodID.getReturnType().getSimpleName();
			if (!methodID.getName().equals("getNumero")) {
				chamadaGet = methodID.getName() + "()";
				chamadaSet = methodID.getName().replaceFirst("get", "set")+"(numero)";
			}
		} else {
			Field campoId = (Field) membroID;
			tipoAtributoId = campoId.getType().getSimpleName();
			if (!campoId.getName().equals("numero")) {
				chamadaGet = campoId.getName();
				chamadaSet = chamadaGet;
			}
		}

		sbConteudo.insert(indexInsert, " extends " + nomeSuperClasse + "<"
				+ tipoAtributoId + ">");

		if (chamadaGet != null) {

			String metodoGetter = "@Transient public " + tipoAtributoId
					+ " getNumero(){ return this." + chamadaGet + ";}";

			String metodoSetter = "public void setNumero(" + tipoAtributoId
					+ " numero){ this." + chamadaSet + ";}";

			int indexUltimaChave = sbConteudo.lastIndexOf("}");

			sbConteudo.insert(indexUltimaChave - 1, " \n " + metodoGetter + " \n "
					+ metodoSetter);

		}

		//File fileOut = new File("/home/geraldo/model/", nomeClasse + ".java");

		FileUtils.writeStringToFile(file, sbConteudo.toString());

	}
	
	
	private static void ajustarHerancaCodigo(String nomeClasse, File file,
			Member membroID) throws IOException {

		String nomeSuperClasse = "BaseEntityCodigo";

		StringBuilder sbConteudo = new StringBuilder(
				FileUtils.readFileToString(file));

		String classDeclaration = "public class " + nomeClasse;

		int indexInsert = sbConteudo.indexOf(classDeclaration) + 13
				+ nomeClasse.length();

		String tipoAtributoId = null;

		String chamadaGet = null;
		String chamadaSet = null;
		if (membroID instanceof Method) {
			Method methodID = (Method) membroID;
			tipoAtributoId = methodID.getReturnType().getSimpleName();
			if (!methodID.getName().equals("getCodigo")) {
				chamadaGet = methodID.getName() + "()";
				chamadaSet = methodID.getName().replaceFirst("get", "set")+"(codigo)";
			}
		} else {
			Field campoId = (Field) membroID;
			tipoAtributoId = campoId.getType().getSimpleName();
			if (!campoId.getName().equals("codigo")) {
				chamadaGet = campoId.getName();
				chamadaSet = chamadaGet;
			}
		}

		sbConteudo.insert(indexInsert, " extends " + nomeSuperClasse + "<"
				+ tipoAtributoId + ">");

		if (chamadaGet != null) {

			String metodoGetter = "@Transient public " + tipoAtributoId
					+ " getCodigo(){ return this." + chamadaGet + ";}";

			String metodoSetter = "public void setCodigo(" + tipoAtributoId
					+ " codigo){ this." + chamadaSet + ";}";

			int indexUltimaChave = sbConteudo.lastIndexOf("}");

			sbConteudo.insert(indexUltimaChave - 1, " \n " + metodoGetter + " \n "
					+ metodoSetter);

		}

	//	File fileOut = new File("/home/geraldo/model/", nomeClasse + ".java");

		FileUtils.writeStringToFile(file, sbConteudo.toString());

	}

	

	private static Method obterMetodoChavePrimaria(Class classe) {
		Method retorno = null;
		for (Method metodo : classe.getDeclaredMethods()) {
			if (metodo.isAnnotationPresent(Id.class)
					|| metodo.isAnnotationPresent(EmbeddedId.class)) {
				retorno = metodo;
				break;
			}
		}
		return retorno;
	}

	private static Field obterCampoChavePrimaria(Class classe) {
		Field retorno = null;
		for (Field campo : classe.getDeclaredFields()) {
			if (campo.isAnnotationPresent(Id.class)
					|| campo.isAnnotationPresent(EmbeddedId.class)) {
				retorno = campo;
				break;
			}
		}
		return retorno;
	}

	private static Class obterClass(File file) throws ClassNotFoundException {
		StringBuilder sb = new StringBuilder(file.getPath());

		sb.delete(0, PATH.length() + 1);

		sb.delete(sb.lastIndexOf("."), sb.length());

		String stClasse = sb.toString().replace("/", ".");

		return Class.forName(stClasse);
	}

	private static boolean verificarEntidade(Class classe) {

		if (!classe.isAnnotationPresent(Entity.class)) {
			return false;
		}

		if (classe.getSuperclass() != Object.class) {
			return false;
		}

		return true;

	}

}
