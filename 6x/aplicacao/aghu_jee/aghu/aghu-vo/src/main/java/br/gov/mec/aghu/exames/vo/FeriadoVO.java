package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioTurno;

public class FeriadoVO {

	private DominioTurno turno;
	private String tipoDia;
	
	public FeriadoVO() {
		super();
	}

	public FeriadoVO(DominioTurno turno, String tipoDia) {
		super();
		this.turno = turno;
		this.tipoDia = tipoDia;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	public String getTipoDia() {
		return tipoDia;
	}

	public void setTipoDia(String tipoDia) {
		this.tipoDia = tipoDia;
	}
}
