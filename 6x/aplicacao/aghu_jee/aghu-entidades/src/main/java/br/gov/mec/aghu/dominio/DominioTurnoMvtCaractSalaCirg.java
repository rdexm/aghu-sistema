package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTurnoMvtCaractSalaCirg implements Dominio {
	/**
	 * M
	 */
	M,
	/**
	 * T
	 */
	T,
	/**
	 * N
	 */
	N,
	/**
	 * O
	 */
	O;

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
		case O:
			return "Outro";
		default:
			return "";
		}
	}
	
	public Integer getCodigoTurno(){
		switch (this) {
		case M:
			return 1;
		case T:
			return 2;
		case N:
			return 3;
		default:
			return 4;
		}
	}
}