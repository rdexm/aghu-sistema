package br.gov.mec.aghu.util.migrador;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

@SuppressWarnings({ "PMD" })
public class AjustadorMecComponents {
	
	
	private AjustadorRichComponents ajustadorRichComponents = new AjustadorRichComponents();
	
	private static final Log LOG = LogFactory.getLog(AjustadorMecComponents.class);

	public void ajustarInputText(List<Element> mecInputTextList) {
		for (Element inputTextElement : mecInputTextList) {
			ajustarNameId(inputTextElement);
			
			Attribute attributeJSFConverter = inputTextElement.attribute("jsfConverter");			
			if (attributeJSFConverter != null) {
				String valueJSFConverter = attributeJSFConverter.getValue();
				inputTextElement.remove(attributeJSFConverter);
				inputTextElement.addAttribute("converter", valueJSFConverter);
			}
			
			Attribute attributeStyleClass = inputTextElement.attribute("styleClass");
			if (attributeStyleClass != null){
				String styleClassValue = attributeStyleClass.getValue();
				if (styleClassValue.contains("mask_prontuario")){
					inputTextElement.setQName(new QName("inputTextProntuario",
							new Namespace("mec",
									"http://xmlns.jcp.org/jsf/composite/components")));		
					inputTextElement.remove(attributeStyleClass);
				}
			}
			
		}

	}

	public void ajustarInputTextArea(List<Element> mecInputTextAreaList) {
		for (Element inputTextAreaElement : mecInputTextAreaList) {

			ajustarNameId(inputTextAreaElement);

			if (inputTextAreaElement.attributeValue("styleClass") != null) {
				String styleClassValue = inputTextAreaElement
						.attributeValue("styleClass");
				String[] stylesClasses = styleClassValue.split(" ");
				String classRemover = null;
				for (String styleclass : stylesClasses) {
					if (styleclass.contains("maxlenght_")) {
						classRemover = styleclass;
						String valorMaxLength = styleclass.substring(styleclass
								.indexOf('_') + 1);
						inputTextAreaElement.addAttribute("maxlength",
								valorMaxLength);
						break;
					}
				}

				if (classRemover != null) {
					styleClassValue = styleClassValue.replace(classRemover, "");
				}
				inputTextAreaElement.remove(inputTextAreaElement
						.attribute("styleClass"));
				if (!StringUtils.isBlank(styleClassValue)) {
					inputTextAreaElement.addAttribute("styleClass",
							styleClassValue);
				}
			}

		}

	}

	public void ajustarInputNumero(List<Element> mecInputNumeroList) {
		for (Element inputNumeroElement : mecInputNumeroList) {

			ajustarNameId(inputNumeroElement);
			
			Attribute attributeJSFConverter = inputNumeroElement.attribute("jsfConverter");
			
			if (attributeJSFConverter != null) {
				String valueJSFConverter = attributeJSFConverter.getValue();
				inputNumeroElement.remove(attributeJSFConverter);
				inputNumeroElement.addAttribute("converter", valueJSFConverter);
			}
			
			Attribute attributeFieldStyle = inputNumeroElement.attribute("fieldStyle");
			if (attributeFieldStyle != null){
				inputNumeroElement.remove(attributeFieldStyle);
			}

		}

	}

	public void ajustarSelectOneMenu(List<Element> mecSelectOneMenuList) {
		for (Element selectOneMenuElement : mecSelectOneMenuList) {
			ajustarNameId(selectOneMenuElement);
		}

	}

	public void ajustarInputNumeroDecimal(
			List<Element> mecInputNumeroDecimalList) {
		for (Element inputNumeroDecimalElement : mecInputNumeroDecimalList) {
			ajustarNameId(inputNumeroDecimalElement);

			inputNumeroDecimalElement.setQName(new QName("inputNumero",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));

		}
	}

	public void ajustarInputTextData(List<Element> inputTextDataList) {
		for (Element inputTextDataElement : inputTextDataList) {
			ajustarNameId(inputTextDataElement);

			inputTextDataElement.addAttribute("tipo", "data");
			
			Attribute attributeFieldStyle = inputTextDataElement.attribute("fieldStyle");
			if (attributeFieldStyle != null){
				inputTextDataElement.remove(attributeFieldStyle);
			}

		}

	}

