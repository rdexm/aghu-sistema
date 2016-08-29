package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lalegre
 *
 */
public enum DominioResponsavelColetaExames implements Dominio {
	/**
	 * Coletador
	 */
	C, 

	/**
	 * Paciente
	 */
	P, 
	/**
	 * Solicitante 
	 */
	S,
	/**
	 * Executor
	 */
	E,
	/**
	 * Não Coletável 
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Coletador";
		case P:
			return "Paciente";
		case S:
			return "Solicitante";
		case E:
			return "Executor";
		case N:
			return "Não Coletável";
		default:
			return "";
		}
	}
	
}
