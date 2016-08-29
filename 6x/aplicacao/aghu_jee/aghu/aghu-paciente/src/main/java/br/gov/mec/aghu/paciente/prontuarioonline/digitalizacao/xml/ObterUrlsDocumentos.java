//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.6 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: AM.03.11 às 10:49:59 AM BRT 
//


package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java de ObterUrlsDocumentos complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ObterUrlsDocumentos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Upload">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Url" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Documentos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Documento" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="UrlExcluir" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                           &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                           &lt;attribute name="DataImportado" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="TamanhoBytes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Codigo" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ficha" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *       &lt;attribute name="loginUsuario" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dataHora" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObterUrlsDocumentos", propOrder = {
    "upload",
    "documentos"
})
public class ObterUrlsDocumentos {

    @XmlElement(name = "Upload", required = true)
    protected ObterUrlsDocumentos.Upload upload;
    @XmlElement(name = "Documentos", required = true)
    protected ObterUrlsDocumentos.Documentos documentos;
    @XmlAttribute(name = "ficha")
    protected Byte ficha;
    @XmlAttribute(name = "loginUsuario")
    protected String loginUsuario;
    @XmlAttribute(name = "dataHora")
    protected String dataHora;

    /**
     * Obtém o valor da propriedade upload.
     * 
     * @return
     *     possible object is
     *     {@link ObterUrlsDocumentos.Upload }
     *     
     */
    public ObterUrlsDocumentos.Upload getUpload() {
        return upload;
    }

    /**
     * Define o valor da propriedade upload.
     * 
     * @param value
     *     allowed object is
     *     {@link ObterUrlsDocumentos.Upload }
     *     
     */
    public void setUpload(ObterUrlsDocumentos.Upload value) {
        this.upload = value;
    }

    /**
     * Obtém o valor da propriedade documentos.
     * 
     * @return
     *     possible object is
     *     {@link ObterUrlsDocumentos.Documentos }
     *     
     */
    public ObterUrlsDocumentos.Documentos getDocumentos() {
        return documentos;
    }

    /**
     * Define o valor da propriedade documentos.
     * 
     * @param value
     *     allowed object is
     *     {@link ObterUrlsDocumentos.Documentos }
     *     
     */
    public void setDocumentos(ObterUrlsDocumentos.Documentos value) {
        this.documentos = value;
    }

    /**
     * Obtém o valor da propriedade ficha.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getFicha() {
        return ficha;
    }

    /**
     * Define o valor da propriedade ficha.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setFicha(Byte value) {
        this.ficha = value;
    }

    /**
     * Obtém o valor da propriedade loginUsuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginUsuario() {
        return loginUsuario;
    }

    /**
     * Define o valor da propriedade loginUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginUsuario(String value) {
        this.loginUsuario = value;
    }

    /**
     * Obtém o valor da propriedade dataHora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataHora() {
        return dataHora;
    }

    /**
     * Define o valor da propriedade dataHora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataHora(String value) {
        this.dataHora = value;
    }


    /**
     * <p>Classe Java de anonymous complex type.
     * 
     * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Documento" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="UrlExcluir" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *                 &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *                 &lt;attribute name="DataImportado" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="TamanhoBytes" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Codigo" type="{http://www.w3.org/2001/XMLSchema}int" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "documento"
    })
    public static class Documentos {

        @XmlElement(name = "Documento")
        protected List<ObterUrlsDocumentos.Documentos.Documento> documento;

        /**
         * Gets the value of the documento property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the documento property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDocumento().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ObterUrlsDocumentos.Documentos.Documento }
         * 
         * 
         */
        public List<ObterUrlsDocumentos.Documentos.Documento> getDocumento() {
            if (documento == null) {
                documento = new ArrayList<ObterUrlsDocumentos.Documentos.Documento>();
            }
            return this.documento;
        }


        /**
         * <p>Classe Java de anonymous complex type.
         * 
         * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="UrlExcluir" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *       &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *       &lt;attribute name="DataImportado" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="TamanhoBytes" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Codigo" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Documento {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "UrlExcluir")
            @XmlSchemaType(name = "anyURI")
            protected String urlExcluir;
            @XmlAttribute(name = "UrlVisualizar")
            @XmlSchemaType(name = "anyURI")
            protected String urlVisualizar;
            @XmlAttribute(name = "DataImportado")
            protected String dataImportado;
            @XmlAttribute(name = "TamanhoBytes")
            protected Integer tamanhoBytes;
            @XmlAttribute(name = "Nome")
            protected String nome;
            @XmlAttribute(name = "Codigo")
            protected Integer codigo;

            /**
             * Obtém o valor da propriedade value.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Define o valor da propriedade value.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Obtém o valor da propriedade urlExcluir.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUrlExcluir() {
                return urlExcluir;
            }

            /**
             * Define o valor da propriedade urlExcluir.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUrlExcluir(String value) {
                this.urlExcluir = value;
            }

            /**
             * Obtém o valor da propriedade urlVisualizar.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUrlVisualizar() {
                return urlVisualizar;
            }

            /**
             * Define o valor da propriedade urlVisualizar.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUrlVisualizar(String value) {
                this.urlVisualizar = value;
            }

            /**
             * Obtém o valor da propriedade dataImportado.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDataImportado() {
                return dataImportado;
            }

            /**
             * Define o valor da propriedade dataImportado.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDataImportado(String value) {
                this.dataImportado = value;
            }

            /**
             * Obtém o valor da propriedade tamanhoBytes.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getTamanhoBytes() {
                return tamanhoBytes;
            }

            /**
             * Define o valor da propriedade tamanhoBytes.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setTamanhoBytes(Integer value) {
                this.tamanhoBytes = value;
            }

            /**
             * Obtém o valor da propriedade nome.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNome() {
                return nome;
            }

            /**
             * Define o valor da propriedade nome.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNome(String value) {
                this.nome = value;
            }

            /**
             * Obtém o valor da propriedade codigo.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getCodigo() {
                return codigo;
            }

            /**
             * Define o valor da propriedade codigo.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setCodigo(Integer value) {
                this.codigo = value;
            }

        }

    }


    /**
     * <p>Classe Java de anonymous complex type.
     * 
     * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="Url" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Upload {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "Url")
        @XmlSchemaType(name = "anyURI")
        protected String url;

        /**
         * Obtém o valor da propriedade value.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Define o valor da propriedade value.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtém o valor da propriedade url.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrl() {
            return url;
        }

        /**
         * Define o valor da propriedade url.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrl(String value) {
            this.url = value;
        }

    }

}
