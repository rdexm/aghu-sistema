package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

public class SumarioAdmissaoObstetricaExamesCondutaVO implements Serializable {

	private static final long serialVersionUID = -5344641999630866539L;
	private String descricaoConduta;
	private String codigoConduta;
	private String complementoConduta;
	private Long cdtSeq;
	
	public String getDescricaoConduta() {
		return descricaoConduta;
	}
	public void setDescricaoConduta(String descricaoConduta) {
		this.descricaoConduta = descricaoConduta;
	}	
	public String getCodigoConduta() {
		return codigoConduta;
	}
	public void setCodigoConduta(String codigoConduta) {
		this.codigoConduta = codigoConduta;
	}
	public String getComplementoConduta() {
		return complementoConduta;
	}
	public void setComplementoConduta(String complementoConduta) {
		this.complementoConduta = complementoConduta;
	}
	public void setCdtSeq(Long cdtSeq) {
		this.cdtSeq = cdtSeq;
	}
	public Long getCdtSeq() {
		return cdtSeq;
	}
	

}

