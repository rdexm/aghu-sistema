package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rafael.nascimento.
 */
public enum DominioSituacaoAceiteTecnico implements Dominio {
	/**
	 * Novo.
	 */
	N,
	/**
	 * Finalizado.
	 */
	F,
	/**
	 * Certificado.
	 */
	C;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Novo";
		case F:
			return "Finalizado";
		case C:
			return "Certificado";
		default:
			return "";
		}
	}
	
}