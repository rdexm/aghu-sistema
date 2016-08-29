package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author gmneto
 * 
 */
public enum DominioSituacaoQualificacao implements Dominio {
	/**
	 * Em andamento
	 */
	E, 
	/**
	 * Concluida
	 */
	C,
	/**
	 * Interrompida
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Interrompida";
		case E:
			return "Em andamento";
		case C:
			return "Concluída";
		default:
			return "";
		}
	}
	


}
