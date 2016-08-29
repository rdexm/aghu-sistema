package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica os status do indAbsenteismo de um Retorno.
 * 
 * @author tfelini
 */
public enum DominioIndAbsenteismo implements Dominio {
	
	/**
	 * Médico - Absenteísmo médico
	 */
	M,
	/**
	 * Paciente - Absenteísmo paciente
	 */
	P,
	/**
	 * Realizado - Atendimento realizado
	 */
	R,
	/**
	 * Nenhum - Não considera absenteísmo nem realização
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
			return "Médico - Absenteísmo médico";
		case P:
			return "Paciente - Absenteísmo paciente";
		case R:
			return "Realizado - Atendimento realizado";
		case N:
			return "Nenhum - Não considera absenteísmo nem realização";

		default:
			return "";
		}
	}

}

