package br.gov.mec.aghu.action;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.RapServidores;

public class MensagemPendenciaAssinaturaDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6837133548008020141L;

	private byte[] arquivoGerado;
	
	private Object entidadePai;
	
	private AghDocumentoCertificado documentoCertificado;
	
	private RapServidores servidorLogado;

	public byte[] getArquivoGerado() {
		return arquivoGerado;
	}

	public void setArquivoGerado(byte[] arquivoGerado) {
		this.arquivoGerado = arquivoGerado;
	}

	public Object getEntidadePai() {
		return entidadePai;
	}

	public void setEntidadePai(Object entidadePai) {
		this.entidadePai = entidadePai;
	}

	public AghDocumentoCertificado getDocumentoCertificado() {
		return documentoCertificado;
	}

	public void setDocumentoCertificado(AghDocumentoCertificado documentoCertificado) {
		this.documentoCertificado = documentoCertificado;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	
	

}
