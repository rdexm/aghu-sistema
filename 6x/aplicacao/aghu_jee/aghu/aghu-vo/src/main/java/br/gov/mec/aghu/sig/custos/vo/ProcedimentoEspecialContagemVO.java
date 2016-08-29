package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;


public class ProcedimentoEspecialContagemVO {

	private Integer atdSeq;
	private Integer pmeSeq; 
	private Short pedSeq; 
	private Integer phiSeq;
	private Integer ocvSeq;
	private Integer mpcSeq;
	private Date pmeDthrInicio;

	public ProcedimentoEspecialContagemVO(){
		super();
	}
	
	public ProcedimentoEspecialContagemVO(Integer atdSeq, Integer pmeSeq, Short pedSeq, Integer phiSeq, Integer ocvSeq, Integer mpcSeq, Date pmeDthrInicio) {
		super();
		this.atdSeq = atdSeq;
		this.pmeSeq = pmeSeq;
		this.pedSeq = pedSeq;
		this.phiSeq = phiSeq;
		this.ocvSeq = ocvSeq;
		this.mpcSeq = mpcSeq;
		this.pmeDthrInicio = pmeDthrInicio;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPmeSeq() {
		return pmeSeq;
	}

	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}

	public Short getPedSeq() {
		return pedSeq;
	}

	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public Integer getMpcSeq() {
		return mpcSeq;
	}

	public void setMpcSeq(Integer mpcSeq) {
		this.mpcSeq = mpcSeq;
	}
	
	public Date getPmeDthrInicio() {
		return pmeDthrInicio;
	}

	public void setPmeDthrInicio(Date pmeDthrInicio) {
		this.pmeDthrInicio = pmeDthrInicio;
	}

	public enum Fields {
		ATD_SEQ("atdSeq"),
		PME_SEQ("pmeSeq"),
		PED_SEQ("pedSeq"),
		PHI_SEQ("phiSeq"),
		OCV_SEQ("ocvSeq"),
		MPC_SEQ("mpcSeq"),
		PME_DTHR_INICIO("pmeDthrInicio")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

}
