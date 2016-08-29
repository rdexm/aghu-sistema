package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

public class SigAtendimentoVO extends SigVO{
	
    private Short unfSeqSolicitante;
    private Integer medicamento;
    private Integer phiSeq;
    private BigDecimal totalSolic;     
    private BigDecimal totalDisp;
    
    public SigAtendimentoVO(){}
    
	public SigAtendimentoVO(Integer atdPaciente, Integer pacCodigo, Integer trpSeq,
			Date dtPrevExecucao, Integer centroCusto, Short unfSeqSolicitante,
			Integer medicamento, Integer phiSeq, BigDecimal totalSolic,
			BigDecimal totalDisp) {
		
		super(atdPaciente, pacCodigo, trpSeq,
				dtPrevExecucao, centroCusto);
		this.unfSeqSolicitante = unfSeqSolicitante;
		this.medicamento = medicamento;
		this.phiSeq = phiSeq;
		this.totalSolic = totalSolic;
		this.totalDisp = totalDisp;
	}

	public Short getUnfSeqSolicitante() {
		return unfSeqSolicitante;
	}

	public void setUnfSeqSolicitante(Short unfSeqSolicitante) {
		this.unfSeqSolicitante = unfSeqSolicitante;
	}

	public Integer getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(Integer medicamento) {
		this.medicamento = medicamento;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public BigDecimal getTotalSolic() {
		return totalSolic;
	}

	public void setTotalSolic(BigDecimal totalSolic) {
		this.totalSolic = totalSolic;
	}

	public BigDecimal getTotalDisp() {
		return totalDisp;
	}

	public void setTotalDisp(BigDecimal totalDisp) {
		this.totalDisp = totalDisp;
	}
    
}
