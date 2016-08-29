package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;


public class SigPreparoMdtoVO extends SigVO{
	private Short unfSeq;
	private String tipoEtiqueta;
	private Integer nroPreparo;
	
	public SigPreparoMdtoVO(
			Short unfSeq, String tipoEtiqueta,
			Integer nroPreparo, Integer itoPtoSeq, Short itoSeqp, 
			Integer atdPaciente, Integer pacCodigo, Integer trpSeq,
			Date dtPrevExecucao, Integer centroCusto) {
		
		super(atdPaciente, pacCodigo, trpSeq,
				dtPrevExecucao, centroCusto);
		
		this.unfSeq = unfSeq;
		this.tipoEtiqueta = tipoEtiqueta;
		this.nroPreparo = nroPreparo;
	}
	
	public SigPreparoMdtoVO(){}
	
	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getTipoEtiqueta() {
		return tipoEtiqueta;
	}

	public void setTipoEtiqueta(String tipoEtiqueta) {
		this.tipoEtiqueta = tipoEtiqueta;
	}

	public Integer getNroPreparo() {
		return nroPreparo;
	}

	public void setNroPreparo(Integer nroPreparo) {
		this.nroPreparo = nroPreparo;
	}	
}
