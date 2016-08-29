package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma coleta. 
 * 
 * @author mtocchetto
 * 
 */
public enum DominioSituacaoColeta implements Dominio {
	/**
	 * Não Definida
	 */
	N,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Em Coleta
	 */
	E,
	/**
	 * Recebida
	 */
	R,
	/**
	 * Desnecessária
	 */
	D,
	/**
	 * Cancelada
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
			return "Não Definida";
		case P:
			return "Pendente";
		case E:
			return "Em Coleta";
		case R:
			return "Recebida";
		case D:
			return "Desnecessária";
		case C:
			return "Cancelada";
		default:
			return "";
		}
	}

}
