package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAcomodacao implements Dominio {
	/**
	 * Poltrona
	 */
	P,
	/**
	 * Cabine
	 */
	C;

	private DominioTipoAcomodacao() {
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Poltrona";
		case C:
			return "Cabine";
		default:
			return "";
		}
	}

	/**
	 * Converte DominioTipoAcomodacao para DominioTipoLocal
	 * 
	 * @return
	 */
	public DominioTipoLocal getTipoLocal() {
		DominioTipoLocal local = null;
		if (DominioTipoAcomodacao.C.equals(this)) {
			local = DominioTipoLocal.C;
		} else if (DominioTipoAcomodacao.P.equals(this)) {
			local = DominioTipoLocal.P;
		}
		return local;
	}

}
