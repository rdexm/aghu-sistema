package br.gov.mec.aghu.sicon.util.integracaosicon;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="erroxml" type="erroxmlTipo"/>
 *         &lt;element name="descxml" type="descxmlTipo"/>
 *         &lt;element name="itens" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="erroxml" type="erroxmlTipo"/>
 *                             &lt;element name="descxml" type="descxmlTipo"/>
 *                             &lt;element name="erronat" type="erronatTipo"/>
 *                             &lt;element name="descnat" type="descnatTipo"/>
 *                           &lt;/sequence>
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "erroxml",
    "descxml",
    "erronat",
    "descnat",
    "posnat",
    "itens"
})

@XmlRootElement(name = "cnet")
@Stateless
public class CnetRet {

    @XmlElement(name= "erroxml", required = true)
    protected String erroxml;
    
    @XmlElement(name = "descxml")
    protected String descxml;
    
    @XmlElement(name= "erronat")
	protected String erronat;
	    
    @XmlElement(name = "descnat")
    protected String descnat;
    
    @XmlElement(name = "posnat")
    protected String posnat;
    
    protected CnetRet.Itens itens;
    
    /**
     * Gets the value of the erroxml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErroxml() {
		return erroxml;
	}

    /**
     * Sets the value of the erroxml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setErroxml(String value) {
		this.erroxml = value;
	}

	/**
     * Gets the value of the descxml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getDescxml() {
		return descxml;
	}

    /**
     * Sets the value of the descxml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setDescxml(String value) {
		this.descxml = value;
	}
	
	/**
     * Gets the value of the erronat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getErronat() {
		return erronat;
	}

	
	/**
     * Sets the value of the erronat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setErronat(String value) {
		this.erronat = value;
	}

	
	/**
     * Gets the value of the descnat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getDescnat() {
		return descnat;
	}

	
	/**
     * Sets the value of the descnat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setDescnat(String value) {
		this.descnat = value;
	}

	
	/**
     * Gets the value of the posnat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getPosnat() {
		return posnat;
	}

	
	/**
     * Sets the value of the posnat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setPosnat(String value) {
		this.posnat = value;
	}
	

	/**
     * Gets the value of the itens property.
     * 
     * @return
     *     possible object is
     *     {@link CnetRet.Itens }
     *     
     */
    public CnetRet.Itens getItens() {
        return itens;
    }

    /**
     * Sets the value of the itens property.
     * 
     * @param value
     *     allowed object is
     *     {@link CnetRet.Itens }
     *     
     */
    public void setItens(CnetRet.Itens value) {
        this.itens = value;
    }

    
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
     *         &lt;element name="item" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="erroxml" type="erroxmlTipo"/>
     *                   &lt;element name="descxml" type="descxmlTipo"/>
     *                   &lt;element name="erronat" type="erronatTipo"/>
     *                   &lt;element name="descnat" type="descnatTipo"/>
     *                 &lt;/sequence>
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
        "item"
    })
    public static class Itens {

        protected List<CnetRet.Itens.Item> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CnetRet.Itens.Item }
         * 
         * 
         */
        public List<CnetRet.Itens.Item> getItem() {
            if (item == null) {
                item = new ArrayList<CnetRet.Itens.Item>();
            }
            return this.item;
        }


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
         *         &lt;element name="erroxml" type="erroxmlTipo"/>
         *         &lt;element name="descxml" type="descxmlTipo"/>
	     *         &lt;element name="erronat" type="erronatTipo"/>
	     *         &lt;element name="descnat" type="descnatTipo"/>
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
            "erroxml",
            "descxml",
            "erronat",
            "descnat"
        })
        public static class Item {

            @XmlElement(name="erroxml", required = true)
        	protected String erroxml;
        	    
    	    @XmlElement(name = "descxml")
    	    protected String descxml;
    	    
    	    @XmlElement(name="erronat", required = true)
        	protected String erronat;
        	    
    	    @XmlElement(name = "descnat")
    	    protected String descnat;

    	    /**
             * Gets the value of the erroxml property.
             * 
             */
			public String getErroxml() {
				return erroxml;
			}

			/**
             * Sets the value of the erroxml property.
             * 
             */
			public void setErroxml(String value) {
				this.erroxml = value;
			}

			/**
             * Gets the value of the descxml property.
             * 
             */
			public String getDescxml() {
				return descxml;
			}

			/**
             * Sets the value of the descxml property.
             * 
             */
			public void setDescxml(String value) {
				this.descxml = value;
			}

			/**
             * Gets the value of the erronat property.
             * 
             */
			public String getErronat() {
				return erronat;
			}

			/**
             * Sets the value of the erronat property.
             * 
             */
			public void setErronat(String value) {
				this.erronat = value;
			}

			/**
             * Gets the value of the descnat property.
             * 
             */
			public String getDescnat() {
				return descnat;
			}

			/**
             * Sets the value of the descnat property.
             * 
             */
			public void setDescnat(String value) {
				this.descnat = value;
			}
            
        }
    }

}
