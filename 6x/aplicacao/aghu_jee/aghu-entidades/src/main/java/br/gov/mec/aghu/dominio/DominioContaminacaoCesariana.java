package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioContaminacaoCesariana implements Dominio {
	L,
	C,
	P,
	I
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case L:
			return "Limpa";
		case C:
			return "Contaminada";
		case P:
			return "Potencialmente Contaminado";
		case I:
			return "Infectada";
		default:
			return "";
		}
	}

}
