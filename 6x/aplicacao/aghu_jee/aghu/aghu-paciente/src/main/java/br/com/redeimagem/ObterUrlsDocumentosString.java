
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
 *         &lt;element name="codigoFicha" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="camposFicha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "codigoFicha",
    "camposFicha"
})
@XmlRootElement(name = "ObterUrlsDocumentosString")
public class ObterUrlsDocumentosString {

    protected String loginUsuario;
    protected String senha;
    protected int codigoFicha;
    protected String camposFicha;

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
     * Gets the value of the codigoFicha property.
     * 
     */
    public int getCodigoFicha() {
        return codigoFicha;
    }

    /**
     * Sets the value of the codigoFicha property.
     * 
     */
    public void setCodigoFicha(int value) {
        this.codigoFicha = value;
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

}
