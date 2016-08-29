package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domimio para entidade SCE_ITEM_NOTA_PRE_RECEBIMENTO
 **/
public enum DominioItemNotaPreRecebimento implements Dominio {

	G,
	C,
	V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "G";
		case C:
			return "C";
		case V:
			return "V";
		default:
			return "";
		}
	}
}
