package br.gov.mec.aghu.util.migrador;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
@SuppressWarnings("PMD")
public class AjustadorNamespacesTemplate {	
	
	//Namespaces a serem removidos do no composition.	
	private String uriNamespaceRichfacesA4J = "http://richfaces.org/a4j";
	
	private String uriNamespaceRichfacesRich = "http://richfaces.org/rich";
	
	private String uriNamespaceMECAntigo = "http://www.mec.gov.br/components";
	
	private String uriNamespaceFacelets = "http://java.sun.com/jsf/facelets";
	
	private String uriNamespaceJSFCore = "http://java.sun.com/jsf/core";
	
	private String uriNamespaceHTML = "http://java.sun.com/jsf/html";
	
	private String uriNamespaceJSTLCore = "http://java.sun.com/jstl/core";
	
	private String uriNamespaceSeam = "http://jboss.com/products/seam/taglib";
	
	private String uriNamespaceDefault = "http://www.w3.org/1999/xhtml";
	
	
	// Namespaces a serem adicionados ao no raiz html.	
	private Namespace namespaceJSTLFunctions = new Namespace("fn", "http://xmlns.jcp.org/jsp/jstl/functions");
	
	private Namespace namespaceMECNovo = new Namespace("mec", "http://xmlns.jcp.org/jsf/composite/components");
	
	private Namespace namespacePrimefaces = new Namespace("p", "http://primefaces.org/ui");
	
	private Namespace namespaceAGHU = new Namespace("aghu", "http://xmlns.jcp.org/jsf/component");
	
	private Namespace namespaceFacelets = new Namespace("ui", "http://xmlns.jcp.org/jsf/facelets");
	
	private Namespace namespaceJSFCore = new Namespace("f", "http://xmlns.jcp.org/jsf/core");
	
	private Namespace namespaceHTML = new Namespace("h", "http://xmlns.jcp.org/jsf/html");
	
	private Namespace namespaceJSTLCore = new Namespace("c", "http://xmlns.jcp.org/jsp/jstl/core");
	
	private Namespace namespacePrimefacesExtensions = new Namespace("pe", "http://primefaces.org/ui/extensions");
	
	private Namespace namespaceMecFunctions = new Namespace("mf", "http://www.mec.gov.br/taglib");
	
	
	public Document ajustar(Document document) throws DocumentException{
		
		Element rootElement = document.getRootElement();	
		
		Element eleComposition = (Element)rootElement.elements().get(0);	
		
		Namespace namespaceRichfacesA4J = eleComposition.getNamespaceForURI(uriNamespaceRichfacesA4J);
		Namespace namespaceRichfacesRich = eleComposition.getNamespaceForURI(uriNamespaceRichfacesRich);
		Namespace namespaceMECAntigo = eleComposition.getNamespaceForURI(uriNamespaceMECAntigo);		
		Namespace namespaceFaceletsOld = eleComposition.getNamespaceForURI(uriNamespaceFacelets);
		Namespace namespaceJSFCoreOld = eleComposition.getNamespaceForURI(uriNamespaceJSFCore);
		Namespace namespaceHTMLOld = eleComposition.getNamespaceForURI(uriNamespaceHTML);
		Namespace namespaceJSTLCoreOld = eleComposition.getNamespaceForURI(uriNamespaceJSTLCore);
		Namespace namespaceSeam = eleComposition.getNamespaceForURI(uriNamespaceSeam);
		
		Namespace namespaceDefault = eleComposition.getNamespaceForURI(uriNamespaceDefault);
		
		
		eleComposition.remove(namespaceRichfacesA4J);
		eleComposition.remove(namespaceRichfacesRich);
		eleComposition.remove(namespaceMECAntigo);
		eleComposition.remove(namespaceFaceletsOld);
		eleComposition.remove(namespaceJSFCoreOld);
		eleComposition.remove(namespaceHTMLOld);
		eleComposition.remove(namespaceJSTLCoreOld);
		eleComposition.remove(namespaceDefault);
		eleComposition.remove(namespaceSeam);
		
		//ajustando template
		Attribute attributeTemplate = eleComposition.attribute("template");
		if (attributeTemplate != null) {
			attributeTemplate.setValue(
					"/WEB-INF/templates/form_template.xhtml");
		}
		
		
		rootElement.add(namespaceJSTLFunctions);
		rootElement.add(namespaceMECNovo );
		rootElement.add(namespacePrimefaces );
		rootElement.add(namespacePrimefacesExtensions );
		rootElement.add(namespaceAGHU );		
		rootElement.add(namespaceFacelets);
		rootElement.add(namespaceJSFCore );
		rootElement.add(namespaceHTML );
		rootElement.add(namespaceJSTLCore );
		rootElement.add(namespaceMecFunctions );
		rootElement.add(namespaceDefault );
		
		String stXMLDocument = document.asXML();
		
		stXMLDocument = stXMLDocument.replace("xmlns:mec=\"http://www.mec.gov.br/components\"", "");
		stXMLDocument = stXMLDocument.replace("xmlns:ui=\"http://java.sun.com/jsf/facelets\"", "");
		stXMLDocument = stXMLDocument.replace("xmlns:h=\"http://java.sun.com/jsf/html\"", "");
		stXMLDocument = stXMLDocument.replace("xmlns:f=\"http://java.sun.com/jsf/core\"", "");
		
		
		
		document = DocumentHelper.parseText(stXMLDocument);
		
		return document;
		
		
	}

}
