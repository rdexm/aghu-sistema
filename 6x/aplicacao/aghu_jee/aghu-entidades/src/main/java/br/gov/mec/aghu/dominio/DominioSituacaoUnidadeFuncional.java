package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica situação da unidade funcional.
 * 
 */
public enum DominioSituacaoUnidadeFuncional implements Dominio {
	
	/**
	 * Pacientes
	 */
	PACIENTES,

	/**
	 * Internações
	 */
	INTERNACOES,
	
	/**
	 * Altas
	 */
	ALTAS,

	/**
	 * Recebidos
	 */
	RECEBIDOS,
	
	/**
	 * Enviados
	 */
	ENVIADOS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PACIENTES:
			return "Pacientes";
		case INTERNACOES:
			return "Internações";
		case ALTAS:
			return "Altas";
		case RECEBIDOS:
			return "Recebidos";
		case ENVIADOS:
			return "Enviados";
		default:
			return "";
		}
	}

}