	public void ajustarInputTextDataHora(List<Element> inputTextDataHoraList) {
		for (Element inputTextDataHoraElement : inputTextDataHoraList) {
			ajustarNameId(inputTextDataHoraElement);
			inputTextDataHoraElement.addAttribute("tipo", "datahora");

			inputTextDataHoraElement.setQName(new QName("inputTextData",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));
			
			Attribute attributeFieldStyle = inputTextDataHoraElement.attribute("fieldStyle");
			if (attributeFieldStyle != null){
				inputTextDataHoraElement.remove(attributeFieldStyle);
			}
			
			//inputTextDataHoraElement.remove(new Namespace("mec", "http://java.sun.com/jsf/composite/components"));
			
			

		}

	}

	public void ajustarInputMesAno(List<Element> inputMesAnoList) {
		for (Element inputMesAnoElement : inputMesAnoList) {
			ajustarNameId(inputMesAnoElement);
		}

	}

	public void ajustarInputCEP(List<Element> inputTextCEPList) {
		for (Element inputTextCEPElement : inputTextCEPList) {
			ajustarNameId(inputTextCEPElement);
		}

	}

	public void ajustarInputCNPJ(List<Element> inputTextCNPJList) {
		for (Element inputTextCNPJElement : inputTextCNPJList) {
			ajustarNameId(inputTextCNPJElement);
		}

	}

	public void ajustarInputCPF(List<Element> inputTextCPFList) {
		for (Element inputTextCPFElement : inputTextCPFList) {
			ajustarNameId(inputTextCPFElement);
		}

	}

	public void ajustarInputTextProntuario(List<Element> inputTextProntuarioList) {
		for (Element inputTextProntuarioElement : inputTextProntuarioList) {
			ajustarNameId(inputTextProntuarioElement);
		}

	}

	public void ajustarInputTextInfo(List<Element> inputTextInfoList) {
		for (Element inputTextInfoElement : inputTextInfoList) {
			ajustarNameId(inputTextInfoElement);
		}

	}

	public void ajustarSelectOneRadio(List<Element> mecSelectOneRadioList) {
		for (Element selectOneRadioElement : mecSelectOneRadioList) {
			ajustarNameId(selectOneRadioElement);

			String reRenderValue = selectOneRadioElement
					.attributeValue("reRender");
			if (reRenderValue != null) {
				String updateValue = this
						.customPrefixAjaxRender(reRenderValue);
				selectOneRadioElement.remove(selectOneRadioElement
						.attribute("reRender"));
				Element pAjaxElement = selectOneRadioElement.addElement(
						"p:ajax", "http://primefaces.org/ui");
				pAjaxElement.addAttribute("update", updateValue);
				pAjaxElement.addAttribute("event", "change");
			}

		}

	}

