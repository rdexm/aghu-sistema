package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

/**
 * VO da estória #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
public class MonitorCirurgiaSalaPreparoVO extends MonitorCirurgiaVO {

	private static final long serialVersionUID = 5139536018159929576L;

	private Date chegada; // Coluna CRIADO_EM em MBC_EXTRATO_CIRURGIAS

	public Date getChegada() {
		return chegada;
	}

	public void setChegada(Date chegada) {
		this.chegada = chegada;
	}

}
