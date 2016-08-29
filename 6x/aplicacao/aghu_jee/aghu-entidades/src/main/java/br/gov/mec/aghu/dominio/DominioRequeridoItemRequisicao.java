package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioRequeridoItemRequisicao implements Dominio {
	REQ,
	NRQ,
	ADC,
	NOV,
	DSP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case REQ:
			return "Padrão Requerido";
		case NRQ:
			return "Padrão Não Requerido";
		case ADC:
			return "Adicionado Usuário";
		case NOV:
			return "Novo Sistema";
		case DSP:
			return "Dispensado";
		default:
			return "";
		}
	}
	
}
