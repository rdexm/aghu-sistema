package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndOutraExtraPulmonar implements Dominio {
	//

	/**
	 * Outras - Nova versão - novembro/2007
	 */
	OUTRAS_NOVA_VERSAO(10),

	/**
	 * Outras
	 */
	OUTRAS(8),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndOutraExtraPulmonar(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OUTRAS_NOVA_VERSAO:
			return "Outras - Nova versão - novembro/2007 ";
		case OUTRAS:
			return "Outras";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}