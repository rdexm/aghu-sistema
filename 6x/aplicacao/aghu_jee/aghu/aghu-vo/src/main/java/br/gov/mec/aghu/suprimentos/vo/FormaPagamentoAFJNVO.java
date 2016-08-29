package br.gov.mec.aghu.suprimentos.vo;

public class FormaPagamentoAFJNVO {
	
	private String fpgDescricao;
	private Short cdpFpgCodigo;
	
	public String getFpgDescricao() {
		return fpgDescricao;
	}
	
	public void setFpgDescricao(String fpgDescricao) {
		this.fpgDescricao = fpgDescricao;
	}
	
	public Short getCdpFpgCodigo() {
		return cdpFpgCodigo;
	}
	
	public void setCdpFpgCodigo(Short cdpFpgCodigo) {
		this.cdpFpgCodigo = cdpFpgCodigo;
	}
}
