package br.gov.mec.aghu.util.migrador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DocumentFactory {
	
	
    private static String caminhoDTD;
	
	private static String caminhoLatin;
	
	private static String caminhoSpecial;
	
	private static String caminhoSymbol;
	
	
	public DocumentFactory(String path){
		caminhoDTD = path + "/aghu/aghu-web/src/main/webapp/WEB-INF/xhtml1-transitional.dtd";
		caminhoLatin = path + "/aghu/aghu-web/src/main/webapp/WEB-INF/xhtml-lat1.ent";
		caminhoSpecial = path + "/aghu/aghu-web/src/main/webapp/WEB-INF/xhtml-special.ent";
		caminhoSymbol = path + "/aghu/aghu-web/src/main/webapp/WEB-INF/xhtml-symbol.ent";
		
	}
	
	public  Document create(String caminho) throws DocumentException{
		EntityResolver resolver = createEntityResolver();
		Document retorno;
		Document documento;
		
		File xhtmlFile = new File(caminho);
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(resolver);
		documento = reader.read(xhtmlFile);
		
		
		//criando um novo nó raiz. <html>
		org.dom4j.DocumentFactory  factory = org.dom4j.DocumentFactory.getInstance();		
		retorno = factory.createDocument();		
		Element novoRoot = retorno.addElement("html");		
		novoRoot.add(documento.getRootElement());		
		return retorno;		
		
	}
	
	
	public  Document create(File arquivo) throws DocumentException{
		EntityResolver resolver = createEntityResolver();
		Document retorno;
		Document documento;		
		
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(resolver);
		documento = reader.read(arquivo);
		
		
		//criando um novo nó raiz. <html>
		org.dom4j.DocumentFactory  factory = org.dom4j.DocumentFactory.getInstance();		
		retorno = factory.createDocument();		
		Element novoRoot = retorno.addElement("html");		
		novoRoot.add(documento.getRootElement());		
		return retorno;		
		
	}
	
	public  Document createFase2(File arquivo) throws DocumentException{
		EntityResolver resolver = createEntityResolver();
	//	Document retorno;
		Document documento;		
		
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(resolver);
		documento = reader.read(arquivo);
		
		
		//criando um novo nó raiz. <html>
//		org.dom4j.DocumentFactory  factory = org.dom4j.DocumentFactory.getInstance();		
//		retorno = factory.createDocument();		
//		Element novoRoot = retorno.addElement("html");		
//		novoRoot.add(documento.getRootElement());		
		return documento;		
		
	}
	
	
	
	
	private static EntityResolver createEntityResolver() {
		EntityResolver resolver = new EntityResolver() {
		    public InputSource resolveEntity(String publicId, String systemId) throws FileNotFoundException {
		        if ( publicId.equals( "-//W3C//DTD XHTML 1.0 Transitional//EN" ) ) {		        	
		        	 InputStream in = new FileInputStream(new File(caminhoDTD));
		            return new InputSource( in );
		        }else if (publicId.equals( "-//W3C//ENTITIES Latin 1 for XHTML//EN" )){
		        	 InputStream in = new FileInputStream(new File(caminhoLatin));
			            return new InputSource( in );		        	
		        }else if (publicId.equals( "-//W3C//ENTITIES Symbols for XHTML//EN" )){
		        	 InputStream in = new FileInputStream(new File(caminhoSymbol));
			            return new InputSource( in );		        	
		        }else if (publicId.equals( "-//W3C//ENTITIES Special for XHTML//EN" )){
		        	 InputStream in = new FileInputStream(new File(caminhoSpecial));
			            return new InputSource( in );		        	
		        }
		        return null;
		    }
		};
		return resolver;
	}

}
