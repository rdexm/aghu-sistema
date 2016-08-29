package br.gov.mec.aghu.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("PMD")
public class AjustadorClassesNegocio {

	private static final String PATH_PROJETO = "/home/geraldo/AGHU/workspace-old/aghu/";

	private static final String PATH_FONTE_MAVEN = "/src/main/java";

	private static final String PATH_DESTINO = "/home/geraldo/AGHU/workspace/aghu/";

	private static Map<String, String> ajustesModulosClientes = new HashMap<>();

	private static Map.Entry<String, String> entradaAtual;

	static long contador = 0;

	static long contadorTotal = 0;

	public static void main(String[] args) throws Exception {
		inicializarMapModulos();
		System.out.println("Iniciando processo de ajuste das classes de negócio do sistema"); // NOPMD
		for (Map.Entry<String, String> entry : ajustesModulosClientes
				.entrySet()) {
			System.out.println("Iniciando processo de ajuste das classes de negócio do modulo: "+ entry.getKey()); // NOPMD
			entradaAtual = entry;
			File folderSourceCodeModule = new File(PATH_PROJETO
					+ entry.getKey() + PATH_FONTE_MAVEN);
			processaArquivos(folderSourceCodeModule);
			System.out.println("Ajustados " + contador	+ " classes de negócio do modulo: " + entry.getKey()); // NOPMD
			contadorTotal += contador;
			contador = 0;
		}
		
		//migração dos VOs do bloco
		File folderSourceCodeVOsBloco = new File(PATH_PROJETO + "vo/src/main/java/br/gov/mec/aghu/blococirurgico");
		
		
		entradaAtual = new Map.Entry<String, String>() {
			
			@Override
			public String setValue(String value) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getValue() {
				return "aghu-vo";
			}
			
			@Override
			public String getKey() {
				return "vo";
			}
		};
		processaArquivos(folderSourceCodeVOsBloco);
		
		
		folderSourceCodeVOsBloco = new File(PATH_PROJETO + "base/src/main/java/br/gov/mec/aghu/blococirurgico");
		
		
		entradaAtual = new Map.Entry<String, String>() {
			
			@Override
			public String setValue(String value) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getValue() {
				return "aghu-vo";
			}
			
			@Override
			public String getKey() {
				return "base";
			}
		};
		processaArquivos(folderSourceCodeVOsBloco);
		
		
		
		System.out.println("ajuste das classes de negócio concluido com sucesso. total: "+ contadorTotal); // NOPMD
	}

