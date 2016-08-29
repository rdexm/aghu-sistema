package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoLocal implements Dominio {
	/**
	 * Poltrona
	 */
	P,
	/**
	 * Cabine
	 */
	C,
	/**
	 * Todos
	 */
	T;

	private DominioTipoLocal() {
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
		case T:
			return "Todos";
		default:
			return "";
		}
	}

	/**
	 * Converte DominioTipoLocal para DominioTipoAcomodacao
	 * 
	 * @return
	 */
	public DominioTipoAcomodacao getTipoAcomodacao() {
		DominioTipoAcomodacao acomodacao = null;
		if (DominioTipoLocal.C.equals(this)) {
			acomodacao = DominioTipoAcomodacao.C;
		} else if (DominioTipoLocal.P.equals(this)) {
			acomodacao = DominioTipoAcomodacao.P;
		}
		return acomodacao;
	}

}
