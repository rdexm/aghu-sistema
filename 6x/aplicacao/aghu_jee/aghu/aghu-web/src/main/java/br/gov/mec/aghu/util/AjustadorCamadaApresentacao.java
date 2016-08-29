package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("PMD")
public class AjustadorCamadaApresentacao {

	private static String path_origem_controller = "web/src/main/java/";
	private static String path_destino_controller = "aghu-web/src/main/java/";
	private static String path_origem_xhtml = "web/src/main/webapp/";
	private static String path_destino_xhtml = "aghu-web/src/main/webapp/pages/";

	private static final Log LOG = LogFactory.getLog(AjustadorCamadaApresentacao.class);

	public static void main(String[] args) throws IOException {
		 // LEIA-ME: Sobre os parâmetros do aplicativo
		 // Formato do parâmetro XHTML. EX: /parametrosistema/manterParametroSistema.xhtml
		 // Formato do parâmetro JAVA. EX: br.gov.mec.aghu.parametrosistema.action.ManterParametrosPaginatorController.java
		
		// LEIA-ME: Modificar a variável 'pathBranchAntigo' para o CAMINHO COMPLETO (CONSIDERANDO A PASTA AGHU) do BRANCH ANTIGO conforme o WORKSPACE. 
		final String pathBranchAntigo = "/home/jefferson/workspace-FR/5_fr_aghu/"; // ATENÇÃO: Adaptar com o caminho completo do branch 5 no workspace.
		
		// Caminho do projeto LOCAL
		String path = AjustadorCamadaApresentacao.class.getResource("").getPath();
		if (StringUtils.isNotBlank(path)) {
			path = path.substring(0, path.indexOf("aghu-web"));
			path_origem_controller = pathBranchAntigo + path_origem_controller;
			path_destino_controller = path + path_destino_controller;
			path_origem_xhtml = pathBranchAntigo + path_origem_xhtml;
			path_destino_xhtml = path + path_destino_xhtml;
		}
		
		LOG.info("INICIANDO PROCESSO DE AJUSTE DOS ARQUIVOS DA CAMADA DE APRESENTAÇÃO: " + path + "\n");
		LOG.info("BRANCH ANTIGO : " + pathBranchAntigo);
		LOG.info("PROJETO LOCAL: " + path);
		LOG.info("ORIGEM XHTML : " + path_origem_xhtml);
		LOG.info("ORIGEM XHTML : " + path_origem_xhtml);
		LOG.info("DESTINO XHTML: " + path_destino_xhtml);
		LOG.info("ORIGEM CONTROLLER : " + path_origem_controller);
		LOG.info("DESTINO CONTROLLER: " + path_destino_controller + "\n");

		for (String nomeArquivo : args) {

			if (nomeArquivo.endsWith(".java")) {
				String nomeClasseController = nomeArquivo.replace(".java", "");

				String caminhoClasseController = path_origem_controller
						+ nomeClasseController.replace(".", "/") + ".java";

				File arquivoClasseController = new File(caminhoClasseController);

				String arquivoProcessado = processarClasseController(arquivoClasseController);

				incluirComentarioMigracao(arquivoClasseController, true);

				copiarClasseController(arquivoProcessado, nomeClasseController);

			} else if (nomeArquivo.endsWith(".xhtml")) {
	
				String caminhoXHTML = getCaminhoXHTML(nomeArquivo);

				File arquivoXHTML = new File(caminhoXHTML);

				File arquivoPAGEXML = getArquivoPAGEXML(nomeArquivo);

				String arquivoProcessado = processarXHTML(arquivoXHTML, nomeArquivo);

				incluirComentarioMigracao(arquivoXHTML, true);
				
				if (arquivoPAGEXML!=null){
					try {
						incluirComentarioMigracao(arquivoPAGEXML, false);
					} catch (IOException e) {
						LOG.error(
								"Não foi encontrado page xml referente a este xhtml",
								e);
					}
				}	

				copiarXHTML(arquivoProcessado, nomeArquivo);
			} else {
				LOG.error("Tipo de arquivo não tratado pela rotina: " + nomeArquivo);
			}

		}

		LOG.info("AJUSTES DE ARQUIVOS DA CAMADA DE APRESENTAÇÃO CONCLUIDO!");
	}
	
