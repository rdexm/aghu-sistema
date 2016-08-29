package br.gov.mec.aghu.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("PMD")
public class AjustadorClassesNegocioInject {

	private static final String PATH_PROJETO = "/home/geraldo/workspace-kepler/aghu/";

	private static final String PATH_FONTE_MAVEN = "/src/main/java";

	private static final String PATH_DESTINO = "/home/geraldo/workspace-kepler/aghu/";

	private static Map<String, String> ajustesModulosClientes = new HashMap<>();

	private static Map.Entry<String, String> entradaAtual;

	static long contador = 0;

	static long contadorTotal = 0;

	public static void main(String[] args) throws Exception {
		inicializarMapModulos();
		System.out.println("Iniciando processo de ajuste das classes de negócio do sistema"); // NOPMD
		for (Map.Entry<String, String> entry : ajustesModulosClientes
				.entrySet()) {
			System.out.println("Iniciando processo de ajuste das classes de negócio do modulo: " + entry.getKey()); // NOPMD
			entradaAtual = entry;
			File folderSourceCodeModule = new File(PATH_PROJETO
					+ entry.getKey() + PATH_FONTE_MAVEN);
			processaArquivos(folderSourceCodeModule);
			System.out.println("Ajustados " + contador + " classes de negócio do modulo: " + entry.getKey()); // NOPMD
			contadorTotal += contador;
			contador = 0;
		}
		System.out.println("ajuste das classes de negócio concluido com sucesso. total: " + contadorTotal); // NOPMD
	}

	private static void inicializarMapModulos() {
		// ajustesModulosClientes.put("base", "aghu-vo");
		// ajustesModulosClientes.put("vo", "aghu-vo");

//		ajustesModulosClientes.put("parametrosistema/business",
//				"aghu-configuracao");
		
	
		ajustesModulosClientes.put("aghu-casca", "aghu-casca");

		ajustesModulosClientes.put("aghu-sicon", "aghu-sicon");
		ajustesModulosClientes.put("aghu-compras", "aghu-compras");
		ajustesModulosClientes.put("aghu-perinatologia",
				"aghu-perinatologia");
		ajustesModulosClientes.put("aghu-controleinfeccao",
				"aghu-controleinfeccao");
		ajustesModulosClientes.put("aghu-registrocolaborador",
				"aghu-registrocolaborador");
		ajustesModulosClientes.put("aghu-controlepaciente",
				"aghu-controlepaciente");
		ajustesModulosClientes.put("aghu-estoque", "aghu-estoque");
		ajustesModulosClientes.put("aghu-prescricaoenfermagem",
				"aghu-prescricaoenfermagem");
		ajustesModulosClientes.put("aghu-bancosangue", "aghu-bancosangue");
		ajustesModulosClientes.put("aghu-blococirurgico",
				"aghu-blococirurgico");
		ajustesModulosClientes.put("aghu-checagemeletronica",
				"aghu-checagemeletronica");
		ajustesModulosClientes.put("aghu-paciente", "aghu-paciente");
		ajustesModulosClientes.put("aghu-farmacia", "aghu-farmacia");

		ajustesModulosClientes.put("aghu-ambulatorio", "aghu-ambulatorio");

		ajustesModulosClientes.put("aghu-internacao", "aghu-internacao");

		ajustesModulosClientes.put("aghu-prescricaomedica",
				"aghu-prescricaomedica");
		ajustesModulosClientes.put("aghu-centrocusto", "aghu-centrocusto");
		ajustesModulosClientes.put("aghu-orcamento", "aghu-orcamento");
		ajustesModulosClientes.put("aghu-protocolos", "aghu-protocolos");
		ajustesModulosClientes.put("aghu-nutricao", "aghu-nutricao");
		ajustesModulosClientes.put("aghu-exames", "aghu-exames");
		ajustesModulosClientes.put("aghu-faturamento", "aghu-faturamento");

		ajustesModulosClientes.put("aghu-procedimentoterapeutico",
				"aghu-procedimentoterapeutico");
		ajustesModulosClientes.put("aghu-indicadores", "aghu-indicadores");

		ajustesModulosClientes.put("aghu-administracao", "aghu-administracao");

		ajustesModulosClientes.put("aghu-certificacaodigital",
				"aghu-certificacaodigital");
		ajustesModulosClientes
				.put("aghu-configuracao", "aghu-configuracao");
		ajustesModulosClientes.put("aghu-sig", "aghu-sig");
		ajustesModulosClientes.put("aghu-comissoes", "aghu-comissoes");

	
		
		
	
		 
	

	}

