package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 *
 */
public enum DominioIndFotoSensibilidade implements Dominio {
	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N,
	/**
	 * Tempo
	 */
	T,
	/**
	 * B -> Incluido pq estava dando erro na pesquisa de medicamentos
	 */
	B
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case T:
			return "Tempo";
		case B:
			return "INSERIR";//TODO rubens.silva não encontrei descrição para B
		default:
			return "";
		}
	}

}
