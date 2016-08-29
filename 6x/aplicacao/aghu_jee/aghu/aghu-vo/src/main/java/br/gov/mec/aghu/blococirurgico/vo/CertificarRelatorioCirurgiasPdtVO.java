package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class CertificarRelatorioCirurgiasPdtVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3510887728830392425L;

	private Integer crgSeq;
	private Integer nroCopias;
	private Boolean previa;
	private Boolean certificar;
	private Boolean gerarRelatorio;
	
	public CertificarRelatorioCirurgiasPdtVO(Integer crgSeq, Integer nroCopias,
			Boolean previa, Boolean certificar, Boolean gerarRelatorio) {
		super();
		this.crgSeq = crgSeq;
		this.nroCopias = nroCopias;
		this.previa = previa;
		this.certificar = certificar;
		this.gerarRelatorio = gerarRelatorio;
	}

	public Boolean getCertificar() {
		return certificar;
	}
	
	public void setCertificar(Boolean certificar) {
		this.certificar = certificar;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getNroCopias() {
		return nroCopias;
	}

	public void setNroCopias(Integer nroCopias) {
		this.nroCopias = nroCopias;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

	public Boolean getGerarRelatorio() {
		return gerarRelatorio;
	}

	public void setGerarRelatorio(Boolean gerarRelatorio) {
		this.gerarRelatorio = gerarRelatorio;
	}
}
