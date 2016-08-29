package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica o tipo de perfil do médico de atendimento.
 * 
 * @author tfelini
 */

public enum DominioTipoPerfilAtendimento implements Dominio {
	/**
	 * Médico Contratado
	 */
	N1,
	/**
	 * Médico Residente
	 */
	N2,
	/**
	 * Acadêmico
	 */
	N3;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N1:
			return "Médico Contratado";
		case N2:
			return "Médico Residente";
		case N3:
			return "Acadêmico";

		default:
			return "";
		}
	}
	
}
