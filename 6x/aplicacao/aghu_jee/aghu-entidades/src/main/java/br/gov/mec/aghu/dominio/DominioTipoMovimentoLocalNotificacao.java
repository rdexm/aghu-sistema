package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMovimentoLocalNotificacao implements Dominio {

	MMP, MIT, MFP, MRI;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MMP:
			return "Movimento Medida Preventiva";
		case MIT:
			return "Movimento Infecção Topografia";
		case MFP:
			return "Movimento Fator Predisponente";
		case MRI:
			return "Movimento Procedimento Risco";
		default:
			return "";
		}
	}

}
