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
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@SuppressWarnings("PMD")
public class MigradorCamadaVisualFase2 {
	
	private static  String pathBase;
	
//	private static AjustadorNamespacesTemplate ajustadorNamespacesTemplate = new AjustadorNamespacesTemplate(); 
	
//	private static AjustadorMecComponents ajustadorMecComponents = new AjustadorMecComponents();	
	
//	private static AjustadorRichComponents ajustadorRichComponents = new AjustadorRichComponents();
	
//	private static AjustadorHTMLComponents ajustadorHTMLComponents = new AjustadorHTMLComponents();
	
//	private static AjustadorJSFComponents ajustadorJSFComponents = new AjustadorJSFComponents();
	
//	private static AjustadorCommandComponents ajustadorCommandComponents = new AjustadorCommandComponents();
	
//	private static ControllersMapFactory controllersMapFactory; 
	
	private static DocumentFactory documentFactory; 
	
	private static List<String> ajustesModulosClientes = new ArrayList<>();	

	private static final Log LOG = LogFactory.getLog(MigradorCamadaVisualFase2.class);
	

	
	/**
	 * 
	 * primeiro argumento deve ser o camiho p/ o workspace ex: '/home/geraldo/AGHU/workspace-transformacao'
	 * segundo argumento em diante são os caminhos para os arquivos ex '/administracao/visualizarLogServidorAplicacao.xhtml'
	 * 
	 * @param args
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws DocumentException, IOException    {	
		
		String workspaceLocal = args[0]; // /home/geraldo/AGHU/workspace-transformacao
		
		pathBase = workspaceLocal + "/aghu/aghu-web/src/main/webapp/pages";
		
	//	controllersMapFactory = new ControllersMapFactory(workspaceLocal);
		
		documentFactory = new DocumentFactory(workspaceLocal);
		
		if (args.length == 1){ //executar es todos os xhtml do sistema
		
		inicializarMapModulos();
		
		for (String entry : ajustesModulosClientes) {
			LOG.info("Iniciando processo de ajuste da camada visual do modulo: "+ entry);
			File folderSourceCodeModule = new File (pathBase + entry);
			processaArquivos(folderSourceCodeModule);			
		}
		
	//	ajustadorCommandComponents.listarSilks();	
		LOG.info("Terminado processamento dos arquivos da camada visual!!!");
		
		}else{
			String[] arquivos = ArrayUtils.removeElement(args, args[0]);
			for (String arquivo : arquivos){
				File arquivoLido = new File(pathBase+arquivo);
				processarArquivo(arquivoLido);
			}
		}
		
		
		
		
		
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

			LOG.info("Iniciando processamento do arquivo " + arquivo.getName());

			Document doc = documentFactory.createFase2(arquivo);

			ajustarComponentes(doc);

			writeDocument(doc, arquivo);

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}

	}











	private static void ajustarComponentes(Document doc) {
		List<Element> inputTextDataList = obterElementos(doc, "//mec:inputTextData");		
		ajustarACommandButton(inputTextDataList);
		
		
		List<Element> commandLinkList = obterElementos(doc, "//mec:commandLink");		
		ajustarCommandLink(commandLinkList);
		
		List<Element> commandButtonList = obterElementos(doc, "//mec:commandButton");		
		ajustarCommandButton(commandButtonList);
		
		List<Element> serverDataTableList = obterElementos(doc, "//mec:serverDataTable");		
		ajustarserverDataTable(serverDataTableList);
		
		List<Element> inputNumeroList = obterElementos(doc, "//mec:inputNumero");		
		ajustarInputNumero(inputNumeroList);
		
		List<Element> calendarList = obterElementos(doc, "//rich:calendar");		
		ajustarRichCalendar(calendarList);
		
		List<Element> selectionOneMenuList = obterElementos(doc, "//mec:selectionOneMenu");		
		ajustarSelectOneMenu(selectionOneMenuList);
		
		List<Element> dialogList = obterElementos(doc, "//p:dialog");		
		ajustarDialog(dialogList);
		
		List<Element> fieldList = obterElementos(doc, "//mec:field");		
		ajustarMecField(fieldList);
		
		
	}



	
	
	
	
	private static void ajustarMecField(List<Element> fieldList) {
		for (Element fieldItem : fieldList) {

			Attribute posicaoLabelAttribute = fieldItem
					.attribute("posicaoLabel");
			if (posicaoLabelAttribute != null) {
				String valuePosicaoLabelAttribute = posicaoLabelAttribute.getValue();
				if (valuePosicaoLabelAttribute
						.equals("#{mf:getDefaultValue(posicaoLabel,'acima')}")) {
					fieldItem.remove(posicaoLabelAttribute);
				}
			}

		}

	}



	private static void ajustarDialog(List<Element> dialogList) {
		for (Element dialogElement : dialogList){
			
			Attribute idAttribute = dialogElement.attribute("id");
			if (idAttribute != null){
				String idValue = idAttribute.getValue();
				if (idValue.contains("loadingModalBox")){
					dialogElement.detach();
				}
			}
			
			Attribute domElementAttachmentAttribute = dialogElement.attribute("domElementAttachment");
			if (domElementAttachmentAttribute != null){
				dialogElement.remove(domElementAttachmentAttribute);
			}
			
			
			List<Element> facets = dialogElement.elements("facet");
			for (Element facet : facets){
				if (facet.attributeValue("name").equals("controls")){
					facet.detach();
					dialogElement.addAttribute("closable", "true");
				}
				
			}
			
		}
		
	}



	private static void ajustarSelectOneMenu(List<Element> selectionOneMenuList) {
		for (Element selectionOneMenuItem : selectionOneMenuList){
			
			Attribute noSelectionLabelAttribute = selectionOneMenuItem.attribute("noSelectionLabel");
			if (noSelectionLabelAttribute != null){
				selectionOneMenuItem.remove(noSelectionLabelAttribute);
			}
			
			
		}
		
	}



	private static void ajustarRichCalendar(List<Element> calendarList) {
		for (Element calendarElement : calendarList){
			calendarElement.setQName(new QName("calendar",
					new Namespace("p",
							"http://primefaces.org/ui")));
			
			calendarElement.remove(new Namespace("rich",
					"http://richfaces.org/rich"));
			
			
			Attribute popupAttribute = calendarElement.attribute("popup");		
			String valuePopupAttribute = "true";
			if (popupAttribute != null){
				valuePopupAttribute = popupAttribute.getValue();				
			}
			if (valuePopupAttribute.equals("true")){
				calendarElement.addAttribute("mode", "popup");
			}else{
				calendarElement.addAttribute("mode", "inline");
			}
			
			Attribute showFooterAttribute = calendarElement.attribute("showFooter");
			String valueShowFooterAttribute = "true";
			if (showFooterAttribute != null){
				valueShowFooterAttribute = showFooterAttribute.getValue();
			}
			if (valueShowFooterAttribute.equals("true")){
				calendarElement.addAttribute("showButtonPanel", "true");
			}else{
				calendarElement.addAttribute("showButtonPanel", "false");
			}
			
			Attribute datePatternAttribute = calendarElement.attribute("datePattern");
			if (datePatternAttribute != null){
				String valueDatePatternAttribute = datePatternAttribute.getValue();
				calendarElement.remove(datePatternAttribute);
				calendarElement.addAttribute("pattern", valueDatePatternAttribute);
			}
			
			
			calendarElement.addAttribute("showOn", "button");
			
		}
		
	}



	private static void ajustarInputNumero(List<Element> inputNumeroList) {
		for (Element inputNumeroElement : inputNumeroList){
			
			Attribute converterAttribute = inputNumeroElement.attribute("converter");
			if (converterAttribute != null){
				inputNumeroElement.remove(converterAttribute);
			}
			
		}
		
	}



	private static void ajustarserverDataTable(List<Element> serverDataTableList) {
		for (Element serverDataTableElement : serverDataTableList){
			
			Attribute barraRolagemAttribute = serverDataTableElement.attribute("barraRolagem");
			if (barraRolagemAttribute != null){
				serverDataTableElement.remove(barraRolagemAttribute);
			}
			
			Attribute blockStatusAttribute = serverDataTableElement.attribute("blockStatus");
			if (blockStatusAttribute != null){
				serverDataTableElement.remove(blockStatusAttribute);
			}
			
		}
		
	}



	private static void ajustarCommandButton(List<Element> commandButtonList) {
		for (Element commandButtonElement : commandButtonList){
			Attribute profileAttribute = commandButtonElement.attribute("profile");
			if (profileAttribute == null){
				Attribute styleClassAttribute = commandButtonElement.attribute("styleClass");
				if (styleClassAttribute == null){
					commandButtonElement.addAttribute("styleClass", "bt_cinza");
				}else{
					String valueStyleClassAttribute = styleClassAttribute.getValue();
					if (!valueStyleClassAttribute.contains("bt_cinza")) {
						styleClassAttribute.setValue(valueStyleClassAttribute
								+ " bt_cinza");
					}
				}
				
			}
			
		
		}
		
	}



	private static void ajustarCommandLink(List<Element> commandLinkList) {
		for (Element commandLinkElement : commandLinkList) {

			Attribute statusAttribute = commandLinkElement.attribute("status");
			if (statusAttribute != null) {
				commandLinkElement.remove(statusAttribute);
			}

			Attribute classAttribute = commandLinkElement
					.attribute("styleClass");
			if (classAttribute != null) {
				String valueAttributeClasses = classAttribute.getValue();
				if (!valueAttributeClasses.contains("silk-icon")) {
					String[] classes = valueAttributeClasses.split(" ");
					boolean temsilk = false;
					for (String classe : classes) {
						if (classe.contains("silk")) {
							temsilk = true;
						}
					}
					if (temsilk) {
						classAttribute.setValue(valueAttributeClasses
								+ " silk-icon");
					}
				}
			}

		}

	}



	private static void ajustarACommandButton(List<Element> inputTextDataList) {
		for (Element inputTextDataElement : inputTextDataList){
			
			Attribute ajaxValidationAttribute = inputTextDataElement.attribute("ajaxValidation");
			if (ajaxValidationAttribute != null){
				inputTextDataElement.remove(ajaxValidationAttribute);
			}
			
			
		}
		
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
		uris.put("p", "http://primefaces.org/ui");
		
		
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
		
	//	ajustesModulosClientes.add("/casca/acesso");

	}




}
