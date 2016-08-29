package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSexoValorNormalidadeCampo implements Dominio {

	/**
	 * Masculino
	 */
	M,

	/**
	 * Feminino
	 */
	F;

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
		default:
			return "";
		}
	}

	public static DominioSexoValorNormalidadeCampo getInstance(String valor) {
		if ("M".equalsIgnoreCase(valor)) {
			return DominioSexoValorNormalidadeCampo.M;
		} else if ("F".equalsIgnoreCase(valor)) {
			return DominioSexoValorNormalidadeCampo.F;
		} else {
			return null;
		}
	}

}