package br.gov.mec.aghu.blococirurgico.vo;

public class MbcFichaTipoAnestesiaVO {
	private Long ficSeq;
	private String pendente;
	private Short tanSeq;
	
	public Long getFicSeq() {
		return ficSeq;
	}
	public void setFicSeq(Long ficSeq) {
		this.ficSeq = ficSeq;
	}
	public String getPendente() {
		return pendente;
	}
	public void setPendente(String pendente) {
		this.pendente = pendente;
	}
	public Short getTanSeq() {
		return tanSeq;
	}
	public void setTanSeq(Short tanSeq) {
		this.tanSeq = tanSeq;
	}
}
