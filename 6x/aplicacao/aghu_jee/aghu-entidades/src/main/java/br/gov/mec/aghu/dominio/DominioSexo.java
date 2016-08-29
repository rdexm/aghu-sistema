package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSexo implements Dominio {
	
	/**
	 * Masculino
	 */
	M,

	/**
	 * Feminino
	 */
	F,
	
	/**
	 * Ignorado
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Masculino";
		case F:
			return "Feminino";
		case I:
			return "Ignorado";	
		default:
			return "";
		}
	}

	public static DominioSexo getInstance(String valor) {
		if ("M".equals(valor)) {
			return DominioSexo.M;
		} else if ("F".equals(valor)) {
			return DominioSexo.F;
		} else if ("I".equals(valor)) {
			return DominioSexo.I;
		} else {
			return null;
		}
	}
	
}