	public void ajustarSuggestionBox(List<Element> mecSuggestionBoxList) throws IOException {
		for (Element suggestionBoxElement : mecSuggestionBoxList) {
			ajustarNameId(suggestionBoxElement);
			
			Attribute attributeJsfConverter = suggestionBoxElement.attribute("jsfConverter");
			if (attributeJsfConverter != null){
				suggestionBoxElement.remove(attributeJsfConverter);
			}
			
			Attribute attributeNothingLabel = suggestionBoxElement.attribute("nothingLabel");
			if (attributeNothingLabel != null){
				suggestionBoxElement.remove(attributeNothingLabel);
			}
			
			Attribute attributePesquisaAutomatica = suggestionBoxElement.attribute("pesquisaAutomatica");
			if (attributePesquisaAutomatica != null){
				suggestionBoxElement.remove(attributePesquisaAutomatica);
			}

			String valorMbean = suggestionBoxElement.attributeValue("mbean");
			String valorValueOld = suggestionBoxElement.attributeValue("value");
			StringBuilder sbValorValueNew = new StringBuilder(valorMbean);
			sbValorValueNew.insert(valorMbean.lastIndexOf('}'), "."
					+ valorValueOld);
			suggestionBoxElement
					.remove(suggestionBoxElement.attribute("mbean"));
			suggestionBoxElement
					.remove(suggestionBoxElement.attribute("value"));
			suggestionBoxElement.addAttribute("controller", valorMbean);
			suggestionBoxElement.addAttribute("value",
					sbValorValueNew.toString());

			String valorReRender = suggestionBoxElement
					.attributeValue("reRender");
			if (valorReRender != null) {
				suggestionBoxElement.remove(suggestionBoxElement
						.attribute("reRender"));
				suggestionBoxElement.addAttribute("render", valorReRender);
			}

			Iterator<Element> hColumnsIterator = suggestionBoxElement
					.elementIterator("column");

			while (hColumnsIterator.hasNext()) {
				Element elemento = hColumnsIterator.next();
			
				
				
				List contentHcollum = elemento.content();
				
				
				suggestionBoxElement.remove(elemento);
				Element pColumnElement = suggestionBoxElement.addElement(
						"p:column", "http://primefaces.org/ui");
				pColumnElement.setContent(contentHcollum);
			}
			
			//incluir ajustes na controller
			
			String valorSuggestionAction = suggestionBoxElement.attributeValue("suggestionAction");			
			String valorMethodCount = suggestionBoxElement.attributeValue("methodCount");			
			ajustarControllerSuggestionBox(valorMbean, valorSuggestionAction, valorMethodCount);
			if (valorMethodCount != null) {
				suggestionBoxElement.remove(suggestionBoxElement
						.attribute("methodCount"));
			}

		}

	}

	private void ajustarControllerSuggestionBox(String valorMbean,
			String valorSuggestionAction, String valorMethodcount) throws IOException {
		
		if (valorSuggestionAction == null){
			LOG.error("Encontrada SuggestionBox sem o atributo suggestionAction. É preciso verificar.");
			return;
		}
		
		String nomeController = valorMbean.replace("#", "").replace("{", "").replace("}", "");		
		String primeiraLetra = String.valueOf(nomeController.charAt(0)).toUpperCase();		
		nomeController = nomeController.replaceFirst(String.valueOf(nomeController.charAt(0)), primeiraLetra);		
		File controller = new File (ControllersMapFactory.controllersMap.get(nomeController));		
		StringBuilder sbController = new StringBuilder(FileUtils.readFileToString(controller));	
		
		
		int indexMetodoSuggestion = sbController.indexOf(valorSuggestionAction+"(");	
		
		int indexObject = sbController.indexOf("Object", indexMetodoSuggestion);
		
		String stNomeParametro = sbController.substring(indexObject+7, sbController.indexOf(")",indexObject));
		
		
		int indexAbreChaves = sbController.indexOf("{", indexMetodoSuggestion);
		if (indexObject != -1 && indexObject < indexAbreChaves){ //para o caso do método já ter sido ajustado.
			sbController.replace(indexObject, indexObject + 6, "String");
			
			if (StringUtils.isNoneBlank(valorMethodcount)) {
				int indexReturn = sbController.indexOf("return",
						indexMetodoSuggestion);
				int indexPontoVirgulaFimReturn = sbController.indexOf(";",
						indexReturn);
				String stReturn = sbController.substring(indexReturn + 7,
						indexPontoVirgulaFimReturn);
				sbController.replace(indexReturn, indexPontoVirgulaFimReturn,
						"return  this.returnSGWithCount(" + stReturn + ","
								+ valorMethodcount + "(" + stNomeParametro
								+ "))");

				int indexMetodoCount = sbController.indexOf(valorMethodcount);
				int indexObjectMethodcount = sbController.indexOf("Object",
						indexMetodoCount);
				sbController.replace(indexObjectMethodcount,
						indexObjectMethodcount + 6, "String");
			}

			FileUtils.writeStringToFile(controller, sbController.toString());
		}

	}

	
	
