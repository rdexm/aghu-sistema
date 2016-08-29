package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o local de coleta da amostra de exame.
 * 
 * @author aghu
 * 
 */
public enum DominioCumpriuJejumColeta implements Dominio {
	
	P, // Paciente não sabe informar tempo de jejum cumprido
	S, // Cumpriu o jejum
	N; // Não cumpriu o jejum

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Não soube informar";
		case S:
			return "Sim";
		case N:
			return "Não";
		default:
			return "";
		}
	}

}
