package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrupoMbcVentilacao implements Dominio {
	ES, 
	MA, 
	ME,
	MO,
	SI,
	ON,
	MN;
	//NÃO ALTERAR A ORDEM ,porque irá alterar o getOrdinal  e o mesmo é utilizado para ordenacao em outro momento
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ES:
			return "";
		case MA:
			return "";
		case ME:
			return "";
		case MO:
			return "";
		case SI:
			return "";
		case ON:
			return "";
		case MN:
			return "";
		default:
			return "";
		}
	}

}
