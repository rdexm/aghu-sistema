package br.gov.mec.aghu.sig.custos.vo;


public class SigProcedimentoMaterialPrescricaoDialiseVO extends SigVO{
	private String tipo;
	private Long quantidade;
	private Integer matCodigo;
	private Integer phiSeq;
	private Short undAtend;
	private Short undAgenda;
	private Integer cProducao;
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Short getUndAtend() {
		return undAtend;
	}
	public void setUndAtend(Short undAtend) {
		this.undAtend = undAtend;
	}
	public Short getUndAgenda() {
		return undAgenda;
	}
	public void setUndAgenda(Short undAgenda) {
		this.undAgenda = undAgenda;
	}
	public Integer getcProducao() {
		return cProducao;
	}
	public void setcProducao(Integer cProducao) {
		this.cProducao = cProducao;
	}
}
