package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as mensagens a serem exibidas 
 * para o usuario quando de um estorno AIH
 * 
 * @author Eduardo Giovany Schweigert
 */
public enum DominioMensagemEstornoAIH implements Dominio {

	/**
	 * nroAIH Confirma estorno?
	 */
	C,
	
	/**
	 * nroAIH já foi impressa. Confirma estorno?
	 */
	I,
	/**
	 * NroAIH ainda não foi impressa. Confirma estorno?
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case C:
				return "Confirma estorno?";
			case I:
				return " já foi impressa. Confirma estorno?";
			case N:
				return " ainda não foi impressa. Confirma estorno?";
			default:
				return "";
		}
	}
}