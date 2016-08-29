package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.List;

public class EscalaSalasVO implements Serializable {

	private static final long		serialVersionUID	= 3628084983519849968L;
	private Short					sala;
	private List<EscalaSalasDiasVO>	turnos;

	public Short getSala() {
		return sala;
	}

	public void setSala(Short sala) {
		this.sala = sala;
	}

	public List<EscalaSalasDiasVO> getTurnos() {
		return turnos;
	}

	public void setTurnos(List<EscalaSalasDiasVO> turnos) {
		this.turnos = turnos;
	}

}
