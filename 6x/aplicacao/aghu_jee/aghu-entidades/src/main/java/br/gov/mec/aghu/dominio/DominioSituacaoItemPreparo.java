package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoItemPreparo implements Dominio{
	
	P,
	E,
	I,
	A,
	R,
	T,
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Preparado";
		case E:
			return "Enviado";
		case I:
			return "Inutilizado";
		case A:
			return "A";
		case R:
			return "R";
		case T:
			return "T";
		case N:
			return "N";
		default:
			return "";
		}
	}
}
