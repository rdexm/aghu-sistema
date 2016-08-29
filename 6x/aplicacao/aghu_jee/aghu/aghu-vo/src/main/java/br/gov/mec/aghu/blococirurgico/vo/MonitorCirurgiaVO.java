package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * VO da estória #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
public class MonitorCirurgiaVO implements Serializable, Comparable<MonitorCirurgiaVO> {

	private static final long serialVersionUID = 2192551693291845879L;
	private Integer crgSeq; // Id da cirurgia
	private String nomePaciente; // Obtido na FUNCTION MBCC_NOME_PONTO

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Override
	public int compareTo(MonitorCirurgiaVO o) {
		return this.getNomePaciente().compareTo(o.getNomePaciente());
	}
}
