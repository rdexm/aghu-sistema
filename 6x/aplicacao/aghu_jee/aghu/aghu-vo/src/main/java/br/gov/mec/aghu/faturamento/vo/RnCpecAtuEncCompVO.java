package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class RnCpecAtuEncCompVO {

	public RnCpecAtuEncCompVO() {}
	
	public RnCpecAtuEncCompVO(final Date pDthrFim, final Date pDthrInicio, final Integer pNewMes, final Integer pNewAno, final Boolean result) {
		this.pDthrFim = pDthrFim;
		this.pDthrInicio = pDthrInicio;
		this.pNewMes = pNewMes;
		this.pNewAno = pNewAno;
		this.result = result;
	}
	
	
	private Date pDthrFim;
	
	private Date pDthrInicio;
	
	private Integer pNewMes;
	
	private Integer pNewAno;
	
	private Boolean result;

	public Date getpDthrFim() {
		return pDthrFim;
	}

	public void setpDthrFim(Date pDthrFim) {
		this.pDthrFim = pDthrFim;
	}

	public Date getpDthrInicio() {
		return pDthrInicio;
	}

	public void setpDthrInicio(Date pDthrInicio) {
		this.pDthrInicio = pDthrInicio;
	}

	public Integer getpNewMes() {
		return pNewMes;
	}

	public void setpNewMes(Integer pNewMes) {
		this.pNewMes = pNewMes;
	}

	public Integer getpNewAno() {
		return pNewAno;
	}

	public void setpNewAno(Integer pNewAno) {
		this.pNewAno = pNewAno;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}
}