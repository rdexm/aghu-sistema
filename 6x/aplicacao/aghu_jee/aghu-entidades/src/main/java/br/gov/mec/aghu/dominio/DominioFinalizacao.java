package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFinalizacao implements Dominio {
	
	/**
	 * Avaliada e Concluída
	 */
	S,

	/**
	 * Manter em acompanhamento
	 */
	A;
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Avaliada e Concluída";
		case A:
			return "Manter em acompanhamento";
		default:
			return "";
		}
	}
	
	public static DominioFinalizacao getInstance(String valor) {
		if ("S".equals(valor)) {
			return DominioFinalizacao.S;
		} else if ("A".equals(valor)) {
			return DominioFinalizacao.A;
		} else {
			return null;
		}
	}
	
}
