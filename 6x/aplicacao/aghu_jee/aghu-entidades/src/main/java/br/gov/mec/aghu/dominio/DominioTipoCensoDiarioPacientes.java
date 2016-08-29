package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author dfriedrich
 * 
 */
public enum DominioTipoCensoDiarioPacientes implements Dominio {
	
	/**
	 * Pacientes internados em unidades de internação
	 */
	I,
	/**
	 * Pacientes internados em área satélite (ex. emergência, Centro obstétrico)
	 */
	A,
	/**
	 * Pacientes em atendimento de Hospital Dia
	 */
	H,

	/**
	 * Leitos sem pacientes (livres, bloqueados, reservados)
	 */
	L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Unidade de Internação";
		case A:
			return "Área Satélite";
		case H:
			return "Hospital Dia";
		case L:
			return "Sem Paciente";
		default:
			return "";
		}
	}

}
