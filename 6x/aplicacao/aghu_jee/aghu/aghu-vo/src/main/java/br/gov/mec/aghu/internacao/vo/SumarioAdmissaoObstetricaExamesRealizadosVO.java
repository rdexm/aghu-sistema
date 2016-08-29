package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;
import java.util.Date;

public class SumarioAdmissaoObstetricaExamesRealizadosVO implements Serializable {

	private static final long serialVersionUID = 1016570298172743367L;
	private Date dtExame;
	private String nomeExame;
	private String resultadoExame;
	private String rxsChave;
	
	public Date getDtExame() {
		return dtExame;
	}
	public void setDtExame(Date dtExame) {
		this.dtExame = dtExame;
	}
	public String getNomeExame() {
		return nomeExame;
	}
	public void setNomeExame(String nomeExame) {
		this.nomeExame = nomeExame;
	}
	public String getResultadoExame() {
		return resultadoExame;
	}
	public void setResultadoExame(String resultadoExame) {
		this.resultadoExame = resultadoExame;
	}
	public String getRxsChave() {
		return rxsChave;
	}
	public void setRxsChave(String rxsChave) {
		this.rxsChave = rxsChave;
	}
}
