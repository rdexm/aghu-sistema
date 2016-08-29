package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEmissaoSumario implements Dominio {

	/**
	 * Emissão após a alta do paciente (diária)
	 */
	I,
	/**
	 * Emissão após a alta do paciente via Prontuário On-Line
	 */
	P,
	/**
	 * Emissão de todos os sumários do paciente pela tela de emissão de sumário
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Emissão após a alta do paciente (diária)";
		case P:
			return "Emissão após a alta do paciente via Prontuário On-Line";
		case C:
			return "Emissão de todos os sumários do paciente pela tela de emissão de sumário";
		default:
			return "";
		}
	}

}
