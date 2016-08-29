package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

/**
 * VO da estória #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
public class MonitorCirurgiaSalaRecuperacaoVO extends MonitorCirurgiaVO {

	private static final long serialVersionUID = -4570669786737509100L;

	private Date entradaSalaRecuperacao; // Coluna DTHR_ENTRADA_SR

	public Date getEntradaSalaRecuperacao() {
		return entradaSalaRecuperacao;
	}

	public void setEntradaSalaRecuperacao(Date entradaSalaRecuperacao) {
		this.entradaSalaRecuperacao = entradaSalaRecuperacao;
	}
}
