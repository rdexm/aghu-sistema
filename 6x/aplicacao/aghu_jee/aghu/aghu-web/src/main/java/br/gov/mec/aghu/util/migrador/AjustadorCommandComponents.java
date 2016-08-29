package br.gov.mec.aghu.util.migrador;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

@SuppressWarnings("PMD")
public class AjustadorCommandComponents {


	private static final Log LOG = LogFactory.getLog(AjustadorCommandComponents.class);
	
	private Set<String> listaSilks = new HashSet<String>();


	public void ajustarMECCommandButton(List<Element> mecCommandButtonList) {
		for (Element commandButtonElement : mecCommandButtonList) {
			ajustarCommandButtonElement(commandButtonElement);
		}

	}

	private void ajustarCommandButtonElement(Element commandButtonElement) {
		Attribute attributeActionBean = commandButtonElement
				.attribute("actionBean");
		if (attributeActionBean != null) {
			String valorMbean = attributeActionBean.getValue();
			String valorValueOld = commandButtonElement
					.attributeValue("method");
			StringBuilder sbValorValueNew = new StringBuilder(valorMbean);
			sbValorValueNew.insert(valorMbean.lastIndexOf('}'), "."
					+ valorValueOld);
			commandButtonElement.remove(commandButtonElement
					.attribute("actionBean"));
			commandButtonElement.remove(commandButtonElement
					.attribute("method"));
			commandButtonElement.addAttribute("action",
					sbValorValueNew.toString());
		}
		
		Attribute attributeReRender = commandButtonElement.attribute("reRender");
		if (attributeReRender != null){
			String attributeReRenderValue = attributeReRender.getValue();
			commandButtonElement.remove(attributeReRender);
			commandButtonElement.addAttribute("render", attributeReRenderValue);
		}

		ajustarStyleClassProfile(commandButtonElement);
	}

	

	
	
	
	
	public void ajustarAjaxCommandButton(List<Element> mecAjaxCommandButtonList) {
		for (Element ajaxCommandButtonElement : mecAjaxCommandButtonList) {
			ajustarCommandButtonElement(ajaxCommandButtonElement);
			
			ajaxCommandButtonElement.setQName(new QName("commandButton",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));
			
			ajaxCommandButtonElement.addAttribute("ajax", "true");
			
			String valorReRender = ajaxCommandButtonElement
					.attributeValue("reRender");
			if (valorReRender != null) {
				ajaxCommandButtonElement.remove(ajaxCommandButtonElement
						.attribute("reRender"));
				ajaxCommandButtonElement.addAttribute("render",  this.customPrefixAjaxRender(valorReRender));
			}
			
			
			String valorAjaxSingle = ajaxCommandButtonElement
					.attributeValue("ajaxSingle");
			if (valorAjaxSingle != null && valorAjaxSingle.equals("true")) {
				ajaxCommandButtonElement.remove(ajaxCommandButtonElement
						.attribute("ajaxSingle"));
				ajaxCommandButtonElement.addAttribute("process", "@this");
			}
			
			
			
		}
		
	}
	
	public void ajustarCommandLink(List<Element> commandLinkList) {
	for (Element commandLinkElement : commandLinkList) {
			
		commandLinkElement.setQName(new QName("commandLink", new Namespace(
					"mec", "http://xmlns.jcp.org/jsf/composite/components")));			

			ajustarStyleClassProfile(commandLinkElement);
			
			
			Attribute attributeStatus= commandLinkElement.attribute("status");
			if (attributeStatus != null){
				commandLinkElement.remove(attributeStatus);
			}
		}
		
	}
	
	
	public void ajustarCommandButton(List<Element> commandButtonList) {
		for (Element commandButtonElement : commandButtonList) {
			
			commandButtonElement.setQName(new QName("commandButton", new Namespace(
					"mec", "http://xmlns.jcp.org/jsf/composite/components")));			

			ajustarStyleClassProfile(commandButtonElement);

		}
	}


