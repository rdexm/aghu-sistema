package br.gov.mec.aghu.ambulatorio.vo;

import java.io.File;
import java.io.Serializable;

public class ArquivosEsusVO implements Serializable {
	private File arquivoEsus;
	private File arquivoInconsistenciasEsus;
	public File getArquivoEsus() {
		return arquivoEsus;
	}
	public void setArquivoEsus(File arquivoEsus) {
		this.arquivoEsus = arquivoEsus;
	}
	public File getArquivoInconsistenciasEsus() {
		return arquivoInconsistenciasEsus;
	}
	public void setArquivoInconsistenciasEsus(File arquivoInconsistenciasEsus) {
		this.arquivoInconsistenciasEsus = arquivoInconsistenciasEsus;
	}
	
	
}