	public void ajustarPageConfig(List<Element> pageConfigList) {
		for (Element pageConfigElement : pageConfigList) {
			
			Attribute attHasCancelButton = pageConfigElement.attribute("hasCancelButton");
			if (attHasCancelButton != null){
				pageConfigElement.remove(pageConfigElement.attribute("hasCancelButton"));
			}
			
			Attribute attImmediate = pageConfigElement.attribute("immediate");
			if (attImmediate != null){
				pageConfigElement.remove(pageConfigElement.attribute("immediate"));
			}
			
			Attribute attReRender = pageConfigElement.attribute("reRender");
			if (attReRender != null){
				pageConfigElement.remove(pageConfigElement.attribute("reRender"));
			}
			
			Attribute attCompleteJS = pageConfigElement.attribute("completeJS");
			if (attCompleteJS != null){
				pageConfigElement.remove(pageConfigElement.attribute("completeJS"));
			}
			
			Attribute attFocus = pageConfigElement.attribute("focus");
			if (attFocus != null){
				pageConfigElement.remove(pageConfigElement.attribute("focus"));
			}			
			
			String initMethodValue = pageConfigElement.attributeValue("initMethod");
			if (initMethodValue != null){
				String controllerValue = pageConfigElement.attributeValue("controller");
				
				StringBuilder actionValue = new StringBuilder(controllerValue);
				actionValue.insert(controllerValue.lastIndexOf('}'), "."+initMethodValue);
				
				Element elementComposition = pageConfigElement.getParent();
				while (elementComposition.getName()!= null &&  !elementComposition.getName().equals("composition")){
					elementComposition = elementComposition.getParent();
				}
				
				Element uiDefinemetadataElement = elementComposition.addElement("ui:define" , "http://xmlns.jcp.org/jsf/facelets");			
				
				uiDefinemetadataElement.addAttribute("name", "metadata");
				
				Element fMetafataElement = uiDefinemetadataElement.addElement("f:metadata", "http://xmlns.jcp.org/jsf/core");
				
				Element fViewActionElement = fMetafataElement.addElement("f:viewAction", "http://xmlns.jcp.org/jsf/core");
				
				fViewActionElement.addAttribute("action", actionValue.toString());
				
				pageConfigElement.remove(pageConfigElement.attribute("initMethod"));
				
				
				// ver isso aqui melhor depois
				List<Element> filhosUiComposition = elementComposition.elements();				
				Element el =  filhosUiComposition.remove(1);				
				filhosUiComposition.add(el);
				
				
			}
		}
		
	}
	
	
	public void ajustarServerDataTable(List<Element> serverDataTableList) throws IOException {
		for (Element serverDataTableElement : serverDataTableList) {	
			String valorDataModelAttribute = serverDataTableElement.attributeValue("dataModel");
			String valorMbeanAttribute = serverDataTableElement.attributeValue("mbean");
			ajustarDataTable(serverDataTableElement);				
			ajustarControllerDataTable(valorMbeanAttribute, valorDataModelAttribute);
		}
		
	}

