package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo da entidade RarCandidatoPrograma, se C ou R.
 * 
 * @author daniel.silva
 * @since 18/07/2012
 * 
 */
public enum DominioTipoRarCandidatoPrograma implements Dominio {
	C, R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "C";
		case R:
			return "R";
		default:
			return "";
		}
	}

}
