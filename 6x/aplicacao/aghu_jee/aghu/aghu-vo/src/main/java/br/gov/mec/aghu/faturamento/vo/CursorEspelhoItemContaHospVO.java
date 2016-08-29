package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;

/**
 * VO utilizado no cursor c_iec
 */
public class CursorEspelhoItemContaHospVO {
	
	// Utilizados no cursor c_iec
	
	private FatEspelhoItemContaHosp espelhoItemContaHosp;
	private Short qtdAma;
	private Short atualizado;
	private Boolean indInternacao;
	
	public CursorEspelhoItemContaHospVO(
			FatEspelhoItemContaHosp espelhoItemContaHosp, Short qtdAma,
			Short atualizado, Boolean indInternacao) {
		super();
		this.espelhoItemContaHosp = espelhoItemContaHosp;
		this.qtdAma = qtdAma;
		this.atualizado = atualizado;
		this.indInternacao = indInternacao;
	}
	
	public FatEspelhoItemContaHosp getEspelhoItemContaHosp() {
		return espelhoItemContaHosp;
	}
	public void setEspelhoItemContaHosp(FatEspelhoItemContaHosp espelhoItemContaHosp) {
		this.espelhoItemContaHosp = espelhoItemContaHosp;
	}
	public Short getQtdAma() {
		return qtdAma;
	}
	public void setQtdAma(Short qtdAma) {
		this.qtdAma = qtdAma;
	}
	public Short getAtualizado() {
		return atualizado;
	}
	public void setAtualizado(Short atualizado) {
		this.atualizado = atualizado;
	}
	public Boolean getIndInternacao() {
		return indInternacao;
	}
	public void setIndInternacao(Boolean indInternacao) {
		this.indInternacao = indInternacao;
	}
}
