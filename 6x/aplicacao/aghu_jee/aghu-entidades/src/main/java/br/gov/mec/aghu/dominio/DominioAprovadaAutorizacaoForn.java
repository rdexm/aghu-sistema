package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioAprovadaAutorizacaoForn implements DominioString {
	N,
	S,
	A,
	E,
	C
	;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Liberar AF p/ Assinatura";
		case C:
			return "Liberada para Assinatura";
		case A:
			return "Versão já Assinada";
		case E:
			return "Versão empenhada";
		case S:
			return "Alteração Pendente de Justificativa";
		default:
			return this.toString();	
		}
	}

}
