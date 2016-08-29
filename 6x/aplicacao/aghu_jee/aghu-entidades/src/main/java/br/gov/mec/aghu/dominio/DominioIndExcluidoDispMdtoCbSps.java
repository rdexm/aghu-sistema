package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndExcluidoDispMdtoCbSps implements Dominio {
	I, 
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Incluído";
		case E:
			return "Estornado";
		default:
			return "";
		}
	}

}
