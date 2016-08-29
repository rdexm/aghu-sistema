package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ProcedimentoSusVO implements Serializable {
	
	private static final long serialVersionUID = 5339954567542543039L;
	private Integer intSeq;
	private Integer phiSeq;
	private Integer cthSeq;
	private Short iphPhoSeq;
	private Integer iphSeq;
	
	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Integer getCthSeq() {
		return cthSeq;
	}
	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof ProcedimentoSusVO)) {
			return false;
		}
		
		ProcedimentoSusVO other = (ProcedimentoSusVO)obj;
		return this.getIntSeq().equals(other.getIntSeq()) 
				&& this.getPhiSeq().equals(other.getPhiSeq())
				&& this.getCthSeq().equals(other.getCthSeq())
				&& this.getIphPhoSeq().equals(other.getIphPhoSeq())
				&& this.getIphSeq().equals(other.getIphSeq());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(intSeq).append(phiSeq).append(cthSeq).append(iphPhoSeq).append(iphSeq).toHashCode();
	}
	
}