	public static String getCaminhoXHTML(String strArquivo) {
		return path_origem_xhtml + strArquivo;
	}
	
	public static String getCaminhoPAGEXML(String strArquivo) {
		if (strArquivo.contains(".xhtml")) {
			strArquivo = strArquivo.replace(".xhtml", ".page.xml");
		} else {
			int indexPonto = strArquivo.lastIndexOf(".");
			strArquivo = strArquivo.substring(0, indexPonto);
			strArquivo = strArquivo + ".page.xml";
		}
		
		return path_origem_xhtml + strArquivo;
	}
	
	public static File getArquivoPAGEXML(String arquivo) {
		String caminhoPAGEXML = getCaminhoPAGEXML(arquivo);

		File arquivoPAGEXML = new File(caminhoPAGEXML);
		if (!arquivoPAGEXML.exists()){
			return null;
		}
		
		return arquivoPAGEXML;
	}

	private static void incluirComentarioMigracao(File arquivo, boolean sobrescreverArquivoCompleto)
			throws IOException {
		
		String stNovoConteudo = " <!--"
				+ "ATENÇÃO: Este arquivo já de encontra em processo de adaptação para a nova arquitetura. \n"
				+ "Observe atentamente os campos abaixo para saber como proceder com merges provenientes \n"
				+ "de outros branches para cá. Se o ESTADO indicar que a tela já se encontra migrada, \n"
				+ "proceda com o merge manual fazendo as devidas adaptações, e em caso de dúvida procure \n"
				+ "os colegas envolvidos. \n" + "ESTADO: EM MIGRACAO \n"
				+ "DESENVOLVEDOR: Migracao Arquitetura \n" + "-->	\n\n";

		if(sobrescreverArquivoCompleto){
			FileUtils.writeStringToFile(arquivo, stNovoConteudo);
		}
		else{
			String stAntigoConteudo = FileUtils.readFileToString(arquivo);
			StringBuffer sbNovoConteudo = new StringBuffer(stAntigoConteudo);
			sbNovoConteudo.insert(0, stNovoConteudo);
			FileUtils.writeStringToFile(arquivo, sbNovoConteudo.toString());
		}
	}

	private static void copiarXHTML(String arquivoProcessado, String nomeArquivo)
			throws IOException {
		File arquivoSaida = new File(path_destino_xhtml + nomeArquivo);

		FileUtils.writeStringToFile(arquivoSaida, arquivoProcessado);
	}

	private static String processarXHTML(File arquivoXHTML, String nomeArquivo) throws IOException {
		AjustadorCamadaApresentacaoXhtml ajustadorXhtml = new AjustadorCamadaApresentacaoXhtml(arquivoXHTML, nomeArquivo);
		return ajustadorXhtml.processar();
	}

	private static void copiarClasseController(String arquivoProcessado,
			String nomeClasseController) throws IOException {
		File arquivoSaida = new File(path_destino_controller
				+ nomeClasseController.replace(".", "/") + ".java");
		
		FileUtils.writeStringToFile(arquivoSaida, arquivoProcessado);
	}

