package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class SigProcedimentoMedicamentoExameVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -7558260413338173646L;
	
	private Integer intdCodPrnt;
	private Integer prhcCodHcpa;
	private BigDecimal qtde;
	private BigDecimal qtdeCsh;
	private Integer phiSeq;
	private Integer iseSoeSeq;
	private Integer matCodigo;
	
	
	public Integer getIntdCodPrnt() {
		return intdCodPrnt;
	}
	public void setIntdCodPrnt(Integer intdCodPrnt) {
		this.intdCodPrnt = intdCodPrnt;
	}
	public Integer getPrhcCodHcpa() {
		return prhcCodHcpa;
	}
	public void setPrhcCodHcpa(Integer prhcCodHcpa) {
		this.prhcCodHcpa = prhcCodHcpa;
	}
	public BigDecimal getQtde() {
		return qtde;
	}
	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}
	public BigDecimal getQtdeCsh() {
		return qtdeCsh;
	}
	public void setQtdeCsh(BigDecimal qtdeCsh) {
		this.qtdeCsh = qtdeCsh;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}
	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	
}
