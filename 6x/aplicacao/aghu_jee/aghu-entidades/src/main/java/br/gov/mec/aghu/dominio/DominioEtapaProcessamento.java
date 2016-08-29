package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEtapaProcessamento implements Dominio{
	/**
	 * Insumos
	 */
	I,
	/**
	 * Equipamentos
	 */
	E,
	/**
	 * Pessoas
	 */
	P,
	/**
	 * Serviços
	 */
	S,
	/**
	 * Diretos
	 */
	D,
	/**
	 * Indiretos
	 */
	N,
	/**
	 * Geral
	 */
	G,
	/**
	 * Contagem
	 */
	C,
	/**
	 * Custo Variável
	 */
	V;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Insumos";
		case E:
			return "Equipamentos";
		case P:
			return "Pessoas";
		case S:
			return "Serviços";
		case D:
			return "Diretos";
		case N:
			return "Indiretos";
		case G:
			return "Geral";
		case C:
			return "Contagem";
		case V:
			return "Custo Variável";	
		default:
			return "";
		}
	}

}
