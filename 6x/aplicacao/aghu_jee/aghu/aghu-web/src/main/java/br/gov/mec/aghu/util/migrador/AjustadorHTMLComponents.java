package br.gov.mec.aghu.util.migrador;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jfree.util.Log;
@SuppressWarnings("PMD")
public class AjustadorHTMLComponents {

	public void ajustarFieldSet(List<Element> fieldsetList) {
		for (Element fieldsetElement : fieldsetList) {

			String classValue = null;
			Attribute classAttribute = fieldsetElement.attribute("class");
			if (classAttribute != null){
				classValue = classAttribute.getValue();
				fieldsetElement.remove(classAttribute);				
			}else {
				Attribute styleClassAttribute = fieldsetElement.attribute("styleClass");
				if (styleClassAttribute != null){
					classValue = styleClassAttribute.getValue();
					fieldsetElement.remove(styleClassAttribute);	
				}
			}
			
			if (classValue == null){
				Log.warn("Encontrado fieldset sem atributo class ou styleclass. É preciso verificar!");
				return;
			}
			
			switch (classValue) {
			case "geral":
				fieldsetElement.setQName(new QName("panelGeral",
						new Namespace("aghu",
								"http://xmlns.jcp.org/jsf/component")));
				Element elemento = fieldsetElement.element("legend");
				if (elemento != null) {
					String txLegend = elemento.getText();
					fieldsetElement.remove(elemento);					
					fieldsetElement.addAttribute("legend", txLegend);
				}
				break;
			case "linha":
				fieldsetElement.setQName(new QName("linha", new Namespace(
						"aghu", "http://xmlns.jcp.org/jsf/component")));
				break;
			case "acao":
				fieldsetElement.setQName(new QName("acao", new Namespace(
						"aghu", "http://xmlns.jcp.org/jsf/component")));
				break;
			case "acaoLateral":
				fieldsetElement.setQName(new QName("acao", new Namespace(
						"aghu", "http://xmlns.jcp.org/jsf/component")));				
				fieldsetElement.addAttribute("newLine", "false");
				break;
			default:
				fieldsetElement.setQName(new QName("linha", new Namespace(
						"aghu", "http://xmlns.jcp.org/jsf/component")));
				break;
			}

		}

	}

	

}
