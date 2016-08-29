package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

public class PortalPlanejamentoTurnosSalaVO implements Serializable{

	private static final long serialVersionUID = -7376826829351050343L;

	private Date horarioInicial;
	private Date horarioFinal;
	private Date dataInicio;
	private Date dataFim;
	private String turno;
	
	public Date getHorarioInicial() {
		return horarioInicial;
	}
	public void setHorarioInicial(Date horarioInicial) {
		this.horarioInicial = horarioInicial;
	}
	public Date getHorarioFinal() {
		return horarioFinal;
	}
	public void setHorarioFinal(Date horarioFinal) {
		this.horarioFinal = horarioFinal;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
}