	private void ajustarDataTable(Element serverDataTableElement) throws IOException {
		ajustarNameId(serverDataTableElement);	
		
		Attribute mbeanAttribute = serverDataTableElement.attribute("mbean");
		
		if (mbeanAttribute == null){
			LOG.error("Encontrado componente dataTable sem o atributo mbean. É preciso verificar");
			return;
		}
		
		String valorMbeanAttribute = serverDataTableElement.attributeValue("mbean");			
		serverDataTableElement.remove(serverDataTableElement.attribute("mbean"));
		serverDataTableElement.addAttribute("controller", valorMbeanAttribute);
		
		
		Attribute attHeaderClass = serverDataTableElement.attribute("headerClass");
		if (attHeaderClass != null){
			serverDataTableElement.remove(attHeaderClass);
		}
		
		
		String valorStyleClassAttribute = serverDataTableElement.attributeValue("styleClass");
		if (valorStyleClassAttribute != null){
			String[] styleClassValues = valorStyleClassAttribute.split(" ");
			for (String styleClass : styleClassValues){
				if ( !(styleClass.trim().equals("tabela") ||  styleClass.trim().equals("tbl_docs"))){
					LOG.warn("O styleclass " + styleClass.trim() + " será removido na tabela " + serverDataTableElement.attributeValue("id"));
				}
			}
			serverDataTableElement.remove(serverDataTableElement.attribute("styleClass"));
		}					
		
		Attribute selectionEventClass = serverDataTableElement.attribute("selectionEvent");
		if (selectionEventClass != null){
			serverDataTableElement.remove(selectionEventClass);
		}
		
		Attribute styleControlsAttribute = serverDataTableElement.attribute("styleControls");
		if (styleControlsAttribute != null){
			serverDataTableElement.remove(styleControlsAttribute);
		}
		
		Attribute onRowMouseOverAttribute = serverDataTableElement.attribute("onRowMouseOver");
		if (onRowMouseOverAttribute != null){
			LOG.warn("Encontrado atributo onRowMouseOver no serverDataTable. Subistituir pela ação selection do serverDataTable");
			serverDataTableElement.remove(onRowMouseOverAttribute);
		}
		
		Element facetElement = serverDataTableElement.element("facet");
		if (facetElement != null){
			String name = facetElement.attributeValue("name");
			if(name.equals("header")){										 
				Element columnGroupElement = facetElement.element("columnGroup");
				if (columnGroupElement != null){
					columnGroupElement.setQName(new QName("columnGroup",
							new Namespace("p",
									"http://primefaces.org/ui")));			
					
					columnGroupElement.remove(new Namespace("rich",
									"http://richfaces.org/rich"));
					
					for (Element columnHeaders : (List<Element>)columnGroupElement.elements("column")){
						ajustadorRichComponents.ajustarRichColumn(columnHeaders);
					}
				}				
			}
		}
		
		Iterator<Element> richColumnsIterator = serverDataTableElement
				.elementIterator("column");
		while (richColumnsIterator.hasNext()) {		
			Element richColumnsElement =  richColumnsIterator.next();
			ajustadorRichComponents.ajustarRichColumn(richColumnsElement);
			
		}		
		
		
		ajustarBotoesEdicaoRemocao(serverDataTableElement,
				valorMbeanAttribute);	
		
		
		
		Attribute attributeVar = serverDataTableElement.attribute("var");		
		if (attributeVar != null){		
			String valorAttributeVar = attributeVar.getValue();
			Element setElement = serverDataTableElement.addElement("c:set", "http://xmlns.jcp.org/jsp/jstl/core");			
			setElement.addAttribute("var", valorAttributeVar);
			setElement.addAttribute("value", "#{item}");			
			List<Element> filhosServerDataTable = serverDataTableElement.elements();			
			Element element =  filhosServerDataTable.remove(filhosServerDataTable.size()-1);			
			filhosServerDataTable.add(0,element);
			
			serverDataTableElement.remove(attributeVar);
		}
	
		
		Attribute atributeSelectionEvent = serverDataTableElement.attribute("selectionEvent");
		if (atributeSelectionEvent != null && atributeSelectionEvent.getValue().equals("true")){
			serverDataTableElement.remove(atributeSelectionEvent);
		}else{
			Attribute atributeSelection = serverDataTableElement.attribute("selection");
			if (atributeSelection != null) {
				serverDataTableElement.remove(atributeSelection);
			}
		}

	}

	

	private void ajustarControllerDataTable(String valorMbeanAttribute,
			String valorDataModelAttribute) throws IOException {
		
		
		String nomeController = valorMbeanAttribute.replace("#", "").replace("{", "").replace("}", "");
		if (nomeController.contains(".")){
			LOG.warn("Atributo mbean apontando para atributo nested: "+ nomeController +". É preciso verificar");
			return;
		}
		String primeiraLetra = String.valueOf(nomeController.charAt(0)).toUpperCase();		
		nomeController = nomeController.replaceFirst(String.valueOf(nomeController.charAt(0)), primeiraLetra);		
		File controller = new File (ControllersMapFactory.controllersMap.get(nomeController));		
		
		String stController = FileUtils.readFileToString(controller);
		if (! stController.contains("@Paginator")) {
			stController = stController.replace("private DynamicDataModel<",
					"@Inject @Paginator\n\tprivate DynamicDataModel<");
			stController = stController
					.replace(
							"import br.gov.mec.aghu.core.action.ActionController;",
							"import br.gov.mec.aghu.core.action.ActionController;\nimport javax.inject.Inject;\nimport br.gov.mec.aghu.core.etc.Paginator;");

			StringBuilder sbController = new StringBuilder(stController);
			int indexDataModel = sbController.indexOf(valorDataModelAttribute);
			int indexObject = sbController.indexOf(";", indexDataModel);
			sbController.replace(
					indexDataModel + valorDataModelAttribute.length(),
					indexObject, "");
			FileUtils.writeStringToFile(controller, sbController.toString());
		}
		
	}

