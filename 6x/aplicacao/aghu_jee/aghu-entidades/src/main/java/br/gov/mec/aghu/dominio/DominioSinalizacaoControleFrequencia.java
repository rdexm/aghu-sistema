package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSinalizacaoControleFrequencia implements Dominio {
	CF,
	PI,
	TR,
	PL,
	IC,
	DI,
	AD,
	NA,
	RE,
	AP;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CF:
			return "Controle de Frequência";
		case PI:
			return "Pendência de impressão";
		case TR:
			return "Transplantado";
		case PL:
			return "Laudo para acompanhamento pós-transplante";
		case IC:
			return "Laudo para implante coclear";
		case DI:
			return "Laudo de diagnóstico";
		case AD:
			return "Laudo de acompanhamento de paciente adaptado";
		case NA:
			return "Laudo de acompanhamento de paciente não adaptado";
		case RE:
			return "Laudo de reavaliação";
		case AP:
			return "Laudo de acompanhamento de paciente com aparelho";
		default:
			return "";
		}
	}

}

