package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoObrigatoriedadeOpms implements Dominio {

	AGENDAMENTO, PRAZO;

	@Override
	public String getDescricao() {
		switch (this) {
		case AGENDAMENTO:
			return "Agendamento Cirurgia";
		case PRAZO:
			return "Prazo Antes Escala";
		default:
			return "";
		}
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public static DominioTipoObrigatoriedadeOpms getInstance(String valor) {
		if ("Agendamento".equalsIgnoreCase(valor)) {
			return DominioTipoObrigatoriedadeOpms.AGENDAMENTO;
		} else if ("Prazo".equalsIgnoreCase(valor)) {
			return DominioTipoObrigatoriedadeOpms.PRAZO;
		} else {
			return null;
		}
	}

}
