package br.gov.mec.aghu.util.migrador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@SuppressWarnings({ "PMD" })
public class MigradorCamadaVisual {
	
	private static  String PATH_BASE;
	
	private static AjustadorNamespacesTemplate ajustadorNamespacesTemplate = new AjustadorNamespacesTemplate(); 
	
	private static AjustadorMecComponents ajustadorMecComponents = new AjustadorMecComponents();	
	
	private static AjustadorRichComponents ajustadorRichComponents = new AjustadorRichComponents();
	
	private static AjustadorHTMLComponents ajustadorHTMLComponents = new AjustadorHTMLComponents();
	
	private static AjustadorJSFComponents ajustadorJSFComponents = new AjustadorJSFComponents();
	
	private static AjustadorCommandComponents ajustadorCommandComponents = new AjustadorCommandComponents();
	
	private static ControllersMapFactory controllersMapFactory; 
	
	private static DocumentFactory documentFactory; 
	
	private static List<String> ajustesModulosClientes = new ArrayList<>();	

	private static final Log LOG = LogFactory.getLog(MigradorCamadaVisual.class);
	

	
	/**
	 * Esta classe transforma o arquivo da versão 6.0 para versão 6.+ <br />
	 * Entao eh necessário copiar o arquivo da versao 6.0 para a workspace da 6.+<br />
	 * 
	 * Primeiro argumento deve ser o camiho p/ o workspace da 6.+. <br /> 
	 * Ex: '/home/geraldo/AGHU/workspace-transformacao' <br />
	 * Segundo argumento em diante são os caminhos para os arquivos, <br /> 
	 * onde o caminho deve ser  a paritr da pasta pages. <br />
	 * Ex '/administracao/visualizarLogServidorAplicacao.xhtml'
	 * 
	 * @param args
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws DocumentException, IOException {
		System.out.println("Iniciando migracao ...");
		
		String workspaceLocal = args[0]; // /home/geraldo/AGHU/workspace-transformacao
		
		PATH_BASE = workspaceLocal + "/aghu/aghu-web/src/main/webapp/pages";
		
		controllersMapFactory = new ControllersMapFactory(workspaceLocal);
		
		documentFactory = new DocumentFactory(workspaceLocal);
		
		if (args.length == 1) { //executar es todos os xhtml do sistema
			inicializarMapModulos();
			
			for (String entry : ajustesModulosClientes) {
				LOG.info("Iniciando processo de ajuste da camada visual do modulo: "+ entry);
				File folderSourceCodeModule = new File (PATH_BASE + entry);
				processaArquivos(folderSourceCodeModule);			
			}
			
			ajustadorCommandComponents.listarSilks();	
			LOG.info("Terminado processamento dos arquivos da camada visual!!!");
			
		} else {
			
			String[] arquivos = ArrayUtils.removeElement(args, args[0]);
			for (String arquivo : arquivos){
				File arquivoLido = new File(PATH_BASE+arquivo);
				processarArquivo(arquivoLido);
			}
		}
		
		System.out.println("Migracao feita!!!!");
	}
	
	
	
	private static void processaArquivos(File baseDir)   {
		File[] files = baseDir.listFiles();

		for (File file : files) {
			if (file.getName().endsWith("svn")) {
				continue;
			}
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				processarArquivo(file);
			}
		}
	}
	
	
	private static void processarArquivo(File arquivo) {
		try {
			LOG.info("Arquivo [" + arquivo.getName() + "] - Iniciando processamento ...");

			Document doc = documentFactory.create(arquivo);

			doc = ajustadorNamespacesTemplate.ajustar(doc);
			
			if (doc == null){ //Arquivo já migrado
				LOG.error("Arquivo já migrado!!!");
				return;
			}

			ajustarComponentesMec(doc);

			ajustarComponentesRichfaces(doc);

			ajustarComponentesComandos(doc);

			List<Element> fieldsetList = obterElementos(doc, "//html:fieldset");

			ajustadorHTMLComponents.ajustarFieldSet(fieldsetList);

			List<Element> setPropertyActionListenerList = obterElementos(doc,
					"//f:setPropertyActionListener");

			ajustadorJSFComponents
					.ajustarSetPropertyActionListener(setPropertyActionListenerList);

			List<Element> validateLongRangeList = obterElementos(doc,
					"//f:validateLongRange");

			ajustadorJSFComponents
					.ajustarValidateLongRange(validateLongRangeList);
			
			List<Element> uiParamList = obterElementos(doc,
					"//ui:param");

			for(Element uiParamElement : uiParamList){
				String nameAttributeValue = uiParamElement.attributeValue("name");
				if (nameAttributeValue.equals("stopEnter")){
					LOG.warn("Encontrada tag <ui:param name=\"stopenter\"/> subistituir pela tag pageConfig com ignoreEnter=\"true\"");
				}
			}
			
			

			verificarJavaScript(doc);

			doc = ajustarJavascript(doc);

			writeDocument(doc, arquivo);

			LOG.info("Arquivo [" + arquivo.getName() + "] - Processamento Finalizado!");
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}

	}



	private static void verificarJavaScript(Document doc) {
		boolean achouJavaScript = false;
		List<Element> javaScriptList = obterElementos(doc, "//html:script");
		if (javaScriptList != null && !javaScriptList.isEmpty()) {
			achouJavaScript = true;
		} else {
			List<Element> uiDefineList = obterElementos(doc, "//ui:define");
			for (Element uiDefineElement : uiDefineList) {
				if (uiDefineElement.attributeValue("name").equals("script")) {
					achouJavaScript = true;
				}
			}
		}
		
		if (achouJavaScript){
			LOG.warn("------------- Javascript encontrado da página. É preciso avaliar!");
		}
		
		
	}



	private static Document ajustarJavascript(Document document) throws DocumentException {
		String stXHTML = document.asXML();
		
		int indexRichElement = stXHTML.indexOf("#{rich:element(");
		while (indexRichElement != -1) {			
			int indexFechaColchetes = stXHTML.indexOf('}', indexRichElement);
			String idElement = stXHTML.substring(indexRichElement + 15,
					indexFechaColchetes -1);
			String chamadaSubistituir = "Document.getElementById(" + idElement
					+ ")";
			String chamadaSubistituida = "#{rich:element(" + idElement + ")}";
			stXHTML = stXHTML.replace(chamadaSubistituida, chamadaSubistituir);
			indexRichElement = stXHTML.indexOf("#{rich:element(",
					indexRichElement+15);

		}
		
		if (stXHTML.contains("jQuery(")){
			LOG.warn("------------- Econtrada chamada ao javascript do jquery. É preciso avaliar.");
		}
		
		return DocumentHelper.parseText(stXHTML);
	}



	private static void ajustarComponentesComandos(Document doc) {
		List<Element> aCommandButtonList = obterElementos(doc, "//a:commandButton");		
		
		ajustadorCommandComponents.ajustarACommandButton(aCommandButtonList);
		
		List<Element> aCommandLinkList = obterElementos(doc, "//a:commandLink");		
		
		ajustadorCommandComponents.ajustarACommandLink(aCommandLinkList);

		List<Element> commandButtonList = obterElementos(doc, "//h:commandButton");		
		
		ajustadorCommandComponents.ajustarCommandButton(commandButtonList);
		
		List<Element> commandLinkList = obterElementos(doc, "//h:commandLink");		
		
		ajustadorCommandComponents.ajustarCommandLink(commandLinkList);
	}



	private static void ajustarComponentesRichfaces(Document doc) {
		List<Element> tooltipList = obterElementos(doc, "//rich:toolTip");		
		
		ajustadorRichComponents.ajustarRichTooltip(tooltipList);
		
		List<Element> modalPanelList = obterElementos(doc, "//rich:modalPanel");		
		
		ajustadorRichComponents.ajustarModalPanel(modalPanelList);		
		
		List<Element> dataTableList = obterElementos(doc, "//rich:dataTable");			
		
		ajustadorRichComponents.ajustarDataTable(dataTableList);
		
		List<Element> aOutputPanelList = obterElementos(doc, "//a:outputPanel");		
		
		ajustadorRichComponents.ajustarAOutputPanel(aOutputPanelList);		
		
		List<Element> simpleTogglePanelList = obterElementos(doc, "//rich:simpleTogglePanel ");		
		
		ajustadorRichComponents.ajustarSimpleTogglePanel(simpleTogglePanelList);
		
		List<Element> supportList = obterElementos(doc, "//a:support");		
		
		ajustadorRichComponents.ajustarSupport(supportList);
		
		List<Element> pollList = obterElementos(doc, "//a:poll");	
		
		ajustadorRichComponents.ajustarPoll(pollList);
		
		List<Element> formList = obterElementos(doc, "//a:form");	
		
		ajustadorRichComponents.ajustarAForm(formList);
		
		List<Element> jsFunctionList = obterElementos(doc, "//a:jsFunction");	
		
		ajustadorRichComponents.ajustarJsFunction(jsFunctionList);	
		
		List<Element> mediaOutputList = obterElementos(doc, "//a:mediaOutput");	
		
		ajustadorRichComponents.ajustarMediaOutput(mediaOutputList);
		
		List<Element> tabPanelList = obterElementos(doc, "//rich:tabPanel");	
		
		ajustadorRichComponents.ajustarTabPanel(tabPanelList);
		
		List<Element> regionList = obterElementos(doc, "//a:region");	
		
		if (regionList != null && ! regionList.isEmpty()){
			LOG.warn("<a:region> encontradas! É preciso verificar!");
		}
		
		List<Element> fileUploadList = obterElementos(doc, "//rich:fileUpload");	
		
		if (fileUploadList != null && ! fileUploadList.isEmpty()){
			LOG.warn("<rich:fileUpload> encontradas! É preciso verificar!");
		}
		
		List<Element> progressBarList = obterElementos(doc, "//rich:progressBar");	
		
		if (progressBarList != null && ! progressBarList.isEmpty()){
			LOG.warn("<rich:progressBar> encontradas! É preciso verificar!");
		}
		
		
	}







	private static void ajustarComponentesMec(Document doc) throws IOException {
		List<Element> mecInputTextList = obterElementos(doc, "//mec:inputText");		
		
		ajustadorMecComponents.ajustarInputText(mecInputTextList);
		
		List<Element> mecInputTextAreaList = obterElementos(doc, "//mec:inputTextArea");		
		
		ajustadorMecComponents.ajustarInputTextArea(mecInputTextAreaList);		
		
		List<Element> mecInputNumeroList = obterElementos(doc, "//mec:inputNumero");		
		
		ajustadorMecComponents.ajustarInputNumero(mecInputNumeroList);
		
		List<Element> mecInputNumeroDecimalList = obterElementos(doc, "//mec:inputNumeroDecimal");		
		
		ajustadorMecComponents.ajustarInputNumeroDecimal(mecInputNumeroDecimalList);		
		
		List<Element> inputTextDataList = obterElementos(doc, "//mec:inputTextData");		
		
		ajustadorMecComponents.ajustarInputTextData(inputTextDataList);
		
		List<Element> inputTextDataHoraList = obterElementos(doc, "//mec:inputTextDataHora");		
		
		ajustadorMecComponents.ajustarInputTextDataHora(inputTextDataHoraList);
		 
		List<Element> inputMesAnoList = obterElementos(doc, "//mec:inputMesAno");		
		
		ajustadorMecComponents.ajustarInputMesAno(inputMesAnoList);		
		
		List<Element> inputTextCEPList = obterElementos(doc, "//mec:inputTextCEP");		
		
		ajustadorMecComponents.ajustarInputCEP(inputTextCEPList);
		
		List<Element> inputTextCNPJList = obterElementos(doc, "//mec:inputTextCNPJ");		
		
		ajustadorMecComponents.ajustarInputCNPJ(inputTextCNPJList);
		
		List<Element> inputTextCPFList = obterElementos(doc, "//mec:inputTextCPF");		
		
		ajustadorMecComponents.ajustarInputCPF(inputTextCPFList);		
		
		List<Element> inputTextProntuarioList = obterElementos(doc, "//mec:inputTextProntuario");		
		
		ajustadorMecComponents.ajustarInputTextProntuario(inputTextProntuarioList);
		
		List<Element> inputTextInfoList = obterElementos(doc, "//mec:inputTextProntuario");		
		
		ajustadorMecComponents.ajustarInputTextInfo(inputTextInfoList);		
		 
		List<Element> mecSelectOneMenuList = obterElementos(doc, "//mec:selectOneMenu");		
		
		ajustadorMecComponents.ajustarSelectOneMenu(mecSelectOneMenuList);
		
		List<Element> mecSelectOneRadioList = obterElementos(doc, "//mec:selectOneRadio");		
		
		ajustadorMecComponents.ajustarSelectOneRadio(mecSelectOneRadioList);
		
		List<Element> mecSuggestionBoxList = obterElementos(doc, "//mec:suggestionBox");		
		
		ajustadorMecComponents.ajustarSuggestionBox(mecSuggestionBoxList);		
		
		List<Element> mecCommandButtonList = obterElementos(doc, "//mec:commandButton");		
		
		ajustadorCommandComponents.ajustarMECCommandButton(mecCommandButtonList);
		
		List<Element> mecAjaxCommandButtonList = obterElementos(doc, "//mec:ajaxCommandButton");		
		
		ajustadorCommandComponents.ajustarAjaxCommandButton(mecAjaxCommandButtonList);
		
		List<Element> pageConfigList = obterElementos(doc, "//mec:pageConfig");		
		
		ajustadorMecComponents.ajustarPageConfig(pageConfigList);
		
		List<Element> serverDataTableList = obterElementos(doc, "//mec:serverDataTable");		
		
		ajustadorMecComponents.ajustarServerDataTable(serverDataTableList);		
		
		List<Element> scrollDataTableList = obterElementos(doc, "//mec:scrollDataTable");		
		
		ajustadorMecComponents.ajustarScrollDataTable(scrollDataTableList);
		
		List<Element> cancelButtonList = obterElementos(doc, "//mec:cancelButton");		
		
		ajustadorCommandComponents.ajustarCancelButton(cancelButtonList);
		
		List<Element> selectBooleanCheckBoxList = obterElementos(doc, "//mec:selectBooleanCheckbox");		
		
		ajustadorMecComponents.ajustarSelectBooleanCheckBox(selectBooleanCheckBoxList);
		
		List<Element> inputTextMesAnoList = obterElementos(doc, "//mec:inputTextMesAno");		
		
		ajustadorMecComponents.ajustarinputTextMesAno(inputTextMesAnoList);
	}
	
	
	
	private static List<Element> obterElementos (Document doc, String stXpath){
		
		Map<String, String> uris = new HashMap<String, String>();
		uris.put("mec", "http://xmlns.jcp.org/jsf/composite/components");
		uris.put("rich", "http://richfaces.org/rich");
		uris.put("a", "http://richfaces.org/a4j");
		uris.put("h", "http://xmlns.jcp.org/jsf/html");
		uris.put("f", "http://xmlns.jcp.org/jsf/core");
		uris.put("html", "http://www.w3.org/1999/xhtml");
		uris.put("ui", "http://xmlns.jcp.org/jsf/facelets");
		
		
		XPath xpath = doc.createXPath(stXpath);
		xpath.setNamespaceURIs(uris);		
		
		return xpath.selectNodes(doc);
	}
	
	
	
	
	




	private static void writeDocument(Document doc, File arquivo) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();		 
		 XMLWriter writer = new XMLWriter(new FileWriter(arquivo),format);		 
		 writer.write(doc);		 
		 writer.flush();
	}
	
	
	private static void inicializarMapModulos() {
		ajustesModulosClientes.add("/administracao");
		ajustesModulosClientes.add("/aghu");		
		ajustesModulosClientes.add("/ambulatorio");		
		ajustesModulosClientes.add("/bancodesangue");
		ajustesModulosClientes.add("/blococirurgico");
		
		
		ajustesModulosClientes.add("/casca");
		ajustesModulosClientes.add("/certificacaodigital");
		ajustesModulosClientes.add("/comissoes");
		ajustesModulosClientes.add("/compras");
		ajustesModulosClientes.add("/consultas");
		ajustesModulosClientes.add("/controleinfeccao");
		ajustesModulosClientes.add("/controlepaciente");
		ajustesModulosClientes.add("/cups");
		
		ajustesModulosClientes.add("/diagnostico");
		ajustesModulosClientes.add("/estoque");
		ajustesModulosClientes.add("/exames");
		ajustesModulosClientes.add("/farmacia");
		ajustesModulosClientes.add("/faturamento");
		ajustesModulosClientes.add("/financeiro");
		ajustesModulosClientes.add("/indicadores");
		ajustesModulosClientes.add("/layout");
		ajustesModulosClientes.add("/orcamento");
		
		ajustesModulosClientes.add("/internacao");
		ajustesModulosClientes.add("/parametrosistema");
		ajustesModulosClientes.add("/prescricaoenfermagem");
		ajustesModulosClientes.add("/prescricaomedica");
		ajustesModulosClientes.add("/registrocolaborador");
		ajustesModulosClientes.add("/sicon");
		
		ajustesModulosClientes.add("/sig");
		ajustesModulosClientes.add("/suprimentos");
		
		
		ajustesModulosClientes.add("/paciente");

	}




}
