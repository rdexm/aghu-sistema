package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTurnoTodos implements Dominio {
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
	N,
	/**
	 * Todos
	 */
	TODOS;

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
		case TODOS:
			return "Todos";
		default:
			return "";
		}
	}
	
	/**
	 * Converte DominioTurnoTodos para DominioTurno
	 * 
	 * @return
	 */
	public DominioTurno getDominioTurno() {
		DominioTurno turno = null;
		if (DominioTurnoTodos.M.equals(this)) {
			turno = DominioTurno.M;
		} else if (DominioTurnoTodos.T.equals(this)) {
			turno = DominioTurno.T;
		}else if (DominioTurnoTodos.N.equals(this)) {
			turno = DominioTurno.N;
		}
		return turno;
	}
}