	private static String processarClasseController(File arquivoClasseController)
			throws IOException {
		String stConteudo = FileUtils.readFileToString(arquivoClasseController);

		stConteudo = ajustarImports(stConteudo);

		stConteudo = stConteudo.replace("AGHUNegocioExceptionSemRollback",
				"ApplicationBusinessException");

		stConteudo = stConteudo.replace("AGHUNegocioException",
				"ApplicationBusinessException");

		stConteudo = stConteudo.replace("AghuUtil", "CoreUtil");

		stConteudo = stConteudo.replace("MecUtil", "CoreUtil");

		stConteudo = stConteudo.replace("MECBaseException", "BaseException");

		stConteudo = stConteudo.replace("CascaService", "ICascaFacade");

		stConteudo = stConteudo.replace("MECNegocioListaException",
				"BaseListException");

		stConteudo = stConteudo.replace("MECModelException",
				"BaseRuntimeException");

		stConteudo = stConteudo.replace("CascaException",
				"ApplicationBusinessException");

		stConteudo = stConteudo.replace("NegocioExceptionCode",
				"BusinessExceptionCode");

		stConteudo = stConteudo.replace(
				"getStatusMessages().addFromResourceBundle",
				"apresentarMsgNegocio");
		
		stConteudo = stConteudo.replace(
				"getStatusMessages().add",
				"apresentarMsgNegocio");
		
		stConteudo = stConteudo.replace(
				"getLog()",
				"LOG");
		
		stConteudo = stConteudo.replace(
				"recuperarColecao() throws BaseException",
				"recuperarColecao() throws ApplicationBusinessException");
		
		stConteudo = stConteudo.replace(
				"this.error(",
				"LOG.error(");
		
		stConteudo = stConteudo.replace(
				"this.info(",
				"LOG.info(");
		
		stConteudo = stConteudo.replace("Interpolator.instance().interpolate"
				, "MessageFormat.format");

		int indexAnotacaoIn = stConteudo.indexOf("@In");

		while (indexAnotacaoIn != -1) {
			int indexFechaParentesesDepoisAnotacaoName = stConteudo.indexOf(
					")", indexAnotacaoIn);
			String stAnotacaoName = stConteudo.substring(indexAnotacaoIn,
					indexFechaParentesesDepoisAnotacaoName + 1);
			stConteudo = stConteudo.replace(stAnotacaoName, "@EJB");
			indexAnotacaoIn = stConteudo.indexOf("@In");
		}

		stConteudo = stConteudo.replace("CoreUtil.isHU(HUsEnum.HCPA);",
				"this.isHCPA();");

		stConteudo = stConteudo.replace("AGHURelatorioController",
				"ActionReport");

		stConteudo = stConteudo.replace("AGHUController", "ActionController");

		stConteudo = stConteudo.replace("AGHUPaginatorController",
				"ActionController implements ActionPaginator");

		String conteudoAdicionalEspecificoInicio = "";
		String conteudoAdicionalEspecificoFim = "";

		// tratamento especial para controllers de paginação
		if (stConteudo.contains("ActionPaginator")) {

			int indexMetodoRecuperaListaPaginada = stConteudo.indexOf("recuperarListaPaginada");
			
			// Volta 100 caracateres
			String aa = (stConteudo.substring((indexMetodoRecuperaListaPaginada-100), indexMetodoRecuperaListaPaginada));
			
			// Descobre o nome da classe a partir da posicao de <List>
			String tipo = aa.substring(aa.lastIndexOf("List")+5);
			tipo = tipo.substring(0, tipo.indexOf(">"));
			
			conteudoAdicionalEspecificoInicio = "\n\n\tprivate DynamicDataModel<"
					+ tipo + "> dataModel = new DynamicDataModel<" + tipo
					+ ">(this);" ;
					
					
			conteudoAdicionalEspecificoFim = " \n\n\n\tpublic DynamicDataModel<" + tipo
					+ "> getDataModel() {" + "\n\t return dataModel;" + "\n\t}"
					+ "\n\n\tpublic void setDataModel(DynamicDataModel<" + tipo
					+ "> dataModel) {" + "\n\t this.dataModel = dataModel;"
					+ "\n\t}";

			stConteudo = stConteudo.replace(
					"protected Integer recuperarCount()",
					"public Long recuperarCount()");

			stConteudo = stConteudo.replace("protected List<" + tipo
					+ "> recuperarListaPaginada", "public List<" + tipo
					+ "> recuperarListaPaginada");
		}

		int indexAnotacaoName = stConteudo.indexOf("@Name");

		if (indexAnotacaoName != -1) {
			int indexFechaParentesesDepoisAnotacaoName = stConteudo.indexOf(
					")", indexAnotacaoName);
			String stAnotacaoName = stConteudo.substring(indexAnotacaoName,
					indexFechaParentesesDepoisAnotacaoName + 1);
			stConteudo = stConteudo.replace(stAnotacaoName, "");
		}

		Integer indexNomeClasse = stConteudo.indexOf("class ");

		Integer indexInclusaoMetodoInicializar = stConteudo.indexOf("{",
				indexNomeClasse) + 1;

		String stMetodoInicializar = "\n\n\t@PostConstruct\n"
				+ "\tprotected void inicializar(){\n"
				+ "\t this.begin(conversation);\n" + "\t}";

		String stclass = arquivoClasseController.getName().replace(".java",
				".class");

		String stLog = "\n\n\tprivate static final Log LOG = LogFactory.getLog("
				+ stclass + ");";

		String conteudoAdiconal = stMetodoInicializar
				+ conteudoAdicionalEspecificoInicio + stLog;

		StringBuffer sbConteudo = new StringBuffer(stConteudo);

		sbConteudo.insert(indexInclusaoMetodoInicializar, conteudoAdiconal);		
		
		int indiceFinalArquivo = sbConteudo.lastIndexOf("}");
		
		sbConteudo.insert(indiceFinalArquivo -1, conteudoAdicionalEspecificoFim);

		return sbConteudo.toString();
	}