	private static void inicializarMapModulos() {
		// ajustesModulosClientes.put("base", "aghu-vo");
		// ajustesModulosClientes.put("vo", "aghu-vo");

		/*
		 * ajustesModulosClientes.put("parametrosistema/client",
		 * "aghu-configuracao-client");
		 * ajustesModulosClientes.put("casca/client", "aghu-casca-client");
		 * ajustesModulosClientes.put("sicon/client", "aghu-sicon-client");
		 * ajustesModulosClientes.put("compras/client", "aghu-compras-client");
		 * ajustesModulosClientes.put("perinatologia/client",
		 * "aghu-perinatologia-client");
		 * ajustesModulosClientes.put("controleinfeccao/client",
		 * "aghu-controleinfeccao-client");
		 * ajustesModulosClientes.put("registrocolaborador/client",
		 * "aghu-registrocolaborador-client");
		 * ajustesModulosClientes.put("controlepaciente/client",
		 * "aghu-controlepaciente-client");
		 * ajustesModulosClientes.put("estoque/client", "aghu-estoque-client");
		 * ajustesModulosClientes.put("prescricaoenfermagem/client",
		 * "aghu-prescricaoenfermagem-client");
		 * ajustesModulosClientes.put("bancosangue/client",
		 * "aghu-bancosangue-client");
		 * ajustesModulosClientes.put("blococirurgico/client",
		 * "aghu-blococirurgico-client");
		 * ajustesModulosClientes.put("checagemeletronica/client",
		 * "aghu-checagemeletronica-client");
		 * ajustesModulosClientes.put("paciente/client",
		 * "aghu-paciente-client");
		 * ajustesModulosClientes.put("farmacia/client",
		 * "aghu-farmacia-client");
		 * ajustesModulosClientes.put("ambulatorio/client",
		 * "aghu-ambulatorio-client");
		 * ajustesModulosClientes.put("internacao/client",
		 * "aghu-internacao-client");
		 * ajustesModulosClientes.put("prescricaomedica/client",
		 * "aghu-prescricaomedica-client");
		 * ajustesModulosClientes.put("centrocusto/client",
		 * "aghu-centrocusto-client");
		 * ajustesModulosClientes.put("orcamento/client",
		 * "aghu-orcamento-client");
		 * ajustesModulosClientes.put("protocolos/client",
		 * "aghu-protocolos-client");
		 * ajustesModulosClientes.put("nutricao/client",
		 * "aghu-nutricao-client"); ajustesModulosClientes.put("exames/client",
		 * "aghu-exames-client");
		 * 
		 * ajustesModulosClientes.put("faturamento/client",
		 * "aghu-faturamento-client");
		 * 
		 * ajustesModulosClientes.put("procedimentoterapeutico/client",
		 * "aghu-procedimentoterapeutico-client");
		 * ajustesModulosClientes.put("indicadores/client",
		 * "aghu-indicadores-client");
		 * ajustesModulosClientes.put("administracao/client",
		 * "aghu-administracao-client");
		 * ajustesModulosClientes.put("certificacaodigital/client",
		 * "aghu-certificacaodigital-client");
		 * ajustesModulosClientes.put("configuracao/client",
		 * "aghu-configuracao-client"); ajustesModulosClientes.put("sig/client",
		 * "aghu-sig-client"); ajustesModulosClientes.put("comissoes/client",
		 * "aghu-comissoes-client");
		 * 
		 * 
		 * 
		 * 
		 * ajustesModulosClientes.put("parametrosistema/business",
		 * "aghu-configuracao"); ajustesModulosClientes.put("casca/business",
		 * "aghu-casca");
		 * 
		 * ajustesModulosClientes.put("sicon/business", "aghu-sicon");
		 * ajustesModulosClientes.put("compras/business", "aghu-compras");
		 * ajustesModulosClientes.put("perinatologia/business",
		 * "aghu-perinatologia");
		 * ajustesModulosClientes.put("controleinfeccao/business",
		 * "aghu-controleinfeccao");
		 * ajustesModulosClientes.put("registrocolaborador/business",
		 * "aghu-registrocolaborador");
		 * ajustesModulosClientes.put("controlepaciente/business",
		 * "aghu-controlepaciente");
		 * ajustesModulosClientes.put("estoque/business", "aghu-estoque");
		 * ajustesModulosClientes.put("prescricaoenfermagem/business",
		 * "aghu-prescricaoenfermagem");
		 * ajustesModulosClientes.put("bancosangue/business",
		 * "aghu-bancosangue");
		 * 
		 * ajustesModulosClientes.put("checagemeletronica/business",
		 * "aghu-checagemeletronica");
		 * ajustesModulosClientes.put("paciente/business", "aghu-paciente");
		 * ajustesModulosClientes.put("farmacia/business", "aghu-farmacia");
		 * 
		 * 
		 * ajustesModulosClientes.put("ambulatorio/business",
		 * "aghu-ambulatorio");
		 * 
		 * ajustesModulosClientes.put("internacao/business", "aghu-internacao");
		 * 
		 * ajustesModulosClientes.put("prescricaomedica/business",
		 * "aghu-prescricaomedica");
		 * ajustesModulosClientes.put("centrocusto/business",
		 * "aghu-centrocusto"); ajustesModulosClientes.put("orcamento/business",
		 * "aghu-orcamento"); ajustesModulosClientes.put("protocolos/business",
		 * "aghu-protocolos"); ajustesModulosClientes.put("nutricao/business",
		 * "aghu-nutricao"); ajustesModulosClientes.put("exames/business",
		 * "aghu-exames"); ajustesModulosClientes.put("faturamento/business",
		 * "aghu-faturamento");
		 * 
		 * ajustesModulosClientes.put("procedimentoterapeutico/business",
		 * "aghu-procedimentoterapeutico");
		 * ajustesModulosClientes.put("indicadores/business",
		 * "aghu-indicadores");
		 * ajustesModulosClientes.put("administracao/business",
		 * "aghu-administracao");
		 * ajustesModulosClientes.put("certificacaodigital/business",
		 * "aghu-certificacaodigital");
		 * ajustesModulosClientes.put("configuracao/business",
		 * "aghu-configuracao"); ajustesModulosClientes.put("sig/business",
		 * "aghu-sig"); ajustesModulosClientes.put("comissoes/business",
		 * "aghu-comissoes");
		 * 
		 * ajustesModulosClientes.put("integracao/client",
		 * "aghu-integracao-client");
		 * ajustesModulosClientes.put("integracao/business", "aghu-integracao");
		 */

		ajustesModulosClientes.put("blococirurgico/business",
				"aghu-blococirurgico");
		
		 ajustesModulosClientes.put("blococirurgico/client",
				  "aghu-blococirurgico-client");
		 
		 

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

	@SuppressWarnings("PMD.NcssMethodCount")
	private static void processarArquivoClasse(File file) throws Exception {

		if (!verificarProcessaArquivo(file)) {
			System.out.println(" - Arquivo não processado: " + file.getName()); // NOPMD
			return;
		}
		contador++;
		System.out.println("Processando classe de negócio " + file.getName()); // NOPMD

		String stConteudo = FileUtils.readFileToString(file);

		stConteudo = stConteudo.replace("br.gov.mec.util.DateUtil",
				"br.gov.mec.aghu.core.utils.DateUtil");

		stConteudo = stConteudo.replace("br.gov.mec.aghu.util.AghuUtil",
				"br.gov.mec.aghu.core.commons.CoreUtil");

		stConteudo = stConteudo.replace("br.gov.mec.aghu.util.NumberUtil",
				"br.gov.mec.aghu.core.commons.NumberUtil");

		stConteudo = stConteudo.replace(
				"br.gov.mec.aghu.util.AghuNumberFormat",
				"br.gov.mec.aghu.core.utils.AghuNumberFormat");

		stConteudo = stConteudo.replace("br.gov.mec.aghu.util.DateFormatUtil",
				"br.gov.mec.aghu.core.utils.DateFormatUtil");

		stConteudo = stConteudo.replace("br.gov.mec.util.MecUtil",
				"br.gov.mec.aghu.core.commons.CoreUtil");

		stConteudo = stConteudo.replace("br.gov.mec.aghu.util.StringUtil",
				"br.gov.mec.aghu.core.utils.StringUtil");

		stConteudo = stConteudo.replace(
				"br.gov.mec.aghu.util.ConselhoRegionalMedicinaEnum",
				"br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum");

		stConteudo = stConteudo.replace(
				"br.gov.mec.aghu.util.ConselhoRegionalOdontologiaEnum",
				"br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum");

		stConteudo = stConteudo.replace("br.gov.mec.dominio.Dominio",
				"br.gov.mec.aghu.core.dominio.Dominio");

		stConteudo = stConteudo
				.replace(
						"br.gov.mec.aghu.business.exception.AGHUNegocioException;",
						"br.gov.mec.aghu.core.exception.ApplicationBusinessException;");

		stConteudo = stConteudo
				.replace(
						"br.gov.mec.aghu.business.exception.AGHUNegocioExceptionSemRollback;",
						"br.gov.mec.aghu.core.exception.ApplicationBusinessException;");

		stConteudo = stConteudo.replace(
				"br.gov.mec.seam.business.exception.MECBaseException;",
				"br.gov.mec.aghu.core.exception.BaseException;");

		stConteudo = stConteudo.replace(
				"org.jboss.seam.international.StatusMessage.Severity",
				"br.gov.mec.aghu.core.exception.Severity");

		stConteudo = stConteudo.replace("AGHUNegocioExceptionSemRollback",
				"ApplicationBusinessException");

		stConteudo = stConteudo.replace("AGHUNegocioException",
				"ApplicationBusinessException");

		stConteudo = stConteudo.replace("AghuUtil", "CoreUtil");

		stConteudo = stConteudo.replace("MecUtil", "CoreUtil");

		stConteudo = stConteudo.replace("MECBaseException", "BaseException");

		// ajustes especificos p/ classes de negócio

		stConteudo = stConteudo.replace(
				"import br.gov.mec.aghu.casca.service.CascaService;",
				"import br.gov.mec.aghu.casca.business.ICascaFacade;");

		stConteudo = stConteudo.replace("CascaService", "ICascaFacade");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.exception.MECNegocioListaException;",
						"import br.gov.mec.aghu.core.exception.BaseListException;");

		stConteudo = stConteudo.replace("MECNegocioListaException",
				"BaseListException");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.model.exception.MECModelException;",
						"import br.gov.mec.aghu.core.exception.BaseRuntimeException;");

