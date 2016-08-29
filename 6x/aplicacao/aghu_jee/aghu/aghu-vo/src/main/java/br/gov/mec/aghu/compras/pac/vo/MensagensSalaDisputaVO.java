package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MensagensSalaDisputaVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String envioMensagem;
	private String loteTipo13;
	private String emitente;
	private String nomeFornecedorTipo13;
	private String mensagem;
	
	
	public String getEnvioMensagem() {
		return envioMensagem;
	}
	public void setEnvioMensagem(String envioMensagem) {
		this.envioMensagem = envioMensagem;
	}
	public String getLoteTipo13() {
		return loteTipo13;
	}
	public void setLoteTipo13(String loteTipo13) {
		this.loteTipo13 = loteTipo13;
	}
	public String getEmitente() {
		return emitente;
	}
	public void setEmitente(String emitente) {
		this.emitente = emitente;
	}
	public String getNomeFornecedorTipo13() {
		return nomeFornecedorTipo13;
	}
	public void setNomeFornecedorTipo13(String nomeFornecedorTipo13) {
		this.nomeFornecedorTipo13 = nomeFornecedorTipo13;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
