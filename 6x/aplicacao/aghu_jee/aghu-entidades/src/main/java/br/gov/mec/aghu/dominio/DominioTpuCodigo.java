package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTpuCodigo implements Dominio {
	
	/**
	 * Comissão de acesso
	 */
	CA,
	
	/**
	 * Super usuario
	 */
	SU,
	
	/**
	 * Usuario Comum Habilitado a criar usuarios
	 */
	UH,
	
	/**
	 * Usuario
	 */
	US;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CA:
			return "Comissão de acesso";
		case SU:
			return "Super usuário";		
		case UH:
			return "Usuário comum habilitado a criar usuários";
		case US:
			return "Usuário";		
		default:
			return "";
		}
	}

}
