package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis tipos de mensagem da entidade ael_exigencia_exames.
 *
 */
public enum DominioTipoMensagemExame implements Dominio {

	I, 
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "I";
		case A:
			return "A";
		default:
			return "";
		}
	}


}