		stConteudo = stConteudo.replace("MECModelException",
				"BaseRuntimeException");

		stConteudo = stConteudo
				.replace("import br.gov.mec.aghu.casca.CascaException;",
						"import br.gov.mec.aghu.core.exception.ApplicationBusinessException;");

		stConteudo = stConteudo.replace("CascaException",
				"ApplicationBusinessException");

		// ajustes EJB
		if ((!stConteudo.contains("@Stateless")) && (!entradaAtual.getKey().equals("vo"))) {
			if (!stConteudo.contains("public class")) {

				stConteudo = stConteudo.replaceFirst("class ",
						"@Stateless\npublic class ");

			} else {
				stConteudo = stConteudo.replaceFirst("public class",
						"@Stateless\npublic class");
			}
		}

		int indexBarra = entradaAtual.getKey().indexOf("/");
		String nomeModulo = null;
		if (indexBarra != -1) {
			nomeModulo = entradaAtual.getKey().substring(0, indexBarra);
		}
		
		stConteudo = stConteudo.replace(
				"import br.gov.mec.aghu.dao.AGHUDAOFactory;",
				"");

		stConteudo = stConteudo.replace(
				"import br.gov.mec.aghu.dao.AGHUGenericDAO;",
				"import br.gov.mec.aghu.core.persistence.dao.BaseDao;");