	private void ajustarStyleClassProfile(Element commandElement) {
		if (commandElement.attributeValue("styleClass") != null) {
			String styleClassValue = commandElement
					.attributeValue("styleClass");
			String[] stylesClasses = styleClassValue.split(" ");
			boolean tembtsecond = false;
			boolean tembtok = false;
			boolean tembterror = false;
			boolean temiconyes = false;
			boolean temiconno = false;
			boolean temiconerase = false;
			boolean temiconadd = false;
			boolean temiconsearch = false;
			boolean temsilkicon = false;
			boolean temsilkdelete = false;
			boolean temsilkpencil = false;
			for (String styleclass : stylesClasses) {
				if (styleclass.contains("bt_secund")) {
					tembtsecond = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("bt_ok")) {
					tembtok = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("bt_error")) {
					tembterror = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("icon-yes")) {
					temiconyes = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("icon-erase")) {
					temiconerase = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("icon-add")) {
					temiconadd = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("icon-no")) {
					temiconno = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				}else if (styleclass.contains("icon-search")) {
					temiconsearch = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}
				} else if (styleclass.contains("silk-icon")) {
					temsilkicon = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("silk-delete")) {
					temsilkdelete = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}

				} else if (styleclass.contains("silk-pencil")) {
					temsilkpencil = true;
					if (verificarAjusteBotao(commandElement, tembtsecond,
							tembtok, tembterror, temiconyes, temiconno,
							temiconerase, temiconadd, temiconsearch,
							temsilkicon, temsilkdelete, temsilkpencil)) {
						break;
					}
					

				}else if (styleclass.contains("silk")){
					listaSilks .add(styleclass);
				}
			}

			styleClassValue = styleClassValue.replace("bt_secund", "");
			styleClassValue = styleClassValue.replace("bt_ok", "");
			styleClassValue = styleClassValue.replace("bt_error", "");
			styleClassValue = styleClassValue.replace("icon-yes", "");
			styleClassValue = styleClassValue.replace("icon-erase", "");
			styleClassValue = styleClassValue.replace("icon-add", "");
			styleClassValue = styleClassValue.replace("icon-no", "");
			styleClassValue = styleClassValue.replace("icon-search", "");
			styleClassValue = styleClassValue.replace("silk-icon", "");
			styleClassValue = styleClassValue.replace("silk-delete", "");
			styleClassValue = styleClassValue.replace("silk-pencil", "");

			commandElement.remove(commandElement
					.attribute("styleClass"));