	private void ajustarBotoesEdicaoRemocao(Element serverDataTableElement,
			String valorMbeanAttribute) {
		
		Attribute controlsAttribute = serverDataTableElement.attribute("controls");
		if (controlsAttribute != null){
			serverDataTableElement.remove(controlsAttribute);
		}
		
		
		Attribute removeMethodAttribute = serverDataTableElement
				.attribute("removeMethod");
		String valueRemoveMethodAttribute = null;
		if (removeMethodAttribute != null) {
			valueRemoveMethodAttribute = removeMethodAttribute.getValue();
			serverDataTableElement.remove(removeMethodAttribute);
		}	
		
		
		
		Attribute editMethodAttribute = serverDataTableElement
				.attribute("editMethod");
		String valueEditMethodAttribute = null;
		if (editMethodAttribute != null) {
			valueEditMethodAttribute = editMethodAttribute.getValue();
			serverDataTableElement.remove(editMethodAttribute);
		}
		
		
		
		if (editMethodAttribute != null || removeMethodAttribute != null || controlsAttribute != null ){
			
			Iterator<Element> filhosServerDataTableIt = serverDataTableElement.elementIterator();
			
			List<Node> contentControlsBefore = null;
			List<Element> contentControls = null;
			List<Node> contentEditParameter = null;
			List<Node> contentRemoveParameter = null;
			
			while (filhosServerDataTableIt.hasNext()){
				Element el = filhosServerDataTableIt.next();
				if (el.getName().equals("define") &&  el.attributeValue("name").equals("controlsBefore")){
					contentControlsBefore = el.content();
					filhosServerDataTableIt.remove();
				}else if (el.getName().equals("define") &&  el.attributeValue("name").equals("controls")){
					contentControls = el.elements();
					filhosServerDataTableIt.remove();
				}else if (el.getName().equals("define") &&  el.attributeValue("name").equals("editParameter")){
					contentEditParameter = el.content();
					filhosServerDataTableIt.remove();
				}else if (el.getName().equals("define") &&  el.attributeValue("name").equals("removeParameter")){
					contentRemoveParameter = el.content();
					filhosServerDataTableIt.remove();
				}
			}		
			
			Element pColumnElementAcao = serverDataTableElement.addElement(
					"p:column", "http://primefaces.org/ui");
			
			pColumnElementAcao.addAttribute("headerText", "Ações"); 
			pColumnElementAcao.addAttribute("styleClass", "first-column auto-adjust");
			pColumnElementAcao.addAttribute("exportable", "false");
			
			List<Element> filhosServerDataTable = serverDataTableElement.elements();			
			Element element =  filhosServerDataTable.remove(filhosServerDataTable.size()-1);			
			filhosServerDataTable.add(0,element);
			
			if (contentControlsBefore != null){
				pColumnElementAcao.setContent(contentControlsBefore);
			}
			
			
			incluirBotaoEdicao(serverDataTableElement, valorMbeanAttribute,
					 editMethodAttribute,
					valueEditMethodAttribute,  contentEditParameter,
					pColumnElementAcao);
			
			incluirBotaoRemocao(serverDataTableElement, valorMbeanAttribute,
					removeMethodAttribute, valueRemoveMethodAttribute,					
					 contentRemoveParameter,
					pColumnElementAcao);	
			
			if (contentControls != null){
				for(Element el : contentControls){
					el.setParent(null);
					pColumnElementAcao.add(el);
				}
			}
			
		}
	}

