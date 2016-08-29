package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFuncaoPatologista implements Dominio{
	/**
	 * Professor
	 */
	P,
	/**
	 * Contratado
	 */
	C,
	/**
	 * Residente
	 */
	R,
	/**
	 * Técnico
	 */
	T;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Patologista Professor";
		case C:
			return "Patologista Contratado";
		case R:
			return "Residente";
		case T:
			return "Técnico";	
		default:
			return "";
		}
	}
	
	public String getPermissao() {
		switch (this) {
		case P:
			return "MED12";
		case C:
			return "MED12";
		case R:
			return "MED12.1";
		case T:
			return "OPS23";	
		default:
			return "";
		}
	}
}
