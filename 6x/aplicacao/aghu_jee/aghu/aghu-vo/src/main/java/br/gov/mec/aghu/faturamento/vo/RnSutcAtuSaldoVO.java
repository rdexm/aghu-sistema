package br.gov.mec.aghu.faturamento.vo;


public class RnSutcAtuSaldoVO {
	
	private final String msgInicial;
	private final String msgAnterior;
	private final String msgAlta;
	private final boolean retorno;
		
	public RnSutcAtuSaldoVO(String msgInicial, String msgAnterior, String msgAlta, boolean retorno) {

		super();
		
		this.msgInicial = msgInicial;
		this.msgAnterior = msgAnterior;
		this.msgAlta = msgAlta;
		this.retorno = retorno;
	}

	
	public String getMsgInicial() {
	
		return this.msgInicial;
	}

	
	public String getMsgAnterior() {
	
		return this.msgAnterior;
	}

	
	public String getMsgAlta() {
	
		return this.msgAlta;
	}

	
	public boolean isRetorno() {
	
		return this.retorno;
	}	
}