	private void incluirBotaoRemocao(Element serverDataTableElement,
			String valorMbeanAttribute, Attribute removeMethodAttribute,
			String valueRemoveMethodAttribute,			
			List<Node> contentRemoveParameter, Element pColumnElementAcao) {
		if (removeMethodAttribute != null) {
			Element editCommandLink = pColumnElementAcao.addElement("mec:commandLink",
					"http://xmlns.jcp.org/jsf/composite/components");
			
			
			Attribute userRemovePermissionAttribute = serverDataTableElement
					.attribute("removePermission");
			String valueUserRemovePermissionAttribute = null;
			if (userRemovePermissionAttribute != null) {
				valueUserRemovePermissionAttribute = userRemovePermissionAttribute.getValue();
				serverDataTableElement.remove(userRemovePermissionAttribute);
			}
			
			String stIdEditCommandButton = serverDataTableElement.attributeValue("id")+ "_link_remover";
			editCommandLink.addAttribute("id", stIdEditCommandButton);					
			editCommandLink.addAttribute("profile", "delete");
			editCommandLink.addAttribute("title", "excluir");
			
			
			StringBuilder stAction = new StringBuilder(valorMbeanAttribute);
			stAction.insert(valorMbeanAttribute.lastIndexOf('}'), "."
					+ valueRemoveMethodAttribute);
			editCommandLink.addAttribute("action", stAction.toString());					
			
			if (userRemovePermissionAttribute != null){
				editCommandLink.addAttribute("permission", valueUserRemovePermissionAttribute);
			}		
			
			if (contentRemoveParameter != null){
				editCommandLink.setContent(contentRemoveParameter);
			}
		
			Element fsetPropertyActionListenerElement = editCommandLink.addElement(
					"f:setPropertyActionListener", "http://xmlns.jcp.org/jsf/core");					
			fsetPropertyActionListenerElement.addAttribute("for", "command");					
			StringBuilder stTarget = new StringBuilder(valorMbeanAttribute);
			stTarget.insert(valorMbeanAttribute.lastIndexOf('}'), "."
					+ serverDataTableElement.attributeValue("selection"));					
			fsetPropertyActionListenerElement.addAttribute("target", stTarget.toString());
			fsetPropertyActionListenerElement.addAttribute("value", "#{"+ serverDataTableElement.attributeValue("var") +"}");

		}
	}

	private void incluirBotaoEdicao(Element serverDataTableElement,
			String valorMbeanAttribute, 
			Attribute editMethodAttribute, String valueEditMethodAttribute,
			List<Node> contentEditParameter, Element pColumnElementAcao) {
		if (editMethodAttribute != null) {
			
			
			Attribute switchAjaxAttribute = serverDataTableElement
					.attribute("switchAjax");
			String valueSwitchAjaxAttribute = null;
			if (switchAjaxAttribute != null) {
				valueSwitchAjaxAttribute = switchAjaxAttribute.getValue();
				serverDataTableElement.remove(switchAjaxAttribute);
			}
			
			Attribute reRenderEditAttribute = serverDataTableElement
					.attribute("reRenderEdit");
			String valueReRenderEditAttribute = null;
			if (reRenderEditAttribute != null) {
				valueReRenderEditAttribute = reRenderEditAttribute.getValue();
				serverDataTableElement.remove(reRenderEditAttribute);
			}
			
			Attribute userEditPermissionAttribute = serverDataTableElement
					.attribute("editPermission");
			String valueUserEditPermissionAttribute = null;
			if (userEditPermissionAttribute != null) {
				valueUserEditPermissionAttribute = userEditPermissionAttribute.getValue();
				serverDataTableElement.remove(userEditPermissionAttribute);
			}
			
			
			Element editCommandLink = pColumnElementAcao.addElement("mec:commandLink",
					"http://xmlns.jcp.org/jsf/composite/components");
			
			String stIdEditCommandButton = serverDataTableElement.attributeValue("id")+ "_link_editar";
			editCommandLink.addAttribute("id", stIdEditCommandButton);					
			editCommandLink.addAttribute("profile", "edit");
			editCommandLink.addAttribute("title", "editar");
			
			
			StringBuilder stAction = new StringBuilder(valorMbeanAttribute);
			stAction.insert(valorMbeanAttribute.lastIndexOf('}'), "."
					+ valueEditMethodAttribute);
			editCommandLink.addAttribute("action", stAction.toString());
			
			editCommandLink.addAttribute("ajax", valueSwitchAjaxAttribute);
			
			if (reRenderEditAttribute != null){
				editCommandLink.addAttribute("render", valueReRenderEditAttribute);
			}
			
			if (userEditPermissionAttribute != null){
				editCommandLink.addAttribute("permission", valueUserEditPermissionAttribute);
			}	
			
			if (contentEditParameter != null){
				editCommandLink.setContent(contentEditParameter);
			}
			
		
			Element fsetPropertyActionListenerElement = editCommandLink.addElement(
					"f:setPropertyActionListener", "http://xmlns.jcp.org/jsf/core");					
			fsetPropertyActionListenerElement.addAttribute("for", "command");					
			StringBuilder stTarget = new StringBuilder(valorMbeanAttribute);
			stTarget.insert(valorMbeanAttribute.lastIndexOf('}'), "."
					+ serverDataTableElement.attributeValue("selection"));					
			fsetPropertyActionListenerElement.addAttribute("target", stTarget.toString());
			fsetPropertyActionListenerElement.addAttribute("value", "#{"+ serverDataTableElement.attributeValue("var") +"}");

		}
	}
	

