//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.6 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: AM.09.29 às 09:37:13 AM BRT 
//

package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ObterUrlDocumentosComFiltroExclusivo_QNAME = new QName("", "ObterUrlDocumentosComFiltroExclusivo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ObterUrlDocumentosComFiltroExclusivo }
     * 
     */
    public ObterUrlDocumentosComFiltroExclusivo createObterUrlDocumentosComFiltroExclusivo() {
        return new ObterUrlDocumentosComFiltroExclusivo();
    }

    /**
     * Create an instance of {@link ObterUrlDocumentosComFiltroExclusivo.Documentos }
     * 
     */
    public ObterUrlDocumentosComFiltroExclusivo.Documentos createObterUrlDocumentosComFiltroExclusivoDocumentos() {
        return new ObterUrlDocumentosComFiltroExclusivo.Documentos();
    }

    /**
     * Create an instance of {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento }
     * 
     */
    public ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento createObterUrlDocumentosComFiltroExclusivoDocumentosDocumento() {
        return new ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento();
    }

    /**
     * Create an instance of {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos }
     * 
     */
    public ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos createObterUrlDocumentosComFiltroExclusivoDocumentosDocumentoCampos() {
        return new ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos();
    }

    
    /**
     * Create an instance of {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo }
     * 
     */
    public ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo createObterUrlDocumentosComFiltroExclusivoDocumentosDocumentoCamposCampo() {
        return new ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo();
    }

    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObterUrlsDocumentos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ObterUrlDocumentosComFiltroExclusivo")
    public JAXBElement<ObterUrlDocumentosComFiltroExclusivo> createObterUrlDocumentosComFiltroExclusivo(ObterUrlDocumentosComFiltroExclusivo value) {
        return new JAXBElement<ObterUrlDocumentosComFiltroExclusivo>(_ObterUrlDocumentosComFiltroExclusivo_QNAME, ObterUrlDocumentosComFiltroExclusivo.class, null, value);
    }

}
