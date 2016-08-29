package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica  se uma requisição de material já foi impressa.
 * 
 * @author Fábio Winck
 * 
 */
public enum DominioImpresso implements Dominio {
	/**
	 * Não Impressa
	 */
	N,

	/**
	 * Local
	 */
	L,
	
	/**
	 * Remota
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não Impressa";
		case L:
			return "Impressão Local";
		case R:
			return "Impressão Remota";
		default:
			return "";
		}
	}
	
	public static DominioImpresso getInstance(String valor){
		if (valor.equalsIgnoreCase("N")) {
			return DominioImpresso.N;
		} else if (valor.equalsIgnoreCase("L")) {
			return DominioImpresso.L;
		} else if (valor.equalsIgnoreCase("R")) {
			return DominioImpresso.R;
		}else{
			return DominioImpresso.N;	
		}
	}
}