	public void ajustarScrollDataTable(List<Element> scrollDataTableList) throws IOException {
		for (Element scrollDataTableElement : scrollDataTableList) {			
			ajustarDataTable(scrollDataTableElement);		
			scrollDataTableElement.addAttribute("scrollable", "true");
			
			Attribute heightAttribute = scrollDataTableElement.attribute("height");
			if (heightAttribute != null) {
				String valueHeightAttribute = heightAttribute.getValue();
				scrollDataTableElement.remove(scrollDataTableElement
						.attribute("height"));
				scrollDataTableElement.addAttribute("scrollHeight",
						valueHeightAttribute);
			}
			if (scrollDataTableElement.attribute("selection") != null) {
				LOG.warn("Atrbibuto 'selection' encontrado no scrollDataTable "
						+ scrollDataTableElement.attributeValue("id"));
			}
		}
		
	}
	
	
	public void ajustarSelectBooleanCheckBox(
			List<Element> selectBooleanCheckBoxList) {
		for (Element selectBooleanCheckBoxElement : selectBooleanCheckBoxList) {
			this.ajustarNameId(selectBooleanCheckBoxElement);
			
			Attribute attributeReRender = selectBooleanCheckBoxElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				selectBooleanCheckBoxElement.remove(attributeReRender);
				Element pAjaxElement = selectBooleanCheckBoxElement.addElement(
						"p:ajax", "http://primefaces.org/ui");
				pAjaxElement.addAttribute("update", this.customPrefixAjaxRender(valueAttributeReRender));
				pAjaxElement.addAttribute("event", "change");
			}
			
			Attribute attributeLayout = selectBooleanCheckBoxElement.attribute("layout");
			if (attributeLayout != null){
				selectBooleanCheckBoxElement.remove(attributeLayout);
			}
			
			Attribute attributeOnSelect = selectBooleanCheckBoxElement.attribute("onselect");
			if (attributeOnSelect != null){
				attributeOnSelect.setName("onchange");
			}
			
			
		}
		
	}
	
	
	private void ajustarNameId(Element inputTextElement) {
		if (inputTextElement.attributeValue("id") == null) {
			String nameValue = inputTextElement.attributeValue("name");
			inputTextElement.remove(inputTextElement.attribute("name"));
			inputTextElement.addAttribute("id", nameValue);
		}else{
			Attribute attributeName = inputTextElement.attribute("name");
			if (attributeName != null){
				inputTextElement.remove(attributeName);
			}
		}
	}
	
	private String customPrefixAjaxRender(String ids){
		if (ids==null || ids.isEmpty()){
			return "";
		}
		String separator=" ";
		StringBuffer result=new StringBuffer();
		if (ids.contains(",")){
			separator=",";
		}
		for (String nid : ids.split(separator)){
			result.append(' ');
			if (nid.contains("@")){
				result.append(nid.trim());
			}else{
				result.append("@(#");
				result.append(nid.trim());
				result.append(')');
			}			
		}
		return result.toString().trim();
	}

	public void ajustarinputTextMesAno(List<Element> inputTextMesAnoList) {
		for (Element inputTextMesAnoElement : inputTextMesAnoList) {
			ajustarNameId(inputTextMesAnoElement);
			

			inputTextMesAnoElement.setQName(new QName("inputMesAno",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));
			
			
			

		}
		
	}

	

	

	

	

	

	

}
