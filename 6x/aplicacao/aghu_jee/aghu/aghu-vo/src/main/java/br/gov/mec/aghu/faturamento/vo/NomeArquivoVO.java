package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class NomeArquivoVO implements Serializable {

	private static final long serialVersionUID = -1761997061691615057L;
	
	private String fileName;
	private String nomeRelatorio;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

}
