package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoExclusaoProgramacaoAf implements Dominio {
	
	/**
	 * Especialidade
	 */
	AF, 
	/**
	 * Paciente
	 */
	ITEM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AF:
			return "Excluir Toda a Programação da AF";
		case ITEM:
			return "Excluir Programação por Item de AF";
		
		default:
			return "";
		}
	}

}
