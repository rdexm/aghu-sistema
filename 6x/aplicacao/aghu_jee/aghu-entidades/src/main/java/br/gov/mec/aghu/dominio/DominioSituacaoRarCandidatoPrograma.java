package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação da entidade RarCandidatoPrograma, se A, D ou E.
 * 
 * @author daniel.silva
 * @since 28/06/2012
 * 
 */
public enum DominioSituacaoRarCandidatoPrograma implements Dominio {
	A, D, E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "A";
		case D:
			return "D";
		case E:
			return "E";
		default:
			return "";
		}
	}

}
