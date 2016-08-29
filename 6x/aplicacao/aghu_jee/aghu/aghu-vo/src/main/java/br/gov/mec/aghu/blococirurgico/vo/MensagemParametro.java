package br.gov.mec.aghu.blococirurgico.vo;

public class MensagemParametro {
	
	private String mensagem;
	private Object[] parametros;
	
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Object[] getParametros() {
		return parametros;
	}
	public void setParametros(Object[] parametros) {
		this.parametros = parametros;
	}
}
