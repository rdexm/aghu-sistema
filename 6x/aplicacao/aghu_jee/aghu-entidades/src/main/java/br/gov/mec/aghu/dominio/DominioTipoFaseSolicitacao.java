package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTipoFaseSolicitacao implements DominioString {
	C,
	S
	;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Compra";
		case S:
			return "Serviço";
		default:
			return "";
		}
	}

}
