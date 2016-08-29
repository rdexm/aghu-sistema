package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de um bem permanente.
 * 
 */
public enum DominioTipoBemPermanente implements Dominio {

	DOACAO,
	COMODATO,
	AQUISICAO,
	BEM_DE_TERCEIRO,
	OUTRO;

	@Override
	public int getCodigo() {

		return this.ordinal() + 1;
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case DOACAO:
			return "Doação";
		case COMODATO:
			return "Comodato";
		case AQUISICAO:
			return "Aquisição";
		case BEM_DE_TERCEIRO:
			return "Bem de Terceiro";
		case OUTRO:
			return "Outro";
		default:
			return "";
		}
	}

}
