package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioParecerOcorrencia implements Dominio{
	/**
	 * PF -PARECER FAVORAVEL
	 * PD-PARECER DESFAVORAVEL
	 * EA-EM AVALIACAO
	 */
	PF,
	PD;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PF:
			return "Favorável";		
		case PD:
			return "Desfavorável";
					
		default:
			return "";
		}
	}

}
