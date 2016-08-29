package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioClassificacaoGestacao implements Dominio{
	V,
	C,
	A,
	M,
	E
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case V:
			return "Vaginal";
		case C:
			return "Cesariana";
		case A:
			return "Aborto";
		case M:
			return "Mola";
		case E:
			return "Ectópia";
		default:
			return "";
		}
	}

}
