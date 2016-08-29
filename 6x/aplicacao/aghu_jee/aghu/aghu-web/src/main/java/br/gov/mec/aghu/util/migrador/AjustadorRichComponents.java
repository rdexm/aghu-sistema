package br.gov.mec.aghu.util.migrador;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


@SuppressWarnings("PMD")
public class AjustadorRichComponents {
	
	private static final Log LOG = LogFactory.getLog(AjustadorRichComponents.class);

	public void ajustarRichTooltip(List<Element> tooltipList) {
		for (Element tolltipElement : tooltipList){
			
			tolltipElement.setQName(new QName("tooltip",
					new Namespace("p",
							"http://primefaces.org/ui")));			
		}
	}

	public void ajustarModalPanel(List<Element> modalPanelList) {
		for (Element modalPanelElement : modalPanelList){
			
			modalPanelElement.setQName(new QName("dialog",
					new Namespace("p",
							"http://primefaces.org/ui")));	
			
			modalPanelElement.remove(new Namespace("rich",
					"http://richfaces.org/rich"));
			
			modalPanelElement.addAttribute("modal", "true");
			modalPanelElement.addAttribute("resizable", "false");
			
			String idValue = modalPanelElement.attributeValue("id");
			
			modalPanelElement.addAttribute("widgetVar", idValue+"WG");	
			
			Attribute attributeOverlapEmbedObjects = modalPanelElement.attribute("overlapEmbedObjects");
			if (attributeOverlapEmbedObjects != null){
				modalPanelElement.remove(attributeOverlapEmbedObjects);
			}
			
			Document doc = modalPanelElement.getDocument();				
			
			List<Element> elementsRichfacesShowModalJavaScript =  doc.selectNodes("//*[@*=\"Richfaces.showModalPanel('"+idValue+"')\"]");
			
			elementsRichfacesShowModalJavaScript.addAll(doc.selectNodes("//*[@*=\"Richfaces.showModalPanel('"+idValue+"');\"]"));
			
			for (Element ele : elementsRichfacesShowModalJavaScript){
				List<Attribute> attributes = ele.attributes();
				for (Attribute attr :attributes){
					if (attr.getValue().contains("Richfaces.showModalPanel('"+idValue+"')")){						
						attr.setValue(attr.getValue().replace("Richfaces.showModalPanel('"+idValue+"')", "PF('"+idValue+"WG').show();") );
					}
					
				}
			}
			
			List<Element> elementsRichfacesHideModalJavaScript =  doc.selectNodes("//*[@*=\"Richfaces.hideModalPanel('"+idValue+"')\"]");
			elementsRichfacesHideModalJavaScript.addAll(doc.selectNodes("//*[@*=\"Richfaces.hideModalPanel('"+idValue+"');\"]"));
			
			for (Element ele : elementsRichfacesHideModalJavaScript){
				List<Attribute> attributes = ele.attributes();
				for (Attribute attr :attributes){
					if (attr.getValue().contains("Richfaces.hideModalPanel('"+idValue+"')")){						
						attr.setValue(attr.getValue().replace("Richfaces.hideModalPanel('"+idValue+"')", "PF('"+idValue+"WG').hide();") );
					}
					
				}
			}
			
			
			
			
		}
		
	}

	public void ajustarDataTable(List<Element> dataTableList) {
		for(Element dataTable : dataTableList){
//			Attribute styleClassAttribute = dataTable.attribute("styleClass");
//			if (styleClassAttribute != null){
//				String valueStyleClassAttribute = styleClassAttribute.getValue();
//				if (valueStyleClassAttribute.contains("scroll-table")){
//					ajustarParaMecServerDataTableComScroll(dataTable);
//				}
//			}
			
			ajustarParaMecServerDataTableComScroll(dataTable);
		}
		
	}

