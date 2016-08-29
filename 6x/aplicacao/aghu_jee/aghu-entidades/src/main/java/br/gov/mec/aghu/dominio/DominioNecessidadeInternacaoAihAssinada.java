package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis retornos das procedures do package AELK_EXI_EXA , que avaliam 
 * a necessidade de internação e de Aih assinada.
 *
 */
public enum DominioNecessidadeInternacaoAihAssinada implements Dominio {
	
	/**
	 * Indicador não encontrado
	 */
	V, 
	/**
	 * Não existem consulta para esse atendimento
	 */
	C,
	/**
	 * Triagem não encontrada
	 */
	T,
	/**
	 * Não dispara mensagem
	 */
	N,
	/**
	 * Deve disparar a mensagem
	 */
	S,
	/**
	 * Sem valor
	 */
	SV;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case V:
			return "Indicador não encontrado";
		case C:
			return "Não existem consulta para esse atendimento";
		case T:
			return "Triagem não encontrada";
		case N:
			return "Não dispara mensagem";
		case S:
			return "Deve disparar a mensagem";
		case SV:
			return "Sem valor";
		default:
			return "";
		}
	}

}