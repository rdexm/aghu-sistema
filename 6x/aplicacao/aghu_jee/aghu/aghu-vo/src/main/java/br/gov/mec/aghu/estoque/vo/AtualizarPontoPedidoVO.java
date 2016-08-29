package br.gov.mec.aghu.estoque.vo;

public class AtualizarPontoPedidoVO {
	
	private Integer seq;
	private Short almSeq;
	private Integer matCodigo;
	private Short tempoReposicao;
	private Boolean calculaMediaPonderada;
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Short getAlmSeq() {
		return almSeq;
	}
	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public Short getTempoReposicao() {
		return tempoReposicao;
	}
	public void setTempoReposicao(Short tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}
	public Boolean getCalculaMediaPonderada() {
		return calculaMediaPonderada;
	}
	public void setCalculaMediaPonderada(Boolean calculaMediaPonderada) {
		this.calculaMediaPonderada = calculaMediaPonderada;
	}
	
}
