
package br.com.redeimagem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loginUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="senha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoPerfilImportacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="camposFicha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nomeArquivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arquivoBase64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="md5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "loginUsuario",
    "senha",
    "codigoPerfilImportacao",
    "camposFicha",
    "nomeArquivo",
    "arquivoBase64",
    "md5"
})
@XmlRootElement(name = "ImportarArquivoPerfil")
public class ImportarArquivoPerfil {

    protected String loginUsuario;
    protected String senha;
    protected int codigoPerfilImportacao;
    protected String camposFicha;
    protected String nomeArquivo;
    protected String arquivoBase64;
    protected String md5;

    /**
     * Gets the value of the loginUsuario property.
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
     * Sets the value of the loginUsuario property.
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
     * Gets the value of the senha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Sets the value of the senha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenha(String value) {
        this.senha = value;
    }

    /**
     * Gets the value of the codigoPerfilImportacao property.
     * 
     */
    public int getCodigoPerfilImportacao() {
        return codigoPerfilImportacao;
    }

    /**
     * Sets the value of the codigoPerfilImportacao property.
     * 
     */
    public void setCodigoPerfilImportacao(int value) {
        this.codigoPerfilImportacao = value;
    }

    /**
     * Gets the value of the camposFicha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCamposFicha() {
        return camposFicha;
    }

    /**
     * Sets the value of the camposFicha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCamposFicha(String value) {
        this.camposFicha = value;
    }

    /**
     * Gets the value of the nomeArquivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeArquivo() {
        return nomeArquivo;
    }

    /**
     * Sets the value of the nomeArquivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeArquivo(String value) {
        this.nomeArquivo = value;
    }

    /**
     * Gets the value of the arquivoBase64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArquivoBase64() {
        return arquivoBase64;
    }

    /**
     * Sets the value of the arquivoBase64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArquivoBase64(String value) {
        this.arquivoBase64 = value;
    }

    /**
     * Gets the value of the md5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets the value of the md5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMd5(String value) {
        this.md5 = value;
    }

}
