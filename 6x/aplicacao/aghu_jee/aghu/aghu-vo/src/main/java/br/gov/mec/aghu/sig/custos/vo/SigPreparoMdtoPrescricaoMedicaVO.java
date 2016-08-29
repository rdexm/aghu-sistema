package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class SigPreparoMdtoPrescricaoMedicaVO extends SigPreparoMdtoVO {
	
	private Integer atdInternacao;
	private Date fimAtendimento;
	private Date dtPreparo;
	
	public SigPreparoMdtoPrescricaoMedicaVO() {
		super();
	}
	
	public Integer getAtdInternacao() {
		return atdInternacao;
	}
	public void setAtdInternacao(Integer atdInternacao) {
		this.atdInternacao = atdInternacao;
	}
	public Date getFimAtendimento() {
		return fimAtendimento;
	}
	public void setFimAtendimento(Date fimAtendimento) {
		this.fimAtendimento = fimAtendimento;
	}
	public Date getDtPreparo() {
		return dtPreparo;
	}
	public void setDtPreparo(Date dtPreparo) {
		this.dtPreparo = dtPreparo;
	}
}