			if (!StringUtils.isBlank(styleClassValue)) {
				commandElement.addAttribute("styleClass",
						styleClassValue);
			}
		}
	}
	
	
	private boolean verificarAjusteBotao(Element commandElement,
			boolean tembtsecond, boolean tembtok, boolean tembterror,
			boolean temiconyes, boolean temiconno, boolean temiconerase,
			boolean temiconadd, boolean temiconsearch, boolean temsilkicon,
			boolean temsilkdelete, boolean temsilkpencil) {
		
		boolean retorno = false;
		String valorProfile = null;
		
		if (tembtsecond){
			if (temiconyes){
				valorProfile = "search";
			}else if (temiconerase){
				valorProfile = "clear";
			}			
		}else if (tembtok && temiconyes){
			valorProfile = "ok";
		}else if (tembterror && temiconno){
			valorProfile = "cancel";
		}		
		if (temiconadd){
			valorProfile = "add";
		}
		if (temiconsearch){
			valorProfile = "search";
		}
		
		if (temsilkicon) {
			if (temsilkpencil) {
				valorProfile = "edit";
			} else if (temsilkdelete) {
				valorProfile = "delete";
			}
		}
		
		if (valorProfile != null){
			commandElement.addAttribute("profile", valorProfile);
			retorno = true;
		}
		
		return retorno;

	}
	

	
	public void ajustarACommandButton(List<Element> aCommandButtonList) {
		for (Element commandButtonElement : aCommandButtonList){
			commandButtonElement.setQName(new QName("commandButton",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));			
			
			commandButtonElement.remove(new Namespace("a",
							"http://richfaces.org/a4j"));
			
			ajustarStyleClassProfile(commandButtonElement);
			
			commandButtonElement.addAttribute("ajax", "true");
			
			Attribute attributeReRender = commandButtonElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				commandButtonElement.remove(attributeReRender);
				commandButtonElement.addAttribute("render", valueAttributeReRender);			
				
			}
			
			
			Attribute attributeAjaxSingle = commandButtonElement.attribute("ajaxSingle");
			if (attributeAjaxSingle != null){
				String valueAttributeAjaxSingle = attributeAjaxSingle.getValue();
				commandButtonElement.remove(attributeAjaxSingle);
				if (valueAttributeAjaxSingle.equals("true")) {
					commandButtonElement.addAttribute("process",
							"@this");
				}				
			}
		}
		
	}

	public void ajustarACommandLink(List<Element> aCommandLinkList) {
		for (Element commandLinkElement : aCommandLinkList){
			commandLinkElement.setQName(new QName("commandLink",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));			
			
			commandLinkElement.remove(new Namespace("a",
							"http://richfaces.org/a4j"));
			
			commandLinkElement.remove(new Namespace("a4j",
					"http://richfaces.org/a4j"));
			
			ajustarStyleClassProfile(commandLinkElement);
			
			commandLinkElement.addAttribute("ajax", "true");
			
			Attribute attributeReRender = commandLinkElement.attribute("reRender");
			if (attributeReRender != null){
				String valueAttributeReRender = attributeReRender.getValue();
				commandLinkElement.remove(attributeReRender);
				commandLinkElement.addAttribute("render", valueAttributeReRender);				
			}
			
			
			Attribute attributeAjaxSingle = commandLinkElement.attribute("ajaxSingle");
			if (attributeAjaxSingle != null){
				String valueAttributeAjaxSingle = attributeAjaxSingle.getValue();
				commandLinkElement.remove(attributeAjaxSingle);
				if (valueAttributeAjaxSingle.equals("true")) {
					commandLinkElement.addAttribute("process",
							"@this");
				}
				
				
			}
			
			
		}
		
	}
	
	
	public void ajustarCancelButton(List<Element> cancelButtonList) {
		for(Element cancelButtonElement : cancelButtonList){
			
			cancelButtonElement.setQName(new QName("backButton",
					new Namespace("mec",
							"http://xmlns.jcp.org/jsf/composite/components")));			
			
			String valorMbean = null;
			Attribute actionBeanAttribute = cancelButtonElement
					.attribute("actionBean");
			if (actionBeanAttribute != null) {
				valorMbean = actionBeanAttribute.getValue();
				cancelButtonElement.remove(actionBeanAttribute);
			} else {
				Attribute BeanAttribute = cancelButtonElement.attribute("bean");
				if (BeanAttribute != null) {
					valorMbean = BeanAttribute.getValue();
					cancelButtonElement.remove(BeanAttribute);
				} else {
					LOG.error("CancelButton sem actionBean foi encontrado. É preciso verificar");
				}
			}

			// String valorMbean =
			// cancelButtonElement.attributeValue("actionBean");
			String valorValueOld = cancelButtonElement
					.attributeValue("metodoCancelar");
			StringBuilder sbValorValueNew = new StringBuilder(valorMbean);
			sbValorValueNew.insert(valorMbean.lastIndexOf('}'), "."
					+ valorValueOld);

			cancelButtonElement.remove(cancelButtonElement
					.attribute("metodoCancelar"));
			cancelButtonElement.addAttribute("action",
					sbValorValueNew.toString());
			
			
		}
		
	}
	
	public  void listarSilks(){
		LOG.trace(this.listaSilks.toString());
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
}
