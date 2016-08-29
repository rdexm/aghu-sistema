package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoPesquisaPacientesAdmitidos implements Dominio {
	/**
	 * Especialidade
	 */
	P, 
	/**
	 * Paciente
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Paciente";
		case E:
			return "Especialidade";
		
		default:
			return "";
		}
	}
}
