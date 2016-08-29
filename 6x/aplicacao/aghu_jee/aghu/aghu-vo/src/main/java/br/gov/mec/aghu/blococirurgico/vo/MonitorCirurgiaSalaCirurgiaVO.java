package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

/**
 * VO da estória #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
public class MonitorCirurgiaSalaCirurgiaVO extends MonitorCirurgiaVO {

	private static final long serialVersionUID = -5866365157470696215L;

	private Date entradaSala; // Coluna DTHR_ENTRADA_SALA de MBC_CIRURGIAS
	private String equipe; // Coluna NOME de V_RAP_PESSOA_SERVIDOR

	public Date getEntradaSala() {
		return entradaSala;
	}

	public void setEntradaSala(Date entradaSala) {
		this.entradaSala = entradaSala;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

}
