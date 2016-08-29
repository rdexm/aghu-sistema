package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica a situação do julgamento de um item do PAC.
 * 
 */
public enum DominioSituacaoJulgamento implements Dominio {
	/**
	 * JULGADO
	 */
	JU,
	/**
	 * EM TESTE
	 */
	TE,
	/**
	 * AGUARDANDO DOC HABILITAÇÃO
	 */
	AH,
	/**
	 * EM NEGOCIAÇÃO
	 */
	EN,
	/**
	 * SEM JULGAMENTO
	 */
	SJ;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case JU:
			return "Julgado";
		case TE:
			return "Em Teste";
		case AH:
			return "Aguardando Doc Habilitação";
		case EN:
			return "Em Negociação";
		case SJ:
			return "Sem Julgamento";
		default:
			return "";
		}

	}
}
