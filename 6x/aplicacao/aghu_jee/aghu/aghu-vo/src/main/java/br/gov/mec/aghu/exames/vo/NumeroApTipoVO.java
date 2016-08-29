package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;


public class NumeroApTipoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8516783320461483305L;

	private Long numeroAp;

	private Integer lu2Seq;
	
	private Boolean verificado;
	
	private List<Short> seqs;

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

	public List<Short> getSeqs() {
		return seqs;
	}

	public void setSeqs(List<Short> seqs) {
		this.seqs = seqs;
	}

	public Boolean getVerificado() {
		return verificado;
	}

	public void setVerificado(Boolean verificado) {
		this.verificado = verificado;
	}


}
