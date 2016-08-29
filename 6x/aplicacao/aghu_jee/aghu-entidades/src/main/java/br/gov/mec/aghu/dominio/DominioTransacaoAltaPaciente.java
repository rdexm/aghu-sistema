package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a transação de alta do paciente. 
 * 
 * @author Marcelo Tocchetto
 */
public enum DominioTransacaoAltaPaciente implements Dominio {
	
	/**
	 * Processar Alta
	 */
	PROCESSA_ALTA,
	
	/**
	 * Estornar Alta
	 */
	ESTORNA_ALTA,
	
	/**
	 * Alterar Alta
	 */
	ALTERA_ALTA,
	
	/**
	 * Internação
	 */
	INTERNACAO,
	/**
	 * Internação Estornada
	 */
	ESTORNO_INTERNACAO,
	/**
	 * Internação Atualizada
	 */
	INTERNACAO_ATUALIZADA;



	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PROCESSA_ALTA:
			return "PROCESSA_ALTA";
		case ESTORNA_ALTA:
			return "ESTORNA_ALTA";
		case ALTERA_ALTA:
			return "ALTERA_ALTA";
		case INTERNACAO:
			return "INTERNACAO";
		case ESTORNO_INTERNACAO:
			return "INT_ESTORNADA";
		case INTERNACAO_ATUALIZADA:
			return "INT_ATUALIZADA";
		default:
			return "";
		}
	}

}
