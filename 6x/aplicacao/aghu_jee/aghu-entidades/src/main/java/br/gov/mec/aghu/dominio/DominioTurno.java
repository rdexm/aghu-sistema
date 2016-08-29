package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTurno implements Dominio {
	/**
	 * Manhã
	 */
	M,
	/**
	 * Tarde
	 */
	T,
	/**
	 * Noite
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Manhã";
		case T:
			return "Tarde";
		case N:
			return "Noite";

		default:
			return "";
		}
	}
	
	/**
	 * Converte DominioTurno para DominioTurnoTodos
	 * 
	 * @return
	 */
	public DominioTurnoTodos getDominioTurnoTodos() {
		DominioTurnoTodos turno = null;
		if (DominioTurno.M.equals(this)) {
			turno = DominioTurnoTodos.M;
		} else if (DominioTurno.T.equals(this)) {
			turno = DominioTurnoTodos.T;
		}else if (DominioTurno.N.equals(this)) {
			turno = DominioTurnoTodos.N;
		}
		return turno;
	}
}