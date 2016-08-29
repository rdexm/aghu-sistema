package br.gov.mec.aghu.vo;

import java.io.Serializable;

import org.apache.commons.io.FileUtils;

public class LogServidorAplicacaoVO implements Serializable {

	private static final long serialVersionUID = -6225979695557934990L;

	private String caminhoArquivo;

	private Long tamanho;

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}

	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public Long getTamanho() {
		return tamanho;
	}

	public void setTamanho(Long tamanho) {
		this.tamanho = tamanho;
	}
	
	public String getTamanhoFormatado() {
		return FileUtils.byteCountToDisplaySize(this.tamanho);
	}
	
}