	private void ajustarParaMecServerDataTableComScroll(Element dataTableElement) {
		dataTableElement.setQName(new QName("serverDataTable",
				new Namespace("mec",
						"http://xmlns.jcp.org/jsf/composite/components")));
		
		dataTableElement.remove(new Namespace("rich",
				"http://richfaces.org/rich"));
		
		String valueAttributeValue = dataTableElement.attributeValue("value");
		dataTableElement.remove(dataTableElement.attribute("value"));
		dataTableElement.addAttribute("list", valueAttributeValue);
		
		
		Attribute styleClassAtribute = dataTableElement.attribute("styleClass");
		if (styleClassAtribute != null) {
			dataTableElement.remove(dataTableElement.attribute("styleClass"));
		}
		
		Attribute attributeVar = dataTableElement.attribute("var");
		if (attributeVar != null){
			String valueAttributeVar = attributeVar.getValue();
			dataTableElement.remove(attributeVar);			
			Element setElement = dataTableElement.addElement("c:set", "http://xmlns.jcp.org/jsp/jstl/core");			
			setElement.addAttribute("var", valueAttributeVar);
			setElement.addAttribute("value", "#{item}");			
			List<Element> filhosServerDataTable = dataTableElement.elements();			
			Element element =  filhosServerDataTable.remove(filhosServerDataTable.size()-1);			
			filhosServerDataTable.add(0,element);
		}
		
		dataTableElement.addAttribute("scrollable", "true");
		
		
		Attribute attributeHeight = dataTableElement.attribute("height");
		if (attributeHeight != null){
			String valueHeightAttribute = attributeHeight.getValue();
			dataTableElement.remove(attributeHeight);
			dataTableElement.addAttribute("scrollHeight", valueHeightAttribute);
		}
		
		
		Element facetElement = dataTableElement.element("facet");
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
						this.ajustarRichColumn(columnHeaders);
					}
				}				
			}
		}
		
		Iterator<Element> richColumnsIterator = dataTableElement
				.elementIterator("column");
		while (richColumnsIterator.hasNext()) {		
			Element richColumnsElement =  richColumnsIterator.next();
			this.ajustarRichColumn(richColumnsElement);
			
		}
		
		Attribute onRowClickAttribute = dataTableElement.attribute("onRowClick");
		if (onRowClickAttribute != null){
			LOG.warn("Atributo onRowClick encontrado! É preciso verificar." );
		}
		
	}
	
	
	public void ajustarRichColumn(Element richColumnsElement) {		
		
		richColumnsElement.setQName(new QName("column",
				new Namespace("p",
						"http://primefaces.org/ui")));			
		
		richColumnsElement.remove(new Namespace("rich",
						"http://richfaces.org/rich"));
		
		Attribute attributeServerOrder = richColumnsElement.attribute("serverOrder");
		if (attributeServerOrder != null){
			String valueAttributeServerOrder = attributeServerOrder.getValue();
			richColumnsElement.remove(attributeServerOrder);
			richColumnsElement.addAttribute("sortBy", "#{item["+valueAttributeServerOrder+"]}" );
		}
	}

	public void ajustarAOutputPanel(List<Element> aOutputPanelList) {
		for(Element aOutputPanelElement : aOutputPanelList){
			aOutputPanelElement.setQName(new QName("outputPanel",
					new Namespace("p",
							"http://primefaces.org/ui")));			
			
			aOutputPanelElement.remove(new Namespace("a",
							"http://richfaces.org/a4j"));
			
			aOutputPanelElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			
			String valueAttributeLayout = aOutputPanelElement.attributeValue("layout");
			if (valueAttributeLayout == null){				
					aOutputPanelElement.addAttribute("layout", "inline");				
			}
			
			
			
		}
		
	}

	public void ajustarSimpleTogglePanel(List<Element> simpleTogglePanelList) {
		for(Element simpleTogglePanelElement : simpleTogglePanelList){
			simpleTogglePanelElement.setQName(new QName("accordionPanel",
					new Namespace("p",
							"http://primefaces.org/ui")));			
			
			simpleTogglePanelElement.remove(new Namespace("rich",
							"http://richfaces.org/rich"));	
			
			Element tabElement = simpleTogglePanelElement.addElement("p:tab", "http://primefaces.org/ui");	
			
			Attribute labelAttribute = simpleTogglePanelElement.attribute("label");			
			if (labelAttribute != null){
				String valueLabelAttribute = labelAttribute.getValue();
				simpleTogglePanelElement.remove(labelAttribute);
				tabElement.addAttribute("title", valueLabelAttribute);
			}
			
				
			Attribute attributeSwitchType = simpleTogglePanelElement.attribute("switchType");			
			if (attributeSwitchType != null){
				String valueAttributeSwitchType = attributeSwitchType.getValue();
				simpleTogglePanelElement.remove(attributeSwitchType);
				if (!valueAttributeSwitchType.equals("client")){
					simpleTogglePanelElement.addAttribute("dynamic", "true");
				}
			}
			
			
			Attribute attributeOpened = simpleTogglePanelElement.attribute("opened");
			if (attributeOpened != null){
				String valueAttributeOpened = attributeOpened.getValue();
				simpleTogglePanelElement.remove(attributeOpened);
				if (valueAttributeOpened.equals("false")){
					simpleTogglePanelElement.addAttribute("activeIndex", "-1");					
				}else if (valueAttributeOpened.contains("#{")){
					LOG.equals("Attributo opened do simpleTogglePanel "
							+ simpleTogglePanelElement.attributeValue("id")
							+ " usa uma EL.");
				}
			}
			
			
			Attribute attributeOnExpand = simpleTogglePanelElement.attribute("onexpand");
			if (attributeOnExpand != null){
				String valueAttributeOnExpand = attributeOnExpand.getValue();
				simpleTogglePanelElement.remove(attributeOnExpand);
				simpleTogglePanelElement.addAttribute("onTabChange", valueAttributeOnExpand);
			}
			
			for (Element e : (List<Element>)simpleTogglePanelElement.elements()){
				if (!e.getName().equals("tab")){
				simpleTogglePanelElement.remove(e);
				tabElement.add(e);
				}
			}
			
			
		}
		
	}

	public void ajustarSupport(List<Element> supportList) {
		for(Element supportElement : supportList){
			supportElement.setQName(new QName("ajax",
					new Namespace("p",
							"http://primefaces.org/ui")));			
			
			supportElement.remove(new Namespace("a",
					"http://richfaces.org/a4j"));	
			
			supportElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			
			Attribute attributeReRender = supportElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				supportElement.remove(attributeReRender);
				supportElement.addAttribute("update", this.customPrefixAjaxRender(valueAttributeReRender));
			}
			
			Attribute attributeAjaxSingle = supportElement.attribute("ajaxSingle");
			if (attributeAjaxSingle != null){
				String valueAttributeAjaxSingle = attributeAjaxSingle.getValue();
				supportElement.remove(attributeAjaxSingle);
				if (valueAttributeAjaxSingle.equals("true")) {
					supportElement.addAttribute("process", "@this");
				}
			}
			
			Attribute attributeProcess = supportElement.attribute("process");
			if (attributeProcess != null){
				String valueAttributeProcess = attributeProcess.getValue();
				supportElement.remove(attributeProcess);
				supportElement.addAttribute("process", this.customPrefixAjaxRender(valueAttributeProcess));
			}
			
			Attribute attributeAction = supportElement.attribute("action");
			if (attributeAction != null){
				String valueAttributeAction = attributeAction.getValue();
				supportElement.remove(attributeAction);
				supportElement.addAttribute("listener", valueAttributeAction);
			}
			
			
			Attribute attributeLimitToList = supportElement.attribute("limitToList");
			if (attributeLimitToList != null){
				supportElement.remove(attributeLimitToList);
			}
			
			
			Attribute attributeIgnoreDupResponses = supportElement.attribute("ignoreDupResponses");
			if (attributeIgnoreDupResponses != null){
				supportElement.remove(attributeIgnoreDupResponses);
			}
			
			Attribute attributeStatus = supportElement.attribute("status");
			if (attributeStatus != null){
				supportElement.remove(attributeStatus);
			}
			
			
			Attribute attributeEvent = supportElement.attribute("event");
			if (attributeEvent != null){
				String valueAttributeEvent = attributeEvent.getValue();
				if (valueAttributeEvent.equals("onRowClick")){
					attributeEvent.setValue("rowSelect");
				}if (valueAttributeEvent.equals("ontableave")){
					attributeEvent.setValue("tabClose");				
				}else if (valueAttributeEvent.startsWith("on")){
					attributeEvent.setValue(valueAttributeEvent.substring(2));
				}
				
			}
			
		}
		
	}
	
	
	/**
	 * customPrefixAjaxRender
	 * 
	 * @param ids
	 * @return
	 */	
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

	public void ajustarPoll(List<Element> pollList) {
		for(Element pollElement : pollList){
			pollElement.setQName(new QName("poll",
					new Namespace("p",
							"http://primefaces.org/ui")));			
			
			pollElement.remove(new Namespace("a",
					"http://richfaces.org/a4j"));
			
			pollElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			
			Attribute attributeReRender = pollElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				pollElement.remove(attributeReRender);
				pollElement.addAttribute("update", valueAttributeReRender);
			}
			
			Attribute attributeAction = pollElement.attribute("action");
			if (attributeAction != null){
				String valueAttributeAction = attributeAction.getValue();
				pollElement.remove(attributeAction);
				pollElement.addAttribute("listener", this.customPrefixAjaxRender(valueAttributeAction));
			}
			
			Attribute attributeAjaxSingle = pollElement.attribute("ajaxSingle");
			if (attributeAjaxSingle != null){
				String valueAttributeAjaxSingle = attributeAjaxSingle.getValue();
				pollElement.remove(attributeAjaxSingle);
				if (valueAttributeAjaxSingle.equals("true")) {
					pollElement.addAttribute("process", "@this");
				}
			}
			
			Attribute attributeProcess = pollElement.attribute("process");
			if (attributeProcess != null){
				String valueAttributeProcess = attributeProcess.getValue();
				pollElement.remove(attributeProcess);
				pollElement.addAttribute("process", valueAttributeProcess);
			}
			
			
			
		}
		
	}

	public void ajustarAForm(List<Element> formList) {
		for(Element formElement : formList){
			formElement.setQName(new QName("form",
					new Namespace("h",
							"http://xmlns.jcp.org/jsf/html")));			
			
			formElement.remove(new Namespace("a",
					"http://richfaces.org/a4j"));
			
			formElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			Attribute attributeAjaxSubmit = formElement.attribute("ajaxSubmit");
			if (attributeAjaxSubmit != null && attributeAjaxSubmit.getValue().equals("true")){
				LOG.warn("-------------- Encontrado a:form com ajaxSubmit=\"true\" É preciso verificar");				
			}
		}
		
	}

	public void ajustarJsFunction(List<Element> jsFunctionList) {
		for(Element jsFunctionElement : jsFunctionList){
			jsFunctionElement.setQName(new QName("remoteCommand",
					new Namespace("pe",
							"http://primefaces.org/ui/extensions")));			
			
			jsFunctionElement.remove(new Namespace("a",
					"http://richfaces.org/a4j"));
			
			jsFunctionElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			
			Attribute attributeReRender = jsFunctionElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				jsFunctionElement.remove(attributeReRender);
				jsFunctionElement.addAttribute("update", valueAttributeReRender);
			}
			
			Attribute attributeAction = jsFunctionElement.attribute("action");
			if (attributeAction != null){
				String valueAttributeAction = attributeAction.getValue();
				jsFunctionElement.remove(attributeAction);
				jsFunctionElement.addAttribute("actionListener", this.customPrefixAjaxRender(valueAttributeAction));
			}
			
			Attribute attributeAjaxSingle = jsFunctionElement.attribute("ajaxSingle");
			if (attributeAjaxSingle != null){
				String valueAttributeAjaxSingle = attributeAjaxSingle.getValue();
				jsFunctionElement.remove(attributeAjaxSingle);
				if (valueAttributeAjaxSingle.equals("true")) {
					jsFunctionElement.addAttribute("process", "@this");
				}
			}
			
			Attribute attributeProcess = jsFunctionElement.attribute("process");
			if (attributeProcess != null){
				String valueAttributeProcess = attributeProcess.getValue();
				jsFunctionElement.remove(attributeProcess);
				jsFunctionElement.addAttribute("process", valueAttributeProcess);
			}
			
//			Attribute attributeLimitToList = jsFunctionElement.attribute("limitToList");
//			if (attributeLimitToList != null){
//				String valueAttributeLimitToList = attributeLimitToList.getValue();
//				jsFunctionElement.remove(attributeLimitToList);				
//				jsFunctionElement.addAttribute("ignoreAutoUpdate", valueAttributeLimitToList);
//				
//			}
//			
//			Attribute attributeRequestDelay = jsFunctionElement.attribute("requestDelay");
//			if (attributeRequestDelay != null){
//				String valueAttributeRequestDelay = attributeRequestDelay.getValue();
//				jsFunctionElement.remove(attributeRequestDelay);				
//				jsFunctionElement.addAttribute("delay", valueAttributeRequestDelay);
//				
//			}
			
			Attribute attributeIgnoreDupResponses = jsFunctionElement.attribute("ignoreDupResponses");
			if (attributeIgnoreDupResponses != null){
				jsFunctionElement.remove(attributeIgnoreDupResponses);
			}
			
			Iterator<Element> actionParamIterator = jsFunctionElement.elementIterator("actionparam");

			while(actionParamIterator.hasNext()){
				Element actionParamElement = actionParamIterator.next();
				
				actionParamElement.setQName(new QName("assignableParam",
						new Namespace("pe",
								"http://primefaces.org/ui/extensions")));			
				
				actionParamElement.remove(new Namespace("a",
						"http://richfaces.org/a4j"));				
			
			}
			
			
		}
		
	}

	public void ajustarMediaOutput(List<Element> mediaOutputList) {
		for(Element mediaOutputElement : mediaOutputList){
			
			mediaOutputElement.setQName(new QName("media",
					new Namespace("p",
							"http://primefaces.org/ui")));			
			
			mediaOutputElement.remove(new Namespace("a",
					"http://richfaces.org/a4j"));
			
			mediaOutputElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			
			Attribute attributeValue = mediaOutputElement.attribute("value");
			if (attributeValue != null){
				mediaOutputElement.remove(attributeValue);
			}
			
			Attribute attributeElement = mediaOutputElement.attribute("element");
			if (attributeElement != null){
				mediaOutputElement.remove(attributeElement);
			}
			
			Attribute attributeCacheable = mediaOutputElement.attribute("cacheable");
			if (attributeCacheable != null){
				mediaOutputElement.remove(attributeCacheable);
			}
			
			Attribute attributeSession = mediaOutputElement.attribute("session");
			if (attributeSession != null){
				mediaOutputElement.remove(attributeSession);
			}
			
			Attribute attributeType = mediaOutputElement.attribute("type");
			if (attributeType != null){
				mediaOutputElement.remove(attributeType);
			}
			
			mediaOutputElement.addAttribute("player", "pdf");
			
			Attribute attributeCreateContent = mediaOutputElement.attribute("createContent");
			if (attributeCreateContent != null){
				String valueAttributeProcess = attributeCreateContent.getValue();
				mediaOutputElement.remove(attributeCreateContent);
				mediaOutputElement.addAttribute("value", valueAttributeProcess);
			}
			
		}
		
	}

	public void ajustarTabPanel(List<Element> tabPanelList) {
		for(Element tabPanelElement : tabPanelList){
			tabPanelElement.setQName(new QName("tabView",
				new Namespace("p",
						"http://primefaces.org/ui")));			
		
			tabPanelElement.remove(new Namespace("rich",
					"http://richfaces.org/rich"));	
			
			Attribute switchTypeAttribute = tabPanelElement.attribute("switchType");
			String valueSwitchTypeAttribute = "server";
			if (switchTypeAttribute != null){
				valueSwitchTypeAttribute = switchTypeAttribute.getValue();
				tabPanelElement.remove(switchTypeAttribute);
			}
			
			String valueDynamicAttribute = null;
			switch (valueSwitchTypeAttribute) {
			case "server":
				valueDynamicAttribute = "true";
				break;
			case "ajax":
				valueDynamicAttribute = "true";
				break;
			case "client":
				valueDynamicAttribute = "false";
				break;
			}		
			tabPanelElement.addAttribute("dynamic", valueDynamicAttribute);
			
			
			Attribute selectedTabAttribute = tabPanelElement.attribute("selectedTab");
			if (selectedTabAttribute != null){
				String valueSelectedTabAttribute = selectedTabAttribute.getValue();
				tabPanelElement.remove(selectedTabAttribute);
				tabPanelElement.addAttribute("activeIndex", valueSelectedTabAttribute);
			}
			
			tabPanelElement.addAttribute("prependId", "false");
			
			List<Element> tabElements = tabPanelElement.elements("tab");
			for (Element tabElement : tabElements) {
				tabElement.setQName(new QName("tab",
						new Namespace("p",
								"http://primefaces.org/ui")));			
				
				tabElement.remove(new Namespace("rich",
							"http://richfaces.org/rich"));
				
				
				Attribute labelAttribute = tabElement.attribute("label");
				if (labelAttribute != null){
					String valueLabelAttribute = labelAttribute.getValue();
					tabElement.remove(labelAttribute);
					tabElement.addAttribute("title", valueLabelAttribute);
				}
			}
			
			
		
		}
		
	}

}
