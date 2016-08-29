package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMedicaoItemControle implements Dominio {
	NU, // Numérico
	SN, // Sim Não
	CA, // Calculado
	TX, // Texto
	IF, // Início e Fim
	MI, // Misto (Num + SimNão)
	MT // Misto (Num + Texto)
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NU:
			return "Numérico";
		case SN:
			return "Sim/Não";
		case CA:
			return "Calculado";
		case TX:
			return "Texto";
		case IF:
			return "Início e Fim";
		case MI:
			return "Misto (Num + SimNão)";
		case MT:
			return "Misto (Num + Texto)";
		default:
			return "";
		}
	}
}