	private static void processaArquivos(File baseDir) throws Exception {
		File[] files = baseDir.listFiles();

		for (File file : files) {
			if (file.getName().endsWith("svn")) {
				continue;
			}
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				processarArquivoClasse(file);
			}
		}
	}

	private static void processarArquivoClasse(File file) throws Exception {

		if (verificarProcessaArquivo(file)) {
			contador++;
			System.out.println("Processando classe de negócio " + file.getName()); // NOPMD

			String stConteudo = FileUtils.readFileToString(file);
			
			
			

			StringBuilder sbConteudo = new StringBuilder(stConteudo);
			
			Map<String, String> mapaAtributosAIncluir = new HashMap<>();

			int indexNew = sbConteudo.indexOf("new ");

			while (indexNew != -1) {		

				int indexPrimeiroEspaco = sbConteudo.indexOf(" ", indexNew);

				int indexAbreParenteses = sbConteudo.indexOf("(",
						indexPrimeiroEspaco + 1);
				
				int indexAbreColchetes = sbConteudo.indexOf("[",
						indexPrimeiroEspaco + 1);
				
				if (indexAbreColchetes == -1 || (indexAbreParenteses != -1 && indexAbreParenteses < indexAbreColchetes)) {

					String classeInstanciacao = sbConteudo.substring(
							indexPrimeiroEspaco + 1, indexAbreParenteses);

					if (classeInstanciacao.endsWith("ON")
							|| classeInstanciacao.endsWith("RN") || classeInstanciacao.endsWith("CRUD")) {
						
						
						System.out.println("Trocando instanciacao por injecao de dependencia classe: "+classeInstanciacao); //NOPMD
						int indexPontoVirgula = sbConteudo.indexOf(";",
								indexAbreParenteses);

						String atributoInstanciacao = StringUtils
								.uncapitalize(classeInstanciacao);

						sbConteudo = sbConteudo.replace(indexNew,
								indexPontoVirgula + 1, atributoInstanciacao
										+ ";");

						mapaAtributosAIncluir.put(classeInstanciacao,
								atributoInstanciacao);
					}else{
						System.out.println("Instanciacao ignorada classe: "+classeInstanciacao);//NOPMD
					}
				}

				indexNew = sbConteudo
						.indexOf("new ", indexPrimeiroEspaco);

			}
			
			
			StringBuilder sbAtributos = new StringBuilder();
			for (String stTipoAtributo : mapaAtributosAIncluir.keySet()) {
				sbAtributos
						.append("\n\n@EJB\nprivate " + stTipoAtributo + " "
								+ mapaAtributosAIncluir.get(stTipoAtributo)
								+ ";");
			}
			
			
			Integer indexNomeClasse = sbConteudo.indexOf("class ");

			Integer indexInclusaoAtributo = sbConteudo.indexOf("{",
					indexNomeClasse) + 1;		
			
			if (sbAtributos.length() > 0) {
				sbConteudo.insert(indexInclusaoAtributo,
						"\n" + sbAtributos.toString());
			}
			
			
			String stNovoConteudo = sbConteudo.toString();
			if (sbAtributos.length() > 0 &&  !stNovoConteudo.contains("import javax.ejb.EJB;")) {
				stNovoConteudo = stNovoConteudo.replace("import javax.ejb.Stateless;",
						"import javax.ejb.Stateless;\nimport javax.ejb.EJB;");
			}
			

			StringBuilder sb = new StringBuilder(file.getPath());

			int index = sb.indexOf(PATH_FONTE_MAVEN);

			sb.delete(0, index + PATH_FONTE_MAVEN.length() + 1);

			File fileSaida = new File(PATH_DESTINO + entradaAtual.getValue()
					+ PATH_FONTE_MAVEN + "/" + sb.toString());

			FileUtils.writeStringToFile(fileSaida, stNovoConteudo);

		} else {
			System.out.println(" - Arquivo não processado: " + file.getName()); // NOPMD
		}
		
		System.out.println("\n"); // NOPMD

	}

	private static boolean verificarProcessaArquivo(File file) {
		boolean retorno = false;

		if (file.getName().endsWith("ON.java")) {
			retorno = true;
		}
		if (file.getName().endsWith("RN.java")) {
			retorno = true;
		}
		if (file.getName().endsWith("CRUD.java")) {
			retorno = true;
		}
		if (file.getName().endsWith("Facade.java")) {
			retorno = true;
		}
		if (file.getName().endsWith("Service.java")) {
			retorno = true;
		}
		if (file.getName().contains("Bean")) {
			retorno = true;
		}

		if (file.getName().contains("CascaService")) {
			retorno = false;
		}

		return retorno;
	}

}
