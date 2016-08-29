package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

public class EscalaSalasDiasVO implements Serializable {
	
	private static final long	serialVersionUID	= 8312070039252375817L;
	
	private String turno;
	private EscalaSalasEquipeVO [] dias;


	public void setDias(EscalaSalasEquipeVO [] dias) {
		this.dias = dias;
	}

	public EscalaSalasEquipeVO [] getDias() {
		return dias;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getTurno() {
		return turno;
	}
}