	private static String ajustarImports(String stConteudo) {
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
				.replace("import br.gov.mec.aghu.util.AghuParametrosEnum;",
						"import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;");

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

		stConteudo = stConteudo.replace(
				"import br.gov.mec.aghu.casca.service.CascaService;",
				"import br.gov.mec.aghu.casca.business.ICascaFacade;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.exception.MECNegocioListaException;",
						"import br.gov.mec.aghu.core.exception.BaseListException;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.model.exception.MECModelException;",
						"import br.gov.mec.aghu.core.exception.BaseRuntimeException;");

		stConteudo = stConteudo
				.replace("import br.gov.mec.aghu.casca.CascaException;",
						"import br.gov.mec.aghu.core.exception.ApplicationBusinessException;");

		stConteudo = stConteudo.replace(
				"import br.gov.mec.util.DateConstants;",
				"import br.gov.mec.aghu.core.utils.DateConstants;");

		stConteudo = stConteudo.replace(
				"import br.gov.mec.seam.util.email.EmailUtil;",
				"import br.gov.mec.aghu.core.mail.EmailUtil;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.seam.business.exception.NegocioExceptionCode",
						"import br.gov.mec.aghu.core.exception.BusinessExceptionCode");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.action.AGHURelatorioController;",
						"import br.gov.mec.aghu.action.report.ActionReport;\nimport javax.annotation.PostConstruct;\nimport org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.action.AGHUController;",
						"import br.gov.mec.aghu.core.action.ActionController;\nimport javax.annotation.PostConstruct;\nimport org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;");

		stConteudo = stConteudo
				.replace(
						"import br.gov.mec.aghu.action.AGHUPaginatorController;",
						"import br.gov.mec.aghu.core.action.ActionController;\nimport br.gov.mec.aghu.core.action.ActionPaginator;\nimport javax.annotation.PostConstruct;\nimport br.gov.mec.aghu.core.etc.DynamicDataModel;\nimport org.apache.commons.logging.Log;\nimport org.apache.commons.logging.LogFactory;");

		stConteudo = stConteudo.replace(
				"import org.jboss.seam.annotations.In;",
				"import javax.ejb.EJB;");

		stConteudo = stConteudo.replace(
				"import org.jboss.seam.annotations.Name;", "");

		stConteudo = stConteudo.replace(
				"import org.jboss.seam.annotations.security.Restrict;", "");

		return stConteudo;
	}

}
