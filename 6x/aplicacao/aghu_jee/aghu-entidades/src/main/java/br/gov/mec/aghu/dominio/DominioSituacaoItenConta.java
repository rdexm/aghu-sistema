package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de um item
 * de conta
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSituacaoItenConta implements Dominio {
	
	/**
	 * Ativo
	 */
	A,
	/**
	 * Cancelado
	 */
	C,
	/**
	 * Pontuação
	 */
	P,
	/**
	 * Valor
	 */
	V,
	/**
	 * Rejeitado
	 */
	R,
	/**
	 * Cancelado por Encerramento
	 */
	N,
	/**
	 * Cancelado por Desdobramento
	 */
	D,
	/**
	 * Transferido
	 */
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ativo";
		case C:
			return "Cancelado";
		case P:
			return "Pontuação";
		case V:
			return "Valor";
		case R:
			return "Rejeitado";
		case N:
			return "Cancelado por Encerramento";
		case D:
			return "Cancelado por Desdobramento";
		case T:
			return "Transferido";
		default:
			return "";
		}

	}

}
