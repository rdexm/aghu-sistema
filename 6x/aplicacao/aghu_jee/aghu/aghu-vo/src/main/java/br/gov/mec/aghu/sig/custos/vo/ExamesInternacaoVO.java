package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ExamesInternacaoVO {
	
	private Integer soeSeq;
	private Date soeCriadoEm;
	private Integer ocvSeq;
	private Integer phiSeq;
	private BigDecimal qtdeExames;
	private Integer cctCodigo;
	
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Date getSoeCriadoEm() {
		return soeCriadoEm;
	}
	public void setSoeCriadoEm(Date soeCriadoEm) {
		this.soeCriadoEm = soeCriadoEm;
	}
	public Integer getOcvSeq() {
		return ocvSeq;
	}
	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public BigDecimal getQtdeExames() {
		return qtdeExames;
	}
	public void setQtdeExames(BigDecimal qtdeExames) {
		this.qtdeExames = qtdeExames;
	}
	public static ExamesInternacaoVO create(Object[] object) {
		ExamesInternacaoVO vo = new ExamesInternacaoVO();
		
		if (object[0] != null) {
			vo.setSoeSeq(Integer.parseInt(object[0].toString()));
		}
		if(object[1] != null){
			vo.setSoeCriadoEm((Date)object[1]);
		}
		if(object[2] != null){
			vo.setOcvSeq(Integer.parseInt(object[2].toString()));
		}
		if(object[3] != null){
			vo.setPhiSeq(Integer.parseInt(object[3].toString()));
		}
		if(object[4] != null){
			vo.setCctCodigo(Integer.parseInt(object[4].toString()));
		}
		if(object[5] != null){
			vo.setQtdeExames(new BigDecimal(object[5].toString()));
		}
		
		return vo;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

}
