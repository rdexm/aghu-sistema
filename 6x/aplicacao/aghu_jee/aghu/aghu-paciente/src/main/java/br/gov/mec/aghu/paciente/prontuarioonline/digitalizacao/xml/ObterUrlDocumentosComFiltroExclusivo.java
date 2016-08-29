//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.6 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: AM.09.29 às 09:37:13 AM BRT 
//

package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


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
 *         &lt;element name="Documentos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Documento" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Campos">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Campo" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;simpleContent>
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                               &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="Valor" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/extension>
 *                                           &lt;/simpleContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
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
@XmlType(name = "", propOrder = {
    "documentos"
})
@XmlRootElement(name = "ObterUrlDocumentosComFiltroExclusivo")
public class ObterUrlDocumentosComFiltroExclusivo {

    @XmlElement(name = "Documentos", required = true)
    protected ObterUrlDocumentosComFiltroExclusivo.Documentos documentos;
    @XmlAttribute(name = "ficha")
    protected Byte ficha;
    @XmlAttribute(name = "loginUsuario")
    protected String loginUsuario;
    @XmlAttribute(name = "dataHora")
    protected String dataHora;

    /**
     * Obtém o valor da propriedade documentos.
     * 
     * @return
     *     possible object is
     *     {@link ObterUrlDocumentosComFiltroExclusivo.Documentos }
     *     
     */
    public ObterUrlDocumentosComFiltroExclusivo.Documentos getDocumentos() {
        return documentos;
    }

    /**
     * Define o valor da propriedade documentos.
     * 
     * @param value
     *     allowed object is
     *     {@link ObterUrlDocumentosComFiltroExclusivo.Documentos }
     *     
     */
    public void setDocumentos(ObterUrlDocumentosComFiltroExclusivo.Documentos value) {
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
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Campos">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Campo" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;simpleContent>
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                     &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="Valor" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/extension>
     *                                 &lt;/simpleContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
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
        protected List<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento> documento;

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
         * {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento }
         * 
         * 
         */
        public List<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento> getDocumento() {
            if (documento == null) {
                documento = new ArrayList<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento>();
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
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="Campos">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Campo" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;simpleContent>
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                           &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="Valor" type="{http://www.w3.org/2001/XMLSchema}string" />
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
         *       &lt;attribute name="UrlVisualizar" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "campos"
        })
        public static class Documento {

            @XmlElement(name = "Campos", required = true)
            protected ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos campos;
            @XmlAttribute(name = "UrlVisualizar")
            @XmlSchemaType(name = "anyURI")
            protected String urlVisualizar;

            /**
             * Obtém o valor da propriedade campos.
             * 
             * @return
             *     possible object is
             *     {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos }
             *     
             */
            public ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos getCampos() {
                return campos;
            }

            /**
             * Define o valor da propriedade campos.
             * 
             * @param value
             *     allowed object is
             *     {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos }
             *     
             */
            public void setCampos(ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos value) {
                this.campos = value;
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
             * <p>Classe Java de anonymous complex type.
             * 
             * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="Campo" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;simpleContent>
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                 &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="Valor" type="{http://www.w3.org/2001/XMLSchema}string" />
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
                "campo"
            })
            public static class Campos {

                @XmlElement(name = "Campo")
                protected List<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo> campo;

                /**
                 * Gets the value of the campo property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the campo property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getCampo().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo }
                 * 
                 * 
                 */
                public List<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo> getCampo() {
                    if (campo == null) {
                        campo = new ArrayList<ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo>();
                    }
                    return this.campo;
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
                 *       &lt;attribute name="Nome" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="Valor" type="{http://www.w3.org/2001/XMLSchema}string" />
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
                public static class Campo {

                    @XmlValue
                    protected String value;
                    @XmlAttribute(name = "Nome")
                    protected String nome;
                    @XmlAttribute(name = "Valor")
                    protected String valor;

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
                     * Obtém o valor da propriedade valor.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValor() {
                        return valor;
                    }

                    /**
                     * Define o valor da propriedade valor.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValor(String value) {
                        this.valor = value;
                    }

                }

            }

        }

    }

}
