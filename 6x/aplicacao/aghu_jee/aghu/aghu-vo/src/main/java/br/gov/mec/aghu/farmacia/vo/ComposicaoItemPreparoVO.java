package br.gov.mec.aghu.farmacia.vo;

import java.math.BigDecimal;

public class ComposicaoItemPreparoVO {
	private Integer itoPtoSeq;
    private Short itoSeqp;
    private Short seqp;
    private Integer medicamento;
    private String indExterno;
    private Integer phiSeq;
    private Double qtdePreparada;
    private BigDecimal fatorConversaoUp;
    private BigDecimal qtdDispensada;
    
    //cip.qtde_preparada / afd.fator_conversao_up as qtd_dispensada
	
    public ComposicaoItemPreparoVO(Integer itoPtoSeq, Short itoSeqp, Integer phiSeq){
    	this.itoPtoSeq = itoPtoSeq;
    	this.itoSeqp = itoSeqp;
    	this.phiSeq = phiSeq;
    }
    
    public ComposicaoItemPreparoVO(Integer itoPtoSeq, Short itoSeqp,
			Short seqp, Integer medicamento, String indExterno, Integer phiSeq,
			Double qtdePreparada, BigDecimal fatorConversaoUp,
			BigDecimal qtdDispensada) {
		super();
		this.itoPtoSeq = itoPtoSeq;
		this.itoSeqp = itoSeqp;
		this.seqp = seqp;
		this.medicamento = medicamento;
		this.indExterno = indExterno;
		this.phiSeq = phiSeq;
		this.qtdePreparada = qtdePreparada;
		this.fatorConversaoUp = fatorConversaoUp;
		this.qtdDispensada = qtdDispensada;
	}
    
    public ComposicaoItemPreparoVO(Integer itoPtoSeq, Short itoSeqp,
			Short seqp, Integer medicamento, String indExterno, Integer phiSeq,
			Double qtdePreparada, BigDecimal fatorConversaoUp) {
		super();
		this.itoPtoSeq = itoPtoSeq;
		this.itoSeqp = itoSeqp;
		this.seqp = seqp;
		this.medicamento = medicamento;
		this.indExterno = indExterno;
		this.phiSeq = phiSeq;
		this.qtdePreparada = qtdePreparada;
		this.fatorConversaoUp = fatorConversaoUp;
	}

	public Integer getItoPtoSeq() {
		return itoPtoSeq;
	}

	public void setItoPtoSeq(Integer itoPtoSeq) {
		this.itoPtoSeq = itoPtoSeq;
	}

	public Short getItoSeqp() {
		return itoSeqp;
	}

	public void setItoSeqp(Short itoSeqp) {
		this.itoSeqp = itoSeqp;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(Integer medicamento) {
		this.medicamento = medicamento;
	}

	public String getIndExterno() {
		return indExterno;
	}

	public void setIndExterno(String indExterno) {
		this.indExterno = indExterno;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Double getQtdePreparada() {
		return qtdePreparada;
	}

	public void setQtdePreparada(Double qtdePreparada) {
		this.qtdePreparada = qtdePreparada;
	}

	public BigDecimal getFatorConversaoUp() {
		return fatorConversaoUp;
	}

	public void setFatorConversaoUp(BigDecimal fatorConversaoUp) {
		this.fatorConversaoUp = fatorConversaoUp;
	}

	public BigDecimal getQtdDispensada() {
		if(this.qtdePreparada != null && this.fatorConversaoUp != null){
			this.qtdDispensada = new BigDecimal(this.qtdePreparada).divide(this.fatorConversaoUp);
		} else {
			qtdDispensada = BigDecimal.ZERO;
		}
		
		return this.qtdDispensada;
	}

	public void setQtdDispensada(BigDecimal qtdDispensada) {
		this.qtdDispensada = qtdDispensada;
	}    
}
