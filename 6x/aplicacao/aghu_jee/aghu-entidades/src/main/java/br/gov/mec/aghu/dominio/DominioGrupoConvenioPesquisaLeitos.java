package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrupoConvenioPesquisaLeitos implements Dominio {
	
	/**
	 * SUS
	 */
	S,
	/**
	 * Convênio
	 */
	C, 
	/**
	 * Particular
	 */
	P,
	/**
	 * Convênio e Particular
	 */
	CP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "SUS";
		case C:
			return "Convênios";
		case P:
			return "Particulares";
		case CP:
			return "Conv e Part";
		default:
			return "";
		}
	}
	
	public String getSigla() {
		switch (this) {
		case S:
			return "S";
		case C:
			return "C";
		case P:
			return "P";
		case CP:
			return "CP";
		default:
			return "";
		}
	}
	

}
