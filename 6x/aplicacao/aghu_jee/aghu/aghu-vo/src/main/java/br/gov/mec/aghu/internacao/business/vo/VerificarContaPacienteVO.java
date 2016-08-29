package br.gov.mec.aghu.internacao.business.vo;

import java.util.Date;

public class VerificarContaPacienteVO {

	private Date dataLimite;
	private Integer seqPhi;

	public VerificarContaPacienteVO() {
		super();
	}

	public VerificarContaPacienteVO(Date dataLimite, Integer seqPhi) {
		super();
		this.dataLimite = dataLimite;
		this.seqPhi = seqPhi;
	}

	public Date getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	public Integer getSeqPhi() {
		return seqPhi;
	}

	public void setSeqPhi(Integer seqPhi) {
		this.seqPhi = seqPhi;
	}

}
