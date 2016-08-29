package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

public class SumarioAtdRecemNascidoSlPartoExamesMaeVO {

	private Date dtExame;
	private String descricao;
	private String resultado;
	private String rxsChave;
	
	public Date getDtExame() {
		return dtExame;
	}
	public void setDtExame(Date dtExame) {
		this.dtExame = dtExame;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getRxsChave() {
		return rxsChave;
	}
	public void setRxsChave(String rxsChave) {
		this.rxsChave = rxsChave;
	}
}
