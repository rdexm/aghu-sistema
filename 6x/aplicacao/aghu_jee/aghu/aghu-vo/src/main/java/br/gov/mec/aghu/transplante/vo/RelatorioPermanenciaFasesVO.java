package br.gov.mec.aghu.transplante.vo;

public class RelatorioPermanenciaFasesVO {
	
	private String textoStatus;
	private String dataFormatada;
	private String textoPermanencia;
	private String textoConcatenado;
	
	
	
	public String getTextoConcatenado() {
		this.textoConcatenado = this.textoStatus + " " + this.dataFormatada + " " + this.textoPermanencia;
		return this.textoConcatenado;
	}
	public void setTextoConcatenado(String textoConcatenado) {
		this.textoConcatenado = textoConcatenado;
	}
	public String getTextoStatus() {
		return textoStatus;
	}
	public void setTextoStatus(String textoStatus) {
		this.textoStatus = textoStatus;
	}
	public String getDataFormatada() {
		return dataFormatada;
	}
	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}
	public String getTextoPermanencia() {
		return textoPermanencia;
	}
	public void setTextoPermanencia(String textoPermanencia) {
		this.textoPermanencia = textoPermanencia;
	}
	
	

}