		stConteudo = stConteudo.replace("AGHUGenericDAO", "BaseDao");

		stConteudo = stConteudo.replace("import br.gov.mec.aghu.dao",
				"import br.gov.mec.aghu." + nomeModulo + ".dao");

		if (stConteudo
				.contains("import br.gov.mec.seam.business.SeamContextsManager;")) {
			stConteudo = stConteudo
					.replace(
							"import br.gov.mec.seam.business.SeamContextsManager;",
							"import org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;\nimport javax.ejb.Stateless;\nimport javax.inject.Inject;\nimport br.gov.mec.aghu.core.business.BaseBusiness;\nimport org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;");
		} else {
			stConteudo = stConteudo
					.replace(
							"import br.gov.mec.seam.business.AGHUBaseFacade;",
							"import org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;\nimport br.gov.mec.aghu.core.business.BaseFacade;\nimport javax.ejb.Stateless;\nimport javax.inject.Inject;\nimport org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;\nimport br.gov.mec.aghu.core.business.seguranca.Secure;");
		}

		if (stConteudo.contains("IAGHUBaseFacade")) {
			stConteudo = stConteudo.replace(
					"import br.gov.mec.seam.business.IAGHUBaseFacade;",
					"import java.io.Serializable;");
			stConteudo = stConteudo.replace("IAGHUBaseFacade", "Serializable");

		}

		if (stConteudo.contains("AGHUBaseFacade")) {
			stConteudo = stConteudo.replace("AGHUBaseFacade", "BaseFacade");
		}

		stConteudo = stConteudo.replace("@Transactional", "");
		stConteudo = stConteudo.replace(
				"import org.jboss.seam.annotations.security.Restrict;", "");
		stConteudo = stConteudo.replace(
				"import org.jboss.seam.annotations.Name;", "");

		stConteudo = stConteudo.replace(
				"import br.gov.mec.util.DateConstants;",
				"import br.gov.mec.aghu.core.utils.DateConstants;");

		stConteudo = stConteudo.replace(
				"import br.gov.mec.seam.util.email.EmailUtil;",
				"import br.gov.mec.aghu.core.mail.EmailUtil;");

