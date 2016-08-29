package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.sicon.util.Cnet;

public class DadosEnvioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3912538723117211286L;
	private String xmlEnvio;
	private Cnet cnet;
	private ScoLogEnvioSicon logEnvioSicon;

	public String getXmlEnvio() {
		return xmlEnvio;
	}

	public void setXmlEnvio(String xmlEnvio) {
		this.xmlEnvio = xmlEnvio;
	}

	public Cnet getCnet() {
		return cnet;
	}

	public void setCnet(Cnet cnet) {
		this.cnet = cnet;
	}

	public ScoLogEnvioSicon getLogEnvioSicon() {
		return logEnvioSicon;
	}

	public void setLogEnvioSicon(ScoLogEnvioSicon logEnvioSicon) {
		this.logEnvioSicon = logEnvioSicon;
	}
	
	

}