		if (!stConteudo.contains("public interface")) {
			stConteudo = stConteudo.replace("@Restrict", "@Secure");
			stConteudo = stConteudo
					.replace(
							"import br.gov.mec.seam.business.BypassInactiveModule;",
							"import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;");
		} else {
			Integer indexAnotacaoRestrict = stConteudo.indexOf("@Restrict");
			while (indexAnotacaoRestrict != -1) { // Remove os @Restrict
				Integer indexFechaColchetesDepoisAnotacaoSecure = stConteudo
						.indexOf('}', indexAnotacaoRestrict);
				String stAnotacaoRestrict = stConteudo.substring(
						indexAnotacaoRestrict,
						indexFechaColchetesDepoisAnotacaoSecure + 3);
				stConteudo = stConteudo.replace(stAnotacaoRestrict, "");
				indexAnotacaoRestrict = stConteudo.indexOf("@Restrict",
						indexFechaColchetesDepoisAnotacaoSecure);
			}

			stConteudo = stConteudo.replace("@BypassInactiveModule", "");
			stConteudo = stConteudo
					.replace(
							"import br.gov.mec.seam.business.BypassInactiveModule;",
							"");

		}

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.exception.NegocioExceptionCode",
						"import br.gov.mec.aghu.core.exception.BusinessExceptionCode");
		
		
		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.dominio.DominioOperacoesJournal;",
						"import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;");
		
		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.factory.BaseJournalFactory;",
						"import br.gov.mec.aghu.core.factory.BaseJournalFactory;");
		
		
		stConteudo = stConteudo.replace("NegocioExceptionCode",
				"BusinessExceptionCode");

		stConteudo = stConteudo.replaceFirst("SeamContextsManager",
				"BaseBusiness");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.InactiveModuleException;",
						"import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.Modulo;",
						"import br.gov.mec.aghu.core.business.moduleintegration.Modulo;\nimport br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;");

		stConteudo = stConteudo
				.replace("import br.gov.mec.aghu.util.AghuParametrosEnum;",
						"import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;");
		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.business.aghparametros.IParametroFacade;",
						"import br.gov.mec.aghu.aghparametros.business.IParametroFacade;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.business.aghparametros.AghParemetrosONExceptionCode;",
						"import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;");

		stConteudo = stConteudo.replace("CoreUtil.isHU(HUsEnum.HCPA);",
				"this.isHCPA();");
		
		
		
		if (stConteudo.contains("public Integer")) {
			int count = StringUtils.countMatches(stConteudo, "public Integer");
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = stConteudo.indexOf("public Integer", indexInit);				
			
				int indexFinish = stConteudo.indexOf(")", indexInit);
				indexFinish = indexFinish + 1;
				String methodDeclaration = stConteudo.substring(indexInit , indexFinish);				
				
				if (methodDeclaration.contains("Count")) {
					String methodDeclarationNew = methodDeclaration.replaceFirst("Integer", "Long");
					
					stConteudo = stConteudo.replace(methodDeclaration, methodDeclarationNew);				
				}			
				
				indexInit = indexInit + 10;
			}
		}
		
		
		

		Map<String, String> mapaAtributosAIncluir = new HashMap<>();
		Map<String, String> mapaAtributosEJBAIncluir = new HashMap<>();

		int indexSuperObterDoContexto = stConteudo
				.indexOf("super.obterDoContexto");

		while (indexSuperObterDoContexto != -1) {
			int indexClass = stConteudo.indexOf(".class",
					indexSuperObterDoContexto);

			int indexPontoVirgula = stConteudo.indexOf(';',
					indexSuperObterDoContexto);

			String stTipoAtributo = stConteudo.substring(
					indexSuperObterDoContexto + 22, indexClass);
			String stReturnGetter = stConteudo.substring(
					indexSuperObterDoContexto, indexPontoVirgula);

			Character primeiraLetra = stTipoAtributo.charAt(0);

			Character primeiraLetraMinuscula = Character
					.toLowerCase(primeiraLetra);

			if (stTipoAtributo.contains("Facade")
					&& stTipoAtributo.charAt(0) != 'I') {
				stTipoAtributo = "I" + stTipoAtributo;
			}

			String stAtributo = stTipoAtributo
					.replaceFirst(primeiraLetra.toString(),
							primeiraLetraMinuscula.toString());
			
			if (stAtributo.endsWith("Facade") || stAtributo.endsWith("ON") || stAtributo.endsWith("RN")){
				mapaAtributosEJBAIncluir.put(stTipoAtributo, stAtributo);
			}else{
				mapaAtributosAIncluir.put(stTipoAtributo, stAtributo);
			}			

			stConteudo = stConteudo.replace(stReturnGetter, stAtributo);

			indexSuperObterDoContexto = stConteudo
					.indexOf("super.obterDoContexto");
		}

		int indexObterDoContexto = stConteudo.indexOf("obterDoContexto");

		while (indexObterDoContexto != -1) {
			int indexClass = stConteudo.indexOf(".class", indexObterDoContexto);

			int indexPontoVirgula = stConteudo.indexOf(";",
					indexObterDoContexto);

			String stTipoAtributo = stConteudo.substring(
					indexObterDoContexto + 16, indexClass);
			String stReturnGetter = stConteudo.substring(indexObterDoContexto,
					indexPontoVirgula);

			Character primeiraLetra = stTipoAtributo.charAt(0);

			Character primeiraLetraMinuscula = Character
					.toLowerCase(primeiraLetra);

			if (stTipoAtributo.contains("Facade")
					&& stTipoAtributo.charAt(0) != 'I') {
				stTipoAtributo = "I" + stTipoAtributo;
			}

			String stAtributo = stTipoAtributo
					.replaceFirst(primeiraLetra.toString(),
							primeiraLetraMinuscula.toString());

			if (stAtributo.endsWith("Facade") || stAtributo.endsWith("ON") || stAtributo.endsWith("RN")){
				mapaAtributosEJBAIncluir.put(stTipoAtributo, stAtributo);
			}else{
				mapaAtributosAIncluir.put(stTipoAtributo, stAtributo);
			}

			stConteudo = stConteudo.replace(stReturnGetter, stAtributo);

			indexObterDoContexto = stConteudo.indexOf("obterDoContexto");
		}

		int indexAghuDAOFactory = stConteudo.indexOf("AGHUDAOFactory.getDAO");

		while (indexAghuDAOFactory != -1) {
			int indexClass = stConteudo.indexOf(".class", indexAghuDAOFactory);

			int indexPontoVirgula = stConteudo
					.indexOf(";", indexAghuDAOFactory);

			String stTipoAtributo = stConteudo.substring(
					indexAghuDAOFactory + 22, indexClass);
			String stReturnGetter = stConteudo.substring(indexAghuDAOFactory,
					indexPontoVirgula);

			Character primeiraLetra = stTipoAtributo.charAt(0);

			Character primeiraLetraMinuscula = Character
					.toLowerCase(primeiraLetra);

			String stAtributo = stTipoAtributo
					.replaceFirst(primeiraLetra.toString(),
							primeiraLetraMinuscula.toString());

			if (stTipoAtributo.contains("Facade")
					&& stTipoAtributo.charAt(0) != 'I') {
				stTipoAtributo = "I" + stTipoAtributo;
			}

			mapaAtributosAIncluir.put(stTipoAtributo, stAtributo);

			stConteudo = stConteudo.replace(stReturnGetter, stAtributo);

			indexAghuDAOFactory = stConteudo.indexOf("AGHUDAOFactory.getDAO");
		}

		int indexGetInstance = stConteudo.indexOf("Component.getInstance");

		while (indexGetInstance != -1) {
			int indexVirgula = stConteudo.indexOf(",", indexGetInstance);

			int indexFechaParenteses = stConteudo
					.indexOf(")", indexGetInstance);

			int indexClass = stConteudo.indexOf(".class", indexGetInstance);

			int indexCorte = 0;
			if (indexVirgula != -1) { // teste para o caso de não haver mais
										// nenhuma virgula no arquivo.
				indexCorte = indexVirgula < indexFechaParenteses ? indexVirgula
						: indexFechaParenteses;
			} else {
				indexCorte = indexFechaParenteses;
			}

			if (indexVirgula != -1 && indexClass != -1) {
				indexCorte = Math.min(indexVirgula,
						Math.min(indexFechaParenteses, indexClass));
			} else if (indexVirgula == -1) {
				indexCorte = Math.min(indexFechaParenteses, indexClass);
			} else {
				indexCorte = Math.min(indexFechaParenteses, indexVirgula);
			}

			int offSetInicio = 23;
			int offSetFim = 1;

			if (indexCorte == indexClass) {
				offSetInicio = 22;
				offSetFim = 0;
			}

			int indexPontoVirgula = stConteudo.indexOf(";", indexGetInstance);

			String stAtributo = stConteudo.substring(indexGetInstance
					+ offSetInicio, indexCorte - offSetFim);
			String stReturnGetter = stConteudo.substring(indexGetInstance,
					indexPontoVirgula);

			if (!stReturnGetter.contains("NTITY_MANAGER")
					&& !stReturnGetter.contains("ntityManager")) {

				String stTipoAtributo = null;

				if (indexCorte != indexClass) {

					Character primeiraLetra = stAtributo.charAt(0);

					Character primeiraLetraMaiuscula = Character
							.toUpperCase(primeiraLetra);

					stTipoAtributo = stAtributo.replaceFirst(
							primeiraLetra.toString(),
							primeiraLetraMaiuscula.toString());

				} else {

					stTipoAtributo = stAtributo;

					Character primeiraLetra = stAtributo.charAt(0);

					Character primeiraLetraMinuscula = Character
							.toLowerCase(primeiraLetra);

					stAtributo = stAtributo.replaceFirst(
							primeiraLetra.toString(),
							primeiraLetraMinuscula.toString());
				}

				if (stTipoAtributo.contains("Facade")
						&& stTipoAtributo.charAt(0) != 'I') {
					stTipoAtributo = "I" + stTipoAtributo;
				}

				mapaAtributosAIncluir.put(stTipoAtributo, stAtributo);

				stConteudo = stConteudo.replace(stReturnGetter, stAtributo);
			} else {
				System.out.println("achou getter entitymanager"); // NOPMD
			}

			indexGetInstance = stConteudo.indexOf("Component.getInstance",
					indexGetInstance + 23);
		}

		

		int indexNew = stConteudo.indexOf("new ");

		while (indexNew != -1) {

			int indexPrimeiroEspaco = stConteudo.indexOf(" ", indexNew);

			int indexAbreParenteses = stConteudo.indexOf("(",
					indexPrimeiroEspaco + 1);

			int indexAbreColchetes = stConteudo.indexOf("[",
					indexPrimeiroEspaco + 1);

			if (indexAbreColchetes == -1
					|| (indexAbreParenteses != -1 && indexAbreParenteses < indexAbreColchetes)) {

				String classeInstanciacao = stConteudo.substring(
						indexPrimeiroEspaco + 1, indexAbreParenteses);

				if (classeInstanciacao.endsWith("ON")
						|| classeInstanciacao.endsWith("RN")
						|| classeInstanciacao.endsWith("CRUD")) {

					System.out.println("Trocando instanciacao por injecao de dependencia classe: "+ classeInstanciacao); // NOPMD
//					int indexPontoVirgula = stConteudo.indexOf(";",
//							indexAbreParenteses);

					String stReturnGetter = stConteudo.substring(indexNew,
							indexAbreParenteses + 2);

					String atributoInstanciacao = StringUtils
							.uncapitalize(classeInstanciacao);

					stConteudo = stConteudo.replace(stReturnGetter,
							atributoInstanciacao);

					mapaAtributosEJBAIncluir.put(classeInstanciacao,
							atributoInstanciacao);
				} else if (classeInstanciacao.endsWith("DAO")
						|| classeInstanciacao.endsWith("Dao")) {

					System.out.println("Trocando instanciacao por injecao de dependencia classe: "+ classeInstanciacao); // NOPMD
//					int indexPontoVirgula = stConteudo.indexOf(";",
//							indexAbreParenteses);

					String stReturnGetter = stConteudo.substring(indexNew,
							indexAbreParenteses + 2);

					String atributoInstanciacao = StringUtils
							.uncapitalize(classeInstanciacao);

					stConteudo = stConteudo.replace(stReturnGetter,
							atributoInstanciacao);

					mapaAtributosAIncluir.put(classeInstanciacao,
							atributoInstanciacao);

				} else {
					System.out.println("Instanciacao ignorada classe: "	+ classeInstanciacao);// NOPMD
				}
			}

			indexNew = stConteudo.indexOf("new ", indexPrimeiroEspaco);

		}

		StringBuilder sbAtributos = new StringBuilder();
		for (String stTipoAtributo : mapaAtributosAIncluir.keySet()) {
			sbAtributos.append("\n\n\t@Inject\n\tprivate " + stTipoAtributo + " "
					+ mapaAtributosAIncluir.get(stTipoAtributo) + ";");
		}

		StringBuilder sbAtributosEJB = new StringBuilder();
		for (String stTipoAtributo : mapaAtributosEJBAIncluir.keySet()) {
			sbAtributosEJB.append("\n\n\t@EJB\n\tprivate " + stTipoAtributo + " "
					+ mapaAtributosEJBAIncluir.get(stTipoAtributo) + ";");
		}

		int indexAnotacaoName = stConteudo.indexOf("@Name");

		if (indexAnotacaoName != -1) {
			int indexFechaParentesesDepoisAnotacaoName = stConteudo.indexOf(
					")", indexAnotacaoName);
			String stAnotacaoName = stConteudo.substring(indexAnotacaoName,
					indexFechaParentesesDepoisAnotacaoName + 1);
			stConteudo = stConteudo.replace(stAnotacaoName, "");

		}

		int indexAnotacaoModulo = stConteudo.indexOf("@Modulo");

		if (indexAnotacaoModulo != -1) {
			int indexFechaParentesesDepoisAnotacaoModulo = stConteudo.indexOf(
					")", indexAnotacaoModulo);
			String stAnotacaoModulo = stConteudo.substring(
					indexAnotacaoModulo + 9,
					indexFechaParentesesDepoisAnotacaoModulo - 1);
			String stModuloEnum = "ModuloEnum."
					+ "BLOCO_CIRURGICO";
			stConteudo = stConteudo.replace("\"" + stAnotacaoModulo + "\"",
					stModuloEnum);

		}
		
		if (sbAtributosEJB.length() > 0
				&& !stConteudo.contains("import javax.ejb.EJB;")) {
			stConteudo = stConteudo.replace(
					"import javax.ejb.Stateless;",
					"import javax.ejb.Stateless;\nimport javax.ejb.EJB;");
		}

		// mudando para string builder

		StringBuilder sbConteudo = new StringBuilder(stConteudo);

		if (stConteudo.contains("extends BaseBusiness")
				|| stConteudo.contains("extends BaseFacade")
				|| stConteudo.contains("extends AbstractFatDebugLogEnableRN")
				|| stConteudo.contains("extends AbstractAGHUCrudPersist")) {

			Integer indexNomeClasse = sbConteudo.indexOf("class ");

			Integer indexInclusaoAtributo = sbConteudo.indexOf("{",
					indexNomeClasse) + 1;

			String stclass = file.getName().replace(".java", ".class");

			String stLog = "private static final Log LOG = LogFactory.getLog("
					+ stclass + ");";

			String stMetodoGetLogger = "@Override\n\t@Deprecated\n\tprotected Log getLogger() {\n\t\treturn LOG;\n\t}";

			sbConteudo.insert(indexInclusaoAtributo, "\n\n\t" + stLog + "\n\n\t"
					+ stMetodoGetLogger + "\n\t" + sbAtributos.toString() + "\n"
					+ sbAtributosEJB.toString());

		}

	
		

		StringBuilder sb = new StringBuilder(file.getPath());

		int index = sb.indexOf(PATH_FONTE_MAVEN);

		sb.delete(0, index + PATH_FONTE_MAVEN.length() + 1);

		File fileSaida = new File(PATH_DESTINO + entradaAtual.getValue()
				+ PATH_FONTE_MAVEN + "/" + sb.toString());

		FileUtils.writeStringToFile(fileSaida, sbConteudo.toString());

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
		if (file.getName().endsWith("ExceptionCode.java")) {
			retorno = true;
		}
		if (file.getName().contains("Bean")) {
			retorno = true;
		}
		
		if (file.getName().endsWith("VO.java")) {
			retorno = true;
		}

		return retorno;
	}

}
